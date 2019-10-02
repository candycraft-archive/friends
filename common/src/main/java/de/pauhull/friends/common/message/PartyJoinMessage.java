package de.pauhull.friends.common.message;

import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import de.pauhull.uuidfetcher.common.communication.message.CommunicationMessage;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
@ToString
public class PartyJoinMessage extends CommunicationMessage {

    public static final String TYPE = "PARTY_JOIN";

    @Getter
    private String playerName, party;

    public PartyJoinMessage(String playerName, String party) {
        super(TYPE);

        this.playerName = playerName;
        this.party = party;
        this.set("playerName", playerName);
        this.set("party", party);
    }

    public PartyJoinMessage(PluginMessage pluginMessage) {
        this(pluginMessage.getString("playerName"), pluginMessage.getString("party"));
    }

}
