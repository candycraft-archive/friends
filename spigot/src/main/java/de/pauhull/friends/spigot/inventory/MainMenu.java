package de.pauhull.friends.spigot.inventory;

import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.friends.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class MainMenu implements InventoryMenu {

    private static final String TITLE = "§cProfil";
    private static final ItemStack FRIENDS = new ItemBuilder().setMaterial(Material.EMERALD).setDisplayName("§8» §3Freunde").build();
    private static final ItemStack PARTY = new ItemBuilder().setMaterial(Material.CAKE).setDisplayName("§8» §5Öffentliche Parties").build();
    private static final ItemStack CLANS = new ItemBuilder().setMaterial(Material.IRON_CHESTPLATE).setDisplayName("§8» §2Clans").setLore(Collections.singletonList("§8× §a§lKOMMT BALD... §8×")).build();
    private static final ItemStack SETTINGS = new ItemBuilder().setMaterial(Material.PAPER).setDisplayName("§8» §eEinstellungen").build();

    private SpigotFriends friends;

    public MainMenu(SpigotFriends friends) {
        this.friends = friends;
        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    @Override
    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, TITLE);

        inventory.setItem(1, FRIENDS);
        inventory.setItem(4, PARTY);
        inventory.setItem(7, SETTINGS);

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        player.openInventory(inventory);
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().equals(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {
            if (stack.equals(SETTINGS)) {
                friends.getSettingsMenu().show(player);
            } else if (stack.equals(FRIENDS)) {
                friends.getFriendMenu().show(player);
            } else if (stack.equals(PARTY)) {
                friends.getPartyMenu().show(player);
            } else if (stack.equals(CLANS)) {
                //TODO
                player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
            }
        }
    }

}
