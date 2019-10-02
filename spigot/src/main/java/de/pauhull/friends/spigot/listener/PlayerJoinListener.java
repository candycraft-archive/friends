package de.pauhull.friends.spigot.listener;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import de.pauhull.friends.spigot.SpigotFriends;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private SpigotFriends friends;

    public PlayerJoinListener(SpigotFriends friends) {
        this.friends = friends;
        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (TimoCloudAPI.getBukkitAPI().getThisServer().getName().startsWith("Lobby")) {
            friends.getHeadCache().saveHead(player.getName());
        }
    }

}
