package it.polimi.ingsw.network.messages.server.gameflow;

import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.network.messages.server.ServerMessage;

public class TurnInfo extends ServerMessage {
    private final String currentPlayer;
    private final GameState state;

    public TurnInfo(String currentPlayer, GameState state) {
        this.currentPlayer = currentPlayer;
        this.state = state;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public GameState getState() {
        return state;
    }
}
