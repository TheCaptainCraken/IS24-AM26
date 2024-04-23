package it.polimi.ingsw.controller.message.server;

import it.polimi.ingsw.controller.ReferredServerMessage;
import it.polimi.ingsw.controller.ServerMessage;
import it.polimi.ingsw.model.Kingdom;

public class DrawnHiddenCardMessage extends ReferredServerMessage {
    private final Kingdom drawnCard;

    public DrawnHiddenCardMessage(Kingdom drawnCard, String nickname) {
        super(true, nickname);
        this.drawnCard = drawnCard;
    }

    public Kingdom getDrawnCard() {
        return drawnCard;
    }
}
