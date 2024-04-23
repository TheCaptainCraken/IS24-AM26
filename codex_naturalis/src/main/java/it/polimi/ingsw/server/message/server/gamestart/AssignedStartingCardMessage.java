package it.polimi.ingsw.server.message.server.gamestart;

import it.polimi.ingsw.server.message.ServerMessage;

public class AssignedStartingCardMessage extends ServerMessage {
    public final Integer startingCard;

    public AssignedStartingCardMessage(Integer startingCard) {
        super(false);
        this.startingCard = startingCard;
    }
}
