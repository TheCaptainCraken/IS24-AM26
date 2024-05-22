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
import it.polimi.ingsw.network.messages.server.gameflow.ShowDrawnCard;
import it.polimi.ingsw.network.messages.server.gameflow.ShowNewTableCard;
import it.polimi.ingsw.network.messages.server.gameflow.TurnInfo;
import it.polimi.ingsw.network.messages.server.gamestart.FirstPlayer;
import it.polimi.ingsw.network.messages.server.gamestart.GiveSecretObjectiveCards;
import it.polimi.ingsw.network.messages.server.gamestart.ShowHand;
import it.polimi.ingsw.network.messages.server.gamestart.ShowHiddenHand;
import it.polimi.ingsw.network.messages.server.gamestart.ShowObjectiveCards;
import it.polimi.ingsw.network.messages.server.gamestart.ShowStartingCard;
import it.polimi.ingsw.network.messages.server.gamestart.ShowTable;
import it.polimi.ingsw.network.messages.server.gamestart.StopWaiting;
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
        controller = Controller.getInstance();
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
            connection.run();
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
    //TODO
    public void finalizingNumberOfPlayers(boolean lobbyIsReady) {
        if (lobbyIsReady) {
            sendBroadCastMessage(new StopWaiting());
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

        ArrayList<String> players = controller.getNicknames();
        //TODO cosi rimandi di nuovo a tutti. In più tue connessioni non quelle di tutti, potresti avere null
        //starting card è un messaggio unicast a singolo player.
        for (String player : players) {
            try {
                sendBroadCastMessage(new ShowStartingCard(controller.getStartingCard(player)));
            } catch (NoNameException e) {
                // This error should never occur
                e.printStackTrace();
            }
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
        //TODO punti e risorse
        if (allWithRootCardPlaced) {
            sendBroadCastMessage(
                    new ShowObjectiveCards(new ArrayList<>(Arrays.asList(controller.getCommonObjectiveCards()))));
            //TODO puoi avere null
            for (String player : controller.getNicknames()) {
                connections.get(player).sendSecretObjectives();
            }
        }
    }

    @Override
    public void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname,
            boolean allWithSecretObjectiveCardChosen) {
        //TODO puoi avere null
        for (String player : controller.getNicknames()) {
            connections.get(player).sendHand(nickname, allWithSecretObjectiveCardChosen);
        }
    }

    @Override
    public void sendPlacedCard(String nickname, int cardId, Point position, boolean side) {
        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, position, side));
        //TODO non è il nickname, è il nuovo giocatore in gioco. Controller.getInstance().getCurrentPlayer()
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

        /**
         * This method is used to handle the messages received from the client.
         */
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
                    sendMessage((new StatusLogin(controller.getIsFirst(parsedMessage.getNickname()))));
                    //TODO lobbyIsReady
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
                    boolean isLobbyReadyToStart = controller.setColour(parsedMessage.getNickname(),
                            parsedMessage.getColor());
                    if (isLobbyReadyToStart) {
                        //TODO non c'è questa parte la fa già networkHandler
                        controller.start();
                        //TODO non deve essere nell'if ma furoi. inviamo sempre il nuovo colore
                        networkHandler.refreshUsersBroadcast();
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

                    //TODO manca parte if finale.
                    // Check if the game has ended
//                    if(Controller.getInstance().isEndGame()){
//                        // Broadcast the end game information
//                        NetworkHandler.getInstance().sendEndGameBroadcast();
//                    }
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
                    //TODO perchè card id, è la nuova carta che c'è sul tavolo. devi mettere onTableOrOnDeck.
                    int newCardId = controller.newCardOnTable(parsedMessage.isGold(), CardId);
                    Kingdom headDeck = controller.getHeadDeck(parsedMessage.isGold());
                    //TODO anche qua che senso ha? mandi due volte newCardId. Devi metter onTableOrDeck.
                    networkHandler.sendDrawnCardBroadcast(parsedMessage.getNickname(), newCardId, headDeck,
                            parsedMessage.isGold(), newCardId);

                    if (controller.isEndGame()) {
                        networkHandler.sendEndGameBroadcast();
                    }

                    sendMessage(new ShowDrawnCard(newCardId, parsedMessage.getNickname()));
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
         * //@param objectiveCardIds The ids of the common objective cards. TODO
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
                sendMessage(new ShowDrawnCard(newCardId, nickname));
            }
            //TODO manca carta sul deck
            sendMessage(new ShowNewTableCard(newCardId, gold, onTableOrDeck));
            //TODO non è il nickname, è il nuovo giocatore in gioco. Controller.getInstance().getCurrentPlayer()
            sendMessage(new TurnInfo(nickname, controller.getGameState()));
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
                    sendMessage(new ShowHand(nickname, new ArrayList<>(Arrays.asList(controller.getHand(nickname)))));
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
                //TODO sistemare isFirstPlayer
                sendMessage(new FirstPlayer(controller.getFirstPlayer()));
            }
        }
    }

}
