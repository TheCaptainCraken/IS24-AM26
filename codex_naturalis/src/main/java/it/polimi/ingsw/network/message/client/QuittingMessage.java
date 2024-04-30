package it.polimi.ingsw.network.message.client;

import it.polimi.ingsw.network.message.ClientMessage;

public class QuittingMessage extends ClientMessage {

    public QuittingMessage(String nickname) {
        super(nickname);
    }
}
