package it.polimi.ingsw.network.message.server.gamestart;

import it.polimi.ingsw.network.message.ServerMessage;

public class AssignedObjectiveCardMessage extends ServerMessage {
    private final Integer[] objectiveCards;

    public AssignedObjectiveCardMessage(Integer[] objectiveCards) {
        super(false);
        this.objectiveCards = objectiveCards;
    }

    public Integer[] getObjectiveCards() {
        Integer[]objectiveCards = new Integer[this.objectiveCards.length];
        for (int i = 0; i < objectiveCards.length; i++) {
            objectiveCards[i] = this.objectiveCards[i];
        }
        return objectiveCards;
    }
}
