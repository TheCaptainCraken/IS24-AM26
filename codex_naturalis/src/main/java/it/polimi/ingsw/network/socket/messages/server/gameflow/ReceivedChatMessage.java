package it.polimi.ingsw.network.socket.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

/**
 * This class represents a chat message received from a client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the received chat message.
 * The `callController` method is used to pass the received message to the controller for further processing.
 *
 * @see ServerMessage
 */
public class ReceivedChatMessage extends ServerMessage {
    /**
     * The nickname of the sender of the message.
     */
    private final String sender;
    /**
     * The message sent by the sender.
     */
    private final String message;

    /**
     * Class constructor that initializes the sender and message fields.
     * @param sender The nickname/nicknames of the sender of the message.
     * @param message The message sent by the sender.
     */
    public ReceivedChatMessage(String sender, String message) {
        super();
        this.sender = sender;
        this.message = message;
    }
    /**
     * This method is used to get the nickname of the sender of the message.
     * @return The nickname of the sender of the message.
     */
    public String getSender() {
        return sender;
    }
    /**
     * This method is used to get the message sent by the sender.
     * @return The message sent by the sender.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param controller The controller that will handle the message.
     */
    @Override
    public void callController(Controller controller) {
        // Pass the received message to the controller for further processing.
        // The controller will handle the message and update the view accordingly.
        controller.receiveMessage(sender, message);
    }
}
