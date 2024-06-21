package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.controller.client.Controller;

/**
 * The ConnectionServerForClient class extends ServerMessage and represents a server message
 * specific for managing connections to the server from the client's perspective.
 *
 * This class is used when the client needs to handle a connection-related message from the server.
 * It extends the `ServerMessage` class and overrides the `callController` method, which currently does not need to be implemented.
 *
 * @see ServerMessage
 */
public class ConnectionServerForClient extends ServerMessage {
    @Override
    public void callController(Controller controller) {
        //TODO: implement does not need to be implemented
    }
}
