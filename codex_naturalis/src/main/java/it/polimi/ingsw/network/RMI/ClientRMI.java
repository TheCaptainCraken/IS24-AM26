package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.NetworkClient;
import it.polimi.ingsw.view.model.Phase;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The ClientRMI class implements the RMIClientInterface and NetworkClient
 * interfaces and
 * provides the functionality for a client to communicate with a server over RMI
 * connection.
 * It defines methods for game actions such as login, choosing color, drawing
 * cards etc., and sends these actions to the server.
 * It also handles responses from the server and updates the client's view
 * accordingly.
 *
 * @author Daniel
 */
public class ClientRMI implements RMIClientInterface, NetworkClient {
    /**
     * The controller that handles the client's view.
     */
    Controller controller;
    /**
     * The exported client object.
     */
    RMIClientInterface exportedClient;
    /**
     * The remote server interface.
     */
    RMIServerInterface stub;
    /**
     * The RMI registry.
     */
    Registry registry;
    /**
     * The scheduler for checking the connection.
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /**
     * The lock object for synchronization.
     */
    private final Object lock = new Object();

    public boolean loadGame;

    /**
     * Constructor for ClientRMI.
     * 
     * @param controller The controller that handles the client's view.
     * @throws RemoteException   if there is a problem with the connection.
     * @throws NotBoundException if the server is not bound.
     */
    public ClientRMI(Controller controller, String ip, Integer port) throws RemoteException, NotBoundException {
        this.controller = controller;
        this.loadGame = false;

        // Exporting the ClientRMI object as a remote object
        exportedClient = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);

        // Creating the RMI register
        registry = LocateRegistry.getRegistry(ip, port);

        // Looking up the remote object
        stub = (RMIServerInterface) registry.lookup("Loggable");

        // periodically check if the client is still connected to the server
        new Thread(this::isClientConnectedToServer).start();
    }

    /**
     * NetworkClient interface methods
     * Logs in the player with the given name.
     * This method is used to authenticate a player in the game.
     * If the player is the first to log in, the game will ask for the number of
     * players.
     * If the player is not the first to log in, the game will wait for the lobby to
     * be ready.
     *
     * @param name The name of the player.
     */
    public void login(String name) {
        boolean isFirst = false;
        boolean login = false;
        try {
            // call remote method loginAndIsFirst, return if is the first layer and have to
            // choose the number of players.
            Controller.setNickname(name);
            isFirst = stub.loginAndIsFirst(exportedClient, name);
            login = true;
        } catch (RemoteException e) {
            controller.noConnection();
        } catch (LobbyCompleteException e) {
            controller.lobbyComplete();
        } catch (SameNameException e) {
            Controller.setNickname(null);
            Controller.phase = Phase.LOGIN;
            controller.sameName(name);
        } catch (NoNameException e) {
            Controller.phase = Phase.LOGIN;
            controller.noName();
        }

        if (login && !loadGame) {
            if (isFirst) {
                // if first player, set FSM to NUMBER_OF_PLAYERS and ask for number of players
                Controller.setPhase(Phase.NUMBER_OF_PLAYERS);
                controller.askNumberOfPlayer();
            } else {
                try {
                    if (stub.lobbyIsReady()) {
                        // if lobby is ready, set FSM to COLOR and ask for color
                        Controller.setPhase(Phase.COLOR);
                    } else {
                        // if lobby is not ready, set FSM to WAIT_NUMBER_OF_PLAYERS and wait for lobby
                        Controller.setPhase(Phase.WAIT);
                        controller.waitLobby();
                    }
                } catch (RemoteException e) {
                    controller.noConnection();
                }
            }
        }else if(loadGame){
            Controller.setPhase(Phase.GAME_FLOW);
        }
    }

    /**
     * NetworkClient interface methods
     *
     * Inserts the number of players in the game.
     * This method is used to set the number of players that will participate in the
     * game.
     *
     * @param numberOfPlayers The number of players.
     */
    public void insertNumberOfPlayers(int numberOfPlayers) {
        try {
            // input always correct because it is checked in the view
            Controller.phase = Phase.WAIT;
            // call remote method insertNumberOfPlayers
            stub.insertNumberOfPlayers(numberOfPlayers);
            // just show the number of players chosen
            controller.correctNumberOfPlayers(numberOfPlayers);
        } catch (RemoteException e) {
            controller.noConnection();
        } catch (ClosingLobbyException e) {
            controller.closingLobbyError();
        } catch (SameNameException e) {
            controller.sameName(controller.getNickname());
        } catch (LobbyCompleteException e) {
            controller.lobbyComplete();
        } catch (NoNameException e) {
            controller.noName();
        }
    }

    /**
     * NetworkClient interface methods
     *
     * Chooses the color for the player.
     * This method is used to set the color of the player in the game.
     *
     * @param color The color chosen by the player.
     */
    public void chooseColor(Color color) {
        try {
            // wait that all players have chosen their color
            Controller.phase = Phase.WAIT;
            // call remote method chooseColor
            stub.chooseColor(controller.getNickname(), color);
        } catch (RemoteException e) {
            controller.noConnection();
        } catch (ColorAlreadyTakenException e) {
            // if the color is already taken, set FSM to COLOR and ask for color
            Controller.phase = Phase.COLOR;
            // show the error message
            controller.colorAlreadyTaken();
        } catch (NoNameException e) {
            controller.noName();
        }
    }

    /**
     * NetworkClient interface methods
     *
     * Sends a chat message.
     * This method is used to send a chat message to the other players in the game.
     *
     * @param nickname The nickname of the player who sent the message.
     * @param message  The message sent by the player and the information about the
     *                 receiver.
     */
    public void sendChatMessage(String nickname, String message) {
        try {
            stub.sendChatMessage(nickname, message);
        } catch (RemoteException e) {
            controller.noConnection();
        }
    }

    /**
     * NetworkClient interface methods
     *
     * Chooses the side of the starting card for the player.
     * This method is used to set the side of the starting card of the player in the
     * game.
     *
     * @param side The side of the starting card chosen by the player.
     */
    public void chooseSideStartingCard(boolean side) {
        try {
            // wait that all players have chosen their side
            Controller.phase = Phase.WAIT;
            // call remote method chooseSideStartingCard
            stub.chooseSideStartingCard(controller.getNickname(), side);
        } catch (RemoteException e) {
            controller.noConnection();
        } catch (WrongGamePhaseException e) {
            // if the game phase is wrong, set FSM to CHOOSE_SIDE_STARTING_CARD and show the
            // error message
            Controller.phase = Phase.CHOOSE_SIDE_STARTING_CARD;
            controller.wrongPhase();
        } catch (NoTurnException e) {
            // if it is not the player's turn, set FSM to CHOOSE_SIDE_STARTING_CARD and show
            // the error message
            Controller.phase = Phase.CHOOSE_SIDE_STARTING_CARD;
            controller.noTurn();
        } catch (NoNameException e) {
            controller.noName();
        }
    }

    /**
     * NetworkClient interface methods
     *
     * Chooses the secret objective card for the player.
     * This method is used to set the secret objective card of the player in the
     * game.
     * It is typically called at the start of the game setup.
     *
     * @param indexCard The index of the secret objective card chosen by the player.
     */
    public void chooseSecretObjectiveCard(int indexCard) {
        try {
            // wait that all players have chosen their secret objective card
            Controller.phase = Phase.WAIT;
            // call remote method chooseSecretObjectiveCard
            stub.chooseSecretObjectiveCard(controller.getNickname(), indexCard);
            // just show the secret objective card chosen
            controller.updateAndShowSecretObjectiveCard(indexCard);
        } catch (RemoteException e) {
            controller.noConnection();
        } catch (WrongGamePhaseException e) {
            // if the game phase is wrong, set FSM to CHOOSE_SECRET_OBJECTIVE_CARD and show
            // the error message
            Controller.phase = Phase.CHOOSE_SECRET_OBJECTIVE_CARD;
            controller.wrongPhase();
        } catch (NoTurnException e) {
            // if it is not the player's turn, set FSM to CHOOSE_SECRET_OBJECTIVE_CARD and
            // show the error message
            Controller.phase = Phase.CHOOSE_SECRET_OBJECTIVE_CARD;
            controller.noTurn();
        } catch (NoNameException e) {
            controller.noName();
        }
    }

    /**
     * NetworkClient interface methods
     *
     * Plays a card from the player's hand.
     * This method is used to play a card from the player's hand onto the game
     * table.
     * It takes three parameters: the index of the card in the player's hand, the
     * position on the table where the card will be placed, and the side of the
     * card.
     *
     * @param indexHand The index of the card in the player's hand.
     * @param position  The position on the table where the card will be placed.
     * @param side      The side of the card.
     */
    public void playCard(int indexHand, Point position, boolean side) {
        try {
            stub.placeCard(controller.getNickname(), indexHand, position, side);
        } catch (RemoteException e) {
            controller.noConnection();
        } catch (WrongGamePhaseException e) {
            controller.wrongPhase();
        } catch (NoTurnException e) {
            controller.noTurn();
        } catch (NotEnoughResourcesException e) {
            controller.notEnoughResources();
        } catch (NoNameException e) {
            controller.NoName();
        } catch (CardPositionException e) {
            controller.cardPositionError();
        }
    }

    /**
     * NetworkClient interface methods
     *
     * Draws a card for the player.
     * This method is used to draw a card for the player.
     * It takes three parameters: the nickname of the player,
     * a boolean indicating whether the card is gold,
     * and an integer indicating whether the card is drawn from the table or the
     * deck(-1 deck, o or 1 for table).
     *
     * @param nickname      The nickname of the player.
     * @param gold          A boolean indicating whether the card is gold.
     * @param onTableOrDeck An integer indicating whether the card is drawn from the
     *                      table or the deck.
     */
    public void drawCard(String nickname, boolean gold, int onTableOrDeck) {
        Integer[] newHand;
        try {
            newHand = stub.drawCard(nickname, gold, onTableOrDeck);
            controller.updateHand(newHand);
        } catch (RemoteException e) {
            controller.noConnection();
        } catch (WrongGamePhaseException e) {
            controller.wrongPhase();
        } catch (NoTurnException e) {
            controller.noTurn();
        } catch (NoNameException e) {
            controller.noName();
        } catch (CardPositionException e) {
            controller.cardPositionError();
        }
    }

    // RMIClientInterface methods. These methods are called by the server to update
    // the client's view.
    /**
     * Stops the waiting phase for the client.
     * This method is used to transition the client from a waiting phase to the
     * color selection phase.
     */
    @Override
    public void stopWaiting() {
        Controller.setPhase(Phase.COLOR);
        controller.stopWaiting();
    }

    @Override
    public void disconnect() {
        controller.disconnect();
    }

    /**
     * Refreshes the list of users in the game.
     * This method is used to update the list of players and their corresponding
     * colors.
     *
     * @param playersAndPins A HashMap containing the player names as keys and their
     *                       corresponding colors as values.
     */
    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        controller.refreshUsers(playersAndPins);
    }

    /**
     * Sends information about the cards on the table to the client.
     * This method is used to update the client's view of the cards on the table.
     *
     * @param resourceCards      An array of resource card IDs.
     * @param goldCard           An array of gold card IDs.
     * @param resourceCardOnDeck The resource card on the deck.
     * @param goldCardOnDeck     The gold card on the deck.
     */
    @Override
    public void sendInfoOnTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck,
            Kingdom goldCardOnDeck) {
        // no need tho change the phase here. We do it in controller.showStartingCard
        controller.cardsOnTable(resourceCards, goldCard, resourceCardOnDeck, goldCardOnDeck);
    }

    /**
     * Receives a chat message from the server.
     * This method is used to receive a chat message from the server and display it
     * to the client.
     *
     * @param sender  The nickname of the player who sent the message.
     * @param message The message sent by the player.
     */
    @Override
    public void receiveChatMessage(String sender, String message, boolean broadcast) {
        controller.receiveChatMessage(sender, message, broadcast);
    }

    /**
     * Shows the starting card to the client.
     * This method is used to update the client's view of the starting card.
     *
     * @param startingCardId The ID of the starting card.
     */
    @Override
    public void showStartingCard(int startingCardId) {
        controller.updateAndShowStartingCard(startingCardId);
    }

    /**
     * Sends the common objective cards to the client.
     * This method is used to update the client's view of the common objective
     * cards.
     *
     * @param objectiveCardIds An array of objective card IDs.
     */
    @Override
    public void sendCommonObjectiveCards(Integer[] objectiveCardIds) {
        controller.showCommonObjectiveCards(objectiveCardIds);
    }

    /**
     * Sends the secret objective cards to the client for selection.
     * This method is used to update the client's view of the secret objective cards
     * for selection.
     *
     * @param objectiveCardIds An array of secret objective card IDs.
     */
    @Override
    public void sendSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {
        controller.showSecretObjectiveCardsToChoose(objectiveCardIds);
    }

    /**
     * Shows the player's hand to the client.
     * This method is used to update the client's view of the player's hand.
     *
     * @param nickname The nickname of the player.
     * @param hand     An array of card IDs in the player's hand.
     */
    @Override
    public void showHand(String nickname, Integer[] hand) {
        controller.updateHand(hand);
    }

    /**
     * Shows the player's hidden hand to the client.
     * This method is used to update the client's view of the player's hidden hand.
     *
     * @param nickname The nickname of the player.
     * @param hand     An array of Pairs, each containing a Kingdom and a Boolean
     *                 indicating the side of the card.
     */
    @Override
    public void showHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand) {
        controller.updateHiddenHand(nickname, hand);
    }

    /**
     * Refreshes the turn information for the client.
     * This method is used to update the client's view of the current player and the
     * game state.
     *
     * @param currentPlayer The nickname of the current player.
     * @param gameState     The current state of the game.
     */
    @Override
    public void refreshTurnInfo(String currentPlayer, GameState gameState) {
        controller.turnInfo(currentPlayer, gameState);
    }

    /**
     * Places a card on the table.
     * This method is used to update the client's view of the table after a card has
     * been placed.
     *
     * @param nickname  The nickname of the player who placed the card.
     * @param id        The ID of the card.
     * @param position  The position where the card was placed.
     * @param side      The side of the card.
     * @param resources A HashMap containing the resources of the player after the
     *                  card was placed.
     * @param points    The points of the player after the card was placed.
     */
    @Override
    public void placeCard(String nickname, int id, Point position, boolean side, int turn,
            HashMap<Sign, Integer> resources, int points) {
        // update the card on the table
        controller.updatePlaceCard(nickname, id, position, side, turn);
        controller.updateResources(nickname, resources);
        controller.updateScore(nickname, points);
    }

    /**
     * Moves a card on the table.
     * This method is used to update the client's view of the table after a card has
     * been moved.
     *
     * @param newCardId     The ID of the new card.
     * @param headDeck      The Kingdom of the card on the deck.
     * @param gold          A boolean indicating whether the deck updated is gold.
     * @param onTableOrDeck An integer indicating whether the card is on the table
     *                      or the deck.
     */
    @Override
    public void moveCard(Integer newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) {
        controller.updateAndShowCommonTable(newCardId, gold, onTableOrDeck, headDeck);
    }

    /**
     * Shows the end game information to the client.
     * This method is used to update the client's view of the extra points and the
     * ranking at the end of the game.
     *
     * @param extraPoints A HashMap containing the extra points of each player.
     * @param ranking     An ArrayList containing the players in their ranking
     *                    order.
     */
    @Override
    public void showEndGame(HashMap<String, Integer> extraPoints, ArrayList<Player> ranking) {
        controller.showExtraPoints(extraPoints);
        controller.showRanking(ranking);
    }

    /**
     * Gets the first player and starts the game.
     * This method is used to update the client's view of the first player and to
     * transition the game to the game flow phase.
     *
     * @param firstPlayer The nickname of the first player.
     */
    @Override
    public void getIsFirstAndStartGame(String firstPlayer) {
        // set FSM to GAME_FLOW and show the first player
        Controller.setPhase(Phase.GAME_FLOW);
        controller.showIsFirst(firstPlayer);
    }

    /**
     * This method is used to stop the game for the client.
     * It is called when the client needs to stop the game, for example, when the
     * client disconnects from the server.
     *
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    @Override
    public void stopGaming() throws RemoteException {
        synchronized (lock) {
            scheduler.shutdown();
            controller.stopGaming();
        }
    }

    /**
     * This method is used to check if the client is still connected to the server.
     * The server will call this method to check if the connection with the client
     * is still active.
     *
     * @throws RemoteException throws a RemoteException if there is a problem with
     *                         the connection.
     */
    @Override
    public void isConnected() throws RemoteException {
        // this method is used to check if the client is still connected. No
        // implementation needed.
    }

    /**
     * This method is used to periodically check if the client is still connected to
     * the server.
     * It creates a Runnable that calls the isConnected method and schedules it to
     * run at a fixed rate.
     * The Runnable is scheduled to run every 30 seconds.
     */
    public void isClientConnectedToServer() {
        final Runnable checker = new Runnable() {
            public void run() {
                try {
                    stub.connectToServer();
                } catch (RemoteException e) {
                    controller.noConnection();
                }
            }
        };
        scheduler.scheduleAtFixedRate(checker, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void loadSavedGame(GameMaster game) throws RemoteException {
        loadGame = true;
        controller.setModel(game);
    }

}
