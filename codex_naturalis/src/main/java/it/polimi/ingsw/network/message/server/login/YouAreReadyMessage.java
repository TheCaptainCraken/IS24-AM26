package it.polimi.ingsw.network.message.server.login;

import it.polimi.ingsw.network.message.ServerMessage;

public class YouAreReadyMessage extends ServerMessage {

    public YouAreReadyMessage(boolean isBroadcast) {
        super(false);
    }
}
