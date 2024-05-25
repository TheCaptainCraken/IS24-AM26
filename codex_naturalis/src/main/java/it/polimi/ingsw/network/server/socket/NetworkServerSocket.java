package it.polimi.ingsw.network.server.socket;

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
import it.polimi.ingsw.network.messages.ErrorType;
import it.polimi.ingsw.network.messages.client.ClientMessage;
import it.polimi.ingsw.network.messages.client.gameflow.CardToBeDrawn;
import it.polimi.ingsw.network.messages.client.gameflow.CardToBePositioned;
import it.polimi.ingsw.network.messages.client.gamestart.ChosenObjectiveCard;
import it.polimi.ingsw.network.messages.client.gamestart.ChosenStartingCardSide;
import it.polimi.ingsw.network.messages.client.login.ColorChosen;
import it.polimi.ingsw.network.messages.client.login.LoginMessage;
import it.polimi.ingsw.network.messages.client.login.NumberOfPlayersMessage;
import it.polimi.ingsw.network.messages.server.ErrorMessage;
import it.polimi.ingsw.network.messages.server.ServerMessage;
import it.polimi.ingsw.network.messages.server.endgame.ShowPointsFromObjectives;
import it.polimi.ingsw.network.messages.server.endgame.ShowRanking;
import it.polimi.ingsw.network.messages.server.gameflow.CardIsPositioned;
import it.polimi.ingsw.network.messages.server.gameflow.RefreshedPoints;
import it.polimi.ingsw.network.messages.server.gameflow.RefreshedResources;
import it.polimi.ingsw.network.messages.server.gameflow.ShowNewTableCard;
import it.polimi.ingsw.network.messages.server.gameflow.TurnInfo;
import it.polimi.ingsw.network.messages.server.gamestart.FirstPlayer;
import it.polimi.ingsw.network.messages.server.gamestart.GiveSecretObjectiveCards;
import it.polimi.ingsw.network.messages.server.gamestart.ShowHand;
import it.polimi.ingsw.network.messages.server.gamestart.ShowHiddenHand;
import it.polimi.ingsw.network.messages.server.gamestart.ShowObjectiveCards;
import it.polimi.ingsw.network.messages.server.gamestart.ShowStartingCard;
import it.polimi.ingsw.network.messages.server.gamestart.ShowTable;
import it.polimi.ingsw.network.messages.server.gamestart.StopWaitingOrDisconnect;
import it.polimi.ingsw.network.messages.server.login.LobbyIsReady;
import it.polimi.ingsw.network.messages.server.login.PlayersAndColorPins;
import it.polimi.ingsw.network.messages.server.login.StatusLogin;
import it.polimi.ingsw.network.server.NetworkHandler;
import it.polimi.ingsw.network.server.NetworkPlug;

import java.io.*;

public class NetworkServerSocket implements NetworkPlug {

    private ServerSocket serverSocket;

    private Controller controller;

    private HashMap<String, ClientHandler> connections;

    /**
     * This constructor is used to create a new NetworkServerSocket.
     * 
     * @param port The port of the server.
     * @throws IOException
     */
    public NetworkServerSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        NetworkHandler.getInstance().addNetworkPlug("socket", this);
        connections = new HashMap<>();
        controller = Controller.getInstance();
        //TODO cosi distruggi sincronizzazione
    }

    /**
     * This method is used to start the server.
     * 
     * @throws IOException
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
        for (String client : connections.keySet()) {
            connections.get(client).sendMessage(message);
        }
    }

    @Override
    // TODO
    public void finalizingNumberOfPlayers() {
       for(ClientHandler client : connections.values()){
           if(controller.isAdmitted(client.getNickname())){
               client.sendMessage(new StopWaitingOrDisconnect(true));
           }else{
               //disconnessione
                client.sendMessage(new StopWaitingOrDisconnect(false));
                client.hastaLaVistaBaby();
           }
       }
    }

    @Override
    public void gameIsStarting() {
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

    @Override
    public void refreshUsers() {
        HashMap<String, Color> playersAndPins = controller.getPlayersAndPins();
        sendBroadCastMessage(new PlayersAndColorPins(playersAndPins));
    }

    @Override
    public void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId,
            boolean allWithRootCardPlaced) {

        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, new Point(0, 0), side));
        try {
            sendBroadCastMessage(new RefreshedPoints(nickname, controller.getPlayerPoints(nickname)));
            sendBroadCastMessage(new RefreshedResources(nickname, controller.getPlayerResources(nickname)));
        } catch (NoNameException e) {
            // This should never occur
            e.printStackTrace();
        }
        if (allWithRootCardPlaced) {
            sendBroadCastMessage(
                    new ShowObjectiveCards(new ArrayList<>(Arrays.asList(controller.getCommonObjectiveCards()))));
            for (ClientHandler connection : connections.values()) {
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

    @Override
    public void sendPlacedCard(String nickname, int cardId, Point position, boolean side) {
        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, position, side));
        try {
            sendBroadCastMessage(new RefreshedPoints(nickname, controller.getPlayerPoints(nickname)));
            sendBroadCastMessage(new RefreshedResources(nickname, controller.getPlayerResources(nickname)));
        } catch (NoNameException e) {
            // This should never occur
            e.printStackTrace();
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

    /**
     * This class is used to handle the connection with the client.
     */
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Controller controller;
        private NetworkHandler networkHandler;
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
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * This method is used to handle the messages received from the client.
         * 
         * @param message The message received from the client.
         * @throws ClassNotFoundException
         */
        private void handleMessage(ClientMessage message) throws ClassNotFoundException {
            if (message instanceof LoginMessage) {
                LoginMessage parsedMessage = (LoginMessage) message;
                // handle login
                try {
                    controller.addPlayer(parsedMessage.getNickname());
                    nickname = parsedMessage.getNickname();
                    networkHandler.refreshUsersBroadcast();
                    sendMessage(new StatusLogin(controller.getIsFirst(parsedMessage.getNickname())));
                    if(!controller.getIsFirst(parsedMessage.getNickname())){
                        sendMessage(new LobbyIsReady(controller.lobbyIsReady()));
                    }
                    networkHandler.finalizingNumberOfPlayersBroadcast();
                } catch (SameNameException e) {
                    sendErrorMessage(ErrorType.NAME_ALREADY_USED);
                } catch (LobbyCompleteException e) {
                    sendErrorMessage(ErrorType.LOBBY_ALREADY_FULL);
                    hastaLaVistaBaby(); // as per diagram
                } catch (NoNameException e) {
                    //TODO
                }
            } else if (message instanceof NumberOfPlayersMessage) {
                NumberOfPlayersMessage parsedMessage = (NumberOfPlayersMessage) message;
                try {
                    controller.initializeLobby(parsedMessage.getNumber());
                    networkHandler.finalizingNumberOfPlayersBroadcast();
                } catch (ClosingLobbyException e) {
                    sendErrorMessage(ErrorType.LOBBY_IS_CLOSED);
                    hastaLaVistaBaby(); // as per diagram
                } catch (LobbyCompleteException e) {
                    sendErrorMessage(ErrorType.LOBBY_ALREADY_FULL);
                    hastaLaVistaBaby(); // as per diagram
                } catch (SameNameException e) {
                    sendErrorMessage(ErrorType.NAME_ALREADY_USED);
                    hastaLaVistaBaby(); // as per diagram
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
                    hastaLaVistaBaby(); // as per diagram
                }
            } else if (message instanceof ColorChosen) {
                ColorChosen parsedMessage = (ColorChosen) message;
                try {
                    boolean isLobbyReadyToStart = controller.setColourAndLobbyisReadyToStart(parsedMessage.getNickname(),
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
                    int CardId = controller.drawCard(parsedMessage.getNickname(), parsedMessage.isGold(),
                            parsedMessage.getOnTableOrOnDeck());

                    int newCardId = controller.newCardOnTable(parsedMessage.isGold(), parsedMessage.getOnTableOrOnDeck());

                    Kingdom headDeck = controller.getHeadDeck(parsedMessage.isGold());

                    networkHandler.sendDrawnCardBroadcast(parsedMessage.getNickname(), CardId, headDeck,
                            parsedMessage.isGold(), newCardId);

                    if (controller.isEndGame()) {
                        networkHandler.sendEndGameBroadcast();
                    }

                    sendMessage(new ShowHand(parsedMessage.getNickname(), controller.getHand(nickname)));
                } catch (WrongGamePhaseException e) {
                    sendErrorMessage(ErrorType.WRONG_PHASE);
                } catch (NoTurnException e) {
                    sendErrorMessage(ErrorType.NO_TURN);
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
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
                e.printStackTrace();
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
                e.printStackTrace();
            }
        }

        /**
         * This method is used to send the common objective cards to the clients. It is
         * a broadcast call.
         * 
         * //@param objectiveCardIds The ids of the common objective cards.
         */
        public void sendSecretObjectives() {
            try {
                ArrayList<Integer> choices = new ArrayList<>(
                        Arrays.asList(controller.getSecretObjectiveCardsToChoose(nickname)));
                sendMessage(new GiveSecretObjectiveCards(choices));
            } catch (NoNameException e) {
                e.printStackTrace();
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
                   //TODO
                }
            }
            sendMessage(new ShowNewTableCard(newCardId, gold, onTableOrDeck, headDeck));
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
                    e.printStackTrace();
                }
            } else {
                try {
                    sendMessage(new ShowHiddenHand(nickname, controller.getHiddenHand(nickname)));
                } catch (NoNameException e) {
                    e.printStackTrace();
                }
            }

            if (allWithSecretObjectiveCardChosen) {
                // TODO sistemare isFirstPlayer
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
    }

}
