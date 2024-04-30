package it.polimi.ingsw.network.message.server.login;

import it.polimi.ingsw.network.message.ServerMessage;

public class FirstPlayerMessage extends ServerMessage {

    public FirstPlayerMessage(boolean isBroadcast) {
        super(false);
    }
}
