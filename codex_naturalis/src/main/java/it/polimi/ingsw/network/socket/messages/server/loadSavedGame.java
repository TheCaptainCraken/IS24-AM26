package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.model.GameMaster;
import it.polimi.ingsw.controller.client.Controller;

/**
 * loadSavedGame is a class created to manage the message that communicates to
 * the client that the game is starting with a saved game.
 * It is used to send the game state to the client.
 */
public class loadSavedGame extends ServerMessage {
    /**
     * The game that is starting.
     */
    private GameMaster game;

    /**
     * Constructs a new loadSavedGame object with the specified game.
     *
     * @param game The game that is starting.
     */
    public loadSavedGame(GameMaster game) {
        this.game = game;
    }

    /**
     * Calls the controller and sets the model with the game state.
     *
     * @param controller The controller to be called.
     */
    @Override
    public void callController(Controller controller) {
        controller.setModel(game);
    }

    /**
     * Returns the game that is starting.
     *
     * @return The game that is starting.
     */
    public GameMaster getGame() {
        return game;
    }
}