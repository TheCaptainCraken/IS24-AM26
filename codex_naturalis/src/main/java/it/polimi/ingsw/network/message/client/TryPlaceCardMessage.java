package it.polimi.ingsw.network.message.client;

import it.polimi.ingsw.network.message.ClientMessage;

import java.awt.*;

public class TryPlaceCardMessage extends ClientMessage {
    private final int id;
    private final Point position;
    private final boolean side;

    public TryPlaceCardMessage(String nickname, int id, Point position, boolean side) {
        super(nickname);
        this.id = id;
        this.position = position;
        this.side = side;
    }

    public int getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isSide() {
        return side;
    }
}
