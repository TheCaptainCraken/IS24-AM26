package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The RMIServerInterface interface defines the methods that are used for
 * network communication in the game.
 * These methods are called by the RMIServer class, which is the class that
 * manages the network connections for client.
 */
public interface RMIServerInterface extends Remote {

    /**
     * This method is used to log in a client and check if it is the first player to
     * login.
     * 
     * @param clientRMI The client that is logging in.
     * @param nickname  The nickname of the client that is logging in.
     * @return True if the client is the first to login, false otherwise.
     * @throws RemoteException        throws a RemoteException if there is a problem
     *                                with the connection.
     * @throws SameNameException      throws a SameNameException if the nickname is
     *                                already taken.
     * @throws LobbyCompleteException throws a LobbyCompleteException if the lobby
     *                                is already full or the game has already
     *                                started.
     * @throws NoNameException        throws a NoNameException if the nickname is
     */
    boolean loginAndIsFirst(RMIClientInterface clientRMI, String nickname)
            throws RemoteException, SameNameException, LobbyCompleteException, NoNameException;

    /**
     * This method is used to check if the lobby is ready to start the game.
     * 
     * @return True if the lobby is ready to start the game, false otherwise.
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    boolean lobbyIsReady() throws RemoteException;

    /**
     * This method is used to insert the number of players in the game.
     * 
     * @param numberOfPlayers The number of players in the game. It must be between
     *                        2 and 4. Client interface will check this.
     * @throws RemoteException        throws a RemoteException if there is a problem
     *                                with the connection.
     * @throws ClosingLobbyException  throws a ClosingLobbyException if the lobby is
     *                                closing.
     * @throws SameNameException      throws a SameNameException if two players have
     *                                the same nickname.
     * @throws LobbyCompleteException throws a LobbyCompleteException if the lobby
     *                                is already full or the game has already
     *                                started.
     * @throws NoNameException        throws a NoNameException if a player with the
     *                                given nickname does not exist.
     */
    void insertNumberOfPlayers(int numberOfPlayers)
            throws RemoteException, ClosingLobbyException, SameNameException, LobbyCompleteException, NoNameException;

    /**
     * This method is used to choose the color of the player.
     * 
     * @param nickname The nickname of the player.
     * @param color    The color chosen by the player.
     * @throws RemoteException            throws a RemoteException if there is a
     *                                    problem with the connection.
     * @throws ColorAlreadyTakenException throws a ColorAlreadyTakenException if the
     *                                    color is already taken by another player.
     * @throws NoNameException            throws a NoNameException if a player with
     *                                    the given nickname does not exist.
     */
    void chooseColor(String nickname, Color color) throws RemoteException, ColorAlreadyTakenException, NoNameException;

    /**
     * This method is used to send a chat message to the other players.
     *
     * @param message The message sent by the player.
     * @param sender  The nickname of the player who sent the message.
     * @throws RemoteException throws a RemoteException if there is a problem with the
     *                         connection.
     */
    void sendChatMessage(String message, String sender) throws RemoteException;

    /**
     * This method is used to choose the starting card of the player.
     * 
     * @param nickname The nickname of the player.
     * @param side     The side of the card chosen by the player.
     * @throws RemoteException         throws a RemoteException if there is a
     *                                 problem with the connection.
     * @throws WrongGamePhaseException throws a WrongGamePhaseException if the
     *                                 player is trying to choose the starting card
     *                                 in the wrong game phase.
     * @throws NoTurnException         throws a NoTurnException if the player is
     *                                 trying to choose the starting card when it is
     *                                 not his turn.
     * @throws NoNameException         throws a NoNameException if a player with the
     *                                 given nickname does not exist.
     */
    void chooseSideStartingCard(String nickname, boolean side)
            throws RemoteException, WrongGamePhaseException, NoTurnException, NoNameException;

    /**
     * This method is used to choose the secret objective card of the player.
     * 
     * @param nickname  The nickname of the player.
     * @param indexCard The index of the secret objective card chosen by the player.
     * @throws RemoteException         throws a RemoteException if there is a
     *                                 problem with the connection.
     * @throws WrongGamePhaseException throws a WrongGamePhaseException if the
     *                                 player is trying to choose the secret
     *                                 objective card in the wrong game phase.
     * @throws NoTurnException         throws a NoTurnException if the player is
     *                                 trying to choose the secret objective card
     *                                 when it is not his turn.
     * @throws NoNameException         throws a NoNameException if a player with the
     *                                 given nickname does not exist.
     */
    void chooseSecretObjectiveCard(String nickname, int indexCard)
            throws RemoteException, WrongGamePhaseException, NoTurnException, NoNameException;

    /**
     * This method is used to place a card on the player's table.
     * 
     * @param nickname  The nickname of the player.
     * @param indexHand The index of the card in the player's hand.
     * @param position  The position where the card will be placed.
     * @param side      The side of the card that will be placed.
     * @throws RemoteException             throws a RemoteException if there is a
     *                                     problem with the connection.
     * @throws WrongGamePhaseException     throws a WrongGamePhaseException if the
     *                                     player is trying to place a card in the
     *                                     wrong game phase.
     * @throws NoTurnException             throws a NoTurnException if the player is
     *                                     trying to place a card when it is not his
     *                                     turn.
     * @throws NotEnoughResourcesException throws a NotEnoughResourcesException if
     *                                     the player does not have enough resources
     *                                     to place the card.
     * @throws NoNameException             throws a NoNameException if a player with
     *                                     the given nickname does not exist.
     * @throws CardPositionException       throws a CardPositionException if the
     *                                     player is trying to place a card in an
     *                                     invalid position.
     */
    void placeCard(String nickname, int indexHand, Point position, boolean side)
            throws RemoteException, WrongGamePhaseException, NoTurnException, NotEnoughResourcesException,
            NoNameException, CardPositionException;

    /**
     * This method is used to draw a card from the deck.
     * 
     * @param nickname      The nickname of the player.
     * @param gold          A boolean indicating whether the card is a gold card.
     * @param onTableOrDeck An integer indicating whether the card is on the table
     *                      or deck. It is -1, to pick from deck, 0 or 1 to pick
     *                      from table.
     * @return The id of the card drawn by the player.
     * @throws RemoteException         throws a RemoteException if there is a
     *                                 problem with the connection.
     * @throws WrongGamePhaseException throws a WrongGamePhaseException if the
     *                                 player is trying to draw a card in the wrong
     *                                 game phase.
     * @throws NoTurnException         throws a NoTurnException if the player is
     *                                 trying to draw a card when it is not his
     *                                 turn.
     * @throws NoNameException         throws a NoNameException if a player with the
     *                                 given nickname does not exist.
     * @throws CardPositionException   throws a CardPositionException if the player
     */
    Integer[] drawCard(String nickname, boolean gold, int onTableOrDeck)
            throws RemoteException, WrongGamePhaseException, NoTurnException, NoNameException, CardPositionException;

    /**
     * This method is used to know if a client is connected to the server.
     * The client will call this method to check if the connection is still active.
     *
     * @throws RemoteException throws a RemoteException if there is a problem with the connection.
     */
    void connectToServer() throws RemoteException;

}
