package it.polimi.ingsw.server.message.server.login;

import it.polimi.ingsw.server.message.ServerMessage;

public class AnswerMessage extends ServerMessage {
    private final boolean admitted;

    public AnswerMessage(boolean admitted) {
        super(false);
        this.admitted = admitted;
    }

    public boolean isAdmitted() {
        return admitted;
    }
}
