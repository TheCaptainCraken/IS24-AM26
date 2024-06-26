package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.Message;
/**
 * This abstract class represents a server message that can be sent over the network.
 *
 * It extends the `Message` class and provides an abstract method `callController` that must be implemented by any specific type of server message.
 */
public abstract class ServerMessage extends Message {
    /**
     *  * The method is used to invoke the appropriate method on the provided controller based on the type of the server message.
     * @param controller The controller(singleton) on which the appropriate method will be invoked.
     */
    public abstract void callController(Controller controller);
}
