package it.polimi.ingsw.controller.client;

import it.polimi.ingsw.model.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.NoNameException;
import it.polimi.ingsw.network.RMI.ClientRMI;
import it.polimi.ingsw.network.RMI.RMIClientInterface;
import it.polimi.ingsw.network.socket.ClientSocket;
import it.polimi.ingsw.network.NetworkClient;
import it.polimi.ingsw.view.*;
import it.polimi.ingsw.view.model.CardClient;
import it.polimi.ingsw.view.model.LittleModel;
import it.polimi.ingsw.view.model.Phase;
import javafx.util.Pair;
import it.polimi.ingsw.view.gui.GUI;

import java.awt.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static javafx.application.Application.launch;

/**
 * The Controller class is responsible for managing the game flow and
 * interactions between the model and the view.
 * It handles user inputs, updates the model, and triggers the view to update
 * its display.
 * The Controller class also communicates with the server to perform actions.
 *
 * The Controller class uses a singleton pattern for the ViewSubmissions
 * instance to ensure that there is only one instance of it in the application.
 * This singleton instance is used to communicate with the controller through
 * the TUI or GUI.
 *
 * The Controller class also uses a phase attribute to keep track of the current
 * phase of the game.
 * Different phases of the game include LOGIN, WAIT, CHOOSE_SIDE_STARTING_CARD,
 * CHOOSE_SECRET_OBJECTIVE_CARD, and GAMEFLOW.
 *
 * The Controller class uses a NetworkClient object to handle network
 * communications.
 * The NetworkClient object can be either a ClientRMI object or a ClientSocket
 * object, depending on the type of connection chosen by the user.
 *
 * The Controller class uses a ViewInterface object to handle the display of the
 * game.
 * The ViewInterface object can be either a TUI object or a GUI object,
 * depending on the type of view chosen by the user.
 *
 * The Controller class uses a LittleModel object to store all the required
 * information about the game.
 * The LittleModel object is updated by the Controller class and used by the
 * ViewInterface object to update the display.
 */
public class Controller {
    /**
     * The nickname of the player.
     */
    private static String nickname;
    /**
     * The current phase of the game.
     */
    public static Phase phase;
    /**
     * The client RMI interface for network communication.
     */
    private static RMIClientInterface clientRMI;
    /**
     * The network client used for communication with the server.
     */
    private NetworkClient connection;
    /**
     * The view interface used for displaying the game.
     */
    private ViewInterface view;
    /**
     * The little model used for storing all the required information about the
     * game.
     */
    private LittleModel model;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructs a new Controller object and initializes the game model, phase, and
     * ViewSubmissions instance.
     *
     * It sets the current phase of the game to LOGIN, which prompts the user to
     * input their unique player name.
     * It also sets the singleton ViewSubmissions instance to communicate with the
     * controller through the TUI or GUI.
     */
    public Controller() {
        // initialize the model, all the required information will be stored in the
        // model.
        model = new LittleModel();
        // current phase: login, it asks the unique name of the player.
        phase = Phase.LOGIN;
        // set the singleton to communicate with the controller through TUI/ GUI.
        ViewSubmissions.getInstance().setController(this);
    }

    /**
     * Returns the current phase of the game.
     *
     * @return The current phase of the game.
     */
    public static synchronized Phase getPhase() {
        return Controller.phase;
    }

    /**
     * Sets the current phase of the game.
     *
     * @param phase The phase to set as the current phase.
     */
    public static synchronized void setPhase(Phase phase) {
        Controller.phase = phase;
    }

    /**
     * Sets the view for the game based on the type of view specified.
     * It initializes a new LittleModel object and sets the view to either TUI or GUI based on the input.
     * If the type of view is GUI, it also starts a new thread to launch the GUI.
     *
     * @param typeOfView The type of view to be set. It can be either "TUI" or "GUI".
     * @throws InterruptedException if any thread has interrupted the current thread.
     */
    public void setView(String typeOfView) throws InterruptedException {
        model = new LittleModel();
        if (typeOfView.equals("TUI")) {
            this.view = new TUI(model, this);
            ((TUI) view).start();
        } else if (typeOfView.equals("GUI")) {
            new Thread(() -> {
                launch(GUI.class);
            }).start();

            view = GUI.getInstance();
            ((GUI) view).setModel(model);
            if (view != null) {
                System.out.println("GUI started");
            }
        }
    }

    /**
     * Creates an instance of the connection for the game based on the type of connection specified.
     * It initializes a new ClientRMI object or ClientSocket object based on the input.
     * If the type of connection is RMI, it also starts a new thread to launch the RMI connection.
     * If the type of connection is Socket, it also starts a new thread to launch the Socket connection.
     *
     * @param typeOfConnection The type of connection to be set. It can be either "RMI" or "Socket".
     */
    public void createInstanceOfConnection(String typeOfConnection, String ip, Integer port) {
        if (typeOfConnection.equals("RMI")) {
            try {
                clientRMI = new ClientRMI(this, ip, port);
            } catch (RemoteException | NotBoundException e) {
                System.out.println("The server is not ready yet. Please restart the application.");
                System.exit(0);
            }

            connection = (NetworkClient) clientRMI;
        } else if (typeOfConnection.equals("Socket")) {
            ClientSocket socket = null;
            try {
                socket = new ClientSocket(this, ip, port);
                connection = socket;
                new Thread(socket::run).start();
            } catch (IOException e) {
                System.out.println("Not able to connect to server, please try again.");
            }
        }
    }

    /**
     * Sets the nickname of the player.
     * 
     * @param nickname The nickname of the player.
     */
    public static void setNickname(String nickname) {
        Controller.nickname = nickname;
    }

    /**
     * Returns the nickname of the player.
     * 
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Triggers the view to ask the user for the number of players.
     *
     * This method is used to prompt the user to input the number of players that
     * will be participating in the game.
     * The actual input is handled by the view (TUI or GUI).
     */
    public void askNumberOfPlayer() {
        phase = Phase.NUMBER_OF_PLAYERS;
        view.askNumberOfPlayers();
    }

    /**
     * Triggers the view to display a waiting message to the user.
     *
     * This method is used to inform the user that they are waiting for other
     * players to join the game lobby.
     * The actual display of the waiting message is handled by the view (TUI or
     * GUI).
     */
    public void waitLobby() {
        view.waitLobby();
    }

    /**
     * Triggers the view to stop displaying the waiting message.
     *
     * This method is used to inform the user that the waiting phase is over and the
     * game is starting.
     * The actual removal of the waiting message is handled by the view (TUI or
     * GUI).
     */
    public void stopWaiting() {
        view.stopWaiting();
    }

    /**
     * Triggers the view to refresh the display of users.
     *
     * This method is used to update the display of users and their associated
     * colors in the game.
     * The actual display update is handled by the view (TUI or GUI).
     *
     * @param playersAndPins A HashMap where the keys are the nicknames of the
     *                       players and the values are their associated colors.
     */
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        model.updateUsers(playersAndPins);
        view.refreshUsers(playersAndPins);
    }

    /**
     * Updates the cards on the table in the game model and triggers the view to
     * display the common table.
     *
     * This method is called when the cards on the table change in the game.
     * It updates the game model with the new cards on the table and then triggers
     * the view to display the common table.
     *
     * @param resourceCards      An array of integers representing the IDs of the
     *                           resource cards on the table.
     * @param goldCard           An array of integers representing the IDs of the
     *                           gold cards on the table.
     * @param resourceCardOnDeck A Kingdom object representing the resource card on
     *                           the deck.
     * @param goldCardOnDeck     A Kingdom object representing the gold card on the
     *                           deck.
     */
    public void cardsOnTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck,
            Kingdom goldCardOnDeck) {
        model.updateCommonTable(resourceCards, goldCard, resourceCardOnDeck, goldCardOnDeck);
        view.showCommonTable();
    }

    /**
     * Triggers the view to display a chat message to the user.
     *
     * This method is used to inform the user about a chat message in the game.
     * The actual display of the chat message is handled by the view (TUI or GUI).
     *
     * @param sender  The nickname of the player who sent the message.
     * @param message The message sent by the player.
     * @param broadcast A boolean value indicating whether the message is a broadcast message.
     */
    public void receiveChatMessage(String sender, String message, boolean broadcast) {
        view.receiveChatMessage(sender, message, broadcast);
    }

    /**
     * Triggers the view to display the starting card to the user.
     * 
     * This method is used to inform the user about their starting card in the game.
     * The actual display of the starting card is handled by the view (TUI or GUI).
     *
     * @param startingCardId The ID of the starting card.
     */
    public void updateAndShowStartingCard(int startingCardId) {
        Controller.setPhase(Phase.CHOOSE_SIDE_STARTING_CARD);
        view.showStartingCard(startingCardId);
    }

    /**
     * Triggers the view to display the objective cards to the user.
     * 
     * This method is used to inform the user about their objective cards in the
     * game.
     * The actual display of the objective cards is handled by the view (TUI or
     * GUI).
     *
     * @param objectiveCardIds The IDs of the objective cards.
     */
    public void showCommonObjectiveCards(Integer[] objectiveCardIds) {
        model.updateCommonObjectives(objectiveCardIds);
        view.showCommonObjectives(objectiveCardIds);
    }

    /**
     * Triggers the view to display the secret objective card to the user.
     * This method is used to inform the user about their secret objective card in
     * the game.
     * The actual display of the secret objective card is handled by the view (TUI
     * or GUI).
     *
     * @param indexCard The index of the secret objective card.
     */
    public void showSecretObjectiveCard(int indexCard) {
        view.showSecretObjectiveCard(model.getSecretObjectiveCardsToChoose()[indexCard]);
    }

    /**
     * Triggers the view to refresh the turn information.
     *
     * This method is used to update the display of the current player and the game
     * state.
     * The actual display update is handled by the view (TUI or GUI).
     *
     * @param currentPlayer The nickname of the current player.
     * @param gameState     The current state of the game.
     */
    public void turnInfo(String currentPlayer, GameState gameState) {
        view.showTurnInfo(currentPlayer, gameState);
    }

    /**
     * Triggers the view to display the extra points to the user.
     * 
     * This method is used to inform the user about their extra points in the game.
     * The actual display of the extra points is handled by the view (TUI or GUI).
     *
     * @param extraPoints A HashMap where the keys are the nicknames of the players
     *                    and the values are their associated extra points.
     */
    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
        view.showExtraPoints(extraPoints);
    }

    /**
     * Triggers the view to display the ranking to the user.
     * 
     * This method is used to inform the user about the ranking in the game.
     * The actual display of the ranking is handled by the view (TUI or GUI).
     *
     * @param ranking A HashMap where the keys are the nicknames of the players and
     *                the values are their associated scores.
     */
    public void showRanking(ArrayList<Player> ranking) {
        view.showRanking(ranking);
    }

    /**
     * Triggers the view to display the secret objective cards to the user for
     * selection.
     * 
     * This method is used to inform the user about their secret objective cards in
     * the game for selection.
     * The actual display of the secret objective cards is handled by the view (TUI
     * or GUI).
     *
     * @param objectiveCardIds An array of integers representing the IDs of the
     *                         secret objective cards.
     */
    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {
        model.updateSecretObjectiveCardsToChoose(objectiveCardIds);
        view.showSecretObjectiveCardsToChoose(objectiveCardIds);
        Controller.setPhase(Phase.CHOOSE_SECRET_OBJECTIVE_CARD);
    }

    /**
     * Updates the game model with the secret objective card chosen by the player
     * and triggers the view to display the secret objective card.
     * 
     * This method is called when the player chooses a secret objective card in the
     * game.
     * It updates the game model with the chosen secret objective card and then
     * triggers the view to display the secret objective card.
     *
     * @param indexCard The index of the secret objective card chosen by the player.
     */
    public void updateAndShowSecretObjectiveCard(int indexCard) {
        model.updateSecretObjectiveCard(indexCard);
        view.showSecretObjectiveCard(model.getSecretObjectiveCard());
    }

    /**
     * Updates the game model with the information of a card that has been placed by
     * a player.
     * This method is called when a player successfully places a card on the board.
     * It updates the game model with the information of the placed card,
     * including the player's nickname, the card ID, the position of the card, the
     * side of the card, and the turn.
     *
     * @param nickname The nickname of the player who has placed the card.
     * @param id       The id of the card that has been placed.
     * @param position The position where the card has been placed on the board.
     * @param side     The side of the card chosen by the player. True for one side,
     *                 false for the other.
     * @param turn     The turn number when the card was placed.
     */
    public void updatePlaceCard(String nickname, int id, Point position, boolean side, int turn) {
        boolean start = false;
        // if the card is the starting card of the given client, show a message to
        // inform the player.
        if (position.x == 0 && position.y == 0 && nickname.equals(Controller.nickname)) {
            model.updatePlaceCard(nickname, id, position, side, turn);
            view.showStartingCardChosen();
            start = true;
        }
        // else if since for starting card we haven't set the hand yet.
        // set the playedCardToNull, if the cards is correctly placed and is the owner
        // of it.
        else if (nickname.equals(Controller.nickname)) {
            for (int i = 0; i < 3; i++) {
                if (model.getCardInHand(i) == id) {
                    // if is the card placed is the same card in the hand, set the card in hand to
                    // null.
                    model.setCardInHand(i, null);
                    break;
                }
            }
            // notify the scene that the hand has been updated. The scene will update the
            // view.
            view.showHand();
        }
        // we update the model with the card placed, only if it is not the starting card
        // chosen by the player.
        if (!start) {
            model.updatePlaceCard(nickname, id, position, side, turn);
            // notify the scene that a card has been placed. The scene will update the view.
            view.showTableOfPlayer(nickname);
        }
    }

    /**
     * Updates the resources of a player in the game model.
     * 
     * This method is called when a player's resources change in the game.
     * It updates the game model with the new resources of the player.
     *
     * @param nickname  The nickname of the player whose resources have been
     *                  updated.
     * @param resources A HashMap where the keys are the types of resources (Sign)
     *                  and the values are the quantities of each resource
     *                  (Integer).
     */
    public void updateResources(String nickname, HashMap<Sign, Integer> resources) {
        model.updateResources(nickname, resources);
        // notify the scene that the resources have been updated. The scene will update
        // the view.
        view.showResourcesPlayer();
    }

    /**
     * Updates the score of a player in the game model.
     * 
     * This method is called when a player's score changes in the game.
     * It updates the game model with the new score of the player.
     *
     * @param nickname The nickname of the player whose score has been updated.
     * @param points   The new score of the player.
     */
    public void updateScore(String nickname, int points) {
        model.updateScore(nickname, points);
        // notify the scene that the score has been updated. The scene will update the
        // view.
        view.showPoints();
    }

    /**
     * Updates the head of the deck and the card on the table in the game model, and
     * triggers the view to display the common table.
     * 
     * This method is called when the head of the deck or a card on the table
     * changes in the game.
     * It updates the game model with the new head of the deck and the new card on
     * the table, and then triggers the view to display the common table.
     *
     * @param newCardId     The id of the new card that has been placed on the
     *                      table.
     * @param gold          The gold status of the new card. True if it's a gold
     *                      card, false otherwise.
     * @param onTableOrDeck The location of the new card. 0 for on the table, 1 for
     *                      on the deck.
     * @param headDeck      The new head of the deck.
     */
    public void updateAndShowCommonTable(Integer newCardId, boolean gold, int onTableOrDeck, Kingdom headDeck) {
        model.updateHeadDeck(headDeck, gold);
        model.updateCardOnTable(newCardId, gold, onTableOrDeck);
        // notify the scene that the table has been updated. The scene will update the
        // view.
        view.showCommonTable();
    }

    /**
     * Updates the hand of a player in the game model.
     * 
     * This method is called when a player's hand changes in the game.
     * It updates the game model with the new hand of the player.
     *
     * @param hand An array of integers representing the IDs of the cards in the
     *             player's hand.
     */
    public void updateHand(Integer[] hand) {
        model.updatePrivateHand(hand);
        // notify the scene that the hand has been updated. The scene will update the
        // view.
        view.showHand();
    }

    /**
     * Updates the hidden hand of a player in the game model.
     * 
     * This method is called when a player's hidden hand changes in the game.
     * It updates the game model with the new hidden hand of the player.
     *
     * @param nickname The nickname of the player whose hidden hand has been
     *                 updated.
     * @param hand     An array of Pair objects, each containing a Kingdom and a
     *                 Boolean, representing the cards in the player's hidden hand.
     */
    public void updateHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand) {
        model.updateHiddenHand(nickname, hand);
        // notify the scene that the hidden hand has been updated. The scene will update
        // the view.
        view.showHiddenHand(nickname);
    }

    /**
     * Triggers the view to display the first player in the game.
     * 
     * This method is used to inform the user about the first player in the game.
     * The actual display of the first player is handled by the view (TUI or GUI).
     *
     * @param firstPlayer The nickname of the player who is first in the game.
     */
    public void showIsFirst(String firstPlayer) {
        Controller.phase = Phase.GAME_FLOW;
        view.showIsFirst(firstPlayer);
    }

    /**
     * Logs in the player to the game.
     * 
     * This method is used to log in the player to the game by using the connection
     * object.
     * The actual login is handled by the connection object.
     *
     * @param nickname The nickname of the player.
     */
    public void login(String nickname) {
        Controller.phase = Phase.WAIT;
        connection.login(nickname);
    }

    /**
     * Sets the number of players in the game.
     * 
     * This method is used to set the number of players that will be participating
     * in the game.
     * The actual setting of the number of players is handled by the connection
     * object.
     *
     * @param numberOfPlayers The number of players.
     */
    public void insertNumberOfPlayers(int numberOfPlayers) {
        Controller.phase = Phase.WAIT;
        connection.insertNumberOfPlayers(numberOfPlayers);
    }

    /**
     * Sets the color of the player in the game.
     * 
     * This method is used to set the color of the player in the game.
     * The actual setting of the color is handled by the connection object.
     *
     * @param color The color chosen by the player.
     */
    public void chooseColor(Color color) {
        Controller.phase = Phase.WAIT;
        connection.chooseColor(color);
    }

    /**
     * Sends a chat message in the game.
     *
     * This method is used to send a chat message in the game.
     * The actual sending of the chat message is handled by the connection object.
     *
     * @param message The message to be sent.
     */
    public void sendChatMessage(String message) {
        connection.sendChatMessage(nickname, message);
    }

    /**
     * Sets the side of the starting card for the player in the game.
     * 
     * This method is used to set the side of the starting card for the player in
     * the game.
     * The actual setting of the side is handled by the connection object.
     *
     * @param side The side chosen by the player.
     */
    public void chooseSideStartingCard(boolean side) {
        connection.chooseSideStartingCard(side);
    }

    /**
     * Sets the secret objective card for the player in the game.
     * 
     * This method is used to set the secret objective card for the player in the
     * game.
     * The actual setting of the card is handled by the connection object.
     *
     * @param indexCard The index of the secret objective card chosen by the player.
     */
    public void chooseSecretObjectiveCard(int indexCard) {
        connection.chooseSecretObjectiveCard(indexCard);
    }

    /**
     * Plays a card in the game.
     * 
     * This method is used to play a card in the game.
     * The actual playing of the card is handled by the connection object.
     *
     * @param indexHand The index of the card in the player's hand.
     * @param position  The position where the card will be placed.
     * @param side      The side of the card.
     */
    public void playCard(int indexHand, Point position, boolean side) {
        connection.playCard(indexHand, position, side);
    }

    /**
     * Draws a card in the game.
     * 
     * This method is used to draw a card in the game.
     * The actual drawing of the card is handled by the connection object.
     *
     * @param gold          The gold status of the card.
     * @param onTableOrDeck The location from where the card is drawn.
     */
    public void drawCard(boolean gold, int onTableOrDeck) {
        connection.drawCard(nickname, gold, onTableOrDeck);
    }

    /**
     * Notifies the view that the number of players is correct.
     * 
     * This method is used to inform the view that the number of players entered by
     * the user is correct.
     *
     * @param numberOfPlayers The number of players.
     */
    public void correctNumberOfPlayers(int numberOfPlayers) {
        view.correctNumberOfPlayers(numberOfPlayers);
    }

    /**
     * Notifies the view that the game is in the wrong phase.
     * 
     * This method is used to inform the view that the game is currently in a phase
     * where the requested action cannot be performed.
     */
    public void wrongPhase() {
        view.wrongGamePhase();
    }

    /**
     * Notifies the view that it is not the player's turn.
     * 
     * This method is used to inform the view that the player cannot perform the
     * requested action because it is not their turn.
     */
    public void noTurn() {
        view.noTurn();
    }

    /**
     * Notifies the view that there is no player with the given name.
     * 
     * This method is used to inform the view that the player name entered by the
     * user does not exist in the game.
     */
    public void noName() {
        view.noPlayer();
    }

    /**
     * Notifies the view that the player does not have enough resources.
     * 
     * This method is used to inform the view that the player cannot perform the
     * requested action because they do not have enough resources.
     */
    public void notEnoughResources() {
        view.notEnoughResources();
    }

    /**
     * Notifies the view that the card cannot be placed at the given position.
     * 
     * This method is used to inform the view that the player cannot place a card at
     * the requested position on the board.
     */
    public void NoName() {
        view.noPlayer();
    }

    /**
     * Notifies the view that the card cannot be placed at the given position.
     * 
     * This method is used to inform the view that the player cannot place a card at
     * the requested position on the board.
     *
     * @param name The name of the player.
     */
    public void sameName(String name) {
        view.sameName(name);
    }

    /**
     * Notifies the view that the color chosen by the player is already taken.
     * 
     * This method is used to inform the view that the color chosen by the player is
     * already taken by another player in the game.
     */
    public void colorAlreadyTaken() {
        view.colorAlreadyTaken();
    }

    /**
     * Notifies the view that the card cannot be placed at the given position.
     * 
     * This method is used to inform the view that the player cannot place a card at
     * the requested position on the board.
     */
    public void cardPositionError() {
        view.cardPositionError();
    }

    /**
     * Notifies the view that the lobby cannot be closed.
     * 
     * This method is used to inform the view that the game lobby cannot be closed
     * because there are still players in the lobby.
     */
    public void closingLobbyError() {
        view.closingLobbyError();
    }

    /**
     * Notifies the view that the lobby is complete.
     * 
     * This method is used to inform the view that the game lobby is complete and
     * the game can start.
     */
    public void lobbyComplete() {
        view.lobbyComplete();
    }

    /**
     * Schedules a task to exit the application after a delay.
     * 
     * This method creates a new Timer instance and schedules a TimerTask to be
     * executed after a delay.
     * The TimerTask is an anonymous class that overrides the run method to call
     * System.exit(0), effectively terminating the application.
     * The delay is currently set to 10 seconds (10000 milliseconds).
     */
    private void exitDelay() {
        scheduler.schedule(() -> {
            System.exit(0);
        }, 10, TimeUnit.SECONDS); // Delay of 10 seconds.

    }

    /**
     * Triggers the view to display the end of the game, since a player has been
     * disconnected.
     */
    public void stopGaming() {
        view.stopGaming();
        exitDelay();
    }

    /**
     * Notifies the view that there is no connection.
     * This method is used to inform the view that the connection to the server has
     * been lost.
     * After 10 seconds, the application will be closed.
     */
    public void noConnection() {
        view.noConnection();

        exitDelay();
    }

    /**
     * Triggers the view to handle the disconnection of the user.
     * 
     * This method is used to inform the user that they have been disconnected from
     * the game.
     */
    public void disconnect() {
        view.disconnect();
        exitDelay();
    }

    /**
     * This method returns the NetworkClient object used for communication with the
     * server.
     *
     * @return the NetworkClient object used for communication with the server.
     */
    public NetworkClient getConnection() {
        return connection;
    }

    /**
     * Sets the model for the game. And triggers the view to display.
     * 
     * @param game The GameMaster object containing all the information about the
     *             game.
     */
    public synchronized void setModel(GameMaster game) {
        HashMap<String, Integer> points = new HashMap<>();
        HashMap<String, HashMap<Sign, Integer>> resources = new HashMap<>();
        Integer[] myCards = new Integer[3];
        Integer[] resourceCardsOnTable = new Integer[2];
        Integer[] goldCardsOnTable = new Integer[2];
        Integer[] commonObjectiveCards = new Integer[2];
        HashMap<String, Pair<Kingdom, Boolean>[]> otherPlayersCards = new HashMap<>();
        Integer[] secretObjectiveCardsToChoose = new Integer[2];

        for (Player player : game.getLobby().getPlayers()) {
            points.put(player.getName(), player.getPoints());
            resources.put(player.getName(), player.getResources());

            if (player.getName().equals(nickname)) {
                // update personal cards
                ResourceCard[] hand = player.getHand();
                for (int i = 0; i < 3; i++) {
                    try {
                        myCards[i] = hand[i].getId();
                    } catch (Exception e) {
                        myCards[i] = null;
                    }
                }
            } else {
                otherPlayersCards.put(player.getName(), new Pair[3]);
                Pair<Kingdom, Boolean>[] cards = otherPlayersCards.get(player.getName());
                for (int i = 0; i < 3; i++) {
                    try {
                        cards[i] = new Pair<>(player.getHand()[i].getKingdom(), player.getHand()[i] instanceof GoldCard);
                    } catch (Exception e) {
                        cards[i] = new Pair<>(null, false);
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            try {
                resourceCardsOnTable[i] = game.getResourceCard(i).getId();
            } catch (Exception e) {
                resourceCardsOnTable[i] = null;
            }
            try {
                goldCardsOnTable[i] = game.getGoldCard(i).getId();
            } catch (Exception e) {
                goldCardsOnTable[i] = null;
            }
            commonObjectiveCards[i] = game.getObjectiveCard(i).getId();
        }

        int playerPosition;

        try {
            playerPosition = game.getOrderPlayer(nickname);
        } catch (NoNameException e) {
            System.out.println("No player with this name");
            playerPosition = -1;
        }
        // secret objective cards to choose
        for (int i = 0; i < 2; i++) {
            secretObjectiveCardsToChoose[i] = game.getObjectiveCardToChoose(playerPosition, i).getId();
        }

        Player player = null;
        try {
            player = game.getLobby().getPlayerFromName(nickname);
        } catch (NoNameException e) {
            System.out.println("No player with this name");
        }

        HashMap<String, CardClient> table = new HashMap<>();

        model = new LittleModel(points, resources, myCards, otherPlayersCards, table , resourceCardsOnTable,
                goldCardsOnTable,
                game.getHeadDeck(true),
                game.getHeadDeck(false), secretObjectiveCardsToChoose, commonObjectiveCards,
                player.getSecretObjective().getId());

        for(int i  = 0; i < game.getLobby().getPlayers().length; i++){
            model.updatePlaceCard(game.getLobby().getPlayers()[i].getName(),
                    game.getLobby().getPlayers()[i].getRootCard().getCard().getId(), new Point(0, 0),  game.getLobby().getPlayers()[i].getRootCard().isFacingUp(), 0);
        }

        buildView(game);
    }

    /**
     * Builds the view for the game.
     *
     * This method is used to build the view for the game.
     * The view will display the hands, points, resources, table, and other
     * information about the game.
     *
     * @param game The GameMaster object containing all the information about the
     *             game.
     */
    private void buildView(GameMaster game) {
        if (view instanceof TUI) {
            ((TUI) view).setModel(model);

            view.refreshUsers(game.getLobby().getPlayersAndPins());
            // print all hands
            view.showHand();
            for (String nickname : model.getOtherPlayersCards().keySet()) {
                view.showHiddenHand(nickname);
            }
            // print points and resources
            view.showPoints();
            view.showResourcesPlayer();
            // print table
            view.showCommonTable();

            view.showCommonObjectives(model.getCommonObjectiveCards());
            view.showSecretObjectiveCard(model.getSecretObjectiveCard());

            for (Player player : game.getLobby().getPlayers()) {
                ArrayList<PlayedCard> playedCards = game.getPlayersCards(player);

                Collections.sort(playedCards,
                        (card1, card2) -> Integer.compare(card1.getTurnOfPositioning(), card2.getTurnOfPositioning()));
                for (PlayedCard playedCard : playedCards) {
                    if(!(playedCard.getPosition().x == 0 && playedCard.getPosition().y == 0)){
                        model.updatePlaceCard(player.getName(), playedCard.getCard().getId(), playedCard.getPosition(),
                                playedCard.isFacingUp(), playedCard.getTurnOfPositioning());
                    }
                }
                view.showTableOfPlayer(player.getName());
            }
            Controller.setPhase(Phase.GAME_FLOW);

            view.showTurnInfo(game.getCurrentPlayer().getName(), game.getGameState());
        } else {
            try {
                view = GUI.getInstance();
            } catch (InterruptedException e) {
                System.out.println("GUI not ready yet");
            }
            // change the model for GUI
            ((GUI) view).setModel(model);

            //mandatory order code for setup
            view.refreshUsers(game.getLobby().getPlayersAndPins());
            view.showCommonTable();

            // show all hands
            view.showHand();
            for (String nickname : model.getOtherPlayersCards().keySet()) {
                view.showHiddenHand(nickname);
            }
            // maybe not necessary
            view.showResourcesPlayer();
            view.showPoints();

            view.showCommonObjectives(model.getCommonObjectiveCards());
            // view.showSecretObjectiveCardsToChoose(model.getSecretObjectiveCardsToChoose());
            view.showSecretObjectiveCard(model.getSecretObjectiveCard());


            for (Player player : game.getLobby().getPlayers()) {
                ArrayList<PlayedCard> playedCards = game.getPlayersCards(player);
                Collections.sort(playedCards,
                        (card1, card2) -> Integer.compare(card1.getTurnOfPositioning(), card2.getTurnOfPositioning()));

                for (PlayedCard playedCard : playedCards) {
                    if(!(playedCard.getPosition().x == 0 && playedCard.getPosition().y == 0)){
                        model.updatePlaceCard(player.getName(), playedCard.getCard().getId(), playedCard.getPosition(),
                                playedCard.isFacingUp(), playedCard.getTurnOfPositioning());

                    }
                    HashMap<Corner, CardClient> hashOfCards = new HashMap<Corner, CardClient>();
                    for(Corner corner: Corner.values()){
                        hashOfCards.put(corner, null);
                    }

                    CardClient card = new CardClient(playedCard.getCard().getId(), playedCard.isFacingUp(),
                            playedCard.getPosition(), playedCard.getTurnOfPositioning(), hashOfCards);
                    ((GUI) view).rebuildBoard(player.getName(), card);

                }
            }

            view.showCommonTable();
            view.showTurnInfo(game.getCurrentPlayer().getName(), game.getGameState());

        }
    }
}
