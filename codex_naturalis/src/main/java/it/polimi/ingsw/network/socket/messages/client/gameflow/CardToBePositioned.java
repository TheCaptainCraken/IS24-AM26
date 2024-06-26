package it.polimi.ingsw.network.socket.messages.client.gameflow;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

import java.awt.*;
/**
 * This class represents a client message that is sent from the client to the server indicating the card to be positioned by the user.
 *
 * It extends the `ClientMessage` class and contains the nickname of the user, an integer representing the hand placement, a Point representing the position, and a boolean indicating the side of the card.
 * It is used to communicate the card to be positioned by the user.
 *
 * @see ClientMessage
 */
public class CardToBePositioned extends ClientMessage {
    /**
     * The nickname of the user that has to position the card.
     */
    private final String nickname;
    /**
     * An integer representing the hand placement of the card. (0,1,2)
     */
    private final int handPlacement;
    /**
     * A Point representing the position of the card.
     */
    private final Point position;
    /**
     * A boolean indicating the side of the card. (true = front, false = back)
     */
    private final boolean side;

    /**
     * Constructor for CardToBePositioned.
     *
     * @param nickname The nickname of the user.
     * @param handPlacement An integer representing the hand placement.
     * @param position A Point representing the position.
     * @param side A boolean indicating the side of the card.
     */
    public CardToBePositioned(String nickname, int handPlacement, Point position, boolean side) {
        this.nickname = nickname;
        this.handPlacement = handPlacement;
        this.position = position;
        this.side = side;
    }

    /**
     * Gets the hand placement.
     *
     * @return An integer representing the hand placement.
     */
    public int getHandPlacement() {
        return handPlacement;
    }

    /**
     * Gets the position.
     *
     * @return A Point representing the position.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Gets the side of the card.
     *
     * @return A boolean indicating the side of the card.
     */
    public boolean getSide() {
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
