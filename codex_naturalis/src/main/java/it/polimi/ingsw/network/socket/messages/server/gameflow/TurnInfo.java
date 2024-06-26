package it.polimi.ingsw.network.socket.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
/**
 * This class represents a server message that provides information about the current turn in the game.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the turn information.
 * The `callController` method is used to update the client's view of the current player and the game state.
 *
 * @see ServerMessage
 */
public class TurnInfo extends ServerMessage {
    /**
     * The nickname of the current player.
     */
    private final String currentPlayer;
    /**
     * The current state of the game.
     */
    private final GameState state;
    /**
     * Constructor for TurnInfo.
     *
     * @param currentPlayer The nickname of the current player.
     * @param state The current state of the game.
     */
    public TurnInfo(String currentPlayer, GameState state) {
        this.currentPlayer = currentPlayer;
        this.state = state;
    }
    /**
     * Gets the nickname of the current player.
     *
     * @return The nickname of the current player.
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }
    /**
     * Gets the current state of the game.
     *
     * @return The current state of the game.
     */
    public GameState getState() {
        return state;
    }
    /**
     * Calls the `turnInfo` method on the controller with the nickname of the current player and the current game state.
     *
     * @param controller The controller on which the `turnInfo` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        controller.turnInfo(currentPlayer, state);
    }
}
