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
import it.polimi.ingsw.network.messages.server.gamestart.GiveSecretObjectiveCards;
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

    // ip : ClientHandler
    private HashMap<String, ClientHandler> connections;

    public NetworkServerSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        NetworkHandler.getInstance().addNetworkPlug("socket", this);
        controller = Controller.getInstance();
    }

    public void start() throws IOException {
        while (2 + 2 == 4) {
            Socket new_connection = serverSocket.accept();
            ClientHandler connection = new ClientHandler(new_connection);
            connections.put(new_connection.getRemoteSocketAddress().toString(), connection);
            connection.run();
        }
    }

    private void sendBroadCastMessage(ServerMessage message) {
        for (String client : connections.keySet()) {
            connections.get(client).sendMessage(message);
        }
    }

    // TODO: I have no idea what I should do here.
    @Override
    public void finalizingNumberOfPlayers() {
        sendBroadCastMessage(new StopWaiting());
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
        if (allWithRootCardPlaced) {
            sendBroadCastMessage(
                    new ShowObjectiveCards(new ArrayList<>(Arrays.asList(controller.getCommonObjectiveCards()))));

            for (String player : controller.getNicknames()) {
                connections.get(player).sendSecretObjectives();
            }
        }
    }

    @Override
    public void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname,
            boolean allWithSecretObjectiveCardChosen) {
    }// TODO discuss if we have to modify

    @Override
    public void sendPlacedCard(String nickname, int cardId, Point position, boolean side) {
        sendBroadCastMessage(new CardIsPositioned(nickname, cardId, position, side));
        sendBroadCastMessage(new TurnInfo(nickname, controller.getGameState()));
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

        // manca la parte di fine della partita.
        // appena finito, refactoring perché è troppo lungo.
        private void handleMessage(ClientMessage message) throws ClassNotFoundException {
            if (message instanceof LoginMessage) {
                LoginMessage parsedMessage = (LoginMessage) message;
                // handle login
                try {
                    controller.addPlayer(parsedMessage.getNickname());
                    nickname = parsedMessage.getNickname();
                    networkHandler.refreshUsersBroadcast();
                    sendMessage((new StatusLogin(controller.getIsFirst(parsedMessage.getNickname()))));
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
                    networkHandler.finalizingNumberOfPlayersBroadcast(parsedMessage.getNumber());
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
                        controller.start();
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
                    int newCardId = controller.newCardOnTable(parsedMessage.isGold(), CardId);
                    Kingdom headDeck = controller.getHeadDeck(parsedMessage.isGold());
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

        private void hastaLaVistaBaby() {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendErrorMessage(ErrorType errorType) {
            sendMessage(new ErrorMessage(errorType));
        }

        public void sendMessage(ServerMessage message) {
            try {
                out.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendSecretObjectives() {
            try {
                ArrayList<Integer> choices = new ArrayList<>(
                        Arrays.asList(controller.getSecretObjectiveCardsToChoose(nickname)));
                sendMessage(new GiveSecretObjectiveCards(choices));
            } catch (NoNameException e) {
                e.printStackTrace();
            }
        }

        public void sendDrawnCardIfPlayer(String nickname, int newCardId, Kingdom headDeck, boolean gold,
                int onTableOrDeck) {
            if (!this.nickname.equals(nickname)) {
                sendMessage(new ShowDrawnCard(newCardId, nickname));
            }

            sendMessage(new ShowNewTableCard(newCardId, gold, onTableOrDeck));
            sendMessage(new TurnInfo(nickname, controller.getGameState()));
        }
    }

}
