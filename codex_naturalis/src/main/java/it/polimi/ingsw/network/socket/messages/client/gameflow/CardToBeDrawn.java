package it.polimi.ingsw.network.socket.messages.client.gameflow;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

public class CardToBeDrawn extends ClientMessage {
    private final String nickname;
    private final boolean gold;
    private final int onTableOrOnDeck;

    public CardToBeDrawn(String nickname, boolean gold, int onTableOrOnDeck) {
        this.nickname = nickname;
        this.gold = gold;
        this.onTableOrOnDeck = onTableOrOnDeck;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isGold() {
        return gold;
    }

    public int getOnTableOrOnDeck() {
        return onTableOrOnDeck;
    }
}
