package it.polimi.ingsw.controller;

public abstract class ServerMessage extends Message{
    public final boolean isBroadcast;

    public ServerMessage(boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }
}
