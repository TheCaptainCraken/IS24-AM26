package it.polimi.ingsw.server.message.client;

import it.polimi.ingsw.server.message.ClientMessage;

public class QuittingMessage extends ClientMessage {

    public QuittingMessage(String nickname) {
        super(nickname);
    }
}
