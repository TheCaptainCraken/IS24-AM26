package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.GameMaster;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NoNameException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.network.RMI.ServerRMI;
import it.polimi.ingsw.network.socket.NetworkServerSocket;

import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the controller of the network part of the server.
 * It manages the connections with the clients and the broadcast of the
 * messages.
 * In particular protocol connection, when a message should be broadcast, the
 * message is sent to all different connection protocols.
 * Each protocol will send the message to all the clients connected to it. Each
 * protocol has a different way to send the message to the clients, but all
 * its methods are override NetworkPlug.
 */
public class NetworkHandler {
    /**
     * The instance of the controller. It is used to access the controller from the
     * other classes.
     */
    private static final NetworkHandler INSTANCE = new NetworkHandler();

    /**
     * The HashMap is used to store the different protocols types.
     * The key is the name of the protocol and the value is the protocol itself.
     */
    protected static HashMap<String, NetworkPlug> networkInterfacesAndConnections;

    /**
     * Gets the instance of the controller.
     * 
     * @return The instance of the controller.
     */
    public static NetworkHandler getInstance() {
        return INSTANCE;
    }

    /**
     * The instance of the RMI server.
     */
    private static ServerRMI serverRMI;

    /**
     * The instance of the Socket server.
     */
    private static NetworkServerSocket networkServerSocket;

    /**
     * The main method of the server.
     * @param args The arguments of the main method.
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        serverRMI = new ServerRMI();
        networkServerSocket = new NetworkServerSocket(0);
        new Thread(() -> {
            try {
                networkServerSocket.start();
            } catch (IOException e) {
                System.out.println("Cannot start the socket server. Please restart the server.");
            }
        }).start();
    }

    /**
     * Constructor for NetworkHandler. Initializes the
     * networkInterfacesAndConnections HashMap.
     * The HashMap is used to store the different protocols types.
     */
    public NetworkHandler() {
        networkInterfacesAndConnections = new HashMap<>();
    }

    /**
     * Adds a new protocol to the networkInterfacesAndConnections HashMap.
     * 
     * @param nameNetworkPlug The name of the protocol.
     * @param networkPlug     The protocol itself. ServerRMI, ServerSocket...
     *
     */
    public void addNetworkPlug(String nameNetworkPlug, NetworkPlug networkPlug) {
        networkInterfacesAndConnections.put(nameNetworkPlug, networkPlug);
    }

    /**
     * This method is responsible for broadcasting a message to all connected
     * clients when the number of players in the game has been finalized.
     * It first checks if the lobby is ready by calling
     * `Controller.getInstance().lobbyIsReady()`. If the lobby is ready, it iterates
     * over all the network interfaces and calls their respective
     * `finalizingNumberOfPlayers` method.
     * This method is only called when all players are ready, so the number of
     * players is chosen and all players have entered the lobby.
     *
     */
    public void finalizingNumberOfPlayersBroadcast() {
        // Filter players if lobby.size() > maxSize
        boolean lobbyIsReady = Controller.getInstance().lobbyIsReady();
        // only done when all players are ready, so the number of players is chosen, and
        // all players have entered the lobby.
        if (lobbyIsReady) {
            GameMaster saved_game = null;
            try {
                saved_game = Controller.getInstance().tryLoadingGame();
            } catch (Exception e) {
                System.out.println("No saved game found");
            }

            if (saved_game != null && saved_game.getLobby().equals(Controller.getInstance().getLobby())) {
                for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
                    networkPlug.loadGame(saved_game);
                    Controller.getInstance().setGameMaster(saved_game);
                }
            } else {
                for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
                    networkPlug.finalizingNumberOfPlayers();
                }
            }
        }
    }

    /**
     * Broadcasts the message that the game is starting.
     * Sends the message to all the different protocols. The message contains
     * Starting Card unicast and Common Cards.
     * 
     */
    public void gameIsStartingBroadcast() {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.gameIsStarting();
        }
    }

    /**
     * Broadcasts the message of users in the game.
     * The message is sent to all the different protocols. It contains the users in
     * the game, and their colors.
     *
     */
    public void refreshUsersBroadcast() {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.refreshUsers();
        }
    }

    /**
     * Broadcasts the message of a player placing their root card and completing
     * their objective cards.
     * The message is sent to all the different protocols.
     *
     * It is called every time the player places a root card. To all players we send
     * the new placed card.
     * After if @param allWithRootCardPlaced is true, we send the objective cards to
     * all players. (common broadcast and secret unicast)
     *
     * @param nickname              The nickname of the player.
     * @param side                  The side of the card.
     * @param cardId                The id of the card.
     * @param allWithRootCardPlaced A boolean indicating whether all players have
     *                              placed their root cards.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    public void sendingPlacedRootCardAndWhenCompleteObjectiveCardsBroadcast(String nickname, boolean side, int cardId,
            boolean allWithRootCardPlaced) throws NoNameException {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendingPlacedRootCardAndWhenCompleteObjectiveCards(nickname, side, cardId,
                    allWithRootCardPlaced);
        }
    }

    /**
     * Broadcasts the message of a player receiving their hand cards and completing
     * their secret objective cards.
     * The message is sent to all the different protocols.
     *
     * @param nickname                         The nickname of the player.
     * @param allWithSecretObjectiveCardChosen A boolean indicating whether all
     *                                         players have chosen their secret
     *                                         objective cards.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    public void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlowBroadcast(String nickname,
            boolean allWithSecretObjectiveCardChosen) throws NoNameException {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(nickname,
                    allWithSecretObjectiveCardChosen);
        }
    }

    /**
     * Broadcasts the message of a player placing a card. It also sends the new
     * points and resources of the player. It uses controller methods to get these
     * information.
     * The message is sent to all the different protocols.
     *
     * @param nickname The nickname of the player.
     * @param cardId   The id of the card that has been placed.
     * @param position The position where the card has been placed.
     * @param side     The side chosen by the player. True for one side, false for
     *                 the other.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    public void sendPlacedCardBroadcast(String nickname, int cardId, Point position, boolean side)
            throws NoNameException {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendPlacedCard(nickname, cardId, position, side);
        }
    }

    /**
     * Broadcasts the message of a player drawing a card.
     * The message is sent to all the different protocols.
     *
     * @param nickname      The nickname of the player.
     * @param newCardId     The id of the new card drawn.
     * @param headDeck      The head of the deck, the new card is drawn from.
     * @param gold          A boolean indicating whether the card is a gold card.
     * @param onTableOrDeck An integer indicating whether the card is on the table
     *                      or deck.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    public void sendDrawnCardBroadcast(String nickname, Integer newCardId, Kingdom headDeck, boolean gold,
            int onTableOrDeck) throws NoNameException {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendDrawnCard(nickname, newCardId, headDeck, gold, onTableOrDeck);
        }
    }

    /**
     * Broadcasts the end game message to all connected clients.
     * The message is sent to all the different protocols.
     */
    public void sendEndGameBroadcast() {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendEndGame();
        }
        // reset the lobby to null and the controller to null
        Controller.getInstance().cancelFile();
    }

    /**
     * Broadcasts a chat message to all connected clients across all network
     * interfaces.
     * The message is sent to all the different protocols.
     *
     * @param sender  The nickname of the client sending the message.
     * @param message The message to be sent.
     */
    public void sendChatMessageBroadcast(String sender, String message) {
        ArrayList<String> receivers = new ArrayList<>();// need to know globally if the list is empty and then send
                                                        // broadcast or not
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            receivers.addAll(networkPlug.fetchReceivers(message));
        }
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendingChatMessage(sender, message, receivers);
        }
    }

    /**
     * This method is used to disconnect all clients from all network interfaces.
     * It iterates over all the network interfaces and calls their respective
     * disconnectAll method.
     */
    public void disconnectBroadcast() {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.disconnectAll();
        }
        // reset the lobby to null and the controller to null
    }

    /**
     * This method is used to notify all connected clients about the current turn
     * across all network interfaces.
     * It iterates over all the network interfaces and calls their respective
     * notifyTurn method.
     * The notifyTurn method in each network interface is responsible for
     * broadcasting the current turn information to all clients connected via that
     * interface.
     */
    public void notifyTurnBroadcast() {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.notifyTurn();
        }
    }
}
