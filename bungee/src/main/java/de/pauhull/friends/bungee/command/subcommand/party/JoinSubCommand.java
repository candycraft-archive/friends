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
 * on 01.12.2018
 *
 * @author pauhull
 */
public class JoinSubCommand extends SubCommand {

    private BungeeFriends friends;

    public JoinSubCommand() {
        super("join");
        this.setTabPermissions(Permissions.Party.JOIN);
        this.friends = BungeeFriends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!requester.hasPermission(Permissions.Party.JOIN)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "§c/party join <Spieler>"));
            return;
        }

        String requestedPlayerName = args[1];

        ProxiedPlayer requestedPlayer = ProxyServer.getInstance().getPlayer(requestedPlayerName);
        if (requestedPlayer == null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    String.format(friends.getMessages().getNotOnline(), requestedPlayerName)));
            return;
        }

        Party party = Party.getParty(requestedPlayer.getName());
        if (party == null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    "§e" + requestedPlayer.getName() + " §7ist in §ckeiner Party§7!"));
            return;
        }

        if (party.isMember(requester.getName())) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du bist bereits in dieser Party"));
            return;
        }

        party.isPrivate(privateParty -> {

            if (privateParty && !party.isInvited(requester.getName())) {
                requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                        "Du wurdest §cnicht §7in diese Party §7eingeladen!"));
                return;
            }

            if (!party.addMember(requester.getName())) {
                requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Diese Party ist §cvoll§7!"));
            } else {
                InviteSubCommand.getLastInvites().entrySet().removeIf(entry -> entry.getValue().equals(requester.getName()));
            }

        });
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return ImmutableSet.of();
        }

        String search = args[1].toLowerCase();
        Set<String> matches = new HashSet<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {

            if (player.equals(sender))
                continue;

            if (!player.getName().toLowerCase().startsWith(search))
                continue;

            if (Party.getParty(player.getName()) == null)
                continue;

            matches.add(player.getName());
        }

        return matches;
    }

}
