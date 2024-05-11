package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.controller.client.Controller;
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

    @Override
    public void callController(Controller controller) {
        switch(type){
            case NO_TURN: //TODO VIEW
            case FULL_LOBBY: //TODO VIEW
            case WRONG_PHASE: //TODO VIEW
            case NAME_UNKNOWN: //TODO VIEW
            case CARD_POSITION: //TODO VIEW
            case LOBBY_IS_CLOSED: //TODO VIEW
            case COLOR_UNAVAILABLE: //TODO VIEW
            case NAME_ALREADY_USED: //TODO VIEW
            case LOBBY_ALREADY_FULL: //TODO VIEW
            case NOT_ENOUGH_RESOURCES: //TODO VIEW
            case PLAYER_DOES_NOT_EXIST: //TODO VIEW
        }
    }
}
