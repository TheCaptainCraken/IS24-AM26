package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.controller.client.Controller;

/**
 * The ConnectionServer class extends ServerMessage and represents a server message
 * specific for managing connections to the server.
 *
 * This class is used when the server needs to communicate connection-related information
 * to the client. For example, it could be used to inform the client that the connection
 * has been successfully established or that an error occurred during the connection.
 *
 */
public class ConnectionServer extends ServerMessage {
    @Override
    public void callController(Controller controller) {
        //TODO
    }
}
