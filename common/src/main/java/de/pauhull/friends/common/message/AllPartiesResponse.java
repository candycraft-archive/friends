package de.pauhull.friends.common.message;

import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import de.pauhull.uuidfetcher.common.communication.message.CommunicationMessage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class AllPartiesResponse extends CommunicationMessage {

    public static final String TYPE = "ALL_PARTIES_RESPONSE";

    @Getter
    private String[] partyJsons;

    public AllPartiesResponse(String[] partyJsons) {
        super(TYPE);

        this.partyJsons = partyJsons;
        for (int i = 0; i < partyJsons.length; i++) {
            this.set(Integer.toString(i), partyJsons[i]);
        }
    }

    public AllPartiesResponse(PluginMessage pluginMessage) {
        this(readStrings(pluginMessage));
    }

    private static String[] readStrings(PluginMessage pluginMessage) {
        List<String> strings = new ArrayList<>();
        int i = 0;
        while (true) {
            String string = pluginMessage.getString(Integer.toString(i));

            if (string == null) {
                break;
            }

            strings.add(string);
            i++;
        }
        return strings.toArray(new String[0]);
    }

}
