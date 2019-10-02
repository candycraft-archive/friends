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
 * on 02.12.2018
 *
 * @author pauhull
 */
public class JumpSubCommand extends SubCommand {

    private BungeeFriends friends;

    public JumpSubCommand() {
        super("jump");
        this.setTabPermissions(Permissions.Party.JUMP);
        this.friends = BungeeFriends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!requester.hasPermission(Permissions.Party.JUMP)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "§c/party jump"));
            return;
        }

        Party party = Party.getParty(requester.getName());
        if (party == null) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    "Du bist ist in §ckeiner Party§7!"));
            return;
        }

        if (party.isOwner(requester.getName())) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() +
                    "Du kannst dir §cnicht §7selbst nachjoinen!"));
            return;
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(party.getOwner());
        if (player != null) {
            if (requester.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getSameServer()));
                return;
            }

            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du bist dem Party-Owner §aerfolgreich §7nachgejoint!"));
            requester.connect(player.getServer().getInfo());
        }
    }

}
