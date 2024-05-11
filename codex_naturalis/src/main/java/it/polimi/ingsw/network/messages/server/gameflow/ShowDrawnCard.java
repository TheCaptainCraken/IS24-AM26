package it.polimi.ingsw.network.messages.server.gameflow;

import it.polimi.ingsw.network.messages.server.ServerMessage;

public class ShowDrawnCard extends ServerMessage {
    private final int cardId;
    private final String nickname;

    public ShowDrawnCard(int cardId, String nickname) {
        this.cardId = cardId;
        this.nickname = nickname;
    }

    public int getCardId() {
        return cardId;
    }

    public String getNickname() {
        return nickname;
    }
}
