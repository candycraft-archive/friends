package de.pauhull.friends.bungee.command.subcommand.party;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.bungee.party.BungeeParty;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class CreateSubCommand extends SubCommand {

    private BungeeFriends friends;

    public CreateSubCommand() {
        super("create");
        this.setTabPermissions(Permissions.Party.CREATE);
        this.friends = BungeeFriends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!requester.hasPermission(Permissions.Party.CREATE)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "§c/party create"));
            return;
        }

        Party party = Party.getParty(requester.getName());
        if (party != null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    "Du bist bereits in einer §cParty§7!"));
            return;
        }

        requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du hast §aerfolgreich §7eine Party erstellt!"));
        BungeeParty.createParty(requester.getName());
    }

}
