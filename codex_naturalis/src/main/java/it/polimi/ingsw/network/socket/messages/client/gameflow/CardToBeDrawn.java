package it.polimi.ingsw.network.socket.messages.client.gameflow;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

/**
 * This class represents a client message that is sent from the client to the server indicating the card to be drawn by the user.
 *
 * It extends the `ClientMessage` class and contains the nickname of the user, a boolean indicating if the card is gold, and an integer representing whether the card is on the table or on the deck.
 * It is used to communicate the card to be drawn by the user.
 *
 * @see ClientMessage
 */
public class CardToBeDrawn extends ClientMessage {
    /**
     * The nickname of the user that has to draw the card.
     */
    private final String nickname;
    /**
     * A boolean indicating if the card to be drawn is gold.
     */
    private final boolean gold;
    /**
     * An integer representing whether the card to be drawn is on the table or on the deck.
     * (-1 on deck, 0 or 1 on table)
     */
    private final int onTableOrOnDeck;

    /**
     * Constructor for CardToBeDrawn.
     *
     * @param nickname The nickname of the user.
     * @param gold A boolean indicating if the card is gold.
     * @param onTableOrOnDeck An integer representing whether the card is on the table or on the deck.
     */
    public CardToBeDrawn(String nickname, boolean gold, int onTableOrOnDeck) {
        this.nickname = nickname;
        this.gold = gold;
        this.onTableOrOnDeck = onTableOrOnDeck;
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
     * Checks if the card is gold.
     *
     * @return A boolean indicating if the card is gold.
     */
    public boolean isGold() {
        return gold;
    }

    /**
     * Gets the status of the card (on the table or on the deck).
     *
     * @return An integer representing whether the card is on the table or on the deck.
     */
    public int getOnTableOrOnDeck() {
        return onTableOrOnDeck;
    }
}
