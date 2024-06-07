package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Color;

import java.awt.*;

/**
 * The NetworkClient interface represents the contract for a network client in the application.
 * It defines the methods that any communication class protocol implements this interface must provide.
 * Currently, communication protocols are RMI and Socket.
 * The methods in this interface are related to game actions such as login, choosing color, drawing cards etc.
 */
public interface NetworkClient {
    //All inputs are controlled by the controller or client interface.It contacts the server appropriately with the communication protocol
    //the communication protocols always communicate with a unique nickname,so the server can identify the player/client.

    /**
     * Logs in the player with the given nickname.
     * This method is used to authenticate a player in the game.
     * It is typically called at the start of the game setup.
     *
     * @param nickname The nickname of the player.
     */
    void login(String nickname);

    /**
     * Inserts the number of players in the game.
     * This method is used to set the number of players that will participate in the game.
     *
     * @param numberOfPlayers The number of players.
     */
    void insertNumberOfPlayers(int numberOfPlayers);

    /**
     * Chooses the color for the player.
     * This method is used to set the color of the player in the game.
     *
     * @param color The color chosen by the player.
     */
    void chooseColor(Color color);

    /**
     * Sends a chat message.
     * This method is used to send a chat message to the other players in the game.
     *
     * @param sender The nickname of the player who sent the message.
     * @param message The message sent by the player.
     */
    void sendChatMessage(String sender, String message);

    /**
     * Chooses the side of the starting card for the player.
     * This method is used to set the side of the starting card of the player in the game.
     *
     * @param side The side of the starting card chosen by the player.
     */
    void chooseSideStartingCard(boolean side);

    /**
    * Chooses the secret objective card for the player.
    * This method is used to set the secret objective card of the player in the game.
    * It is typically called at the start of the game setup.
     *
    *  @param indexCard The index of the secret objective card chosen by the player.
    */
    void chooseSecretObjectiveCard(int indexCard);

    /**
     * Plays a card from the player's hand.
     * This method is used to play a card from the player's hand onto the game table.
     * It takes three parameters: the index of the card in the player's hand, the position on the table where the card will be placed, and the side of the card.
     *
     * @param indexHand The index of the card in the player's hand.
     * @param position The position on the table where the card will be placed.
     * @param side The side of the card.
     */
    void playCard(int indexHand, Point position, boolean side);

    /**
     * Draws a card for the player.
     * This method is used to draw a card for the player.
     * It takes three parameters: the nickname of the player,
     * a boolean indicating whether the card is gold,
     * and an integer indicating whether the card is drawn from the table or the deck(-1 deck, o or 1 for table).
     *
     * @param nickname The nickname of the player.
     * @param gold A boolean indicating whether the card is gold.
     * @param onTableOrDeck An integer indicating whether the card is drawn from the table or the deck.
     */
    void drawCard(String nickname, boolean gold, int onTableOrDeck);
}

