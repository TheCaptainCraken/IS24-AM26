package it.polimi.ingsw.view.model;

import it.polimi.ingsw.model.Corner;

import java.awt.*;
import java.util.*;

/**
 * Represents a card in the client-side view of the game.

 * This class holds information about a card, including its ID, side, position,
 * the turn it was positioned, and the cards attached to its corners.

 * Each card has a unique ID and a side (true or false). The position of the card
 * is represented as a Point object. The turn of positioning indicates the turn
 * in which the card was positioned on the board.

 * The attached cards are stored in a HashMap, where the keys are the corners of
 * the card (represented as Corner objects), and the values are the cards attached
 * to those corners (represented as CardClient objects).
 */
public class CardClient {
    /**
     * The ID of the card.
     */
    private final int id;
    /**
     * The side of the card.
     */
    private final boolean side;
    /**
     * The position of the card.
     */
    private final Point position;
    /**
     * The turn in which the card was positioned.
     */
    private final int turnOfPositioning;
    /**
     * The cards attached to the corners of the card.
     */
    private final HashMap<Corner, CardClient> attachmentCorners;

    /**
     * Creates a new CardClient object with the specified ID, side, position,
     * turn of positioning, and attachment corners.
     *
     * @param id The ID of the card.
     * @param side The side of the card.
     * @param position The position of the card.
     * @param turnOfPositioning The turn in which the card was positioned.
     * @param attachmentCorners The cards attached to the corners of the card.
     */
    public CardClient(int id, boolean side, Point position, int turnOfPositioning, HashMap<Corner, CardClient> attachmentCorners) {
        this.id = id;
        this.side = side;
        this.position = position;
        this.turnOfPositioning = turnOfPositioning;
        this.attachmentCorners = attachmentCorners;
    }
    /**
     * Returns the ID of the card.
     * @return The ID of the card.
     */
    public int getId() {
        return id;
    }
    /**
     * Returns the side of the card.
     * @return  The side of the card.
     */
    public boolean getSide() {
        return side;
    }
    /**
     *  Returns the position of the card.
     * @return The position of the card.
     */
    public Point getPosition() {
        return position;
    }
    /**
     * Returns the card attached to the specified corner of the card.
     * @param corner The corner of the card.
     * @return The card attached to the corner.
     */
    public CardClient getAttached(Corner corner) {
        return attachmentCorners.get(corner);
    }
    /**
     * Returns the turn in which the card was positioned.
     * @return The turn in which the card was positioned.
     */
    public int getTurnOfPositioning() {
        return turnOfPositioning;
    }
    /**
     * Sets the card attached to the specified corner of the card.
     * @param corner The corner of the card.
     * @param newCard The card to attach to the corner.
     */
    public void setAttached(Corner corner, CardClient newCard) {
        attachmentCorners.put(corner, newCard);
    }

}
