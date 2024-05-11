package it.polimi.ingsw.network.messages.server.login;

import it.polimi.ingsw.network.messages.server.ServerMessage;

public class StatusLogin extends ServerMessage {
    private final boolean  isFirst;

    public StatusLogin(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isFirst() {
        return isFirst;
    }
}
