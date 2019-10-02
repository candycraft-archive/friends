package de.pauhull.friends.bungee.listener;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.common.party.Party;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    private BungeeFriends friends;

    public PlayerDisconnectListener(BungeeFriends friends) {
        this.friends = friends;
        friends.getProxy().getPluginManager().registerListener(friends, this);
    }

    public static void register() {
        new PlayerDisconnectListener(BungeeFriends.getInstance());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        Party party = Party.getParty(player.getName());
        if (party != null) {
            party.removeMember(player.getName());
        }

        friends.getLastOnlineTable().setLastOnline(player.getUniqueId());

        friends.getSettingsTable().isNotifications(player.getUniqueId(), sendsNotifications -> {
            if (sendsNotifications) {
                friends.getFriendTable().getFriends(player.getUniqueId(), players -> {

                    for (ProxiedPlayer friend : players) {
                        friends.getSettingsTable().isNotifications(friend.getUniqueId(), receivesNotifications -> {
                            if (receivesNotifications) {
                                friend.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getNowOffline(), player.getName())));
                            }
                        });
                    }

                });
            }
        });

    }

}
