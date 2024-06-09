package it.polimi.ingsw.network.socket.messages.client.login;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

/**
 * This class represents a client message that is sent from the client to the server during the login process.
 *
 * It extends the `ClientMessage` class and contains the nickname of the user who is trying to log in.
 *
 * It is sent to communicate the nickname of the user that is trying to log in.
 * @see ClientMessage
 */
public class LoginMessage extends ClientMessage {
    /**
     * The nickname of the user that is trying to log in.
     */
    private final String nickname;

    /**
     * Constructor for LoginMessage.
     *
     * @param nickname The nickname of the user trying to log in.
     */
    public LoginMessage(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets the nickname of the user trying to log in.
     *
     * @return The nickname of the user.
     */
    public String getNickname() {
        return nickname;
    }
}
