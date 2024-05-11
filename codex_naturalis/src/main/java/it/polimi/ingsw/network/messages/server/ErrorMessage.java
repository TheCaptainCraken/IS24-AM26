package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.ErrorType;
import it.polimi.ingsw.network.messages.server.ServerMessage;

public class ErrorMessage extends ServerMessage {
    private final ErrorType type;

    public ErrorMessage(ErrorType type) {
        this.type = type;
    }

    public ErrorType getType() {
        return type;
    }
}
