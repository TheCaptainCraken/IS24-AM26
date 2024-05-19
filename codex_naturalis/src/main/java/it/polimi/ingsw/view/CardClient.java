package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Corner;

import java.awt.*;
import java.util.*;

public class CardClient {
    private final int id;
    private final boolean side;
    private final Point position;
    private final int turnOfPositioning;
    private final HashMap<Corner, CardClient> attachmentCorners;

    public CardClient(int id, boolean side, Point position, int turnOfPositioning, HashMap<Corner, CardClient> attachmentCorners) {
        this.id = id;
        this.side = side;
        this.position = position;
        this.turnOfPositioning = turnOfPositioning;
        this.attachmentCorners = attachmentCorners;
    }

    public int getId() {
        return id;
    }

    public boolean getSide() {
        return side;
    }

    public Point getPosition() {
        return position;
    }

    public CardClient getAttached(Corner corner) {
        return attachmentCorners.get(corner);
    }

    public int getTurnOfPositioning() {
        return turnOfPositioning;
    }

    public void setAttached(Corner corner, CardClient newCard) {
        attachmentCorners.put(corner, newCard);
    }

}
