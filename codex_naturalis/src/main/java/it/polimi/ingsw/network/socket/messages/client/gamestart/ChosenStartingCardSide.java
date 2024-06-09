package it.polimi.ingsw.network.socket.messages.client.gamestart;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

/**
 * This class represents a client message that is sent from the client to the server indicating the side of the starting card chosen by the user.
 *
 * It extends the `ClientMessage` class and contains the side of the starting card (true or false) and the nickname of the user.
 * It is used to communicate the side of the starting card chosen by the user.
 *
 * @see ClientMessage
 */
public class ChosenStartingCardSide extends ClientMessage {
    /**
     * The side of the starting card chosen by the user. (true = front, false = back)
     */
    private final boolean side;
    /**
     * The nickname of the user that has chosen the side of the starting card.
     */
    private final String nickname;

    /**
     * Constructor for ChosenStartingCardSide.
     *
     * @param nickname The nickname of the user.
     * @param side The side of the starting card chosen by the user.
     */
    public ChosenStartingCardSide(String nickname, boolean side) {
        this.nickname = nickname;
        this.side = side;
    }

    /**
     * Gets the side of the starting card chosen by the user.
     *
     * @return The side of the starting card.
     */
    public boolean isSide() {
        return side;
    }

    /**
     * Gets the nickname of the user.
     *
     * @return The nickname of the user.
     */
    public String getNickname() {
        return nickname;
    }
}
