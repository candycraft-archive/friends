package de.pauhull.friends.spigot.util;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.messages.listeners.MessageListener;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import de.pauhull.friends.common.message.*;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.friends.spigot.event.PartyUpdateEvent;
import de.pauhull.friends.spigot.event.PlayerJoinPartyEvent;
import de.pauhull.friends.spigot.event.PlayerLeavePartyEvent;
import de.pauhull.friends.spigot.party.SpigotParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class PartyManager implements MessageListener {

    private SpigotFriends friends;

    private List<Consumer<List<Party>>> consumers;

    public PartyManager(SpigotFriends friends) {
        this.friends = friends;
        this.consumers = new ArrayList<>();
        TimoCloudAPI.getMessageAPI().registerMessageListener(this);
    }

    public void getAllParties(Consumer<List<Party>> consumer) {
        new GetAllPartiesMessage().sendToProxy("Proxy");

        consumers.add(consumer);
    }

    @Override
    public void onPluginMessage(AddressedPluginMessage addressedPluginMessage) {

        PluginMessage pluginMessage = addressedPluginMessage.getMessage();

        if (pluginMessage.getType().equals(AllPartiesResponse.TYPE)) {
            AllPartiesResponse response = new AllPartiesResponse(pluginMessage);

            List<Party> parties = new ArrayList<>();
            for (String partyJson : response.getPartyJsons()) {
                Party party = Party.fromJson(partyJson, SpigotParty.class);
                parties.add(party);
            }

            Iterator<Consumer<List<Party>>> iterator = consumers.iterator();
            while (iterator.hasNext()) {
                Consumer<List<Party>> consumer = iterator.next();
                consumer.accept(new ArrayList<>(parties));
            }

            consumers.clear();
        } else if (pluginMessage.getType().equals(PartyJoinMessage.TYPE)) {
            PartyJoinMessage message = new PartyJoinMessage(pluginMessage);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getDisplayName().equals(message.getPlayerName())) {
                    PlayerJoinPartyEvent event = new PlayerJoinPartyEvent(player, SpigotParty.fromJson(message.getParty(), SpigotParty.class));
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        } else if (pluginMessage.getType().equals(PartyLeaveMessage.TYPE)) {
            PartyLeaveMessage message = new PartyLeaveMessage(pluginMessage);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getDisplayName().equals(message.getPlayerName())) {
                    PlayerLeavePartyEvent event = new PlayerLeavePartyEvent(player, SpigotParty.fromJson(message.getParty(), SpigotParty.class));
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        } else if (pluginMessage.getType().equals(PartySetOwnerMessage.TYPE)) {
            PartySetOwnerMessage message = new PartySetOwnerMessage(pluginMessage);
            PartyUpdateEvent event = new PartyUpdateEvent(SpigotParty.fromJson(message.getParty(), SpigotParty.class),
                    new PartyUpdateEvent.ActionOwnerSet(message.getOldOwner(), message.getNewOwner()));
            Bukkit.getPluginManager().callEvent(event);
        }
    }
}
