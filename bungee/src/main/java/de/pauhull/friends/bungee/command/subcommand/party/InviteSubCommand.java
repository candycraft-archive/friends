package de.pauhull.friends.bungee.command.subcommand.party;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.bungee.party.BungeeParty;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.common.util.Permissions;
import de.pauhull.friends.common.util.TimedHashMap;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Paul
 * on 24.11.2018
 *
 * @author pauhull
 */
public class InviteSubCommand extends SubCommand {

    @Getter
    private static TimedHashMap<String, String> lastInvites = new TimedHashMap<>(TimeUnit.SECONDS, 90);

    private BungeeFriends friends;

    public InviteSubCommand() {
        super("invite");
        this.setTabPermissions(Permissions.Party.INVITE);
        this.friends = BungeeFriends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!requester.hasPermission(Permissions.Party.INVITE)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "§c/party join <Spieler>"));
            return;
        }

        String requestedPlayerName = args[1];

        if (requester.getName().equalsIgnoreCase(requestedPlayerName)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getPartyNotSelf()));
            return;
        }

        ProxiedPlayer requestedPlayer = ProxyServer.getInstance().getPlayer(requestedPlayerName);
        if (requestedPlayer == null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    String.format(friends.getMessages().getNotOnline(), requestedPlayerName)));
            return;
        }

        if (Party.getParty(requestedPlayer.getName()) != null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    String.format(friends.getMessages().getAlreadyInParty(), requestedPlayer.getName())));
            return;
        }

        friends.getSettingsTable().isInvites(requestedPlayer.getUniqueId(), invites -> {

            if (!invites) {
                requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "§e" + requestedPlayer.getName() + "§7 empfängt §ckeine §7Party-Einladungen!"));
                return;
            }

            Party party = Party.getParty(requester.getName());
            if (party == null) {
                if (requester.hasPermission(Permissions.Party.CREATE)) {
                    requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getPartyCreated2()));
                    party = BungeeParty.createParty(requester.getName());
                } else {
                    requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du kannst §ckeine §7Party erstellen!"));
                    return;
                }
            } else {
                if (!party.isOwner(requester.getName())) {
                    requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Nur der §cOwner §7kann Einladungen verschicken!"));
                    return;
                } else if (lastInvites.containsKey(requester.getName()) && lastInvites.get(requester.getName()).equals(requestedPlayer.getName())) {
                    requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du hast §e" + requestedPlayer.getName() + "§7 bereits eingeladen!"));
                    return;
                }
            }

            lastInvites.put(requester.getName(), requestedPlayer.getName());
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du hast §e" + requestedPlayer.getName() + "§a erfolgreich §7in deine Party §7eingeladen!"));
            party.invite(requestedPlayer.getName());
            requestedPlayer.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du wurdest von §e" + requester.getName() + "§7 in seine Party§7 eingeladen!"));
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Command: /party join " + requester.getName()));
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + requester.getName());
            requestedPlayer.sendMessage(new ComponentBuilder(BungeeFriends.getPartyPrefix()).append("§8[§a§lANFRAGE ANNEHMEN§8]").event(hoverEvent).event(clickEvent).create());

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

            if (Party.getParty(player.getName()) != null)
                continue;

            matches.add(player.getName());
        }

        return matches;
    }

}
