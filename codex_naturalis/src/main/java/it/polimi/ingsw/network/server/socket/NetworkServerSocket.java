package it.polimi.ingsw.network.server.socket;

import java.net.*;

import it.polimi.ingsw.controller.server.Controller;
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
                        out.writeObject(new ErrorMessage(ErrorType.INVALID_MESSAGE));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // manca la parte di fine della partita.
        private void handleMessage(ClientMessage message) throws ClassNotFoundException {
            if (message instanceof LoginMessage) {
                // login
            } else if (message instanceof NumberOfPlayersMessage) {
                // set number of players
            } else if (message instanceof ColorChosen) {
                // set color
            } else if (message instanceof ChosenStartingCardSide) {
                // set starting card
            } else if (message instanceof ChosenObjectiveCard) {
                // set objective card
            } else if (message instanceof CardToBePositioned) {
                // set card position
            } else if (message instanceof CardToBeDrawn) {
                // draw card
            } else {
                throw new ClassNotFoundException("Message not recognized");
            }
        }

        private void closeConnection() throws IOException {
            in.close();
            out.close();
            clientSocket.close();
        }

    }

}
