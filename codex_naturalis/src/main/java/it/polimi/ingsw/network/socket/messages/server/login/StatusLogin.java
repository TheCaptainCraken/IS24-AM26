package it.polimi.ingsw.network.socket.messages.server.login;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.view.model.Phase;
/**
 * This class represents a status message sent from the server to a client during the login phase.
 * It is used to inform the client about the status of the login phase.
 * If it should ask the number of players or wait for other players to login.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the status message.
 * The `callController` method is used to set the phase of the game and potentially ask the number of players based on the login status.
 *
 * @see ServerMessage
 */
public class StatusLogin extends ServerMessage {
    /**
     * A boolean value that indicates if the client is the first to login.
     */
    private final boolean isFirst;
    /**
     * Class constructor that initializes the boolean value.
     * @param isFirst A boolean value that indicates if the client is the first to login.
     */
    public StatusLogin(boolean isFirst) {
        this.isFirst = isFirst;
    }
    /**
     * This method is used to get the boolean value that indicates if the client is the first to login.
     * @return A boolean value that indicates if the client is the first to login.
     */
    public boolean isFirst() {
        return isFirst;
    }
    /**
     * This method is used to invoke the appropriate method on the provided controller based on the status of the login phase.
     * @param controller The controller(singletone) on which the appropriate method will be invoked.
     */
    @Override
    public void callController(Controller controller){
        if(isFirst){
            Controller.setPhase(Phase.NUMBER_OF_PLAYERS);
            // Ask the number of players to the client.
            controller.askNumberOfPlayer();
        } else {
            // Wait for other players to login.
            Controller.setPhase(Phase.WAIT);
        }
    }
}
