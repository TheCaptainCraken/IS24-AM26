package it.polimi.ingsw.server.message.server;

import it.polimi.ingsw.server.message.ServerMessage;
import it.polimi.ingsw.model.*;

public class InfoDrawableCardOnTableMessage extends ServerMessage {
    private final Kingdom kingdomFirstCardResourceDeck;
    private final Kingdom kingdomFirstCardGoldDeck;
    private final Integer[] onTableResourceCards;
    private final Integer[] onTableGoldCards;
    private final String currentPlayer;

    public InfoDrawableCardOnTableMessage(Kingdom kingdomFirstCardResourceDeck, Kingdom kingdomFirstCardGoldDeck, Integer[] onTableResourceCards, Integer[] onTableGoldCards, String currentPlayer) {
        super(true);
        this.kingdomFirstCardResourceDeck = kingdomFirstCardResourceDeck;
        this.kingdomFirstCardGoldDeck = kingdomFirstCardGoldDeck;
        this.onTableResourceCards = onTableResourceCards;
        this.onTableGoldCards = onTableGoldCards;
        this.currentPlayer = currentPlayer;
    }

    public Kingdom getKingdomFirstCardResourceDeck() {
        return kingdomFirstCardResourceDeck;
    }

    public Kingdom getKingdomFirstCardGoldDeck() {
        return kingdomFirstCardGoldDeck;
    }

    public Integer[] getOnTableResourceCards() {
        Integer[] onTableResourceCardsToSend = new Integer[onTableResourceCards.length];
        for (int i = 0; i < onTableResourceCards.length; i++) {
            onTableResourceCardsToSend[i] = onTableResourceCards[i];
        }
        return onTableResourceCardsToSend;
    }

    public Integer[] getOnTableGoldCards() {
        Integer[] onTableGoldCardsToSend = new Integer[onTableGoldCards.length];
        for (int i = 0; i < onTableResourceCards.length; i++) {
            onTableGoldCardsToSend[i] = onTableResourceCards[i];
        }
        return onTableGoldCardsToSend;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }
}
