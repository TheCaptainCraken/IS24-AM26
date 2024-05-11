package it.polimi.ingsw.network.messages.client.gamestart;

import it.polimi.ingsw.network.messages.client.ClientMessage;

public class ChosenObjectiveCard extends ClientMessage {
    private final int indexCard;

    public ChosenObjectiveCard(int indexCard) {
        this.indexCard = indexCard;
    }

    public int getIndexCard() {
        return indexCard;
    }
}
