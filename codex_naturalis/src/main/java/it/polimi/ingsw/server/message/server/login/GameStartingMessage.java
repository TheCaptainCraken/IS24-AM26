package it.polimi.ingsw.server.message.server.login;

import it.polimi.ingsw.server.message.ServerMessage;
import it.polimi.ingsw.model.Lobby;

public class GameStartingMessage extends ServerMessage {
    private final Lobby lobby;

    public GameStartingMessage(Lobby lobby) {
        super(true);
        this.lobby = lobby;
    }
}
