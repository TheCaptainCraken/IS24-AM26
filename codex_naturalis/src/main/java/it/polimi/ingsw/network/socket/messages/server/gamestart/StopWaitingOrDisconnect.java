package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.view.model.Phase;
/**
 * This class represents a server message that indicates whether the client should stop waiting or disconnect.
 * It is called before the game is starting, when the first player has chosen the number of players.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the received instruction.
 * The `callController` method is used to either stop waiting or disconnect based on the received instruction.
 *
 * @see ServerMessage
 */
public class StopWaitingOrDisconnect extends ServerMessage {
    /**
     * A boolean value that indicates whether the client should stop waiting or disconnect.
     */
    boolean StopWaitingOrDisconnect;
    /**
     * Class constructor that initializes the boolean value.
     * @param StopWaitingOrDisconnect A boolean value that indicates whether the client should stop waiting or disconnect.
     */
    public StopWaitingOrDisconnect(boolean StopWaitingOrDisconnect) {
         this.StopWaitingOrDisconnect = StopWaitingOrDisconnect;
    }
    /**
     * This method is used to check the status of the StopWaitingOrDisconnect flag.
     *
     * @return boolean - Returns true if the client should stop waiting or disconnect, false otherwise.
     */
    public boolean isStopWaitingOrDisconnect() {
        return StopWaitingOrDisconnect;
    }

    /**
     * This method is used to get the boolean value that indicates whether the client should stop waiting or disconnect.
     * @param controller The controller(singleton) on which the appropriate method will be invoked.
     */
    public void callController(Controller controller) {
        if(StopWaitingOrDisconnect) {
            Controller.setPhase(Phase.COLOR);
            controller.stopWaiting();
        } else {
            // Disconnect the client. The client will be disconnected from the server.
            controller.disconnect();
        }
    }

}
