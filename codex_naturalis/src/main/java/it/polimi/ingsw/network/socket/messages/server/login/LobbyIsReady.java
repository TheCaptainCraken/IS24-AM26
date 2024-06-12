package it.polimi.ingsw.network.socket.messages.server.login;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.view.model.Phase;

/**
 * This class represents a server message that indicates whether the lobby is ready or not.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the lobby status.
 * The `callController` method is used to either stop waiting or continue waiting for the lobby based on the lobby status.
 *
 * @see ServerMessage
 */
public class LobbyIsReady extends ServerMessage {
    /**
     * A boolean value that indicates whether the lobby is ready or not.
     */
    private final boolean isReady;
    /**
     * Constructor for LobbyIsReady.
     *
     * @param isReady A boolean indicating whether the lobby is ready or not.
     */
    public LobbyIsReady(boolean isReady) {
        this.isReady = isReady;
    }
    /**
     * Gets the lobby status.
     *
     * @return A boolean indicating whether the lobby is ready or not.
     */
    public boolean isReady() {
        return isReady;
    }
    /**
     * Calls the appropriate method on the controller based on the lobby status.
     *
     * @param controller The controller on which the appropriate method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        if(isReady) {
            // Stop waiting for the lobby.
            // I do nothing since it will arrive a new message in the queue. (stopWaitingOrDisconnect)
        } else{
            // Continue waiting for the lobby.
            Controller.setPhase(Phase.WAIT_NUMBER_OF_PLAYERS);
            controller.waitLobby();
        }
    }
}
