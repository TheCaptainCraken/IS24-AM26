package it.polimi.ingsw.network.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;

public class ShowNewTableCard extends ServerMessage {
    private final int idCard;
    private final boolean gold;

    private final int onTableOrDeck;

    public ShowNewTableCard(int idCard, boolean gold, int onTableOrDeck) {
        this.idCard = idCard;
        this.gold = gold;
        this.onTableOrDeck = onTableOrDeck;
    }

    public int getIdCard() {
        return idCard;
    }

    public boolean isGold() {
        return gold;
    }

    public int getOnTableOrDeck() {
        return onTableOrDeck;
    }

    @Override
    public void callController(Controller controller) {
        controller.updateCardOnTable(idCard,gold,onTableOrDeck);
    }
}
