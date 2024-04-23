package it.polimi.ingsw.server.message.server;

import it.polimi.ingsw.server.message.ServerMessage;

public class ErrorMessage extends ServerMessage {
    private final String type;
    private final String error;

    public ErrorMessage(String type, String error) {
        super(false);
        this.type = type;
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public String getError() {
        return error;
    }
}
