package it.polimi.ingsw.network.socket.messages.client.login;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

/**
 * This class represents a client message that is sent from the client to the server indicating the color chosen by the user.
 *
 * It extends the `ClientMessage` class and contains the nickname of the user and the chosen color.
 *
 * @see ClientMessage
 */
public class ColorChosen extends ClientMessage {
    /**
     * The nickname of the user that has chosen the color.
     */
    private final String nickname;
    /**
     * The color chosen by the user.
     */
    private final Color color;

    /**
     * Constructor for ColorChosen.
     *
     * @param nickname The nickname of the user.
     * @param color The color chosen by the user.
     */
    public ColorChosen(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
    }

    /**
     * Gets the nickname of the user.
     *
     * @return The nickname of the user.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Gets the color chosen by the user.
     *
     * @return The color chosen by the user.
     */
    public Color getColor() {
        return color;
    }
}
