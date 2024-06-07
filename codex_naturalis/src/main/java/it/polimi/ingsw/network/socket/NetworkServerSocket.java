package it.polimi.ingsw.network.socket;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.awt.Point;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.Color;
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
import it.polimi.ingsw.network.NetworkHandler;
import it.polimi.ingsw.network.NetworkPlug;
import it.polimi.ingsw.network.socket.messages.ErrorType;
import it.polimi.ingsw.network.socket.messages.client.ClientMessage;
import it.polimi.ingsw.network.socket.messages.client.SentChatMessage;
import it.polimi.ingsw.network.socket.messages.client.gameflow.CardToBeDrawn;
import it.polimi.ingsw.network.socket.messages.client.gameflow.CardToBePositioned;
import it.polimi.ingsw.network.socket.messages.client.gamestart.ChosenObjectiveCard;
import it.polimi.ingsw.network.socket.messages.client.gamestart.ChosenStartingCardSide;
import it.polimi.ingsw.network.socket.messages.client.login.ColorChosen;
import it.polimi.ingsw.network.socket.messages.client.login.LoginMessage;
import it.polimi.ingsw.network.socket.messages.client.login.NumberOfPlayersMessage;
import it.polimi.ingsw.network.socket.messages.server.ErrorMessage;
import it.polimi.ingsw.network.socket.messages.server.ReceivedChatMessage;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.network.socket.messages.server.StopGaming;
import it.polimi.ingsw.network.socket.messages.server.endgame.ShowPointsFromObjectives;
import it.polimi.ingsw.network.socket.messages.server.endgame.ShowRanking;
import it.polimi.ingsw.network.socket.messages.server.gameflow.*;
import it.polimi.ingsw.network.socket.messages.server.gamestart.*;
import it.polimi.ingsw.network.socket.messages.server.login.LobbyIsReady;
import it.polimi.ingsw.network.socket.messages.server.login.PlayersAndColorPins;
import it.polimi.ingsw.network.socket.messages.server.login.StatusLogin;

import java.io.*;

public class NetworkServerSocket implements NetworkPlug {
    /**
     * The ServerSocket object used to accept incoming connections from clients.
     */
    private final ServerSocket serverSocket;
    /**
     * The Controller object used to access the game state and perform game actions.
     */
    private final Controller controller;
    /**
     * A HashMap used to store the connections to the clients.
     * The key is the address of the client socket, and the value is the ClientHandler object representing the connection.
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
        //add the network plug to the network handler, who manages the different connections protocols.
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
            //start the thread connection
            connection.start();
        }
    }
    /**
     * This method is used to send a message to all the clients.
     *
     * @param message The message to be sent.
     */
    private void sendBroadCastMessage(ServerMessage message) {
        //all the clients connected to SocketServer
        for (String client : connections.keySet()) {
            connections.get(client).sendMessage(message);
        }
    }
    /**
     * This method is used to send a message to all to disconnect all the clients.
     * @param message The message to be sent.
     */
    private void sendBroadCastMessageDisconnection(ServerMessage message) {
        //disconnect all the clients connected to SocketServer. Different to sendBroadCastMessage
        //since we catch the exception and close the connection.
        for (String client : connections.keySet()) {
            connections.get(client).sendMessageDisconnection(message);
        }
    }
    /**
     * Implements the finalizingNumberOfPlayers method of the NetworkPlug interface.
     *
     * This method is called when the number of players in the game has been finalized.
     * It broadcasts a message to all connected clients indicating that the lobby is ready and the game can start.
     */
    @Override
    public void finalizingNumberOfPlayers() {
        for(ClientHandler client : connections.values()){
            //if the client is admitted to the game, we send a message to stop waiting and start play
            if(controller.isAdmitted(client.getNickname())){
                client.sendMessage(new StopWaitingOrDisconnect(true));
            }else{
                //disconnection of the users not admitted
                client.sendMessage(new StopWaitingOrDisconnect(false));
                //close the connection
                client.hastaLaVistaBaby();
            }
        }
    }
    /**
     * Implements the gameIsStarting method of the NetworkPlug interface.
     * This method is responsible for initiating the game start process.
     *
     * It iterates over all the connections and sends a game start signal to each client.
     * It is used to notify all clients that the game is starting, sending the commonTable.
     *
     */
    @Override
    public void gameIsStarting() {
        //get the resource cards and gold cards of the players
        int resourceCard0 = controller.getResourceCards(0);
        int resourceCard1 = controller.getResourceCards(1);

        int goldCard0 = controller.getGoldCard(0);
        int goldCard1 = controller.getGoldCard(1);

        //get the cards on the deck
        Kingdom goldCardOnDeck = controller.getHeadDeck(true);
        Kingdom resourceCardOnDeck = controller.getHeadDeck(false);
        //send the common table to all the clients
        sendBroadCastMessage(
                new ShowTable(resourceCard0, resourceCard1, goldCard0, goldCard1, resourceCardOnDeck, goldCardOnDeck));

        for (ClientHandler connection : connections.values()) {
            connection.sendStartingCard();
        }
    }
    /**
     * Implements the refreshUsers method of the NetworkPlug interface.
     * This method is responsible for refreshing the user list for all connected clients.
     * It iterates over all the connections and sends a user list update to each client.
     *
     * It does not take any parameters and does not return any value.
     * @catch RemoteException If a communication-related error occurs during the execution of a remote method call.
     */
    @Override
    public void refreshUsers() {
        //the name of player and its correspondent pin/colour
        HashMap<String, Color> playersAndPins = controller.getPlayersAndPins();
        sendBroadCastMessage(new PlayersAndColorPins(playersAndPins));
    }


    @Override
    public void sendingChatMessage(String message, String sender) {
        ArrayList<String> receivers = new ArrayList<>();
        for (String nickname : connections.keySet()) {
            if (message.toLowerCase().contains("@"+nickname.toLowerCase())) {
                receivers.add(nickname);
            }
        }
        if(receivers.isEmpty()){
            sendBroadCastMessage(new ReceivedChatMessage(sender, message));
        }else{
            for (String nickname : connections.keySet()) {
                if(receivers.contains(nickname)){//TODO thread should be done right?
                    new Thread(() -> {
                        connections.get(nickname).sendChatMessageIfPlayer(sender, message);
                    }).start();
                }
            }
        }
    }

    /**
     * Broadcasts the information of a root card that has been placed by a player.
     * This method is called when a player successfully places a root card on the board.
     * It broadcasts the information of the placed root card, including the player's nickname, the side of the card, the card ID, and a flag indicating if all players have placed their root cards.
     *
     * @param nickname The nickname of the player who has placed the card.
     * @param side The side of the card chosen by the player. True for one side, false for the other.
     * @param cardId The id of the card that has been placed.
     * @param allWithRootCardPlaced A boolean flag indicating if all players have placed their root cards.
     */
    @Override
    public void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId,
                                                                   boolean allWithRootCardPlaced) {
        //send the card placed to all the clients, turn is 0 since it is the first card placed.
        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, new Point(0, 0), side, 0));
        try {
            //send the points and resources of the player to all the clients
            sendBroadCastMessage(new RefreshedPoints(nickname, controller.getPlayerPoints(nickname)));
            sendBroadCastMessage(new RefreshedResources(nickname, controller.getPlayerResources(nickname)));
        } catch (NoNameException e) {
            // This should never occur
            System.out.println("Debugging error: NoNameException in sendingPlacedRootCardAndWhenCompleteObjectiveCards");
        }

        //if all the root card are placed, we send the objective cards to all the clients(common) and the secret objective cards to a specific client.
        if (allWithRootCardPlaced) {
            //send the objective cards to all the clients, the common objective cards
            sendBroadCastMessage(
                    new ShowObjectiveCards(new ArrayList<>(Arrays.asList(controller.getCommonObjectiveCards()))));
            for (ClientHandler connection : connections.values()) {
                //send the secret objective cards to a specific client.
                connection.sendSecretObjectives();
            }
        }
    }
    /**
     * Sends the hands of the players and starts the game flow when all players have chosen their secret objective cards.
     *
     * This method is used to send the hands of the players to the clients. It also checks if all players have chosen their secret objective cards.
     * If all players have chosen their secret objective cards, it starts the game flow.
     *
     * @param nickname The nickname of the player whose hand is being sent.
     * @param allWithSecretObjectiveCardChosen A boolean flag indicating if all players have chosen their secret objective cards.
     */
    @Override
    //this method is called when all the players have chosen their secret objective cards.
    public void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname,
                                                                             boolean allWithSecretObjectiveCardChosen) {
        //sendHand method manages the sending of the hand to the client(private if it is the client, hidden to all others)
        //if allWithSecretObjectiveCardChosen sends to all the clients the starting player.
        for (ClientHandler connection : connections.values()) {
            connection.sendHand(nickname, allWithSecretObjectiveCardChosen);
        }
    }
    /**
     * Implements the sendPlacedCard method of the NetworkPlug interface.
     *
     * This method is responsible for broadcasting the information of a card that has been placed by a player.
     * It is called when a player successfully places a card on the board.
     * It broadcasts the information of the placed card, including the player's nickname, the card ID, the position of the card, and the side of the card.
     *
     * @param nickname The nickname of the player who has placed the card.
     * @param cardId The id of the card that has been placed.
     * @param position The position where the card has been placed on the board.
     * @param side The side of the card chosen by the player. True for one side, false for the other.
     */
    @Override
    public void sendPlacedCard(String nickname, int cardId, Point position, boolean side) {
        //send the card placed to all the clients
        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, position, side, controller.getTurn()));
        try {
            sendBroadCastMessage(new RefreshedPoints(nickname, controller.getPlayerPoints(nickname)));
            sendBroadCastMessage(new RefreshedResources(nickname, controller.getPlayerResources(nickname)));
        } catch (NoNameException e) {
            System.out.println("Debugging error: NoNameException in sendPlacedCard");
        }
        //notify the new turn to all the clients
        sendBroadCastMessage(new TurnInfo(controller.getCurrentPlayer(), controller.getGameState()));
    }
    /**
     * Sends the drawn card information to all clients.
     *
     * This method is used to broadcast the information of a card that has been drawn by a player.
     * It sends the player's nickname, the ID of the new card, the head of the deck, a flag indicating if the card is gold,
     * and an integer indicating whether the card is on the table or the deck.
     *
     * @param nickname The nickname of the player who has drawn the card.
     * @param newCardId The ID of the new card that has been drawn.
     * @param headDeck The head of the deck after the card has been drawn.
     * @param gold A boolean flag indicating if the card is gold.
     * @param onTableOrDeck An integer indicating whether the card is on the table or the deck.
     */
    @Override
    public void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) {
        for (String player : connections.keySet()) {
            //send the hiddenHand to the players different from the player that has drawn the card.
            //the player that has drawn the card receives the new card in the hand(different method)
            connections.get(player).sendDrawnCardIfPlayer(nickname, newCardId, headDeck, gold, onTableOrDeck);
        }
    }
    /**
     * Sends the end game signal to all clients.
     *
     * This method is used to broadcast the end game signal to all clients.
     * It is called when the game has reached its end condition.
     * The method should gather the final points and ranking of the players from the game controller
     * and send this information to all clients.
     */
    @Override
    public void sendEndGame() {
        //it sends the extra points(objective points), the ranking contains all the information of the players.
        sendBroadCastMessage(new ShowPointsFromObjectives(controller.getExtraPoints()));
        sendBroadCastMessage(new ShowRanking(controller.getRanking()));
    }
    /**
     * Disconnects all clients from the server.
     *
     * This method is used to broadcast a disconnection signal to all clients.
     * It is called when the server needs to terminate all active connections, when a client is disconnected.
     */
    @Override
    public void disconnectAll() {
        sendBroadCastMessageDisconnection(new StopGaming());
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
         * This constructor is used to create a new ClientHandler.
         * @param socket The client socket to be handled.
         */
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            controller = Controller.getInstance();
            networkHandler = NetworkHandler.getInstance();
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
                //disconnect all the clients connected to the server RMI and Socket
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
                    //set the nickname of the player
                    nickname = parsedMessage.getNickname();
                    //communicate to all the clients the new user
                    networkHandler.refreshUsersBroadcast();
                    //send the status of the login to the client
                    sendMessage(new StatusLogin(controller.isFirst(parsedMessage.getNickname())));

                    //if is not first, it delivers if the lobby is ready to start
                    if(!controller.isFirst(parsedMessage.getNickname())){
                        sendMessage(new LobbyIsReady(controller.lobbyIsReady()));
                    }
                    //check if all players have join the lobby(number of players chosen and enough number of players)
                    networkHandler.finalizingNumberOfPlayersBroadcast();
                } catch (SameNameException e) {
                    sendErrorMessage(ErrorType.NAME_ALREADY_USED);
                } catch (LobbyCompleteException e) {
                    sendErrorMessage(ErrorType.LOBBY_ALREADY_FULL);
                    hastaLaVistaBaby(); // as per diagram
                }
            } else if (message instanceof NumberOfPlayersMessage) {
                //this message sent the number of players in the lobby
                NumberOfPlayersMessage parsedMessage = (NumberOfPlayersMessage) message;
                try {
                    controller.initializeLobby(parsedMessage.getNumber());
                    //check if all players have join the lobby(number of players chosen and enough number of players)
                    networkHandler.finalizingNumberOfPlayersBroadcast();
                } catch (ClosingLobbyException e) {
                    sendErrorMessage(ErrorType.LOBBY_IS_CLOSED);
                }
            } else if (message instanceof ColorChosen) {
                ColorChosen parsedMessage = (ColorChosen) message;
                try {
                    //set the colour of the player, and return if the lobby is ready to start
                    boolean isLobbyReadyToStart = controller.setColourAndGameIsReadyToStart(parsedMessage.getNickname(),
                            parsedMessage.getColor());
                    //communicate to all the clients the new user and the new colour
                    networkHandler.refreshUsersBroadcast();
                    //if the lobby is ready to start, we send the message to all the clients(starting card)
                    if (isLobbyReadyToStart) {
                        networkHandler.gameIsStartingBroadcast();
                    }
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
                } catch (ColorAlreadyTakenException e) {
                    sendErrorMessage(ErrorType.COLOR_UNAVAILABLE);
                }
            } else if(message instanceof SentChatMessage){
                SentChatMessage sentChatMessage = (SentChatMessage) message;
                networkHandler.sendChatMessageBroadcast(sentChatMessage.getSender(), sentChatMessage.getMessage());
            } else if (message instanceof ChosenStartingCardSide) {
                //this message is used to choose the side of the root card of the player.
                ChosenStartingCardSide parsedMessage = (ChosenStartingCardSide) message;
                try {
                    int cardId = controller.placeRootCard(parsedMessage.getNickname(), parsedMessage.isSide());

                    //check if all clients have chosen the root card, if so we send the common objective cards to all the clients.
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
                //this message is used to choose the secret objective card of the player.
                ChosenObjectiveCard parsedMessage = (ChosenObjectiveCard) message;
                try {
                    //index card should be between 0, 1.
                    controller.chooseObjectiveCard(parsedMessage.getNickname(), parsedMessage.getIndexCard());
                    //take the correct the position of the card and respond to the client with the correct choice.
                    sendMessage(new ObjectiveCardChosen(parsedMessage.getIndexCard()));

                    //check if all clients have chosen the secret objective card, if so we send the hands to all the clients.
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
                //this message is used to place a card on the board.
                CardToBePositioned parsedMessage = (CardToBePositioned) message;
                try {
                    int cardId = controller.placeCard(parsedMessage.getNickname(), parsedMessage.getHandPlacement(),
                            parsedMessage.getPosition(), parsedMessage.isSide());
                    //send the card placed to all the clients
                    networkHandler.sendPlacedCardBroadcast(parsedMessage.getNickname(), cardId,
                            parsedMessage.getPosition(), parsedMessage.isSide());
                    //two possible end game situations: deck finished we finished in placing phase, or we normally finish in drawing phase.
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
                //this message is used to draw a card.
                CardToBeDrawn parsedMessage = (CardToBeDrawn) message;
                try {
                    //this set the new card in the hand of the player
                    controller.drawCard(parsedMessage.getNickname(), parsedMessage.isGold(),
                            parsedMessage.getOnTableOrOnDeck());
                    // this get the new card on the table. It is -1, we send anyway the newCardId to the client, but is the same as previous.
                    int newCardId = controller.newCardOnTable(parsedMessage.isGold(), parsedMessage.getOnTableOrOnDeck());
                    //this get the head of the deck, the new card is drawn from.
                    Kingdom headDeck = controller.getHeadDeck(parsedMessage.isGold());

                    networkHandler.sendDrawnCardBroadcast(parsedMessage.getNickname(), newCardId, headDeck,
                            parsedMessage.isGold(), parsedMessage.getOnTableOrOnDeck());

                    //two possible end game situations: deck finished we finished in placing phase, or we normally finish in drawing phase.
                    if (controller.isEndGame()) {
                        networkHandler.sendEndGameBroadcast();
                    }

                    //we send the new hand of the player. Unicast message.
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
                //disconnect all the clients connected to the server RMI and Socket
                connections.remove(clientSocket.getRemoteSocketAddress().toString());
                NetworkHandler.getInstance().disconnectBroadcast();
            }
        }

        /**
         * This method is used to send a chat message to a specific client without using broadcast.
         *
         * @param sender  The nickname of the sender.
         * @param message The message.
         */
        public void sendChatMessageIfPlayer(String sender, String message){
            sendMessage(new ReceivedChatMessage(sender, message));
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
            //if the player is the one that has drawn the card, we send the hidden hand to the player.
            if (!this.nickname.equals(nickname)) {
                try {
                    sendMessage(new ShowHiddenHand(nickname, controller.getHiddenHand(nickname)));
                } catch (NoNameException e) {
                    System.out.println("No name exception");
                }
            }
            //this message has two cards information: the new card drawn and the head deck
            //onTableOrDeck is used to know if we should update the table.
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
            //this method is called loop in all connections by the method above.

            //if the player is the one that has drawn the card, we send the hand to the player.
            if (this.nickname.equals(nickname)) {
                try {
                    sendMessage(new ShowHand(nickname, controller.getHand(nickname)));
                } catch (NoNameException e) {
                    System.out.println("No name exception");
                }
            } else {
                //if the player is not the one that has drawn the card, we send the hidden hand to the player.
                try {
                    sendMessage(new ShowHiddenHand(nickname, controller.getHiddenHand(nickname)));
                } catch (NoNameException e) {
                    System.out.println("No name exception");
                }
            }
            //if all the players have chosen the secret objective card, we send the starting player to all the clients.
            if (allWithSecretObjectiveCardChosen) {
                sendMessage(new FirstPlayer(controller.getFirstPlayer()));
            }
        }
        /**
         * Sends the starting card information to the client.
         *
         * This method is used to send the starting card of the player to the client.
         * It retrieves the starting card from the game controller using the player's nickname and sends this information to the client.
         *
         * Please note that this method should be called at the beginning of the game, after the player has been successfully added to the game and the game is starting.
         */
        public void sendStartingCard() {
            try {
                sendMessage(new ShowStartingCard(controller.getStartingCard(nickname)));
            } catch (NoNameException e) {
                System.out.println("No name exception");
            }
        }
    }
}