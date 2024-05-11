package it.polimi.ingsw.network.messages.server.login;

import it.polimi.ingsw.network.messages.server.ServerMessage;

public class LobbyIsReady extends ServerMessage {
    private final boolean isReady;

    public LobbyIsReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isReady() {
        return isReady;
    }
}
