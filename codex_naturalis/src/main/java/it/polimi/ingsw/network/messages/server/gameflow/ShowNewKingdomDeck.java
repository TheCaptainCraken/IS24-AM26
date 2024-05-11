package it.polimi.ingsw.network.messages.server.gameflow;

import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.network.messages.server.ServerMessage;

public class ShowNewKingdomDeck extends ServerMessage {
    private final Kingdom kingdom;
    private final boolean gold;

    public ShowNewKingdomDeck(Kingdom kingdom, boolean gold) {
        this.kingdom = kingdom;
        this.gold = gold;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public boolean isGold() {
        return gold;
    }
}
