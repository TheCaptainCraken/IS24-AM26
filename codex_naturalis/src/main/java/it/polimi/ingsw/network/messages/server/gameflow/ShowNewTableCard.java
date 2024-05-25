package it.polimi.ingsw.network.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.network.messages.server.ServerMessage;

public class ShowNewTableCard extends ServerMessage {
    private final int idCard;
    private final boolean gold;
    private final Kingdom topCard;

    private final int onTableOrDeck;

    public ShowNewTableCard(int idCard, boolean gold, int onTableOrDeck, Kingdom topCard) {
        this.idCard = idCard;
        this.gold = gold;
        this.onTableOrDeck = onTableOrDeck;
        this.topCard = topCard;
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

    public Kingdom getTopCard() {
        return topCard;
    }

    @Override
    public void callController(Controller controller) {
        controller.updateCardOnTable(idCard, gold, onTableOrDeck);
        //TODO ricontrollare logica.
        controller.updateHeadDeck(topCard, gold);
    }
}
