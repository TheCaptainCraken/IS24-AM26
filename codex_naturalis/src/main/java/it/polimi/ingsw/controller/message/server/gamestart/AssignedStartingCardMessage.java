package it.polimi.ingsw.controller.message.server.gamestart;

import it.polimi.ingsw.controller.ServerMessage;

public class AssignedStartingCardMessage extends ServerMessage {
    public final Integer startingCard;

    public AssignedStartingCardMessage(Integer startingCard) {
        super(false);
        this.startingCard = startingCard;
    }
}
