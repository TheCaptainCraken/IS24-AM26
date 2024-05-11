package it.polimi.ingsw.network.messages.client.gamestart;

import it.polimi.ingsw.network.messages.client.ClientMessage;

public class ChosenStartingCardSide extends ClientMessage {
    private final boolean side;

    public ChosenStartingCardSide(boolean side) {
        this.side = side;
    }

    public boolean isSide() {
        return side;
    }
}
