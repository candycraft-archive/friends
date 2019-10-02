package de.pauhull.friends.bungee.command.subcommand.party;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class SetOwnerSubCommand extends SubCommand {

    private BungeeFriends friends;

    public SetOwnerSubCommand() {
        super("setowner");
        this.setTabPermissions(Permissions.Party.SETOWNER);
        this.friends = BungeeFriends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!requester.hasPermission(Permissions.Party.SETOWNER)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "§c/party setowner <Spieler>"));
            return;
        }

        Party party = Party.getParty(requester.getName());
        if (party == null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    "Du bist ist in §ckeiner Party§7!"));
            return;
        }

        String requestedPlayerName = args[1];

        if (requester.getName().equalsIgnoreCase(requestedPlayerName)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du bist bereits der §cParty-Owner§7!"));
            return;
        }

        ProxiedPlayer requestedPlayer = ProxyServer.getInstance().getPlayer(requestedPlayerName);
        if (requestedPlayer == null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    String.format(friends.getMessages().getNotOnline(), requestedPlayerName)));
            return;
        }

        if (!party.isOwner(requester.getName())) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    "Die Party muss dir gehören, damit du den neuen Party-Owner setzen kannst!"));
            return;
        }

        if (!party.isMember(requestedPlayer.getName())) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    "§e" + requestedPlayer.getName() + "§7 ist §cnicht §7in deiner Party!"));
            return;
        }

        party.setOwner(requestedPlayer.getName());
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return ImmutableSet.of();
        }

        Party party = Party.getParty(sender.getName());
        if (party == null)
            return ImmutableSet.of();

        if (!party.isOwner(sender.getName()))
            return ImmutableSet.of();

        String search = args[1].toLowerCase();
        Set<String> matches = new HashSet<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {

            if (player.equals(sender))
                continue;

            if (!player.getName().toLowerCase().startsWith(search))
                continue;

            if (!party.isMember(player.getName()))
                continue;

            matches.add(player.getName());
        }

        return matches;
    }

}
