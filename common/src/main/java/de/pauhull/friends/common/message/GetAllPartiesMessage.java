package de.pauhull.friends.common.message;

import de.pauhull.uuidfetcher.common.communication.message.CommunicationMessage;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class GetAllPartiesMessage extends CommunicationMessage {

    public static final String TYPE = "GET_ALL_PARTIES";

    public GetAllPartiesMessage() {
        super(TYPE);
    }

}
