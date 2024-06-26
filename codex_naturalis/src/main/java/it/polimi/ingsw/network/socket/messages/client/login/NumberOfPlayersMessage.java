package it.polimi.ingsw.network.socket.messages.client.login;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

/**
 * This class represents a client message that is sent from the client to the server indicating the number of players in the game.
 *
 * It extends the `ClientMessage` class and contains the number of players.
 *
 * @see ClientMessage
 */
public class NumberOfPlayersMessage extends ClientMessage {
    /**
     * The number of players in the game.
     */
    private final int number;

    /**
     * Constructor for NumberOfPlayersMessage.
     *
     * @param number The number of players in the game.
     */
    public NumberOfPlayersMessage(int number) {
        this.number = number;
    }

    /**
     * Gets the number of players in the game.
     *
     * @return The number of players.
     */
    public int getNumber() {
        return number;
    }
}
