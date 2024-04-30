package it.polimi.ingsw.network.message.server.gamestart;

import it.polimi.ingsw.network.message.ServerMessage;

public class AssignedStartingCardMessage extends ServerMessage {
    public final Integer startingCard;

    public AssignedStartingCardMessage(Integer startingCard) {
        super(false);
        this.startingCard = startingCard;
    }
}
