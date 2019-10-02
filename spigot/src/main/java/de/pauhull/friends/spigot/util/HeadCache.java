package de.pauhull.friends.spigot.util;

import de.pauhull.friends.common.util.TimedHashMap;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.concurrent.TimeUnit;

public class HeadCache {

    private TimedHashMap<String, net.minecraft.server.v1_8_R3.ItemStack> heads = new TimedHashMap<>(TimeUnit.MINUTES, 30);

    public ItemStack getHead(String owner) {
        if (heads.containsKey(owner)) {
            return CraftItemStack.asBukkitCopy(heads.get(owner));
        } else {
            saveHead(owner);
            return CraftItemStack.asBukkitCopy(heads.get(owner));
        }
    }

    public void saveHead(String owner) {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(owner);
        stack.setItemMeta(meta);
        heads.put(owner, CraftItemStack.asNMSCopy(stack));
    }

}
