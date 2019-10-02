package de.pauhull.friends.bungee.command.subcommand.friend;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StatusSubCommand extends SubCommand {

    private BungeeFriends friends;

    public StatusSubCommand() {
        super("status");
        this.friends = BungeeFriends.getInstance();
        this.setTabPermissions(Permissions.Friends.STATUS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        //fix empty status
        //status filter

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission(Permissions.Friends.STATUS)) {
            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 2) {
            friends.getSettingsTable().getStatus(player.getUniqueId(), status -> {
                player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getYourStatus(), status)));
            });
            return;
        }

        StringBuilder statusBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) {
                statusBuilder.append(" ");
            }
            statusBuilder.append(args[i]);
        }
        String status = ChatColor.translateAlternateColorCodes('&', statusBuilder.toString());

        if (ChatColor.stripColor(status).length() > 20) {
            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getStatusTooLong()));
            return;
        }

        if (status.contains("\\")) {
            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getUnallowedCharacters()));
            return;
        }

        friends.getSettingsTable().setStatus(player.getUniqueId(), status);
        player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getStatusChanged(), status)));

    }

}
