package de.pauhull.friends.bungee.command.subcommand.friend;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

public class JumpSubCommand extends SubCommand {

    private BungeeFriends friends;

    public JumpSubCommand() {
        super("jump");
        this.friends = BungeeFriends.getInstance();
        this.setTabPermissions(Permissions.Friends.JUMP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!sender.hasPermission(Permissions.Friends.JUMP)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + "§c/friend jump <Spieler>"));
            return;
        }

        String requestedPlayerName = args[1];

        friends.getUuidFetcher().fetchUUIDAsync(requestedPlayerName, requestedUUID -> {

            if (requestedUUID == null) {
                requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getPlayerDoesntExist()));
            } else {
                friends.getFriendTable().areFriends(requester.getUniqueId(), requestedUUID, areFriends -> {

                    if (areFriends) {
                        ProxiedPlayer requested = ProxyServer.getInstance().getPlayer(requestedUUID);

                        if (requested != null) {
                            friends.getSettingsTable().isJumping(requestedUUID, jumping -> {
                                if (jumping) {
                                    if (requester.getServer().getInfo().equals(requested.getServer().getInfo())) {
                                        requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getSameServer()));
                                    } else {
                                        requester.connect(requested.getServer().getInfo());
                                    }
                                } else {
                                    requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getNoJumping(), requested.getName())));
                                }
                            });
                        } else {
                            friends.getUuidFetcher().fetchNameAsync(requestedUUID, name -> {
                                requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getNotOnline(), name)));
                            });
                        }
                    } else {
                        friends.getUuidFetcher().fetchNameAsync(requestedUUID, name -> {
                            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getNoFriend(), name)));
                        });
                    }

                });
            }

        });

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        String search = args[1].toLowerCase();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.equals(sender)) {
                continue;
            }

            if (player.getName().toLowerCase().startsWith(search)) {
                matches.add(player.getName());
            }
        }

        return matches;
    }

}
