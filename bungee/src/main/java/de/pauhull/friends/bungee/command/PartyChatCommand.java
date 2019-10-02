package de.pauhull.friends.bungee.command;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class PartyChatCommand extends Command {

    private BungeeFriends friends;

    public PartyChatCommand(BungeeFriends friends) {
        super("partychat", null, "pc");
        this.friends = friends;
        ProxyServer.getInstance().getPluginManager().registerCommand(friends, this);
    }

    public static void register() {
        new PartyChatCommand(BungeeFriends.getInstance());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission(Permissions.Party.PARTYCHAT)) {
            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        Party party = Party.getParty(player.getName());
        if (party == null) {
            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du bist in §ckeiner §7Party!"));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "§c/pc <Nachricht...>"));
            return;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                message.append(" ");
            }

            message.append(args[i]);
        }

        BaseComponent[] messageComponent = TextComponent.fromLegacyText("§8[§5§lPARTY§8] §7" + player.getName() + " §8» §f" + message.toString());
        for (String member : party.getMembers()) {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(member);

            if (proxiedPlayer != null) {
                proxiedPlayer.sendMessage(messageComponent);
            }
        }
    }

}
