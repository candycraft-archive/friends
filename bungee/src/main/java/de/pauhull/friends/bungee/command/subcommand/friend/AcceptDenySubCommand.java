package de.pauhull.friends.bungee.command.subcommand.friend;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.bungee.command.subcommand.SubCommand;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class AcceptDenySubCommand extends SubCommand {

    private BungeeFriends friends;

    public AcceptDenySubCommand() {
        super("accept", "deny");
        this.friends = BungeeFriends.getInstance();
        this.setTabPermissions(Permissions.Friends.ACCEPT, Permissions.Friends.DENY);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args[0].equalsIgnoreCase("accept")) {
            if (!player.hasPermission(Permissions.Friends.ACCEPT)) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getNoPermissions()));
                return;
            }
        } else {
            if (!player.hasPermission(Permissions.Friends.DENY)) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getNoPermissions()));
                return;
            }
        }

        if (args.length < 2) {
            if (args[0].equalsIgnoreCase("accept")) {
                sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + "§c/friend accept <Spieler>"));
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + "§c/friend deny <Spieler>"));
            }
            return;
        }

        UUID to = player.getUniqueId();
        String fromName = args[1];

        if (fromName.equalsIgnoreCase("all")) {
            if (args[0].equalsIgnoreCase("accept")) {
                if (!player.hasPermission(Permissions.Friends.ACCEPT_ALL)) {
                    sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + "§c/friend deny <Spieler>"));
                    return;
                }
            } else {
                if (!player.hasPermission(Permissions.Friends.DENY_ALL)) {
                    sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + "§c/friend deny <Spieler>"));
                    return;
                }
            }

            friends.getFriendRequestTable().getOpenFriendRequests(player.getUniqueId(), requests -> {

                if (requests > 0) {
                    BiConsumer<Collection<ProxiedPlayer>, Integer> consumer = (players, requestAmount) -> {
                        for (ProxiedPlayer proxiedPlayer : players) {
                            if (proxiedPlayer == null)
                                continue;

                            if (args[0].equalsIgnoreCase("accept")) {
                                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getRequestAccepted(), player.getName())));
                            } else {
                                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getRequestDenied(), player.getName())));
                            }
                        }

                        if (args[0].equalsIgnoreCase("accept")) {
                            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getRequestsAccepted(), requestAmount)));
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getRequestsDenied(), requestAmount)));
                        }
                    };

                    if (args[0].equalsIgnoreCase("accept")) {
                        friends.getFriendRequestTable().acceptAll(friends.getFriendTable(), player.getUniqueId(), consumer);
                    } else {
                        friends.getFriendRequestTable().denyAll(player.getUniqueId(), consumer);
                    }
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getNoRequests()));
                }

            });

            return;
        }

        friends.getUuidFetcher().fetchUUIDAsync(fromName, from -> {

            ProxiedPlayer fromPlayer = ProxyServer.getInstance().getPlayer(from);

            if (from == null) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + friends.getMessages().getPlayerDoesntExist()));
                return;
            }

            friends.getFriendRequestTable().getTime(from, to, time -> {

                if (time == null) {
                    friends.getUuidFetcher().fetchNameAsync(from, name -> {
                        player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getNoRequest(), name)));
                    });
                } else {
                    if (args[0].equalsIgnoreCase("accept")) {
                        friends.getFriendRequestTable().acceptFriendRequest(friends.getFriendTable(), from, to);

                        friends.getUuidFetcher().fetchNameAsync(from, name -> {
                            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getYouAccepted(), name)));
                        });

                        if (fromPlayer != null) {
                            fromPlayer.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getRequestAccepted(), player.getName())));
                        }
                    } else {
                        friends.getFriendRequestTable().denyFriendRequest(from, to);

                        friends.getUuidFetcher().fetchNameAsync(from, name -> {
                            player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getYouDenied(), name)));
                        });

                        if (fromPlayer != null) {
                            fromPlayer.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getFriendsPrefix() + String.format(friends.getMessages().getRequestDenied(), player.getName())));
                        }
                    }
                }

            });

        });

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return ImmutableSet.of();
        }

        String search = args[1].toLowerCase();
        Set<String> matches = new HashSet<>();

        if (args[0].equalsIgnoreCase("accept")) {
            if (sender.hasPermission(Permissions.Friends.ACCEPT_ALL)) {
                matches.add("all");
            }
        } else {
            if (sender.hasPermission(Permissions.Friends.DENY_ALL)) {
                matches.add("all");
            }
        }

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {

            if (player.equals(sender))
                continue;

            if (!player.getName().toLowerCase().startsWith(search))
                continue;

            matches.add(player.getName());
        }

        return matches;
    }

}
