package de.pauhull.friends.bungee.command;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.bungee.command.subcommand.party.*;
import de.pauhull.friends.common.util.Permissions;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Paul
 * on 24.11.2018
 *
 * @author pauhull
 */
public class PartyCommand extends Command implements TabExecutor {

    @Getter
    private static List<SubCommand> subCommands = new ArrayList<>();

    static {
        subCommands.add(new CreateSubCommand());
        subCommands.add(new DeleteSubCommand());
        subCommands.add(new InviteSubCommand());
        subCommands.add(new JoinSubCommand());
        subCommands.add(new JumpSubCommand());
        subCommands.add(new KickSubCommand());
        subCommands.add(new LeaveSubCommand());
        subCommands.add(new ListSubCommand());
        subCommands.add(new SetOwnerSubCommand());
        subCommands.add(new ToggleSubCommand());
    }

    public PartyCommand(BungeeFriends friends) {
        super("party");
        friends.getProxy().getPluginManager().registerCommand(friends, this);
    }

    public static void register() {
        new PartyCommand(BungeeFriends.getInstance());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length > 0) {
            for (SubCommand command : subCommands) {
                for (String name : command.getNames()) {
                    if (name.equalsIgnoreCase(args[0])) {
                        command.execute(sender, args);
                        return;
                    }
                }
            }
        }

        if (sender.hasPermission(Permissions.Party.PARTYCHAT))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/pc <Nachricht...> §8×§7 Partychat benutzen"));
        if (sender.hasPermission(Permissions.Party.CREATE))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party create §8×§7 Party erstellen"));
        if (sender.hasPermission(Permissions.Party.DELETE))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party delete §8×§7 Party löschen"));
        if (sender.hasPermission(Permissions.Party.INVITE))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party invite <Spieler> §8×§7 Spieler zu Party einladen"));
        if (sender.hasPermission(Permissions.Party.JOIN))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party join <Spieler> §8×§7 Party joinen"));
        if (sender.hasPermission(Permissions.Party.JUMP))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party jump §8×§7 Party-Owner nachjoinen"));
        if (sender.hasPermission(Permissions.Party.KICK))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party kick <Spieler> §8×§7 Spieler aus Party kicken"));
        if (sender.hasPermission(Permissions.Party.LEAVE))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party leave §8×§7 Party verlassen"));
        if (sender.hasPermission(Permissions.Party.LIST))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party list §8×§7 Alle Member der Party anzeigen"));
        if (sender.hasPermission(Permissions.Party.SETOWNER))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party setowner <Spieler> §8×§7 Party-Owner setzen"));
        if (sender.hasPermission(Permissions.Party.TOGGLE_INVITES))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party toggleinvites §8×§7 Party-Einladungen an/aus stellen"));
        if (sender.hasPermission(Permissions.Party.PREMIUM))
            sender.sendMessage(TextComponent.fromLegacyText("§8» §e/party togglepublic §8×§7 Öffentliche Party an/aus stellen"));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 0) {
            for (SubCommand command : subCommands) {
                for (int i = 0; i < command.getNames().length; i++) {
                    String name = command.getNames()[i];

                    if (name.equalsIgnoreCase(args[0])) {
                        if (i < command.getTabPermissions().length && command.getTabPermissions()[i] != null) {
                            if (!sender.hasPermission(command.getTabPermissions()[i])) {
                                return ImmutableSet.of();
                            }
                        }

                        if (sender.hasPermission(command.getTabPermissions()[i])) {
                            return command.onTabComplete(sender, args);
                        }
                    }
                }
            }
        }

        Set<String> matches = new HashSet<>();
        String search = args[0].toLowerCase();
        if (args.length == 1) {
            for (SubCommand command : subCommands) {
                for (int i = 0; i < command.getNames().length; i++) {
                    String name = command.getNames()[i];

                    if (name.toLowerCase().startsWith(search)) {
                        if (i < command.getTabPermissions().length && command.getTabPermissions()[i] != null) {
                            if (!sender.hasPermission(command.getTabPermissions()[i])) {
                                continue;
                            }
                        }

                        matches.add(name);
                    }
                }
            }
        }

        return matches;
    }

}
