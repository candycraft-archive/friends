package de.pauhull.friends.bungee.party;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.common.message.PartyJoinMessage;
import de.pauhull.friends.common.message.PartyLeaveMessage;
import de.pauhull.friends.common.message.PartySetOwnerMessage;
import de.pauhull.friends.common.party.Party;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class BungeeParty extends Party {

    protected BungeeParty(String owner, List<String> members) {
        super(owner, members, BungeeFriends.getPartyPrefix());
        Party.getAllParties().add(this);
    }

    public static Party createParty(String owner) {
        if (getParty(owner) != null) {
            return getParty(owner);
        }

        List<String> members = new ArrayList<>();
        members.add(owner);
        BungeeParty party = new BungeeParty(owner, members);

        PartyJoinMessage message = new PartyJoinMessage(owner, party.toJson());
        for (ServerGroupObject group : TimoCloudAPI.getUniversalAPI().getServerGroups()) {
            for (ServerObject server : group.getServers()) {
                message.sendToServer(server.getName());
            }
        }

        return party;
    }

    @Override
    public boolean addMember(String player) {
        boolean success = super.addMember(player);

        if (success) {
            PartyJoinMessage message = new PartyJoinMessage(player, toJson());
            for (ServerGroupObject group : TimoCloudAPI.getUniversalAPI().getServerGroups()) {
                for (ServerObject server : group.getServers()) {
                    message.sendToServer(server.getName());
                }
            }
        }

        return success;
    }

    @Override
    public void broadcast(String message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (members.contains(player.getName())) {
                player.sendMessage(TextComponent.fromLegacyText(message));
            }
        }
    }

    @Override
    public void delete() {
        for (String member : members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(member);

            if (player != null) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Die Party wurde §caufgelöst§7."));
            }
        }

        super.delete();
    }

    @Override
    public String pickNewOwner() {
        if (super.pickNewOwner() != null) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(owner);
            if (player != null) {
                broadcast(BungeeFriends.getPartyPrefix() + "§e" + player.getName() + "§7 ist der neue §aOwner§7, da der alte Owner die Party §cverlassen §7hat!");
            }
        }

        return owner;
    }

    @Override
    public boolean setOwner(String player) {
        PartySetOwnerMessage message = new PartySetOwnerMessage(owner, player, toJson());
        for (ServerGroupObject group : TimoCloudAPI.getUniversalAPI().getServerGroups()) {
            for (ServerObject server : group.getServers()) {
                message.sendToServer(server.getName());
            }
        }
        if (super.setOwner(player)) {
            broadcast(BungeeFriends.getPartyPrefix() + "§e" + player + "§7 ist der neue Party-Owner!");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean join(String server) {
        if (super.join(server)) {

            ServerInfo info = ProxyServer.getInstance().getServerInfo(server);

            if (info != null) {
                for (String member : members) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(member);

                    if (player == null)
                        continue;

                    player.connect(info);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removeMember(String player) {
        super.removeMember(player);

        PartyLeaveMessage message = new PartyLeaveMessage(player, toJson());
        for (ServerGroupObject group : TimoCloudAPI.getUniversalAPI().getServerGroups()) {
            for (ServerObject server : group.getServers()) {
                message.sendToServer(server.getName());
            }
        }
    }

    @Override
    public void kickMember(String player) {
        removeMember(player);

        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);

        if (proxiedPlayer != null) {
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPartyPrefix() + "Du wurdest aus der Party §cgekickt§7!"));
        }
    }

    @Override
    public void isPrivate(Consumer<Boolean> consumer) {
        BungeeFriends.getInstance().getUuidFetcher().fetchUUIDAsync(owner, uuid -> {
            if (uuid == null) {
                consumer.accept(true);
                return;
            }

            BungeeFriends.getInstance().getSettingsTable().isPublic(uuid, isPublic -> {
                consumer.accept(!isPublic);
            });
        });
    }

}
