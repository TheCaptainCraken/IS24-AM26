package it.polimi.ingsw.network.server.socket;

import java.net.*;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.exception.ClosingLobbyException;
import it.polimi.ingsw.model.exception.ColorAlreadyTakenException;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NoNameException;
import it.polimi.ingsw.model.exception.SameNameException;
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
import it.polimi.ingsw.network.messages.server.login.StatusLogin;

import java.io.*;

public class NetworkServerSocket {

    private ServerSocket serverSocket;

    public NetworkServerSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        while (2 + 2 == 4) {
            Socket new_connection = serverSocket.accept();

            new ClientHandler(new_connection).run();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Controller controller;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            controller = Controller.getInstance();
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
                    // Salvo connessione
                    // Condivido con altri giocatori
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
                    // wait stuff for players to join
                } catch (ClosingLobbyException e) {
                    sendErrorMessage(ErrorType.LOBBY_IS_CLOSED);
                    hastaLaVistaBaby(); // as per diagram
                }
            } else if (message instanceof ColorChosen) {
                ColorChosen parsedMessage = (ColorChosen) message;
                try {
                    boolean isLobbyReadyToStart = controller.setColour(parsedMessage.getNickname(),
                            parsedMessage.getColor());
                    // Ok (number of players in the lobby, name)
                    if (isLobbyReadyToStart) {
                        controller.start();
                        // GameStart()
                    }
                } catch (NoNameException e) {
                    sendErrorMessage(ErrorType.NAME_UNKNOWN);
                } catch (ColorAlreadyTakenException e) {
                    sendErrorMessage(ErrorType.COLOR_UNAVAILABLE);
                }
            } else if (message instanceof ChosenStartingCardSide) {
                // set starting card
            } else if (message instanceof ChosenObjectiveCard) {
                // set objective card
            } else if (message instanceof CardToBePositioned) {
                // set card position
            } else if (message instanceof CardToBeDrawn) {
                // draw card
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

        private void sendErrorMessage(ErrorType errorType) {
            sendMessage(new ErrorMessage(errorType));
        }

        private void sendMessage(ServerMessage message) {
            try {
                out.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
