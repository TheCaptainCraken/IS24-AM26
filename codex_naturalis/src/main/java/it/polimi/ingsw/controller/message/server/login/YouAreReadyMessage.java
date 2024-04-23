package it.polimi.ingsw.controller.message.server.login;

import it.polimi.ingsw.controller.ServerMessage;

public class YouAreReadyMessage extends ServerMessage {

    public YouAreReadyMessage(boolean isBroadcast) {
        super(false);
    }
}
