package it.polimi.ingsw.controller;

public abstract class ReferredServerMessage extends ServerMessage {
    public final String playerInterested;

    public ReferredServerMessage(Boolean isBroadcast, String player) {
        super(isBroadcast);
        this.playerInterested = player;
    }

    public String getPlayerInterested() {
        return playerInterested;
    }
}
