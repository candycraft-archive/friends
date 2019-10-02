package de.pauhull.friends.bungee.command.subcommand.party;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
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
public class ToggleSubCommand extends SubCommand {

    private BungeeFriends friends;

    public ToggleSubCommand() {
        super("toggleinvites", "togglepublic");
        this.setTabPermissions(Permissions.Party.TOGGLE_INVITES, Permissions.Party.PREMIUM);
        this.friends = BungeeFriends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args[0].equalsIgnoreCase("togglepublic")) {
            if (!player.hasPermission(Permissions.Party.PREMIUM)) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
                return;
            }

            friends.getSettingsTable().isPublic(player.getUniqueId(), publicParty -> {
                friends.getSettingsTable().setPublic(player.getUniqueId(), !publicParty);

                if (!publicParty) {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Deine Party ist nun §aöffentlich§7."));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Deine Party ist nun §cprivat§7."));
                }
            });
        } else if (args[0].equalsIgnoreCase("toggleinvites")) {
            if (!player.hasPermission(Permissions.Party.TOGGLE_INVITES)) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + friends.getMessages().getNoPermissions()));
                return;
            }

            friends.getSettingsTable().isInvites(player.getUniqueId(), invites -> {
                friends.getSettingsTable().setInvites(player.getUniqueId(), !invites);

                if (!invites) {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du erhältst nun wieder §aParty-Einladungen§7."));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du erhältst nun §ckeine §7Party-Einladungen mehr."));
                }
            });
        }
    }

}
