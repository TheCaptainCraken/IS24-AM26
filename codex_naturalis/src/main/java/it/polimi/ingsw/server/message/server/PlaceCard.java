package it.polimi.ingsw.server.message.server;

import it.polimi.ingsw.server.message.ReferredServerMessage;
import it.polimi.ingsw.model.Sign;

import java.awt.*;
import java.util.HashMap;

public class PlaceCard extends ReferredServerMessage {
    private final int id;
    private final Point position;
    private final boolean side;
    private final int score;
    private final HashMap<Sign, Integer> resources;

    public PlaceCard(String nickname, int id, Point position, boolean side, int score, HashMap<Sign, Integer> resources) {
        super(true, nickname);
        this.id = id;
        this.position = position;
        this.side = side;
        this.score = score;
        this.resources = resources;
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

    public int getScore() {
        return score;
    }

    public HashMap<Sign, Integer> getResources() {
        return resources;
    }
}
