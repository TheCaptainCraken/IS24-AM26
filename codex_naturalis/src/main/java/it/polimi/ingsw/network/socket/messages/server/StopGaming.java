package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.controller.client.Controller;

/**
 * This message is used to stop the game for a client since other players have stop gaming
 * (voluntarily, or they lost the connection).
 **
 */
public class StopGaming extends ServerMessage {
    /**
     * This method is used to invoke the `stopGaming` method on the provided controller.
     *
     * It is called when the `StopGaming` message is received by the client.
     * The method is overridden from the `ServerMessage` class.
     *
     * @param controller The controller on which the `stopGaming` method will be invoked.
     */
    @Override
    public void callController(Controller controller){
        controller.stopGaming();
    }
}
