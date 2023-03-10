package de.pauhull.friends.bungee.command.subcommand.friend;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ToggleSubCommand extends SubCommand {

    private BungeeFriends friends;

    public ToggleSubCommand() {
        super("toggle", "togglemsg", "togglenotify", "togglejump");
        this.friends = BungeeFriends.getInstance();
        this.setTabPermissions(Permissions.Friends.TOGGLE, Permissions.Friends.TOGGLE, Permissions.Friends.TOGGLE, Permissions.Friends.TOGGLE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission(Permissions.Friends.TOGGLE)) {
            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args[0].equalsIgnoreCase("togglemsg")) {
            friends.getSettingsTable().isMessages(player.getUniqueId(), messages -> {
                friends.getSettingsTable().setMessages(player.getUniqueId(), !messages);
                if (!messages) {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getMessagesOn()));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getMessagesOff()));
                }
            });
        } else if (args[0].equalsIgnoreCase("togglenotify")) {
            friends.getSettingsTable().isNotifications(player.getUniqueId(), notifications -> {
                friends.getSettingsTable().setNotifications(player.getUniqueId(), !notifications);
                if (!notifications) {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getNotificationsOn()));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getNotificationsOff()));
                }
            });
        } else if (args[0].equalsIgnoreCase("togglejump")) {
            friends.getSettingsTable().isJumping(player.getUniqueId(), jumping -> {
                friends.getSettingsTable().setJumping(player.getUniqueId(), !jumping);
                if (!jumping) {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getJumpingOn()));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getJumpingOff()));
                }
            });
        } else if (args[0].equalsIgnoreCase("toggle")) {
            friends.getSettingsTable().isRequests(player.getUniqueId(), requests -> {
                friends.getSettingsTable().setRequests(player.getUniqueId(), !requests);
                if (!requests) {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getRequestsOn()));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getRequestsOff()));
                }
            });
        }

    }

}
