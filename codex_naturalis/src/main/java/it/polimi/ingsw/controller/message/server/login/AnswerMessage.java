package it.polimi.ingsw.controller.message.server.login;

import it.polimi.ingsw.controller.ServerMessage;

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
