package it.polimi.ingsw.network.socket.messages;

import java.io.Serializable;
/**
 *
 * This abstract class represents a message that can be sent over the network.
 *
 * It is a part of the network communication system of the application.
 * Any specific type of message that needs to be sent over the network should extend this class.
 * The class implements Serializable to allow the objects of subclasses to be converted
 * into a format that can be sent over the network.
 */
public abstract class Message implements Serializable {
}
