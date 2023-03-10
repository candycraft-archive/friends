package de.pauhull.friends.bungee.listener;

import de.pauhull.friends.bungee.BungeeFriends;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {

    private BungeeFriends friends;

    public PostLoginListener(BungeeFriends friends) {
        this.friends = friends;
        friends.getProxy().getPluginManager().registerListener(friends, this);
    }

    public static void register() {
        new PostLoginListener(BungeeFriends.getInstance());
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        friends.getLastOnlineTable().setLastOnline(player.getUniqueId());

        friends.getSettingsTable().exists(player.getUniqueId(), exists -> {
            if (!exists) {
                friends.getSettingsTable().createRow(player.getUniqueId());
            }
        });

        friends.getFriendRequestTable().getOpenFriendRequests(player.getUniqueId(), requests -> {

            if (requests != 0) {

                HoverEvent denyHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§cAlle Freundschaftsanfragen ablehnen"));
                ClickEvent denyClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny all");
                HoverEvent acceptHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aAlle Freundschaftsanfragen annehmen"));
                ClickEvent acceptClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept all");

                BaseComponent[] message = new ComponentBuilder(BungeeFriends.getFriendsPrefix()).append("§8[").append("Alle annehmen").event(acceptHover).event(acceptClick)
                        .color(ChatColor.GREEN).bold(true).append("§8/").append("Alle ablehnen").event(denyHover).event(denyClick).color(ChatColor.RED).bold(true)
                        .append("§8]").create();

                player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getOpenRequests(), requests)));
                player.sendMessage(message);

            }

        });

        friends.getSettingsTable().isNotifications(player.getUniqueId(), sendsNotifications -> {
            if (sendsNotifications) {
                friends.getFriendTable().getFriends(player.getUniqueId(), players -> {

                    for (ProxiedPlayer friend : players) {
                        friends.getSettingsTable().isNotifications(friend.getUniqueId(), receivesNotifications -> {
                            if (receivesNotifications) {
                                friend.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getNowOnline(), player.getName())));
                            }
                        });
                    }

                });
            }
        });
    }

}
