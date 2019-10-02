package de.pauhull.friends.spigot.inventory;

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

public class PlayerViewMenu implements InventoryMenu {

    private static final ItemStack GLASS_PANE = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData(15).setDisplayName(" ").build();
    private static final ItemStack REMOVE_FRIEND = new ItemBuilder().setMaterial(Material.REDSTONE).setDisplayName("§8» §cFreund entfernen").build();
    private static final ItemStack INVITE_PARTY = new ItemBuilder().setMaterial(Material.CAKE).setDisplayName("§8» §dIn Party einladen").build();
    private static final ItemStack JUMP = new ItemBuilder().setMaterial(Material.FIREWORK).setDisplayName("§8» §eZu Freund springen").build();
    private static final ItemStack BACK = new ItemBuilder().setMaterial(Material.ARROW).setDisplayName("§8» §cZurück").build();

    private SpigotFriends friends;

    public PlayerViewMenu(SpigotFriends friends) {
        this.friends = friends;

        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    public void show(Player player, String viewed) {

        friends.getUuidFetcher().fetchProfileAsync(viewed, profile -> {
            friends.getSettingsTable().getStatus(profile.getUuid(), statusMessage -> {
                Inventory inventory = Bukkit.createInventory(null, 45, "§cFreund: " + viewed);

                for (int i = 0; i < inventory.getSize(); i++) {
                    if ((i < 9 || i > inventory.getSize() - 9) || (i % 9 == 0 || (i + 1) % 9 == 0)) {
                        inventory.setItem(i, GLASS_PANE);
                    }
                }

                ItemStack head = friends.getHeadCache().getHead(viewed);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName("§8» §a" + viewed);
                head.setItemMeta(meta);
                inventory.setItem(13, head);
                ItemStack status = new ItemStack(Material.PAPER);
                ItemMeta statusMeta = status.getItemMeta();
                statusMeta.setDisplayName("§8» §6Status: §r" + statusMessage);
                status.setItemMeta(statusMeta);
                inventory.setItem(22, status);
                inventory.setItem(20, JUMP);
                inventory.setItem(24, INVITE_PARTY);
                inventory.setItem(30, REMOVE_FRIEND);
                inventory.setItem(32, BACK);

                Bukkit.getScheduler().runTask(friends, () -> {
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                    player.openInventory(inventory);
                });
            });
        });

    }

    @Override
    public void show(Player player) {
        this.show(player, player.getName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory != null && inventory.getTitle() != null && inventory.getTitle().startsWith("§cFreund: ")) {
            event.setCancelled(true);
        } else {
            return;
        }

        String playerName = inventory.getTitle().substring("§cFreund: ".length());

        ItemStack item = event.getCurrentItem();

        if (item != null) {
            if (item.equals(BACK)) {
                friends.getFriendMenu().show(player, 0);
            } else if (item.equals(REMOVE_FRIEND)) {
                new RunCommandMessage(player.getName(), "friend remove " + playerName).sendToProxy("Proxy");
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            } else if (item.equals(JUMP)) {
                new RunCommandMessage(player.getName(), "friend jump " + playerName).sendToProxy("Proxy");
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            } else if (item.equals(INVITE_PARTY)) {
                new RunCommandMessage(player.getName(), "party invite " + playerName).sendToProxy("Proxy");
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                player.closeInventory();
            }
        }

    }

}
