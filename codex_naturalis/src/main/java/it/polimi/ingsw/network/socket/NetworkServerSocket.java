package it.polimi.ingsw.network.socket;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.awt.Point;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameMaster;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.CardPositionException;
import it.polimi.ingsw.model.exception.ClosingLobbyException;
import it.polimi.ingsw.model.exception.ColorAlreadyTakenException;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NoNameException;
import it.polimi.ingsw.model.exception.NoTurnException;
import it.polimi.ingsw.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.model.exception.WrongGamePhaseException;
import it.polimi.ingsw.network.socket.messages.ErrorType;
import it.polimi.ingsw.network.socket.messages.client.ClientMessage;
import it.polimi.ingsw.network.socket.messages.client.gameflow.CardToBeDrawn;
import it.polimi.ingsw.network.socket.messages.client.gameflow.CardToBePositioned;
import it.polimi.ingsw.network.socket.messages.client.gamestart.ChosenObjectiveCard;
import it.polimi.ingsw.network.socket.messages.client.gamestart.ChosenStartingCardSide;
import it.polimi.ingsw.network.socket.messages.client.login.ColorChosen;
import it.polimi.ingsw.network.socket.messages.client.login.LoginMessage;
import it.polimi.ingsw.network.socket.messages.client.login.NumberOfPlayersMessage;
import it.polimi.ingsw.network.socket.messages.server.ErrorMessage;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.network.socket.messages.server.StopGaming;
import it.polimi.ingsw.network.socket.messages.server.loadSavedGame;
import it.polimi.ingsw.network.socket.messages.server.endgame.ShowPointsFromObjectives;
import it.polimi.ingsw.network.socket.messages.server.endgame.ShowRanking;
import it.polimi.ingsw.network.socket.messages.server.gameflow.CardIsPositioned;
import it.polimi.ingsw.network.socket.messages.server.gameflow.RefreshedPoints;
import it.polimi.ingsw.network.socket.messages.server.gameflow.RefreshedResources;
import it.polimi.ingsw.network.socket.messages.server.gameflow.ShowNewTable;
import it.polimi.ingsw.network.socket.messages.server.gameflow.TurnInfo;
import it.polimi.ingsw.network.socket.messages.server.gamestart.*;
import it.polimi.ingsw.network.socket.messages.server.login.LobbyIsReady;
import it.polimi.ingsw.network.socket.messages.server.login.PlayersAndColorPins;
import it.polimi.ingsw.network.socket.messages.server.login.StatusLogin;
import it.polimi.ingsw.network.NetworkHandler;
import it.polimi.ingsw.network.NetworkPlug;

import java.io.*;

/**
 * The NetworkServerSocket class is responsible for managing the server-side
 * logic of the socket network communication.
 * It maintains a list of connections to client sockets, representing the
 * connected clients.
 *
 * The class provides methods for various game actions such as login, choosing
 * color, placing cards, drawing cards, etc.
 * These methods are invoked by the client by sending a messages, and the
 * corresponding actions are performed on the server side.
 *
 * The class also provides methods for broadcasting game state updates to all
 * connected clients.
 * These methods are invoked by the server when the game state changes, and the
 * updates are sent to the clients.
 */
public class NetworkServerSocket implements NetworkPlug {

    private final ServerSocket serverSocket;

    private final Controller controller;

    private static HashMap<String, ClientHandler> connections;

    /**
     * This constructor is used to create a new NetworkServerSocket.
     * 
     * @param port The port of the server.
     * @throws IOException If there is an error creating the server socket.
     */
    public NetworkServerSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        NetworkHandler.getInstance().addNetworkPlug("socket", this);
        connections = new HashMap<>();
        controller = Controller.getInstance();
    }

    /**
     * This method is used to start the server.
     * 
     * @throws IOException If there is an error accepting a connection.
     */
    public void start() throws IOException {
        while (2 + 2 == 4) {
            Socket new_connection = serverSocket.accept();
            ClientHandler connection = new ClientHandler(new_connection);
            connections.put(new_connection.getRemoteSocketAddress().toString(), connection);
            connection.start();
        }
    }

    /**
     * This method is used to send a message to all the clients.
     * 
     * @param message The message to be sent.
     */
    private void sendBroadCastMessage(ServerMessage message) {
        // all the clients connected to SocketServer
        for (String client : connections.keySet()) {
            connections.get(client).sendMessage(message);
        }
    }

    /**
     * This method is used to send a message to all to disconnect all the clients.
     * 
     * @param message The message to be sent.
     */
    private void sendBroadCastMessageDisconnection(ServerMessage message) {
        // disconnect all the clients connected to SocketServer. Different to
        // sendBroadCastMessage
        // since we catch the exception and close the connection.
        for (String client : connections.keySet()) {
            connections.get(client).sendMessageDisconnection(message);
        }
    }

    /**
     * This method is called when the number of players in the game has been
     * finalized.
     * It broadcasts a message to all connected clients indicating that the lobby is
     * ready and the game can start.
     */
    @Override
    public void finalizingNumberOfPlayers() {
        for (ClientHandler client : connections.values()) {
            // if the client is admitted to the game, we send a message to stop waiting and
            // start play
            if (controller.isAdmitted(client.getNickname())) {
                client.sendMessage(new StopWaitingOrDisconnect(true));
            } else {
                // disconnection of the users not admitted
                client.sendMessage(new StopWaitingOrDisconnect(false));
                client.hastaLaVistaBaby();
            }
        }
    }

    /**
     * Implements the gameIsStarting method of the NetworkPlug interface.
     * This method is responsible for initiating the game start process.
     *
     * It iterates over all the connections and sends a game start signal to each
     * client.
     * It is used to notify all clients that the game is starting, sending the
     * commonTable.
     *
     */
    @Override
    public void gameIsStarting() {
        // get the resource cards and gold cards of the players
        int resourceCard0 = controller.getResourceCards(0);
        int resourceCard1 = controller.getResourceCards(1);

        int goldCard0 = controller.getGoldCard(0);
        int goldCard1 = controller.getGoldCard(1);

        Kingdom goldCardOnDeck = controller.getHeadDeck(true);
        Kingdom resourceCardOnDeck = controller.getHeadDeck(false);

        sendBroadCastMessage(
                new ShowTable(resourceCard0, resourceCard1, goldCard0, goldCard1, resourceCardOnDeck, goldCardOnDeck));

        for (ClientHandler connection : connections.values()) {
            connection.sendStartingCard();
        }
    }

    /**
     * Implements the login method of the NetworkPlug interface.
     * This method is responsible for refreshing the user list for all connected
     * clients.
     * It iterates over all the connections and sends a user list update to each
     * client.
     *
     * It does not take any parameters and does not return any value.
     * 
     * @catch RemoteException If a communication-related error occurs during the
     *        execution of a remote method call.
     */
    @Override
    public void refreshUsers() {
        // the name of player and its correspondent pin/colour
        HashMap<String, Color> playersAndPins = controller.getPlayersAndPins();
        sendBroadCastMessage(new PlayersAndColorPins(playersAndPins));
    }

    @Override
    public void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId,
            boolean allWithRootCardPlaced) {
        // send the card placed to all the clients, turn is 0 since it is the first card
        // placed.
        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, new Point(0, 0), side, 0));
        try {
            sendBroadCastMessage(new RefreshedPoints(nickname, controller.getPlayerPoints(nickname)));
            sendBroadCastMessage(new RefreshedResources(nickname, controller.getPlayerResources(nickname)));
        } catch (NoNameException e) {
            // This should never occur
            System.out
                    .println("Debugging error: NoNameException in sendingPlacedRootCardAndWhenCompleteObjectiveCards");
        }

        if (allWithRootCardPlaced) {
            // send the objective cards to all the clients, the common objective cards
            sendBroadCastMessage(
                    new ShowObjectiveCards(new ArrayList<>(Arrays.asList(controller.getCommonObjectiveCards()))));
            for (ClientHandler connection : connections.values()) {
                // send the secret objective cards to a specific client.
                connection.sendSecretObjectives();
            }
        }
    }

    @Override
    public void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname,
            boolean allWithSecretObjectiveCardChosen) {

        for (ClientHandler connection : connections.values()) {
            connection.sendHand(nickname, allWithSecretObjectiveCardChosen);
        }
    }

    /**
     * Implements the sendPlacedCard method of the NetworkPlug interface.
     *
     * This method is responsible for broadcasting the information of a card that
     * has been placed by a player.
     * It is called when a player successfully places a card on the board.
     * It broadcasts the information of the placed card, including the player's
     * nickname, the card ID, the position of the card, and the side of the card.
     *
     * @param nickname The nickname of the player who has placed the card.
     * @param cardId   The id of the card that has been placed.
     * @param position The position where the card has been placed on the board.
     * @param side     The side of the card chosen by the player. True for one side,
     *                 false for the other.
     */
    @Override
    public void sendPlacedCard(String nickname, int cardId, Point position, boolean side) {
        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, position, side, controller.getTurn()));
        try {
            sendBroadCastMessage(new RefreshedPoints(nickname, controller.getPlayerPoints(nickname)));
            sendBroadCastMessage(new RefreshedResources(nickname, controller.getPlayerResources(nickname)));
        } catch (NoNameException e) {
            // disconnect all connections
            NetworkHandler.getInstance().disconnectBroadcast();
        }
        sendBroadCastMessage(new TurnInfo(controller.getCurrentPlayer(), controller.getGameState()));
    }

    @Override
    public void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) {
        for (String player : connections.keySet()) {
            connections.get(player).sendDrawnCardIfPlayer(nickname, newCardId, headDeck, gold, onTableOrDeck);
        }
    }

    @Override
    public void sendEndGame() {
        sendBroadCastMessage(new ShowPointsFromObjectives(controller.getExtraPoints()));
        sendBroadCastMessage(new ShowRanking(controller.getRanking()));
    }

    @Override
    public void disconnectAll() {
        sendBroadCastMessageDisconnection(new StopGaming());
    }

    @Override
    public void loadGame(GameMaster game) {
        for (String player : connections.keySet()) {
            connections.get(player).sendFullGameState(game);
        }
    }

    /**
     * This class is used to handle the connection with the client.
     */
    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private final Controller controller;
        private final NetworkHandler networkHandler;
        private String nickname;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            controller = Controller.getInstance();
            networkHandler = NetworkHandler.getInstance();
        }

        public String getNickname() {
            return nickname;
        }

        /**
         * This method is used to handle the messages received from the client.
         */
        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                ClientMessage message;
                while (clientSocket.isConnected()) {
                    try {
                        message = (ClientMessage) in.readObject();
                        handleMessage(message);
                    } catch (ClassNotFoundException e) {
                        sendErrorMessage(ErrorType.INVALID_MESSAGE);
                        System.out.println("Invalid message received");
                    }
                }
            } catch (IOException e) {
                // disconnect all the clients connected to the server RMI and Socket
                connections.remove(clientSocket.getRemoteSocketAddress().toString());
                NetworkHandler.getInstance().disconnectBroadcast();
            }
        }

        /**
         * This method is used to handle the messages received from the client.
         * 
         * @param message The message received from the client.
         * @throws ClassNotFoundException If the message is not recognized.
         */
        private void handleMessage(ClientMessage message) throws ClassNotFoundException {
            if (message instanceof LoginMessage) {
                LoginMessage parsedMessage = (LoginMessage) message;
                // handle login
                try {
                    // add the player to the lobby
                    controller.addPlayer(parsedMessage.getNickname());
                    nickname = parsedMessage.getNickname();
                    // communicate to all the clients the new user
                    networkHandler.refreshUsersBroadcast();
                    // send the status of the login to the client
                    sendMessage(new StatusLogin(controller.getIsFirst(parsedMessage.getNickname())));

                    if (!controller.getIsFirst(parsedMessage.getNickname())) {
                        // check if there is a saved game, if there is send a message to the clients,
                        // otherwise just continue as normal

                        sendMessage(new LobbyIsReady(controller.lobbyIsReady()));
                    }
                    networkHandler.finalizingNumberOfPlayersBroadcast();
                } catch (SameNameException e) {
                    sendErrorMessage(ErrorType.NAME_ALREADY_USED);
                } catch (LobbyCompleteException e) {
                    sendErrorMessage(ErrorType.LOBBY_ALREADY_FULL);
                    hastaLaVistaBaby(); // as per diagram
                }
            } else if (message instanceof NumberOfPlayersMessage) {
                NumberOfPlayersMessage parsedMessage = (NumberOfPlayersMessage) message;
                try {
                    controller.initializeLobby(parsedMessage.getNumber());
                    networkHandler.finalizingNumberOfPlayersBroadcast();
                } catch (ClosingLobbyException e) {
                    sendErrorMessage(ErrorType.LOBBY_IS_CLOSED);
                }
            } else if (message instanceof ColorChosen) {
                ColorChosen parsedMessage = (ColorChosen) message;
                try {
                    boolean isLobbyReadyToStart = controller.setColourAndLobbyIsReadyToStart(
                            parsedMessage.getNickname(),
                            parsedMessage.getColor());
                    networkHandler.refreshUsersBroadcast();
                    if (isLobbyReadyToStart) {
                        networkHandler.gameIsStartingBroadcast();
                    }
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
                } catch (ColorAlreadyTakenException e) {
                    sendErrorMessage(ErrorType.COLOR_UNAVAILABLE);
                }
            } else if (message instanceof ChosenStartingCardSide) {
                ChosenStartingCardSide parsedMessage = (ChosenStartingCardSide) message;
                try {
                    int cardId = controller.placeRootCard(parsedMessage.getNickname(), parsedMessage.isSide());
                    boolean allWithRootCardPlaced = controller.areAllRootCardPlaced();
                    networkHandler.sendingPlacedRootCardAndWhenCompleteObjectiveCardsBroadcast(
                            parsedMessage.getNickname(),
                            parsedMessage.isSide(), cardId, allWithRootCardPlaced);
                } catch (WrongGamePhaseException e) {
                    sendErrorMessage(ErrorType.WRONG_PHASE);
                } catch (NoTurnException e) {
                    sendErrorMessage(ErrorType.NO_TURN);
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
                }

            } else if (message instanceof ChosenObjectiveCard) {
                ChosenObjectiveCard parsedMessage = (ChosenObjectiveCard) message;
                try {
                    controller.chooseObjectiveCard(parsedMessage.getNickname(), parsedMessage.getIndexCard());
                    // take the correct the position of the card and respond to the client with the
                    // correct choice.
                    sendMessage(new ObjectiveCardChosen(parsedMessage.getIndexCard()));
                    boolean allWithSecretObjectiveCardChosen = controller.areAllSecretObjectiveCardChosen();

                    networkHandler.sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlowBroadcast(
                            parsedMessage.getNickname(), allWithSecretObjectiveCardChosen);
                } catch (WrongGamePhaseException e) {
                    sendErrorMessage(ErrorType.WRONG_PHASE);
                } catch (NoTurnException e) {
                    sendErrorMessage(ErrorType.NO_TURN);
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
                }
            } else if (message instanceof CardToBePositioned) {
                CardToBePositioned parsedMessage = (CardToBePositioned) message;

                try {
                    int cardId = controller.placeCard(parsedMessage.getNickname(), parsedMessage.getHandPlacement(),
                            parsedMessage.getPosition(), parsedMessage.isSide());
                    networkHandler.sendPlacedCardBroadcast(parsedMessage.getNickname(), cardId,
                            parsedMessage.getPosition(), parsedMessage.isSide());
                    if (controller.isEndGame()) {
                        networkHandler.sendEndGameBroadcast();
                    }
                } catch (WrongGamePhaseException e) {
                    sendErrorMessage(ErrorType.WRONG_PHASE);
                } catch (NoTurnException e) {
                    sendErrorMessage(ErrorType.NO_TURN);
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
                } catch (NotEnoughResourcesException e) {
                    sendErrorMessage(ErrorType.NOT_ENOUGH_RESOURCES);
                } catch (CardPositionException e) {
                    sendErrorMessage(ErrorType.CARD_POSITION);
                }
            } else if (message instanceof CardToBeDrawn) {
                CardToBeDrawn parsedMessage = (CardToBeDrawn) message;

                try {
                    // this set the new card in the hand of the player
                    controller.drawCard(parsedMessage.getNickname(), parsedMessage.isGold(),
                            parsedMessage.getOnTableOrOnDeck());
                    // this get the new card on the table. It is -1 , we send anyway the newCardId
                    // to the client, but is the same as previous.
                    int newCardId = controller.newCardOnTable(parsedMessage.isGold(),
                            parsedMessage.getOnTableOrOnDeck());

                    Kingdom headDeck = controller.getHeadDeck(parsedMessage.isGold());

                    networkHandler.sendDrawnCardBroadcast(parsedMessage.getNickname(), newCardId, headDeck,
                            parsedMessage.isGold(), parsedMessage.getOnTableOrOnDeck());

                    if (controller.isEndGame()) {
                        networkHandler.sendEndGameBroadcast();
                    }

                    // we send the new hand of the player. Unicast message.
                    sendMessage(new ShowHand(parsedMessage.getNickname(), controller.getHand(nickname)));
                } catch (WrongGamePhaseException e) {
                    sendErrorMessage(ErrorType.WRONG_PHASE);
                } catch (NoTurnException e) {
                    sendErrorMessage(ErrorType.NO_TURN);
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
                } catch (CardPositionException e) {
                    sendErrorMessage(ErrorType.CARD_POSITION);
                }
            } else {
                throw new ClassNotFoundException();
            }
        }

        /**
         * This method is used to close the connection with the client.
         */
        private void hastaLaVistaBaby() {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing connection. Connection already closed.");
            }
        }

        /**
         * This method is used to send an error message to the client.
         * 
         * @param errorType The type of the error.
         */
        public void sendErrorMessage(ErrorType errorType) {
            sendMessage(new ErrorMessage(errorType));
        }

        /**
         * This method is used to send a message to the client.
         * 
         * @param message The message to be sent.
         */
        public void sendMessage(ServerMessage message) {
            try {
                out.writeObject(message);
            } catch (IOException e) {
                connections.remove(clientSocket.getRemoteSocketAddress().toString());
                NetworkHandler.getInstance().disconnectBroadcast();
            }
        }

        /**
         * This method is used to send a message to the client, to disconnect all
         *
         * @param message The message to be sent.
         */
        public void sendMessageDisconnection(ServerMessage message) {
            try {
                out.writeObject(message);
                hastaLaVistaBaby();
            } catch (IOException e) {
                System.out.println("Error closing connection. Connection already closed.");
            }
        }

        /**
         * This method is used to send the common objective cards to the clients. It is
         * a broadcast call.
         * 
         */
        public void sendSecretObjectives() {
            try {
                ArrayList<Integer> choices = new ArrayList<>(
                        Arrays.asList(controller.getSecretObjectiveCardsToChoose(nickname)));
                sendMessage(new GiveSecretObjectiveCards(choices));
            } catch (NoNameException e) {
                System.out.println("No name exception");
            }
        }

        /**
         * This method is used to send the drawn card to the clients. It is a broadcast
         * call.
         * 
         * @param nickname      The nickname of the player.
         * @param newCardId     The id of the new card.
         * @param headDeck      The head deck.
         * @param gold          A boolean indicating whether the card is gold. To know
         *                      which deck to update.
         * @param onTableOrDeck An integer indicating whether the card is on the table
         *                      or
         *                      the deck.
         */
        public void sendDrawnCardIfPlayer(String nickname, int newCardId, Kingdom headDeck, boolean gold,
                int onTableOrDeck) {
            if (!this.nickname.equals(nickname)) {
                try {
                    sendMessage(new ShowHiddenHand(nickname, controller.getHiddenHand(nickname)));
                } catch (NoNameException e) {
                    System.out.println("No name exception");
                }
            }
            // this message has two cards information: the new card drawn and the head deck
            // onTableOrDeck is used to know if we should update the table.
            sendMessage(new ShowNewTable(newCardId, gold, onTableOrDeck, headDeck));
            sendMessage(new TurnInfo(controller.getCurrentPlayer(), controller.getGameState()));
        }

        /**
         * This method is used to send the hand of the player.
         * 
         * @param nickname                         The nickname of the player.
         * @param allWithSecretObjectiveCardChosen A boolean indicating whether all the
         *                                         players have chosen their secret
         *                                         objective card.
         */
        public void sendHand(String nickname, boolean allWithSecretObjectiveCardChosen) {
            if (this.nickname.equals(nickname)) {
                try {
                    sendMessage(new ShowHand(nickname, controller.getHand(nickname)));
                } catch (NoNameException e) {
                    System.out.println("No name exception");
                }
            } else {
                try {
                    sendMessage(new ShowHiddenHand(nickname, controller.getHiddenHand(nickname)));
                } catch (NoNameException e) {
                    System.out.println("No name exception");
                }
            }

            if (allWithSecretObjectiveCardChosen) {
                sendMessage(new FirstPlayer(controller.getFirstPlayer()));
            }
        }

        public void sendStartingCard() {
            try {
                sendMessage(new ShowStartingCard(controller.getStartingCard(nickname)));
            } catch (NoNameException e) {
                sendErrorMessage(ErrorType.NAME_UNKNOWN);
            }
        }

        public void sendFullGameState(GameMaster game) {
            sendMessage(new loadSavedGame(game));
        }
    }

}
