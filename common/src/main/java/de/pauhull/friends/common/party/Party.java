package de.pauhull.friends.common.party;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 24.11.2018
 *
 * @author pauhull
 */
public abstract class Party {

    private static final transient int DEFAULT_MAX_MEMBERS = 20;
    @Getter
    protected static transient List<Party> allParties = new ArrayList<>();
    private static transient Random random = new Random();
    @Getter
    protected String owner;

    @Getter
    protected List<String> members;

    @Getter
    protected List<String> invited;

    @Getter
    protected String prefix;

    protected Party(String owner, List<String> members, String prefix) {
        this.owner = owner;
        this.invited = new ArrayList<>();
        this.members = members;
        this.prefix = prefix;
    }

    public static Party getParty(String player) {
        for (Party party : allParties) {
            if (party.isMember(player)) {
                return party;
            }
        }

        return null;
    }

    public static Party fromJson(String json, Class<? extends Party> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public abstract void broadcast(String message);

    public boolean isInvited(String player) {
        return invited.contains(player);
    }

    public void delete() {
        members.clear();
        allParties.remove(this);
    }

    public boolean isOwner(String player) {
        return owner.equals(player);
    }

    public void removeMember(String player) {
        broadcast(prefix + "§e" + player + "§7 hat die Party §cverlassen§7!");
        members.remove(player);

        if (isOwner(player))
            pickNewOwner();

        if (members.size() == 0 || owner == null) {
            delete();
        }
    }

    public String pickNewOwner() {
        if (members.size() == 0) {
            return null;
        } else {
            this.owner = members.get(random.nextInt(members.size()));
            return owner;
        }
    }

    public boolean setOwner(String player) {

        if (!isMember(player) || isOwner(player))
            return false;

        this.owner = player;

        return true;
    }

    public boolean join(String server) {
        if (server.startsWith("Lobby-")) {
            return false;
        }

        broadcast(prefix + "Die Party joint §e" + server + "§7!");

        return true;
    }

    public void kickMember(String player) {
        removeMember(player);
    }

    public boolean addMember(String player) {
        if (members.size() < DEFAULT_MAX_MEMBERS) {
            members.add(player);
            broadcast(prefix + "§e" + player + "§7 ist der Party §abeigetreten§7!");
            invited.remove(player);
            return true;
        } else {
            return false;
        }
    }

    public void invite(String player) {
        if (!invited.contains(player)) {
            invited.add(player);
        }
    }

    public boolean isMember(String player) {
        return members.contains(player);
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public abstract void isPrivate(Consumer<Boolean> consumer);

}
