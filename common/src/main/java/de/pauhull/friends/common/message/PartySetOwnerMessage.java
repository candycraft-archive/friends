package de.pauhull.friends.common.message;

import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import de.pauhull.uuidfetcher.common.communication.message.CommunicationMessage;
import lombok.Getter;

/**
 * Created by Paul
 * on 08.01.2019
 *
 * @author pauhull
 */
public class PartySetOwnerMessage extends CommunicationMessage {

    public static final String TYPE = "PARTY_SET_OWNER";

    @Getter
    private String oldOwner, newOwner, party;

    public PartySetOwnerMessage(String oldOwner, String newOwner, String party) {
        super(TYPE);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
        this.party = party;
        this.set("oldOwner", oldOwner);
        this.set("newOwner", newOwner);
        this.set("party", party);
    }

    public PartySetOwnerMessage(PluginMessage pluginMessage) {
        this(pluginMessage.getString("oldOwner"), pluginMessage.getString("newOwner"), pluginMessage.getString("party"));
    }

}
