package it.polimi.ingsw.network;

import it.polimi.ingsw.model.GameMaster;
import it.polimi.ingsw.model.Kingdom;

import java.awt.*;
import java.util.ArrayList;

/**
 * The NetworkPlug interface defines the methods that are used for network
 * communication in the game.
 * These methods are called by the NetworkHandler class, which is the class that
 * manages the network connections.
 * When something happens in the game and this should be notified in a broadcast
 * way, NetworkHandler call the methods for all
 * the different types of connection protocols.
 * 
 * It includes methods for finalizing the number of players, starting the game,
 * refreshing users,
 * sending placed root cards and objective cards, sending hands and secret
 * objective cards,
 * sending placed cards, sending drawn cards, and sending end game signals.
 * 
 * It is used by the server to communicate with the clients with different types
 * of network connections.
 * Each connection protocol will implement this interface, to send messages to
 * the clients broadcast.
 * Currently, the implemented connections protocols are
 * 1. Socket connection
 * 2. RMI connection
 */
public interface NetworkPlug {
        /**
         * Finalizes the number of players in the game. It communicates to the clients
         * that the number of players is set.
         * All players allowed to join the game receive a stopWaiting message,
         * the others not allowed to play should be cancelled from the connection list,
         * and this should be notified to the clients.
         *
         */
        void finalizingNumberOfPlayers();

        /**
         * Refreshes the list of users.
         * Send a hashmap with the players and their colors to all clients.
         */
        void refreshUsers();

        /**
         * Communicates to the clients that the game is starting. The message should be
         * broadcast to all clients.
         * It should notify the Common Card on Table (2 gold cards, 2 resource card, 1
         * gold card on deck, 1 resource card on deck).
         * It should notify the personal starting card for each player.
         */
        void gameIsStarting();

        /**
         * Fetches the receivers of the message.
         *
         * @param message The message to send.
         * @return The list of nicknames of the players across connections who should
         *         receive the message.
         *
         */
        ArrayList<String> fetchReceivers(String message);

        /**
         * Sends a chat message to players checking tags.
         *
         * @param sender    The nickname of the player who sent the message.
         * @param message   The message sent by the player.
         * @param receivers The list of nicknames of the players across connectios who
         *                  should receive the message.
         */
        void sendingChatMessage(String sender, String message, ArrayList<String> receivers);

        /**
         * Sends the placed root card broadcast to all client.
         * If all players have placed their root card, and thus @param
         * allWithRootCardPlaced is true, you notify the common Objectives cards.
         * Also with unicast message you notify the secret objective cards that the
         * player can choose.
         *
         * @param nickname              The nickname of the player.
         * @param side                  The side of the card.
         * @param cardId                The id of the card.
         * @param allWithRootCardPlaced A boolean indicating whether all players have
         *                              placed their root cards.
         */
        void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId,
                        boolean allWithRootCardPlaced);

        /**
         * Sends the hands and secret objective cards.
         * 
         * @param nickname                         The nickname of the player.
         * @param allWithSecretObjectiveCardChosen A boolean indicating whether all
         *                                         players have chosen their secret
         *                                         objective cards.
         */
        void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname,
                        boolean allWithSecretObjectiveCardChosen);

        /**
         * Sends the placed card. Broadcast to all clients.
         * It should notify the card placed by the player, the position, and the side.
         * 
         * Also with methods from controller you should get the new points and the new
         * resources of the player.
         *
         * @param nickname The nickname of the player.
         * @param cardId   The id of the card.
         * @param position The position of the card.
         * @param side     The side of the card.
         */
        void sendPlacedCard(String nickname, int cardId, Point position, boolean side);

        /**
         * Sends the drawn card. Broadcast to all clients.
         * It should notify the card drawn by the player, the head deck, and whether the
         * card is gold or not.
         *
         * @param nickname      The nickname of the player.
         * @param newCardId     The id of the new card.
         * @param headDeck      The head deck.
         * @param gold          A boolean indicating whether the card is gold.
         * @param onTableOrDeck An integer indicating whether the card is on the table
         *                      or the deck.
         */
        void sendDrawnCard(String nickname, Integer newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck);

        /**
         * Sends the end game signal. Broadcast to all clients.
         * With controller methods, you should get the extra points and the ranking of
         * the players.
         */
        void sendEndGame();

        /**
         * This method is used to disconnect all clients from a specific network
         * interface.
         * It should be implemented by each network interface to handle the
         * disconnection process according to its specific protocol.
         */
        void disconnectAll();

        /**
         * This method is used to notify all connected clients about the current turn.
         * It should be implemented by each network interface to handle the notification
         * process according to its specific protocol.
         */
        void notifyTurn();

        /**
         * This method is used to load a game from a save file.
         * It should be implemented by each network interface to handle the loading
         * process according to its specific protocol.
         *
         * @param game The game to load.
         */
        void loadGame(GameMaster game);
}
