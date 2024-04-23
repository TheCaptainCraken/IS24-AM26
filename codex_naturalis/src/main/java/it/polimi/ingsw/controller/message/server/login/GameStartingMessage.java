package it.polimi.ingsw.controller.message.server.login;

import it.polimi.ingsw.controller.ServerMessage;
import it.polimi.ingsw.model.Lobby;

public class GameStartingMessage extends ServerMessage {
    private final Lobby lobby;

    public GameStartingMessage(Lobby lobby) {
        super(true);
        this.lobby = lobby;
    }
}
