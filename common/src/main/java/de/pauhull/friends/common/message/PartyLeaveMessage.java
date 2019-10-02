package de.pauhull.friends.common.message;

import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import de.pauhull.uuidfetcher.common.communication.message.CommunicationMessage;
import lombok.Getter;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class PartyLeaveMessage extends CommunicationMessage {

    public static final String TYPE = "PARTY_LEAVE";

    @Getter
    private String playerName, party;

    public PartyLeaveMessage(String playerName, String party) {
        super(TYPE);

        this.playerName = playerName;
        this.party = party;
        this.set("playerName", playerName);
        this.set("party", party);
    }

    public PartyLeaveMessage(PluginMessage pluginMessage) {
        this(pluginMessage.getString("playerName"), pluginMessage.getString("party"));
    }

}
