package it.polimi.ingsw.server.message;

public abstract class ServerMessage extends Message{
    public final boolean isBroadcast;

    public ServerMessage(boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }
}
