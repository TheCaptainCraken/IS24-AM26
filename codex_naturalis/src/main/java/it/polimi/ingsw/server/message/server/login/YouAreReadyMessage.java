package it.polimi.ingsw.server.message.server.login;

import it.polimi.ingsw.server.message.ServerMessage;

public class YouAreReadyMessage extends ServerMessage {

    public YouAreReadyMessage(boolean isBroadcast) {
        super(false);
    }
}
