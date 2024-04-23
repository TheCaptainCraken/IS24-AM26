package it.polimi.ingsw.controller.message.server;

import it.polimi.ingsw.controller.ReferredServerMessage;

public class BlackPinPlayerMessage extends ReferredServerMessage {

    public BlackPinPlayerMessage(String nickname) {
        super(true, nickname);
    }
}
