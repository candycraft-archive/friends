package de.pauhull.friends.spigot.inventory;

import de.pauhull.friends.common.util.TimeUtil;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.friends.spigot.util.ItemBuilder;
import de.pauhull.uuidfetcher.common.communication.message.RunCommandMessage;
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

public class FriendRequestMenu implements InventoryMenu {

    private static final String TITLE = "§cAnfragen";
    private static final ItemStack NO_REQUESTS = new ItemBuilder().setMaterial(Material.BARRIER).setDisplayName("§8» §cDu hast keine Freundschaftsanfragem.").build();
    private static final ItemStack GLASS_PANE = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData(15).setDisplayName(" ").build();
    private static final ItemStack NEXT_PAGE = new ItemBuilder().setMaterial(Material.ARROW).setGlowing(true).setDisplayName("§8» §eNächste Seite").build();
    private static final ItemStack PREVIOUS_PAGE = new ItemBuilder().setMaterial(Material.ARROW).setGlowing(true).setDisplayName("§8» §eVorherige Seite").build();
    private static final ItemStack NEXT_PAGE_DISABLED = new ItemBuilder().setMaterial(Material.ARROW).setDisplayName("§8» §7Keine nächste Seite").build();
    private static final ItemStack PREVIOUS_PAGE_DISABLED = new ItemBuilder().setMaterial(Material.ARROW).setDisplayName("§8» §7Keine vorherige Seite").build();
    private static final ItemStack ACCEPT_ALL = new ItemBuilder().setMaterial(Material.EMERALD_BLOCK).setDisplayName("§8» §a§lAlle annehmen").build();
    private static final ItemStack DENY_ALL = new ItemBuilder().setMaterial(Material.REDSTONE_BLOCK).setDisplayName("§8» §c§lAlle ablehnen").build();
    private static final ItemStack BACK = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setDisplayName("§8» §cZurück").setData(14).build();

    private SpigotFriends friends;

    public FriendRequestMenu(SpigotFriends friends) {
        this.friends = friends;

        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    public void show(Player player, int page) {
        int results = 45;
        int start = page * results;

        friends.getFriendRequestTable().getFriendRequests(player.getUniqueId(), start, results, requests -> {

            int inventorySize = 9 * ((int) Math.ceil(Math.max(requests.size(), 1) / 9.0) + 1);
            if (inventorySize > 54) {
                inventorySize = 54;
            }

            Inventory inventory = Bukkit.createInventory(null, inventorySize, TITLE + " §8(Seite " + (page + 1) + ")");

            for (int i = 0; i < 9; i++) {
                inventory.setItem(inventorySize - 9 + i, GLASS_PANE);
            }

            inventory.setItem(inventorySize - 9, page > 0 ? PREVIOUS_PAGE : PREVIOUS_PAGE_DISABLED);
            inventory.setItem(inventorySize - 7, ACCEPT_ALL);
            inventory.setItem(inventorySize - 5, BACK);
            inventory.setItem(inventorySize - 3, DENY_ALL);
            inventory.setItem(inventorySize - 1, requests.size() >= 45 ? NEXT_PAGE : NEXT_PAGE_DISABLED);

            if (requests.isEmpty()) {
                if (page == 0) {
                    inventory.setItem(0, NO_REQUESTS);
                }
            } else {
                int i = 0;
                for (UUID requesterUUID : requests.keySet()) {
                    final int index = i++;
                    friends.getUuidFetcher().fetchNameAsync(requesterUUID, name -> {
                        friends.getLastOnlineTable().getLastOnline(requesterUUID, lastOnline -> {
                            friends.getFriendTable().getTime(player.getUniqueId(), requesterUUID, friendTime -> {
                                friends.getSettingsTable().getStatus(requesterUUID, status -> {

                                    ItemStack head = friends.getHeadCache().getHead(name);
                                    ItemMeta meta = head.getItemMeta();
                                    meta.setDisplayName("§8» §a" + name);
                                    List<String> lore = new ArrayList<>();

                                    long time = requests.get(requesterUUID);
                                    lore.add("§8× §fGestellt vor " + TimeUtil.format(time, System.currentTimeMillis()));

                                    meta.setLore(lore);
                                    head.setItemMeta(meta);

                                    inventory.setItem(index, head);
                                });
                            });
                        });
                    });
                }
            }

            Bukkit.getScheduler().runTask(friends, () -> {
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                player.openInventory(inventory);
            });
        });

    }

    @Override
    public void show(Player player) {
        this.show(player, 0);
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
                friends.getFriendRequestMenu().show(player, pageIndex + 1);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            } else if (item.equals(PREVIOUS_PAGE)) {
                friends.getFriendRequestMenu().show(player, Math.max(0, pageIndex - 1));
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            } else if (item.equals(BACK)) {
                friends.getFriendMenu().show(player);
            } else if (item.equals(ACCEPT_ALL)) {
                new RunCommandMessage(player.getName(), "friend accept all").sendToProxy("Proxy");
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            } else if (item.equals(DENY_ALL)) {
                new RunCommandMessage(player.getName(), "friend deny all").sendToProxy("Proxy");
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            }
        }

        if (item != null && item.getType() == Material.SKULL_ITEM && item.getDurability() == 3) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            friends.getAcceptMenu().show(player, meta.getOwner());
        }
    }

}
