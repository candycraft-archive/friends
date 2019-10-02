package de.pauhull.friends.spigot.party;

import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.uuidfetcher.common.communication.message.ConnectMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class SpigotParty extends Party {

    private SpigotParty() {
        super("", new ArrayList<>(), "§7");
    }

    @Override
    public void broadcast(String message) {
        for (String member : members) {
            Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void isPrivate(Consumer<Boolean> consumer) {
        SpigotFriends.getInstance().getUuidFetcher().fetchUUIDAsync(owner, uuid -> {
            if (uuid == null) {
                consumer.accept(true);
                return;
            }

            SpigotFriends.getInstance().getSettingsTable().isPublic(uuid, isPublic -> {
                consumer.accept(!isPublic);
            });
        });
    }

    @Override
    public void delete() {
        for (String member : members) {
            Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.sendMessage(prefix + "Die Party wurde §caufgelöst§7.");
            }
        }

        super.delete();
    }

    @Override
    public String pickNewOwner() {
        if (super.pickNewOwner() != null) {
            Player player = Bukkit.getPlayer(owner);
            if (player != null) {
                broadcast(prefix + "§e" + player.getName() + " ist der neue §aOwner§7, da der alte Owner die Party §cverlassen §7hat!");
            }
        }

        return owner;
    }

    @Override
    public boolean setOwner(String player) {
        if (super.setOwner(player)) {
            broadcast(prefix + "§e" + player + "§7 ist der neue Party-Owner!");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean join(String server) {
        if (super.join(server)) {
            for (String member : members) {
                new ConnectMessage(member, server).sendToProxy("Proxy");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void kickMember(String player) {
        super.kickMember(player);

        Player bukkitPlayer = Bukkit.getPlayer(player);

        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(prefix + "Du wurdest aus der Party §cgekickt§7!");
        }
    }


}
