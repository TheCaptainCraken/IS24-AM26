package it.polimi.ingsw.network.socket.messages.client.gameflow;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

/**
 * This class represents a client message that is sent from the client to the server containing a chat message from the user.
 *
 * It extends the `ClientMessage` class and contains the sender's nickname and the message content.
 * It is sent to communicate a chat message from the user to the other users.
 *
 * @see ClientMessage
 */
public class SentChatMessage extends ClientMessage {
    /**
     * The nickname/nicknames of the user that sent the message.
     * The format is "@User1 @User2 ...". If the message is sent to all users, there is no @User.
     */
    private final String sender;
    /**
     * The content of the message.
     */
    private final String message;

    /**
     * Constructor for SentChatMessage.
     *
     * @param sender The nickname of the sender.
     * @param message The content of the message.
     */
    public SentChatMessage(String sender, String message) {
        super();
        this.sender = sender;
        this.message = message;
    }

    /**
     * Gets the sender's nickname.
     *
     * @return The nickname of the sender.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Gets the content of the message.
     *
     * @return The content of the message.
     */
    public String getMessage() {
        return message;
    }
}
