package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NoNameException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.awt.*;

/**
 * The NetworkPlug interface defines the methods that are used for network communication in the game.
 * These methods are called by the NetworkHandler class, which is the class that manages the network connections.
 * When something happens in the game and this should be notified in a broadcast way, NetworkHandler call the methods for all
 * the different type of connections protocols.
 *
 * It includes methods for finalizing the number of players, starting the game, refreshing users,
 * sending placed root cards and objective cards, sending hands and secret objective cards,
 * sending placed cards, sending drawn cards, and sending end game signals.
 *
 * It is used by the server to communicate with the clients with different types of network connections.
 * Each connection protocol will implement this interface, to send messages to the clients broadcast.
 * Currently, the implemented connections protocol are
 *    1. the socket connection
 *    2. RMI connection
 */
public interface NetworkPlug {
      //TODO non ci dovrebbe essere eccezioni qua, sistemare quando pietro finisce socket

      /**
       * Finalizes the number of players in the game. It communicates to the clients that the number of players is set.
       * All players allowed to join the game receive a stopWaiting message,
       * the others not allowed to play should be cancelled from the connection list, and this should be notified to the clients.
       * .
       * @throws NoNameException If a player with the given nickname does not exist.
       * @throws SameNameException If two players have the same nickname.
       * @throws LobbyCompleteException If the lobby is already full.
       */
      void finalizingNumberOfPlayers() throws NoNameException, SameNameException, LobbyCompleteException;

      /**
       * Communicates to the clients that the game is starting. The message should be broadcast to all clients.
       * It should notify the Common Card on Table (2 gold cards, 2 resource card, 1 gold card on deck, 1 resource card on deck).
       * It should notify the personal starting card for each player.
       */
      void gameIsStarting();

      /**
       * Refreshes the list of users.
       * Send a hashmap with the players and their colors to all clients.
       */
      void refreshUsers();


      /**
       * Sends the placed root card broadcast to all client.
       * If all players have placed their root card, and thus @param allWithRootCardPlaced is true, you notify the common Objectives cards.
       * Also with a unicast message you notify the secret objective cards that the player can choose.
       *
       * @param nickname The nickname of the player.
       * @param side The side of the card.
       * @param cardId The id of the card.
       * @param allWithRootCardPlaced A boolean indicating whether all players have placed their root cards.
       * @throws NoNameException If a player with the given nickname does not exist.
       */
      void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId, boolean allWithRootCardPlaced) throws NoNameException;//TODO discuss if we have to modify

      /**
       * Sends the hands and secret objective cards.
       * @param nickname The nickname of the player.
       * @param allWithSecretObjectiveCardChosen A boolean indicating whether all players have chosen their secret objective cards.
       * @throws NoNameException If a player with the given nickname does not exist.
       */
      //TODO chiedere a danil se logica è corretta, sembra che a ogni scelta di carta segreta si invia a tutti i giocatori la mano dei giocatori
      void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname, boolean allWithSecretObjectiveCardChosen) throws NoNameException;//TODO discuss if we have to modify

        /**
         * Sends the placed card. Broadcast to all clients.
         * It should notify the card placed by the player, the position, and the side.
         *
         * Also with methods from controller you should get the new points and the new resources of the player.
         *
         * @param nickname The nickname of the player.
         * @param cardId The id of the card.
         * @param position The position of the card.
         * @param side The side of the card.
         * @throws NoNameException If a player with the given nickname does not exist.
         */
      void sendPlacedCard(String nickname, int cardId, Point position, boolean side) throws NoNameException;

        /**
         * Sends the drawn card. Broadcast to all clients.
         * It should notify the card drawn by the player, the head deck, and whether the card is gold or not.
         *
         * @param nickname The nickname of the player.
         * @param newCardId The id of the new card.
         * @param headDeck The head deck.
         * @param gold A boolean indicating whether the card is gold.
         * @param onTableOrDeck An integer indicating whether the card is on the table or the deck.
         * @throws NoNameException If a player with the given nickname does not exist.
         */

        //TODO sistemare cosa invia, a volte inviamo meno uno per dire qualcosa, scriverlo
      void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) throws NoNameException;

      /**
       * Sends the end game signal. Broadcast to all clients.
       * With controller methods you should get the extra points and the ranking of the players.
       */
      void sendEndGame();
}
