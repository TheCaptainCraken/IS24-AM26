package it.polimi.ingsw.network.socket.messages.client.gamestart;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

public class ChosenObjectiveCard extends ClientMessage {
    private final int indexCard;
    private final String nickname;

    public ChosenObjectiveCard(String nickname, int indexCard) {
        this.nickname = nickname;
        this.indexCard = indexCard;
    }

    public int getIndexCard() {
        return indexCard;
    }

    public String getNickname() {
        return nickname;
    }
}
