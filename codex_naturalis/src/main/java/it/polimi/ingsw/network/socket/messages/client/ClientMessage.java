package it.polimi.ingsw.network.socket.messages.client;

import it.polimi.ingsw.network.socket.messages.Message;

/**
 * This abstract class represents a client message that is sent from the client to the server.
 *
 * It extends the `Message` class and can be used as a base for all client messages.
 * Specific types of client messages should extend this class and implement their own behavior.
 *
 * @see Message
 */
public abstract class ClientMessage extends Message {

}
