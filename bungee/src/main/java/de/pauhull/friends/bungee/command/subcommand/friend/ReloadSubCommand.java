package de.pauhull.friends.bungee.command.subcommand.friend;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class ReloadSubCommand extends SubCommand {

    private BungeeFriends friends;

    public ReloadSubCommand() {
        super("reload");
        this.friends = BungeeFriends.getInstance();
        this.setTabPermissions(Permissions.Friends.RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.Friends.RELOAD)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        friends.reload();

        sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + "Config reloaded"));

    }

}
