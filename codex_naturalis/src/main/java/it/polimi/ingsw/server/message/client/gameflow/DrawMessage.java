package it.polimi.ingsw.server.message.client.gameflow;

import it.polimi.ingsw.server.message.ClientMessage;

public class DrawMessage extends ClientMessage {
    private final boolean goldOrNot;
    private final int onTableOrDeck;

    public DrawMessage(String nickname, boolean goldOrNot, int onTableOrDeck){
        super(nickname);
        this.goldOrNot = goldOrNot;
        this.onTableOrDeck = onTableOrDeck;
    }

    public boolean isGoldOrNot() {
        return goldOrNot;
    }

    public int getOnTableOrDeck() {
        return onTableOrDeck;
    }
}
