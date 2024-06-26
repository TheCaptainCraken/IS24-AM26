package it.polimi.ingsw.network.socket;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.network.NetworkClient;
import it.polimi.ingsw.network.socket.messages.client.ConnectionClient;
import it.polimi.ingsw.network.socket.messages.client.ConnectionClientForServer;
import it.polimi.ingsw.network.socket.messages.client.gameflow.SentChatMessage;
import it.polimi.ingsw.network.socket.messages.client.ClientMessage;
import it.polimi.ingsw.network.socket.messages.client.gameflow.CardToBeDrawn;
import it.polimi.ingsw.network.socket.messages.client.gameflow.CardToBePositioned;
import it.polimi.ingsw.network.socket.messages.client.gamestart.ChosenObjectiveCard;
import it.polimi.ingsw.network.socket.messages.client.gamestart.ChosenStartingCardSide;
import it.polimi.ingsw.network.socket.messages.client.login.ColorChosen;
import it.polimi.ingsw.network.socket.messages.client.login.LoginMessage;
import it.polimi.ingsw.network.socket.messages.client.login.NumberOfPlayersMessage;
import it.polimi.ingsw.network.socket.messages.server.ConnectionServer;
import it.polimi.ingsw.network.socket.messages.server.ConnectionServerForClient;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.view.model.Phase;


import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * The ClientSocket class implements the NetworkClient interface and provides the functionality for a client to communicate with a server over a socket connection.
 * It defines methods for game actions such as login, choosing color, drawing cards etc., and sends these actions to the server.
 * It also handles responses from the server and updates the client's view accordingly.
 *
 * @author Arturo
 */
public class ClientSocket implements Runnable, NetworkClient{
    /**
     * The controller that manages the client's view and game logic.
     */
    private final Controller controller;

    /**
     * The socket connection to the server.
     */
    private final Socket socket;
    /**
     * The input stream for receiving messages from the server.
     */
    private final InputStream inputStream;
    /**
     * The object input stream for deserializing messages from the server.
     */
    private final ObjectInputStream objInputStream;
    /**
     * The output stream for sending messages to the server.
     */
    private final OutputStream outputStream;
    /**
     * The object output stream for serializing messages to the server.
     */
    private final ObjectOutputStream objOutputStream;

    private boolean connection;

    /**
     * Creates a new ClientSocket with the given controller, address, and port.
     * It establishes a socket connection to the server and initializes input and output streams for communication.
     *
     * @param controller The controller that manages the client's view and game logic.
     * @param address The address of the server.
     * @param port The port of the server.
     *
     * @throws IOException if an I/O error occurs while creating the socket or the input/output streams.
     */
    public ClientSocket(Controller controller, String address, int port) throws IOException {
        socket = new Socket(address, port);

        outputStream = socket.getOutputStream();
        objOutputStream = new ObjectOutputStream(outputStream);

        inputStream = socket.getInputStream();
        objInputStream = new ObjectInputStream(inputStream);

        this.controller = controller;

        connection = true;

        //start the thread to check if the client is still connected
        new Thread(this::iAmConnected).start();
    }

    //Methods that correspond to the actions a player takes throughout the life of the connection.
    // They have a 1:1 correspondence with methods used for RMI and NetworkClient.

    /**
     * NetworkClient implementation
     *
     * Logs in the player with the given nickname.
     * This method is used to authenticate a player in the game.
     *
     * @param nickname The nickname of the player.
     */
    @Override
    public void login(String nickname) {
        //create a message with the nickname
        //wait a response from the server, to know if is the first player or to wait the first player to choose the number of players.
        // if the nickname is already taken, the server will send a message to the client
        //Possible errors can be:
        // - the nickname is already taken: NAME_ALREADY_TAKEN
        Controller.setPhase(Phase.WAIT);
        Controller.setNickname(nickname);
        ClientMessage message = new LoginMessage(nickname);
        sendMessage(message);
    }

    /**
     * NetworkClient implementation
     *
     * Inserts the number of players in the game.
     * This method is used to set the number of players that will participate in the game.
     *
     * @param numberOfPlayers The number of players.
     */
    @Override
    public void insertNumberOfPlayers(int numberOfPlayers){
        //input always correct. The client interface will control the input.
        //wait for message STOP_WAITING_OR_DISCONNECT true.
        Controller.setPhase(Phase.WAIT);
        ClientMessage message = new NumberOfPlayersMessage(numberOfPlayers);
        sendMessage(message);
        controller.correctNumberOfPlayers(numberOfPlayers);
    }

    /**
     * NetworkClient implementation
     *
     * Chooses the color for the player.
     * This method is used to set the color of the player in the game.
     *
     * @param color The color chosen by the player.
     */
    @Override
    public void chooseColor(Color color) {
        //wait that all players have chosen the color.
        Controller.setPhase(Phase.WAIT_ALL_CHOSEN_COLOR);
        //send the message to the server.
        //Possible errors can be:
        // - the color is already taken: COLOR_UNAVAILABLE
        ClientMessage message = new ColorChosen(controller.getNickname(), color);
        sendMessage(message);
    }

    /**
     * NetworkClient implementation
     *
     * Sends a chat message.
     * This method is used to send a chat message to the other players in the game.
     *
     * @param sender The nickname of the player who sent the message.
     * @param message The message sent by the player.
     */
    @Override
    public void sendChatMessage(String sender, String message) {
        SentChatMessage sentChatMessage = new SentChatMessage(sender, message);
        sendMessage(sentChatMessage);
    }

    /**
     * NetworkClient implementation
     *
     * Chooses the side of the starting card for the player.
     * This method is used to set the side of the starting card of the player in the game.
     *
     * @param side The side of the starting card chosen by the player.
     */
    @Override
    public void chooseSideStartingCard(boolean side){
        //input control by the client interface.
        //wait that all players have chosen the side of the starting card.
        Controller.setPhase(Phase.WAIT_ALL_CHOSEN_STARTING_CARD);
        //send the message to the server.
        //possible errors can be:
        // - the turn is not correct: NO_TURN
        // - the phase is not correct: WRONG_PHASE
        ClientMessage message = new ChosenStartingCardSide(controller.getNickname(), side);
        sendMessage(message);
    }

    /**
     * NetworkClient implementation
     *
     * Chooses the secret objective card for the player.
     * This method is used to set the secret objective card of the player in the game.
     *
     * @param indexCard The index of the secret objective card chosen by the player.
     */
    @Override
    public void chooseSecretObjectiveCard(int indexCard) {
        //input control by the client interface.
        //wait that all players have chosen the secret objective card.
        Controller.setPhase(Phase.WAIT_ALL_CHOSEN_SECRET_CARD);
        //send the message to the server.
        //possible errors can be:
        // - the turn is not correct: NO_TURN
        // - the phase is not correct: WRONG_PHASE
        ClientMessage message = new ChosenObjectiveCard(controller.getNickname(), indexCard);
        sendMessage(message);
    }

    /**
     * NetworkClient implementation
     *
     * Plays a card from the player's hand.
     * This method is used to play a card from the player's hand onto the game table.
     * It takes three parameters: the index of the card in the player's hand,
     * the position on the table where the card will be placed,
     * and the side of the card.
     *
     * @param indexHand The index of the card in the player's hand.
     * @param position The position on the table where the card will be placed.
     * @param side The side of the card.
     */
    @Override
    public void playCard(int indexHand, Point position, boolean side) {
        //the FSM controller is in GAME_FLOW, no need to change it
        ClientMessage message = new CardToBePositioned(controller.getNickname(), indexHand, position, side);
        sendMessage(message);
    }

    /**
     * NetworkClient implementation
     *
     * Draws a card for the player.
     * This method is used to draw a card for the player.
     * It takes three parameters: the nickname of the player,
     * a boolean indicating whether the card is gold,
     * and an integer indicating whether the card is drawn from the table or the deck(-1 deck, o or 1 for table).
     *
     * @param nickname The nickname of the player.
     * @param gold A boolean indicating whether the card is gold.
     * @param onTableOrDeck An integer indicating whether the card is drawn from the table or the deck.
     */
    @Override
    public void drawCard(String nickname, boolean gold, int onTableOrDeck) {
        //the FSM controller is in GAME_FLOW, no need to change it
        ClientMessage message = new CardToBeDrawn(nickname, gold, onTableOrDeck);
        sendMessage(message);
    }

   // Methods that are related to handling updates from the server and updating the view should be called by the methods
   // mentioned above once they have finished their logic. These methods are unique to the Socket implementation.
    /**
     * The run method is the entry point for the thread that handles communication with the server.
     * It continuously receives messages from the server and handles them accordingly.
     */
    @Override
    public void run() {
        boolean connected = true;
        while(connected){
            ServerMessage serverMessage = null;
            try {
                serverMessage = receiveMessage();
            } catch (IOException e) {
               connected = false;
            }
            handleResponse(serverMessage);
        }

        try {
            if(socket.getInputStream().read() == -1){
                //if the socket is closed correctly, a message StopGaming will be sent
               System.out.println("Connection closed");
            } else {
                //if the socket is not closed correctly
                controller.noConnection();
            }
        } catch (IOException e) {
            controller.noConnection();
        }
    }

    /**
     * This method is used to periodically check if the client is still connected to the server.
     * It runs in a separate thread and pauses for a specified interval (30 seconds in this case) between each check.
     * If the client is no longer connected, we stop gaming client-side.
     */
    private void iAmConnected() {
        while (connection) {
            try {
                connection = false;
                sendMessage(new ConnectionClient());
                Thread.sleep(300000); // 30 seconds
            } catch (InterruptedException e) {
                System.out.println("Error in sleep");
            }
        }

        controller.noConnection();
    }

    /**
     * Handles the response from the server.
     * This method is called by the run method for each received server message.
     * It calls the appropriate method on the controller based on the type of the server message.
     *
     * @param message The server message to be handled.
     */
    public void handleResponse(ServerMessage message) {
        if (message instanceof ConnectionServer) {
            sendMessage(new ConnectionClientForServer());
        }
        else if(message instanceof ConnectionServerForClient) {
            connection = true;
        }
        else if (message != null) {
            message.callController(controller);
        }
    }
    /**
     * Receives a message from the server.
     * This method is called by the run method to receive a server message.
     * It blocks until a message is received or an error occurs.
     *
     * @return The received server message.
     *
     * @throws IOException if an I/O error occurs while receiving the message.
     */
    public ServerMessage receiveMessage() throws IOException {
        ServerMessage answer;
        try {
            answer = (ServerMessage) objInputStream.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("This error should never happen. The server is sending a message that the client does not know how to handle.");
            return null;
        }
        return answer;
    }

    /**
     * Closes the input and output streams and the socket connection to the server.
     * If an I/O error occurs while closing the streams or the connection, it calls the `noConnection` method on the controller.
     */
    public void disconnect(){
        try {
            inputStream.close();
            objInputStream.close();
            objOutputStream.close();
        } catch (IOException e) {
            controller.noConnection();
        }
    }

    /**
     * Sends a message to the server.
     * This method is used to send a message to the server over the socket connection.
     * It takes a Message object as a parameter, which is serialized and sent over the connection.
     *
     * @param message The message to be sent to the server.
     * @throws RuntimeException if an I/O error occurs while sending the message.
     */
    public void sendMessage(ClientMessage message){
        try{
            objOutputStream.writeObject(message);
        } catch (IOException e) {
            controller.noConnection();
        }
    }
}
