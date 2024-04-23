package it.polimi.ingsw.server.message.server;

import it.polimi.ingsw.server.message.ReferredServerMessage;
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
