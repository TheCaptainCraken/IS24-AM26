package it.polimi.ingsw.network.socket.messages.client.gameflow;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

import java.awt.*;

public class CardToBePositioned extends ClientMessage {
    private final String nickname;
    private final int handPlacement;
    private final Point position;
    private final boolean side;

    public CardToBePositioned(String nickname, int handPlacement, Point position, boolean side) {
        this.nickname = nickname;
        this.handPlacement = handPlacement;
        this.position = position;
        this.side = side;
    }

    public int getHandPlacement() {
        return handPlacement;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isSide() {
        return side;
    }

    public String getNickname() {
        return nickname;
    }
}
