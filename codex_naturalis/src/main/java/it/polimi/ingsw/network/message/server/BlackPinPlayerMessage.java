package it.polimi.ingsw.network.message.server;

import it.polimi.ingsw.network.message.ReferredServerMessage;

public class BlackPinPlayerMessage extends ReferredServerMessage {

    public BlackPinPlayerMessage(String nickname) {
        super(true, nickname);
    }
}
