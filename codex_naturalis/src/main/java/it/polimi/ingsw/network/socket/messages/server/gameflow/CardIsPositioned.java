package it.polimi.ingsw.network.socket.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.awt.*;
/**
 * This class represents a server message that shows the position of a card to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the positioned card.
 * The `callController` method is used to update the client's view of the positioned card.
 *
 * @see ServerMessage
 */
public class CardIsPositioned extends ServerMessage {
    /**
     * The nickname of the player.
     */
    private final String nickname;
    /**
     * The id of the card.
     */
    private final int cardId;
    /**
     * The position of the card.
     */
    private final Point position;
    /**
     * The side of the card.
     */
    private final boolean side;
    /**
     * The turn number.
     */
    private final int turn;
    /**
     * Constructor for CardIsPositioned.
     *
     * @param nickname The nickname of the player.
     * @param cardId The id of the card.
     * @param position The position of the card.
     * @param side The side of the card.
     * @param turn The turn number.
     */
    public CardIsPositioned(String nickname, int cardId, Point position, boolean side, int turn) {
        this.nickname = nickname;
        this.cardId = cardId;
        this.position = position;
        this.side = side;
        this.turn = turn;
    }
    /**
     * Gets the nickname of the player.
     *
     * @return The nickname of the player.
     */
    public String getNickname() {
        return nickname;
    }
    /**
     * Gets the id of the card.
     *
     * @return The id of the card.
     */
    public int getCardId() {
        return cardId;
    }
    /**
     * Gets the position of the card.
     *
     * @return The position of the card.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Gets the side of the card.
     *
     * @return The side of the card.
     */
    public boolean isSide() {
        return side;
    }

    /**
     * Calls the `updatePlaceCard` method on the controller with the nickname, card id, position, side, and turn.
     *
     * @param controller The controller on which the `updatePlaceCard` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        //Resources and points of players are send in a different messages.
        // This messages contains only the information of a placed card.
        controller.updatePlaceCard(nickname, cardId, position, side, turn);
    }
}
