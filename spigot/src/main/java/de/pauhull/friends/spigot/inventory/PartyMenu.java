package de.pauhull.friends.spigot.inventory;

import de.pauhull.friends.common.party.Party;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class PartyMenu implements InventoryMenu {

    private static final String TITLE = "§cParties";
    private static final ItemStack NO_PARTIES = new ItemBuilder().setMaterial(Material.BARRIER).setDisplayName("§8» §cKeine öffentlichen Parties verfügbar").build();
    private static final ItemStack BLACK_GLASS = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData(15).setDisplayName(" ").build();
    private static final ItemStack GRAY_GLASS = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData(7).setDisplayName(" ").build();
    private static final ItemStack BACK = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setDisplayName("§8» §cZurück").setData(14).build();

    private SpigotFriends friends;

    public PartyMenu(SpigotFriends friends) {
        this.friends = friends;
        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    @Override
    public void show(Player player) {
        friends.getPartyManager().getAllParties(parties -> {
            int inventorySize = 9 * ((int) Math.ceil(Math.max(parties.size(), 1) / 9.0) + 1);
            if (inventorySize > 54) {
                inventorySize = 54;
            }

            Inventory inventory = Bukkit.createInventory(null, inventorySize, TITLE);

            for (int i = inventorySize - 9; i < inventorySize; i++) {
                inventory.setItem(i, BLACK_GLASS);
            }

            inventory.setItem(inventorySize - 9, GRAY_GLASS);
            inventory.setItem(inventorySize - 5, BACK);
            inventory.setItem(inventorySize - 1, GRAY_GLASS);

            AtomicInteger index = new AtomicInteger(0);
            AtomicInteger checkedParties = new AtomicInteger(0);

            if (parties.size() <= 0) {
                inventory.setItem(0, NO_PARTIES);
                Bukkit.getScheduler().runTask(friends, () -> {
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                    player.openInventory(inventory);
                });
                return;
            }

            for (Party party : parties) {
                party.isPrivate(isPrivate -> {

                    if (!isPrivate) {
                        ItemStack head = friends.getHeadCache().getHead(party.getOwner());
                        ItemMeta meta = head.getItemMeta();
                        meta.setDisplayName("§8» §5Party von " + party.getOwner());
                        List<String> lore = new ArrayList<>();
                        lore.add("§8× §9§oLinksklick zum joinen §8×");
                        lore.add("§7Mitglieder:");
                        for (int i = 0; i < party.getMembers().size(); i++) {
                            if (i > 9) {
                                int remaining = lore.size() - 1;

                                if (remaining != 0) {
                                    lore.add("§8" + remaining + " Mehr...");
                                }
                                break;
                            }

                            lore.add("§7" + party.getMembers().get(i));
                        }
                        meta.setLore(lore);
                        head.setItemMeta(meta);
                        inventory.setItem(index.getAndIncrement(), head);
                    }

                    if (checkedParties.incrementAndGet() >= parties.size()) {
                        if (index.get() == 0) {
                            inventory.setItem(0, NO_PARTIES);
                        }

                        Bukkit.getScheduler().runTask(friends, () -> {
                            player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                            player.openInventory(inventory);
                        });
                    }

                });
            }
        });
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
            if (stack.equals(BACK)) {
                friends.getMainMenu().show(player);
            } else if (stack.getType() == Material.SKULL_ITEM && stack.getDurability() == 3) {
                String owner = stack.getItemMeta().getDisplayName().replace("§8» §5Party von ", "");
                new RunCommandMessage(player.getName(), "party join " + owner).sendToProxy("Proxy");
                player.closeInventory();
            }
        }
    }
}
