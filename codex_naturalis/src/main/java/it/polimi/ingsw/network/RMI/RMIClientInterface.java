package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameMaster;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Sign;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The RMIClientInterface interface defines the methods that are used for
 * network communication in the game.
 * These methods are called by the ServerRMI class, which is the class that
 * manages the network connections for the server.
 */
public interface RMIClientInterface extends Remote {
    /**
     * This method is used to refresh the players in the lobby.
     *
     * @param playersAndPins The players in the lobby and their colors.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void refreshUsers(HashMap<String, Color> playersAndPins) throws RemoteException;

    /**
     * This method is used to send the common cards to the clients. They are the
     * common cards on the table.
     *
     * @param resourceCards      The resource cards on the table.
     * @param goldCard           The gold cards on the table.
     * @param resourceCardOnDeck The resource card on the deck.
     * @param goldCardOnDeck     The gold card on the deck.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void sendInfoOnTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck,
            Kingdom goldCardOnDeck) throws RemoteException;

    /**
     * This method is used to show the starting card to the clients.
     *
     * @param startingCardId The id of the starting card.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void showStartingCard(int startingCardId) throws RemoteException;

    /**
     * This method is used to send the common objective cards to the clients.
     *
     * @param objectiveCardIds The ids of the common objective cards.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void sendCommonObjectiveCards(Integer[] objectiveCardIds) throws RemoteException;

    /**
     * This method is used to send the secret objective cards to the clients.
     *
     * @param objectiveCardIds The ids of the secret objective cards.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void sendSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) throws RemoteException;

    /**
     * This method is used to show the hand of the player. We send the hand of the
     * player to the client.
     * This is the private hand of the player. It is a unicast call.
     *
     * @param nickname The nickname of the player.
     * @param hand     The hand of the player.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void showHand(String nickname, Integer[] hand) throws RemoteException;

    /**
     * This method is used to show the hidden hand of the player. We send the hidden
     * hand of the player to the client.
     * This is the hidden hand of the player. It is just a pair: kingdom and
     * boolean. The boolean is true if the card is gold, false otherwise.
     *
     * @param nickname The nickname of the player.
     * @param hand     The hidden hand of the player.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void showHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand) throws RemoteException;

    /**
     * This method is used to send TurnInfo to the clients. It contains the current
     * player and the game state.
     *
     * @param currentPlayer The current player.
     * @param gameState     The game state. It is a enum that can be
     *                      CHOOSING_ROOT_CARD, CHOOSING_OBJECTIVE_CARD,
     *                      DRAWING_PHASE, PLACING_PHASE, END
     * @throws RemoteException throws a RemoteException if there is a problem with
     */
    void refreshTurnInfo(String currentPlayer, GameState gameState) throws RemoteException;

    /**
     * This method is used to place a card on the board. It also sends the resources
     * and the points of the player.
     * It is a broadcast call.
     * 
     * @param nickname  The nickname of the player.
     * @param id        The id of the card.
     * @param position  The position of the card.
     * @param side      The side of the card.
     * @param turn      turn of position of the card.
     * @param resources The resources of the player.
     * @param points    The points of the player.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void placeCard(String nickname, int id, Point position, boolean side, int turn, HashMap<Sign, Integer> resources,
            int points) throws RemoteException;

    /**
     * This method is used to communicate the new card on table and the new card on
     * deck. It is a broadcast call.
     *
     * @param newCardId     The id of the new card.
     * @param headDeck      The head deck.
     * @param gold          A boolean indicating whether the card is gold. To know
     *                      which deck to update.
     * @param onTableOrDeck An integer indicating whether the card is on the table
     *                      or the deck.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void moveCard(Integer newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) throws RemoteException;

    /**
     * This method is used to show the end game to the clients. It is a broadcast
     * call.
     *
     * @param extraPoints The extra points of the players.
     * @param ranking     The ranking of the players.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void showEndGame(HashMap<String, Integer> extraPoints, ArrayList<Player> ranking) throws RemoteException;

    /**
     * This method sends the first player information. When this call arrives, the
     * client knows who is the first player and can start the game.
     *
     * @param firstPlayer The first player.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void getIsFirstAndStartGame(String firstPlayer) throws RemoteException;

    /**
     * This method is used to deliver a message to the clients.
     * 
     * @param sender  The sender of the message.
     * @param message The message.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     * @param broadcast A boolean indicating whether the message is a broadcast message.
     */
    void receiveChatMessage(String sender, String message, boolean broadcast) throws RemoteException;

    /**
     * This method sends to all the stopWaiting signal. When this call arrives, the
     * client knows that it can be in CHOOSE COLOR PHASE.
     *
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void stopWaiting() throws RemoteException;

    /**
     * This method is used to disconnect the client from the server.
     *
     * It is called when enough players have joined the lobby and the player cannot
     * continue gaming.
     *
     * @throws RemoteException if there is a problem with the network connection.
     */
    void disconnect() throws RemoteException;

    /**
     * This method is used to stop the game for a client.
     *
     * It is called when the server decides to end the game or when the client
     * decides to leave the game or
     * lose the connection with the server.
     *
     * @throws RemoteException if there is a problem with the network connection.
     */
    void stopGaming() throws RemoteException;

    /**
     * This method is used to check if the client is still connected to the server.
     *
     * It is called periodically by the server to ensure that the client's
     * connection is still active.
     * If the client is not connected, a RemoteException will be thrown.
     *
     * @throws RemoteException if there is a problem with the network connection or
     *                         the client is not connected.
     */
    void isConnected() throws RemoteException;

    /**
     * Loads a saved game.
     * 
     * @param game The game to load.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    void loadSavedGame(GameMaster game) throws RemoteException;
}
