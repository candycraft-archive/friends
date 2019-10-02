package de.pauhull.friends.bungee.util;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.messages.listeners.MessageListener;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.common.message.AllPartiesResponse;
import de.pauhull.friends.common.message.GetAllPartiesMessage;
import de.pauhull.friends.common.party.Party;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class BungeeMessageListener implements MessageListener {

    private BungeeFriends friends;

    public BungeeMessageListener(BungeeFriends friends) {
        this.friends = friends;

        TimoCloudAPI.getMessageAPI().registerMessageListener(this);
    }

    @Override
    public void onPluginMessage(AddressedPluginMessage addressedPluginMessage) {
        PluginMessage pluginMessage = addressedPluginMessage.getMessage();

        if (pluginMessage.getType().equals(GetAllPartiesMessage.TYPE)) {
            List<String> allJsons = new ArrayList<>();
            for (Party party : Party.getAllParties()) {
                allJsons.add(party.toJson());
            }
            new AllPartiesResponse(allJsons.toArray(new String[0])).sendToServer(addressedPluginMessage.getSender().getName());
        }
    }

}
