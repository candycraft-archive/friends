package de.pauhull.friends.bungee.listener;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.common.party.Party;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class ServerSwitchListener implements Listener {

    private BungeeFriends friends;

    public ServerSwitchListener(BungeeFriends friends) {
        this.friends = friends;

        ProxyServer.getInstance().getPluginManager().registerListener(friends, this);
    }

    public static void register() {
        new ServerSwitchListener(BungeeFriends.getInstance());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Party party = Party.getParty(player.getName());

        if (party != null && party.isOwner(player.getName())) {
            party.join(player.getServer().getInfo().getName());
        }
    }

}
