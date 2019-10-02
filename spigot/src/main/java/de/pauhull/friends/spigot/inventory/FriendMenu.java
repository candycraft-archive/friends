package de.pauhull.friends.spigot.inventory;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import de.pauhull.friends.common.util.TimeUtil;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.friends.spigot.util.ItemBuilder;
import de.pauhull.uuidfetcher.common.communication.message.RunCommandMessage;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendMenu implements InventoryMenu {

    private static final String TITLE = "§cFreunde";
    private static final ItemStack REQUESTS = new ItemBuilder().setMaterial(Material.BOOK).setDisplayName("§8» §eFreundschaftsanfragen").build();
    private static final ItemStack NO_FRIENDS = new ItemBuilder().setMaterial(Material.BARRIER).setDisplayName("§8» §cDu hast keine Freunde.").build();
    private static final ItemStack GLASS_PANE = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData(15).setDisplayName(" ").build();
    private static final ItemStack NEXT_PAGE = new ItemBuilder().setMaterial(Material.ARROW).setGlowing(true).setDisplayName("§8» §eNächste Seite").build();
    private static final ItemStack PREVIOUS_PAGE = new ItemBuilder().setMaterial(Material.ARROW).setGlowing(true).setDisplayName("§8» §eVorherige Seite").build();
    private static final ItemStack NEXT_PAGE_DISABLED = new ItemBuilder().setMaterial(Material.ARROW).setDisplayName("§8» §7Keine nächste Seite").build();
    private static final ItemStack PREVIOUS_PAGE_DISABLED = new ItemBuilder().setMaterial(Material.ARROW).setDisplayName("§8» §7Keine vorherige Seite").build();
    private static final ItemStack SEND_REQUEST = new ItemBuilder().setMaterial(Material.ANVIL).setDisplayName("§8» §eAnfrage stellen").build();
    private static final ItemStack LOADING = new ItemBuilder().setMaterial(Material.SKULL).setDisplayName("§c§lLÄDT...").build();
    private static final ItemStack BACK = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setDisplayName("§8» §cZurück").setData(14).build();

    private SpigotFriends friends;

    public FriendMenu(SpigotFriends friends) {
        this.friends = friends;
        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    public void show(Player player, int page) {

        int results = 45;
        int start = page * results;

        friends.getFriendTable().getFriends(player.getUniqueId(), start, results, friendList -> {

            int inventorySize = 9 * ((int) Math.ceil(Math.max(friendList.size(), 1) / 9.0) + 1);
            if (inventorySize > 54) {
                inventorySize = 54;
            }

            Inventory inventory = Bukkit.createInventory(null, inventorySize, TITLE + " §8(Seite " + (page + 1) + ")");

            for (int i = 0; i < 9; i++) {
                inventory.setItem(inventorySize - 9 + i, GLASS_PANE);
            }

            inventory.setItem(inventorySize - 9, page > 0 ? PREVIOUS_PAGE : PREVIOUS_PAGE_DISABLED);
            inventory.setItem(inventorySize - 7, SEND_REQUEST);
            inventory.setItem(inventorySize - 5, BACK);
            inventory.setItem(inventorySize - 3, REQUESTS);
            inventory.setItem(inventorySize - 1, friendList.size() >= 45 ? NEXT_PAGE : NEXT_PAGE_DISABLED);

            if (friendList.isEmpty()) {
                if (page == 0) {
                    inventory.setItem(0, NO_FRIENDS);
                }

                Bukkit.getScheduler().runTask(friends, () -> {
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                    player.openInventory(inventory);
                });
            } else {
                for (int i = 0; i < friendList.size(); i++) {

                    inventory.setItem(i, LOADING);

                    UUID friendUUID = friendList.get(i);
                    final int index = i;
                    friends.getUuidFetcher().fetchProfileAsync(friendUUID, profile -> {
                        long lastOnline = friends.getLastOnlineTable().getLastOnlineSync(friendUUID);
                        long friendTime = friends.getFriendTable().getTimeSync(player.getUniqueId(), friendUUID);
                        String status = friends.getSettingsTable().getStatusSync(friendUUID);

                        boolean online = TimoCloudAPI.getUniversalAPI().getPlayer(friendUUID) != null;

                        ItemStack head = friends.getHeadCache().getHead(profile.getPlayerName());
                        ItemMeta meta = head.getItemMeta();
                        meta.setDisplayName("§8» §a" + profile.getPlayerName());
                        List<String> lore = new ArrayList<>();

                        lore.add("§f" + status);

                        lore.add("§8§m                   ");

                        if (online) {
                            lore.add("§a§lONLINE");
                        } else {
                            if (lastOnline == 0) {
                                lore.add("§8× §fLetztes Onlinedatum unbekannt");
                            } else {
                                lore.add("§8× §fZuletzt online vor " + TimeUtil.format(lastOnline, System.currentTimeMillis()));
                            }
                        }

                        if (friendTime != 0) {
                            lore.add("§8× §fBefreundet seit " + TimeUtil.format(friendTime, System.currentTimeMillis()));
                        } else {
                            lore.add("§8× §fBefreundet seit: Unbekannt");
                        }

                        meta.setLore(lore);
                        head.setItemMeta(meta);

                        inventory.setItem(index, head);
                        if (index >= friendList.size() - 1) {
                            Bukkit.getScheduler().runTask(friends, () -> {
                                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                                player.openInventory(inventory);
                            });
                        }
                    });
                }
            }

        });

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().startsWith(TITLE)) {
            return;
        }

        event.setCancelled(true);

        String currentPageString = inventory.getTitle().substring((TITLE + " §8(Seite ").length(), inventory.getTitle().length() - 1);
        int pageIndex = Integer.parseInt(currentPageString) - 1;

        ItemStack item = event.getCurrentItem();

        if (item != null) {
            if (item.equals(NEXT_PAGE)) {
                friends.getFriendMenu().show(player, pageIndex + 1);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            } else if (item.equals(PREVIOUS_PAGE)) {
                friends.getFriendMenu().show(player, Math.max(0, pageIndex - 1));
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            } else if (item.equals(REQUESTS)) {
                friends.getFriendRequestMenu().show(player);
            } else if (item.equals(BACK)) {
                friends.getMainMenu().show(player);
            } else if (item.equals(SEND_REQUEST)) {
                new AnvilGUI(friends, player, "Anfrage senden...", (ignored, reply) -> {
                    new RunCommandMessage(player.getName(), "friend add " + reply).sendToProxy("Proxy");
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                    return null;
                });
            }
        }

        if (item != null && item.getType() == Material.SKULL_ITEM && item.getDurability() == 3) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            friends.getPlayerViewMenu().show(player, meta.getOwner());
        }

    }

    @Override
    public void show(Player player) {
        this.show(player, 0);
    }

}
