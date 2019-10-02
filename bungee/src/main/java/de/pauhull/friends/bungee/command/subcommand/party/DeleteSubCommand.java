package de.pauhull.friends.bungee.command.subcommand.party;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Paul
 * on 01.12.2018
 *
 * @author pauhull
 */
public class DeleteSubCommand extends SubCommand {

    private BungeeFriends friends;

    public DeleteSubCommand() {
        super("delete");
        this.setTabPermissions(Permissions.Party.DELETE);
        this.friends = BungeeFriends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!requester.hasPermission(Permissions.Party.DELETE)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "§c/party delete"));
            return;
        }

        Party party = Party.getParty(requester.getName());
        if (party == null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    "Du bist in §ckeiner Party§7!"));
            return;
        }

        if (!party.isOwner(requester.getName())) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du musst der Party-Owner sein, um die Party §clöschen §7zu können!"));
            return;
        }

        party.delete();
    }

}
