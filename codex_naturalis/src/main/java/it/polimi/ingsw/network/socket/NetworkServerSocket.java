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
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.CardPositionException;
import it.polimi.ingsw.model.exception.ClosingLobbyException;
import it.polimi.ingsw.model.exception.ColorAlreadyTakenException;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NoNameException;
import it.polimi.ingsw.model.exception.NoTurnException;
import it.polimi.ingsw.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.model.exception.WrongGamePhaseException;
import it.polimi.ingsw.network.NetworkHandler;
import it.polimi.ingsw.network.NetworkPlug;
import it.polimi.ingsw.network.socket.messages.client.ConnectionClient;
import it.polimi.ingsw.network.socket.messages.ErrorType;
import it.polimi.ingsw.network.socket.messages.client.ClientMessage;
import it.polimi.ingsw.network.socket.messages.client.ConnectionClientForServer;
import it.polimi.ingsw.network.socket.messages.client.gameflow.SentChatMessage;
import it.polimi.ingsw.network.socket.messages.client.gameflow.CardToBeDrawn;
import it.polimi.ingsw.network.socket.messages.client.gameflow.CardToBePositioned;
import it.polimi.ingsw.network.socket.messages.client.gamestart.ChosenObjectiveCard;
import it.polimi.ingsw.network.socket.messages.client.gamestart.ChosenStartingCardSide;
import it.polimi.ingsw.network.socket.messages.client.login.ColorChosen;
import it.polimi.ingsw.network.socket.messages.client.login.LoginMessage;
import it.polimi.ingsw.network.socket.messages.client.login.NumberOfPlayersMessage;
import it.polimi.ingsw.network.socket.messages.server.*;
import it.polimi.ingsw.network.socket.messages.server.endgame.ShowPointsFromObjectives;
import it.polimi.ingsw.network.socket.messages.server.endgame.ShowRanking;
import it.polimi.ingsw.network.socket.messages.server.gameflow.*;
import it.polimi.ingsw.network.socket.messages.server.gamestart.*;
import it.polimi.ingsw.network.socket.messages.server.login.LobbyIsReady;
import it.polimi.ingsw.network.socket.messages.server.login.PlayersAndColorPins;
import it.polimi.ingsw.network.socket.messages.server.login.StatusLogin;

import java.io.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Represents the server-side socket in the network communication.
 *
 * This class implements the NetworkPlug interface and is responsible for
 * managing the server-side socket communication.
 * It maintains a ServerSocket object for accepting incoming connections, a
 * Controller object for accessing the game state and performing game actions,
 * and a HashMap for storing the connections to the clients.
 *
 * The key in the HashMap is the address of the client socket, and the value is
 * the ClientHandler object representing the connection.
 * The class also includes methods for starting the server, broadcasting
 * messages to all clients, handling the game start process, refreshing the user
 * list,
 * sending chat messages, broadcasting the information of a placed root card,
 * sending the hands of the players, broadcasting the information of a drawn
 * card,
 * broadcasting the end game signal, and disconnecting all clients.
 */
public class NetworkServerSocket implements NetworkPlug {
    /**
     * The ServerSocket object used to accept incoming connections from clients.
     */
    private final ServerSocket serverSocket;
    /**
     * The Controller object used to access the game state and perform game actions.
     */
    private Controller controller;
    /**
     * A HashMap used to store the connections to the clients.
     * The key is the address of the client socket, and the value is the
     * ClientHandler object representing the connection.
     */
    private static HashMap<String, ClientHandler> connections;

    /**
     * This constructor is used to create a new NetworkServerSocket.
     *
     * @param port The port of the server.
     * @throws IOException If there is an error creating the server socket.
     */
    public NetworkServerSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + serverSocket.getLocalPort());
        // add the network plug to the network handler, who manages the different
        // connections protocols.
        NetworkHandler.getInstance().addNetworkPlug("socket", this);

        try {
            System.out.println("Server Socket" + "is listening on IP: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Server Socket is listening on Port: " + serverSocket.getLocalPort());
        } catch (Exception e) {
            System.out.println("Error in getting the IP address and port of the server: " + e.getMessage());
        }

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
            // start the thread connection
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
        for (ClientHandler connection : connections.values()) {
            connection.sendMessageDisconnection(message);
        }
        connections = new HashMap<>();
        // set controller to null, and recreate that after the disconnection of all the
        // clients.
        Controller.getInstance().reset();
        controller = Controller.getInstance();
    }

    /**
     * Implements the finalizingNumberOfPlayers method of the NetworkPlug interface.
     *
     * This method is called when the number of players in the game has been
     * finalized.
     * It broadcasts a message to all connected clients indicating that the lobby is
     * ready and the game can start.
     */
    @Override
    public void finalizingNumberOfPlayers() {
        HashMap<String, ClientHandler> connectionsToDelete = new HashMap<>();

        for (String address : connections.keySet()) {
            // if the client is admitted to the game, we send a message to stop waiting and
            // start play
            if (controller.isAdmitted(connections.get(address).getNickname())) {
                connections.get(address).sendMessage(new StopWaitingOrDisconnect(true));
            } else {
                // disconnection of the users isn't admitted
                connections.get(address).sendMessage(new StopWaitingOrDisconnect(false));
                // close the connection
                connections.get(address).hastaLaVistaBaby();
                // remove from the connection list
                connectionsToDelete.put(address, connections.get(address));
            }
        }
        // delete the connections that are not admitted to the game
        for (String address : connectionsToDelete.keySet()) {
            connections.remove(address);
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

        // get the cards on the deck
        Kingdom goldCardOnDeck = controller.getHeadDeck(true);
        Kingdom resourceCardOnDeck = controller.getHeadDeck(false);
        // send the common table to all the clients
        sendBroadCastMessage(
                new ShowTable(resourceCard0, resourceCard1, goldCard0, goldCard1, resourceCardOnDeck, goldCardOnDeck));

        for (ClientHandler connection : connections.values()) {
            connection.sendStartingCard();
        }
    }

    /**
     * Implements the refreshUsers method of the NetworkPlug interface.
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

    /**
     * Implements the fetchReceivers method of the NetworkPlug interface.
     *
     * This method is used to fetch the receivers of a chat message.
     * It iterates over all the connections and checks if the message contains a tag
     * for a specific client.
     * If a tag is found, the nickname of the tagged client is added to the list of
     * receivers.
     *
     * @param message The message to be fetched.
     * @return The list of nicknames of the players across connections who should
     *         receive the message.
     */
    @Override
    public ArrayList<String> fetchReceivers(String message) {
        ArrayList<String> receivers = new ArrayList<>();
        for (ClientHandler connection : connections.values()) {
            // see how connections work. It is a map with the address of the client as key
            // and the ClientHandler as value.
            // client handler has the nickname of the client.
            if (message.toLowerCase().contains("@" + connection.getNickname().toLowerCase())) {
                receivers.add(connection.getNickname().toLowerCase());// to avoid mistake for capslock
            }
        }
        return receivers;
    }

    /**
     * Sends a chat message to players checking tags.
     *
     * This method is used to send a chat message from a sender to one or more
     * receivers.
     * If the message contains a "@nickname" tag, the message will be sent only to
     * the client associated with that nickname.
     * If no such tags are found in the message, it will be broadcast to all
     * connected clients.
     *
     * @param sender  The nickname of the player who sent the message.
     * @param message The message sent by the player.
     */
    @Override
    public void sendingChatMessage(String sender, String message, ArrayList<String> receivers) {
        if (receivers.isEmpty()) {
            // the message is sent to all the clients
            sendBroadCastMessage(new ReceivedChatMessage(sender, message, true));
        } else {
            for (ClientHandler connection : connections.values()) {
                if (receivers.contains(connection.getNickname().toLowerCase())
                        || connection.getNickname().equalsIgnoreCase(sender)) {
                    connection.sendMessage(new ReceivedChatMessage(sender, message, false));
                }
            }
        }
    }

    /**
     * Broadcasts the information of a root card that has been placed by a player.
     * This method is called when a player successfully places a root card on the
     * board.
     * It broadcasts the information of the placed root card, including the player's
     * nickname, the side of the card, the card ID, and a flag indicating if all
     * players have placed their root cards.
     *
     * @param nickname              The nickname of the player who has placed the
     *                              card.
     * @param side                  The side of the card chosen by the player. True
     *                              for one side, false for the other.
     * @param cardId                The id of the card that has been placed.
     * @param allWithRootCardPlaced A boolean flag indicating if all players have
     *                              placed their root cards.
     */
    @Override
    public void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId,
            boolean allWithRootCardPlaced) {
        // send the card placed to all the clients; turn is 0 since it is the first card
        // placed.
        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, new Point(0, 0), side, 0));
        try {
            // send the points and resources of the player to all the clients
            sendBroadCastMessage(new RefreshedPoints(nickname, controller.getPlayerPoints(nickname)));
            HashMap<Sign, Integer> resourcesCopy = new HashMap<>(controller.getPlayerResources(nickname));
            sendBroadCastMessage(new RefreshedResources(nickname, resourcesCopy));
        } catch (NoNameException e) {
            // This should never occur
            System.out
                    .println("Debugging error: NoNameException in sendingPlacedRootCardAndWhenCompleteObjectiveCards");
        }

        // if all the root card are placed, we send the objective cards to all the
        // clients(common) and the secret objective cards to a specific client.
        if (allWithRootCardPlaced) {
            // send the objective cards to all the clients, the common objective cards
            sendBroadCastMessage(
                    new ShowObjectiveCards(new ArrayList<>(Arrays.asList(controller.getCommonObjectiveCards()))));
            for (ClientHandler connection : connections.values()) {
                // send the secret objective cards to a specific client.
                connection.sendSecretObjectives();
            }
        }
        // notify the new turn to all the clients
        sendBroadCastMessage(new TurnInfo(controller.getCurrentPlayer(), controller.getGameState()));
    }

    /**
     * Sends the hands of the players and starts the game flow when all players have
     * chosen their secret objective cards.
     *
     * This method is used to send the hands of the players to the clients. It also
     * checks if all players have chosen their secret objective cards.
     * If all players have chosen their secret objective cards, it starts the game
     * flow.
     *
     * @param nickname                         The nickname of the player whose hand
     *                                         is being sent.
     * @param allWithSecretObjectiveCardChosen A boolean flag indicating if all
     *                                         players have chosen their secret
     *                                         objective cards.
     */
    @Override
    // this method is called when all the players have chosen their secret objective
    // cards.
    public void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname,
            boolean allWithSecretObjectiveCardChosen) {
        // sendHand method manages the sending of the hand to the client(private if it
        // is the client, hidden to all others)
        // if allWithSecretObjectiveCardChosen sends to all the clients the starting
        // player.
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
        // send the card placed to all the clients
        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, position, side, controller.getTurn()));

        try {
            HashMap<Sign, Integer> resources = controller.getPlayerResources(nickname);

            sendBroadCastMessage(new RefreshedResources(nickname, resources));
            sendBroadCastMessage(new RefreshedPoints(nickname, controller.getPlayerPoints(nickname)));
        } catch (NoNameException e) {
            System.out.println("Debugging error: NoNameException in sendPlacedCard");
        }

        // notify the new turn to all the clients
        sendBroadCastMessage(new TurnInfo(controller.getCurrentPlayer(), controller.getGameState()));
    }

    /**
     * Sends the drawn card information to all clients.
     *
     * This method is used to broadcast the information of a card that has been
     * drawn by a player.
     * It sends the player's nickname, the ID of the new card, the head of the deck,
     * a flag indicating if the card is gold,
     * and an integer indicating whether the card is on the table or the deck.
     *
     * @param nickname      The nickname of the player who has drawn the card.
     * @param newCardId     The ID of the new card that has been drawn.
     * @param headDeck      The head of the deck after the card has been drawn.
     * @param gold          A boolean flag indicating if the card is gold.
     * @param onTableOrDeck An integer indicating whether the card is on the table
     *                      or the deck.
     */
    @Override
    public void sendDrawnCard(String nickname, Integer newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) {
        for (ClientHandler connection : connections.values()) {
            // send the hiddenHand to the players different from the player that has drawn
            // the card.
            // the player that has drawn the card receives the new card in the
            // hand(different method)
            connection.sendDrawnCardIfPlayer(nickname, newCardId, headDeck, gold, onTableOrDeck);
        }
    }

    /**
     * Sends the end game signal to all clients.
     *
     * This method is used to broadcast the end game signal to all clients.
     * It is called when the game has reached its end condition.
     * The method should gather the final points and ranking of the players from the
     * game controller
     * and send this information to all clients.
     */
    @Override
    public void sendEndGame() {
        // it sends the extra points(objective points), the ranking contains all the
        // information of the players.
        sendBroadCastMessage(new ShowPointsFromObjectives(controller.getExtraPoints()));
        sendBroadCastMessage(new ShowRanking(controller.getRanking()));
    }

    /**
     * Disconnects all clients from the server.
     *
     * This method is used to broadcast a disconnection signal to all clients.
     * It is called when the server needs to terminate all active connections, when
     * a client is disconnected.
     */
    @Override
    public void disconnectAll() {
        sendBroadCastMessageDisconnection(new StopGaming());
    }

    /**
     * Notifies all connected clients about the current turn.
     *
     * This method is used to broadcast the current turn information to all clients.
     * It retrieves the current player and the game state from the game controller
     * and sends this information to all clients.
     * It is called at the end of each turn, after a player has finished their
     * actions.
     */
    @Override
    public void notifyTurn() {
        for (ClientHandler connection : connections.values()) {
            connection.sendMessage(new TurnInfo(controller.getCurrentPlayer(), controller.getGameState()));
        }
    }

    /**
     * This method is used to load a game from a save file.
     * It should be implemented by each network interface to handle the loading
     * process according to its specific protocol.
     *
     * @param game The game to load.
     */
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
        /**
         * The client socket used to communicate with the client.
         */
        private final Socket clientSocket;
        /**
         * The ObjectOutputStream used to send messages to the client.
         */
        private ObjectOutputStream out;
        /**
         * The ObjectInputStream used to receive messages from the client.
         */
        private ObjectInputStream in;
        /**
         * The Controller object used to access the game state and perform game actions.
         */
        private final Controller controller;
        /**
         * The NetworkHandler object used to manage the network communication.
         */
        private final NetworkHandler networkHandler;
        /**
         * The nickname of the client.
         */
        private String nickname;
        /**
         * The connection status of the client.
         */
        private boolean connection;
        /**
         * The ScheduledExecutorService used to periodically check if the client is
         * still connected to the server.
         */
        private ScheduledExecutorService scheduler;

        /**
         * This constructor is used to create a new ClientHandler.
         * 
         * @param socket The client socket to be handled.
         */
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            controller = Controller.getInstance();
            networkHandler = NetworkHandler.getInstance();
            connection = true;
        }

        /**
         * Getter of nickname of client.
         */
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
                connection = true;

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
                try {
                    if (controller.getLobby().getPlayerFromName(nickname) != null) {
                        // disconnect all the clients connected to the server RMI and Socket
                        NetworkHandler.getInstance().disconnectBroadcast();
                    }
                } catch (NoNameException ex) {
                    System.out.println("Seems this player was not in the lobby.");
                }
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
                    // set the nickname of the player
                    nickname = parsedMessage.getNickname();
                    // communicate to all the clients the new user
                    networkHandler.refreshUsersBroadcast();
                    // send the status of the login to the client
                    sendMessage(new StatusLogin(controller.isFirst(parsedMessage.getNickname())));

                    // if is not first, it delivers if the lobby is ready to start
                    if (!controller.isFirst(parsedMessage.getNickname())) {
                        sendMessage(new LobbyIsReady(controller.lobbyIsReady()));
                    }
                    // check if all players have join the lobby(number of players chosen and enough
                    // number of players)
                    networkHandler.finalizingNumberOfPlayersBroadcast();
                } catch (SameNameException e) {
                    sendErrorMessage(ErrorType.NAME_ALREADY_USED);
                } catch (LobbyCompleteException e) {
                    sendErrorMessage(ErrorType.LOBBY_ALREADY_FULL);
                    hastaLaVistaBaby(); // as per diagram
                }
            } else if (message instanceof NumberOfPlayersMessage) {
                // this message sent the number of players in the lobby
                NumberOfPlayersMessage parsedMessage = (NumberOfPlayersMessage) message;
                try {
                    controller.initializeLobby(parsedMessage.getNumber());

                    networkHandler.refreshUsersBroadcast();
                    // check if all players have join the lobby(number of players chosen and enough
                    // number of players)
                    networkHandler.finalizingNumberOfPlayersBroadcast();
                } catch (ClosingLobbyException e) {
                    sendErrorMessage(ErrorType.LOBBY_IS_CLOSED);
                }
            } else if (message instanceof ColorChosen) {
                ColorChosen parsedMessage = (ColorChosen) message;
                try {
                    // set the colour of the player, and return if the lobby is ready to start
                    boolean isLobbyReadyToStart = controller.setColourAndGameIsReadyToStart(parsedMessage.getNickname(),
                            parsedMessage.getColor());
                    // communicate to all the clients the new user and the new colour
                    networkHandler.refreshUsersBroadcast();
                    // if the lobby is ready to start, we send the message to all the
                    // clients(starting card)
                    if (isLobbyReadyToStart) {
                        networkHandler.gameIsStartingBroadcast();
                        networkHandler.notifyTurnBroadcast();
                    }
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
                } catch (ColorAlreadyTakenException e) {
                    sendErrorMessage(ErrorType.COLOR_UNAVAILABLE);
                }
            } else if (message instanceof SentChatMessage) {
                SentChatMessage sentChatMessage = (SentChatMessage) message;
                networkHandler.sendChatMessageBroadcast(sentChatMessage.getSender(), sentChatMessage.getMessage());
            } else if (message instanceof ChosenStartingCardSide) {
                // this message is used to choose the side of the root card of the player.
                ChosenStartingCardSide parsedMessage = (ChosenStartingCardSide) message;
                try {
                    int cardId = controller.placeRootCard(parsedMessage.getNickname(), parsedMessage.isSide());

                    // check if all clients have chosen the root card, if so we send the common
                    // objective cards to all the clients.
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
                // this message is used to choose the secret objective card of the player.
                ChosenObjectiveCard parsedMessage = (ChosenObjectiveCard) message;
                try {
                    // index card should be between 0, 1.
                    controller.chooseObjectiveCard(parsedMessage.getNickname(), parsedMessage.getIndexCard());
                    // take the correct the position of the card and respond to the client with the
                    // correct choice.
                    sendMessage(new ObjectiveCardChosen(parsedMessage.getIndexCard()));

                    // check if all clients have chosen the secret objective card, if so we send the
                    // hands to all the clients.
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
                // this message is used to place a card on the board.
                CardToBePositioned parsedMessage = (CardToBePositioned) message;
                try {
                    int cardId = controller.placeCard(parsedMessage.getNickname(), parsedMessage.getHandPlacement(),
                            parsedMessage.getPosition(), parsedMessage.getSide());
                    // send the card placed to all the clients
                    networkHandler.sendPlacedCardBroadcast(parsedMessage.getNickname(), cardId,
                            parsedMessage.getPosition(), parsedMessage.getSide());
                    // two possible end game situations: deck finished we finished in placing phase,
                    // or we normally finish in drawing phase.
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
                // this message is used to draw a card.
                CardToBeDrawn parsedMessage = (CardToBeDrawn) message;
                try {
                    // this set the new card in the hand of the player
                    controller.drawCard(parsedMessage.getNickname(), parsedMessage.isGold(),
                            parsedMessage.getOnTableOrOnDeck());
                    // this get the new card on the table. It is -1, we send anyway the newCardId to
                    // the client, but is the same as previous.
                    Integer newCardId = controller.newCardOnTable(parsedMessage.isGold(),
                            parsedMessage.getOnTableOrOnDeck());
                    // this get the head of the deck, the new card is drawn from.
                    Kingdom headDeck = controller.getHeadDeck(parsedMessage.isGold());

                    networkHandler.sendDrawnCardBroadcast(parsedMessage.getNickname(), newCardId, headDeck,
                            parsedMessage.isGold(), parsedMessage.getOnTableOrOnDeck());

                    // two possible end game situations: deck finished we finished in placing phase,
                    // or we normally finish in drawing phase.
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
            } else if (message instanceof ConnectionClient) {
                sendMessage(new ConnectionServerForClient());
            } else if (message instanceof ConnectionClientForServer) {
                connection = true;
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
         * This method is used to periodically check if the client is still connected to
         * the server.
         * It runs in a separate thread and pauses for a specified interval (30 seconds
         * in this case) between each check.
         * If the client is no longer connected, it triggers a broadcast to disconnect
         * all clients.
         */
        private void isClientConnected() {
            scheduler = Executors.newScheduledThreadPool(1);

            final Runnable checker = new Runnable() {
                public void run() {
                    if (!connection) {
                        NetworkHandler.getInstance().disconnectBroadcast();
                        scheduler.shutdown(); // Stop the scheduler when connection is lost
                    } else {
                        connection = false;
                        sendMessage(new ConnectionServer());
                    }
                }
            };

            // Schedule the task to run every 30 seconds
            scheduler.scheduleAtFixedRate(checker, 30, 30, TimeUnit.SECONDS);
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
            // after sending the message, we start the thread to check if the client is
            // still connected
            if (message instanceof StopWaitingOrDisconnect) {
                // start the thread to check if the client is still connected
                if (((StopWaitingOrDisconnect) message).isStopWaitingOrDisconnect()) {
                    new Thread(this::isClientConnected).start();
                }
            }

            try {
                out.writeObject(message);
                out.reset();
            } catch (IOException e) {
                // disconnect all the clients connected to the server RMI and Socket
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
                scheduler.shutdownNow();
                hastaLaVistaBaby();
            } catch (IOException e) {
                System.out.println("SOCKET: Error closing connection." + nickname + "Connection already closed.");
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
        public void sendDrawnCardIfPlayer(String nickname, Integer newCardId, Kingdom headDeck, boolean gold,
                int onTableOrDeck) {
            // if the player is the one that has drawn the card, we send the hidden hand to
            // the player.
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
         * This method is used the first time, we send the hand to the player.
         *
         * @param nickname                         The nickname of the player.
         * @param allWithSecretObjectiveCardChosen A boolean indicating whether all the
         *                                         players have chosen their secret
         *                                         objective card.
         */
        public void sendHand(String nickname, boolean allWithSecretObjectiveCardChosen) {
            // this method is called loop in all connections by the method above.

            // if the player is the one that has drawn the card, we send the hand to the
            // player.
            if (this.nickname.equals(nickname)) {
                try {
                    sendMessage(new ShowHand(nickname, controller.getHand(nickname)));
                } catch (NoNameException e) {
                    System.out.println("No name exception");
                }
            } else {
                // if the player is not the one that has drawn the card, we send the hidden hand
                // to the player.
                try {
                    sendMessage(new ShowHiddenHand(nickname, controller.getHiddenHand(nickname)));
                } catch (NoNameException e) {
                    System.out.println("No name exception");
                }
            }
            // if all the players have chosen the secret objective card, we send the
            // starting player to all the clients.
            if (allWithSecretObjectiveCardChosen) {
                sendMessage(new FirstPlayer(controller.getFirstPlayer()));
            } else {
                // if not all the players have chosen the secret objective card, we send the new
                // turn to all the clients.
                sendMessage(new TurnInfo(controller.getCurrentPlayer(), controller.getGameState()));
            }
        }

        /**
         * Sends the starting card information to the client.
         *
         * This method is used to send the starting card of the player to the client.
         * It retrieves the starting card from the game controller using the player's
         * nickname and sends this information to the client.
         *
         * Please note that this method should be called at the beginning of the game,
         * after the player has been successfully added to the game and the game is
         * starting.
         */
        public void sendStartingCard() {
            try {
                sendMessage(new ShowStartingCard(controller.getStartingCard(nickname)));
            } catch (NoNameException e) {
                System.out.println("No name exception");
            }
        }

        /**
         * This method is used to send the full game state to the clients.
         * 
         * @param game The game state to be sent.
         */
        public void sendFullGameState(GameMaster game) {
            sendMessage(new loadSavedGame(game));

            // start the thread to check if the client is still connected
            new Thread(this::isClientConnected).start();
        }
    }
}