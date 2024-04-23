package it.polimi.ingsw.server.message.server;

import it.polimi.ingsw.server.message.ReferredServerMessage;

public class BlackPinPlayerMessage extends ReferredServerMessage {

    public BlackPinPlayerMessage(String nickname) {
        super(true, nickname);
    }
}
