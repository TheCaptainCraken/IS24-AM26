package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.model.GameMaster;
import it.polimi.ingsw.controller.client.Controller;

/**
 * loadSavedGame is a class created to manage the message that communicates to
 * the client that the game is starting with a saved game.
 * It is used to send the game to the client.
 */
public class loadSavedGame extends ServerMessage {
    /**
     * The game that is starting.
     */
    private GameMaster game;

    public loadSavedGame(GameMaster game) {
        this.game = game;
    }

    @Override
    public void callController(Controller controller) {
        controller.setModel(game);
    }
}
