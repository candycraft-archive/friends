package de.pauhull.friends.bungee.command.subcommand.party;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Paul
 * on 01.12.2018
 *
 * @author pauhull
 */
public class ListSubCommand extends SubCommand {

    private BungeeFriends friends;

    public ListSubCommand() {
        super("list");
        this.setTabPermissions(Permissions.Party.LIST);
        this.friends = BungeeFriends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!requester.hasPermission(Permissions.Party.LIST)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "§c/party list"));
            return;
        }

        Party party = Party.getParty(requester.getName());
        if (party == null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    "Du bist in §ckeiner Party§7!"));
            return;
        }

        sender.sendMessage(TextComponent.fromLegacyText("§8§m                                 "));
        for (String member : party.getMembers()) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(member);
            if (player != null) {
                sender.sendMessage(TextComponent.fromLegacyText("§8● §7" + member + " §8(§e" + player.getServer().getInfo().getName() + "§8)" + (party.isOwner(player.getName()) ? " §8[§a§lOWNER§8]" : "")));
            }
        }
        sender.sendMessage(TextComponent.fromLegacyText("§8§m                                 "));
    }

}
