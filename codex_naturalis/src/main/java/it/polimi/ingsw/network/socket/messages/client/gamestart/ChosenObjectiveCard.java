package it.polimi.ingsw.network.socket.messages.client.gamestart;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

/**
 * This class represents a client message that is sent from the client to the server indicating the objective card chosen by the user.
 *
 * It extends the `ClientMessage` class and contains the index of the chosen objective card and the nickname of the user.
 * It is used to communicate the objective card chosen by the user.
 *
 * @see ClientMessage
 */
public class ChosenObjectiveCard extends ClientMessage {
    /**
     * The index of the chosen objective card. (0,1)
     */
    private final int indexCard;
    /**
     * The nickname of the user that has chosen the objective card.
     */
    private final String nickname;

    /**
     * Constructor for ChosenObjectiveCard.
     *
     * @param nickname The nickname of the user.
     * @param indexCard The index of the chosen objective card.
     */
    public ChosenObjectiveCard(String nickname, int indexCard) {
        this.nickname = nickname;
        this.indexCard = indexCard;
    }

    /**
     * Gets the index of the chosen objective card.
     *
     * @return The index of the chosen objective card.
     */
    public int getIndexCard() {
        return indexCard;
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
