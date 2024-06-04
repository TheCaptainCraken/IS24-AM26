package it.polimi.ingsw.network.socket.messages.client.gamestart;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

public class ChosenStartingCardSide extends ClientMessage {
    private final boolean side;
    private final String nickname;

    public ChosenStartingCardSide(String nickname, boolean side) {
        this.nickname = nickname;
        this.side = side;
    }

    public boolean isSide() {
        return side;
    }

    public String getNickname() {
        return nickname;
    }
}
