package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.server.NetworkHandler;
import it.polimi.ingsw.network.server.NetworkPlug;
import it.polimi.ingsw.network.server.socket.NetworkServerSocket;
import javafx.util.Pair;

import java.awt.*;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The ServerRMI class implements the RMIServerInterface and NetworkPlug interfaces.
 * It is responsible for handling the server-side logic of the RMI network communication.
 * It maintains a map of connections to RMIClientInterface objects, representing the connected clients.
 *
 * The class provides methods for various game actions such as login, choosing color, placing cards, drawing cards, etc.
 * These methods are invoked by the client and the corresponding actions are performed on the server side.
 *
 * The class also provides methods for broadcasting game state updates to all connected clients.
 * These methods are invoked by the server when the game state changes, and the updates are sent to the clients.
 *
 */
public class ServerRMI implements RMIServerInterface, NetworkPlug {
    static int PORT = 1099; //TODO porta dinamica
    HashMap<String, RMIClientInterface> connections = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerRMI obj = new ServerRMI();
        NetworkServerSocket networkServerSocket = new NetworkServerSocket(0);
        networkServerSocket.start();
    }

    /**
     * The ServerRMI constructor initializes the ServerRMI instance.
     * It adds the instance to the NetworkHandler and sets up the RMI registry and server skeleton.
     * It also binds the server skeleton to the registry.
     *
     * @throws RemoteException if a communication-related error occurs during the execution of a remote method call
     */
    public ServerRMI() throws RemoteException {
        NetworkHandler.getInstance().addNetworkPlug("RMI", this);
        RMIServerInterface serverSkeleton = null;//https://www.baeldung.com/java-rmi
        Registry registry = null;

        try {
            serverSkeleton = (RMIServerInterface) UnicastRemoteObject.exportObject(this, PORT);
            System.out.println("Server skeleton created");
        } catch (RemoteException e) {
            System.out.println("Server skeleton not created");
            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        try {
            registry = LocateRegistry.createRegistry(PORT);
            System.out.println("Registry created");
        } catch (RemoteException e) {
            System.out.println("Registry not created");
            System.out.println("Registry exception: " + e.toString());
            e.printStackTrace();
        }

        if (registry != null && serverSkeleton != null) {
            try {
                registry.bind("Loggable", serverSkeleton);
                System.out.println("Server bound");
                System.out.println("Server ready");
            } catch (RemoteException e) {
                System.out.println("Rebind exception: " + e.toString());
                e.printStackTrace();
            } catch (AlreadyBoundException e) {
                System.out.println("Already bound exception: " + e.toString());
                e.printStackTrace();
            }
        } else {
            if (registry == null) {
                System.out.println("Registry is null, cannot bind the object.");
            }
            if (serverSkeleton == null) {
                System.out.println("serverSkeleton is null, cannot bind the object.");
            }
        }

    }

    /**
     * RMIServerInterface interface method
     *
     * This method is responsible for logging in a client and checking if it's the first one to log in.
     *
     * @param clientRMI The RMIClientInterface object representing the client that is trying to log in.
     * @param nickname The nickname of the client that is trying to log in.
     * @return A boolean value indicating whether the client is the first one to log in.
     * @throws RemoteException If a communication-related error occurs during the execution of a remote method call.
     * @throws SameNameException If a player with the same nickname already exists in the game.
     * @throws LobbyCompleteException If the lobby is already complete and no more players can join.
     */
    @Override
    public boolean loginAndIsFirst(RMIClientInterface clientRMI, String nickname) throws RemoteException, SameNameException, LobbyCompleteException, NoNameException {
        //Add player to the starting lobby, throws exception if the lobby is already complete or the nickname is already taken
        Controller.getInstance().addPlayer(nickname);
        //Add the player to the connections map
        connections.put(nickname, clientRMI);
        //We told players if all joined in
        NetworkHandler.getInstance().finalizingNumberOfPlayersBroadcast();
        //Return whether the player is the first one to log in
        return Controller.getInstance().getIsFirst(nickname);
    }
    /**
     * RMIs interface method
     *
     * This method checks if the lobby is ready for the game to start.
     *
     * @return A boolean value indicating whether the lobby is ready.
     * @throws RemoteException If a communication-related error occurs during the execution of a remote method call.
     */
    @Override
    public boolean lobbyIsReady() throws RemoteException {
        //Return whether the lobby is locked or not
        return Controller.getInstance().isLobbyLocked();
    }

    @Override
    public void insertNumberOfPlayers(int numberOfPlayers) throws
            RemoteException, ClosingLobbyException, SameNameException, LobbyCompleteException, NoNameException {

        Controller.getInstance().initializeLobby(numberOfPlayers);
        //TODO remove all players before, quando viene chiusa la lobby
        //Deletes all other connections that are not in the lobby

        NetworkHandler.getInstance().finalizingNumberOfPlayersBroadcast();
        NetworkHandler.getInstance().refreshUsersBroadcast();
    }
    /**
     * RMIServerInterface interface method
     *
     * This method is responsible for handling the color selection process of a player.
     *
     * @param nickname The nickname of the player who is choosing the color.
     * @param color The color chosen by the player.
     * @throws RemoteException If a communication-related error occurs during the execution of a remote method call.
     * @throws ColorAlreadyTakenException If the chosen color is already taken by another player.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    @Override
    public void chooseColor(String nickname, Color color) throws RemoteException,
            ColorAlreadyTakenException, NoNameException {
        //Set the color of the player, returns true if the color was set successfully. If the color was set successfully, the game is starting.
        //Throws an exception if the color is already taken by another player.
        if(Controller.getInstance().setColour(nickname, color)){
            NetworkHandler.getInstance().gameIsStartingBroadcast();
        }
        //TODO qua sicuri?
        //Refresh the users list for all clients
        NetworkHandler.getInstance().refreshUsersBroadcast();
    }

    /**
     * RMIServerInterface interface method
     *
     * This method is responsible for handling the side selection process of a player's starting card.
     *
     * @param nickname The nickname of the player who is choosing the side.
     * @param side The side chosen by the player. True for one side, false for the other.
     * @throws WrongGamePhaseException If the game is not in the correct phase for this action.
     * @throws NoTurnException If it's not the turn of the player who is trying to perform the action.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    //We could optimize and use a unique function without any
    @Override
    public void chooseSideStartingCard(String nickname, boolean side)
            throws WrongGamePhaseException, NoTurnException, NoNameException {

        // The player chooses the side of their starting card
        int cardId = Controller.getInstance().placeRootCard(nickname, side);
        // Check if all players have placed their root card
        boolean allWithRootCardPlaced = Controller.getInstance().areAllRootCardPlaced();

        // Broadcast the information of the side of Starting Card. Also if all players have placed their root card, broadcast this information.
        NetworkHandler.getInstance().
                sendingPlacedRootCardAndWhenCompleteObjectiveCardsBroadcast(nickname, side, cardId, allWithRootCardPlaced);
    }

    /**
     * RMIServerInterface interface method
     *
     * This method is responsible for handling the secret objective card selection process of a player.
     * It is called when a player chooses a secret objective card ( position 0 or position 1).
     *
     *  if all players have chosen their secret objective card, broadcast the information of Common and Hidden hands.
     *
     *
     * @param nickname The nickname of the player who is choosing the secret objective card.
     * @param indexCard The index of the secret objective card chosen by the player.
     * @throws WrongGamePhaseException If the game is not in the correct phase for this action.
     * @throws NoTurnException If it's not the turn of the player who is trying to perform the action.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    public void chooseSecretObjectiveCard(String nickname, int indexCard)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        //set the secret objective card for the player. indexCard is the position of the card in the list of secret objective cards to choose
        //it can be 0 or 1: the input of client verify this.
        Controller.getInstance().chooseObjectiveCard(nickname, indexCard);
        //check if all players have chosen their secret objective card
        boolean allWithSecretObjectiveCardChosen = Controller.getInstance().areAllSecretObjectiveCardChosen();
        // broadcast the information of Common and Hidden hands
        // if all players have chosen their secret objective card.
        NetworkHandler.getInstance().
                sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlowBroadcast(nickname, allWithSecretObjectiveCardChosen);
    }
    /**
     * RMIServerInterface interface method
     *
     * This method is responsible for handling the card placement process of a player.
     * If the card is placed successfully, the method broadcasts the information of the placed card.
     * If the game has ended, the method broadcasts the end game signal.
     *
     * @param nickname The nickname of the player who is placing the card.
     * @param indexHand The index of the card in the player's hand.
     * @param position The position where the card is to be placed.
     * @param side The side chosen by the player. True for one side, false for the other.
     * @throws WrongGamePhaseException If the game is not in the correct phase for this action.
     * @throws NoTurnException If it's not the turn of the player who is trying to perform the action.
     * @throws NotEnoughResourcesException If the player does not have enough resources to place the card.
     * @throws NoNameException If a player with the given nickname does not exist.
     * @throws CardPositionException If the card cannot be placed at the given position.
     */
    @Override
    public void placeCard(String nickname, int indexHand, Point position, boolean side)
            throws WrongGamePhaseException, NoTurnException,
            NotEnoughResourcesException, NoNameException, CardPositionException {

        // The player places the card, returns the id of the placed card. Throws an exception if the card cannot be placed,
        // or if the player does not have enough resources, or if it's not the player's turn, or if the game is not in the correct phase.
        int cardId = Controller.getInstance().placeCard(nickname, indexHand, position, side);
        // Broadcast the placed card information
        NetworkHandler.getInstance().sendPlacedCardBroadcast(nickname, cardId, position, side);

        // Check if the game has ended
        if(Controller.getInstance().isEndGame()){
            // Broadcast the end game information
            NetworkHandler.getInstance().sendEndGameBroadcast();
        }
    }

    //TODO thread, è corretto?
    /**
     * RMIServerInterface interface method.
     *
     * This method is responsible for handling the card drawing process of a player.
     * If the card is drawn successfully, the method broadcasts the information of the drawn card.
     * If the game has ended, the method broadcasts the end game signal.
     *
     * @param nickname The nickname of the player who is drawing the card.
     * @param gold A boolean indicating whether the card is a gold card.
     * @param onTableOrDeck An integer indicating whether the card is on the table or deck.
     * @return The id of the drawn card.
     * @throws WrongGamePhaseException If the game is not in the correct phase for this action.
     * @throws NoTurnException If it's not the turn of the player who is trying to perform the action.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    @Override
    public Integer[] drawCard(String nickname, boolean gold, int onTableOrDeck)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        // The player draws the card, returns the id of the drawn card. Throws an exception if the card cannot be drawn,
        // or if it's not the player's turn, or if the game is not in the correct phase.
        //TODO
        // int cardId = Controller.getInstance().drawCard(nickname, gold, onTableOrDeck);
        // Get the id of the new card on the table
        int newCardId = Controller.getInstance().newCardOnTable(gold, onTableOrDeck);
        // Get the head of the deck, the new card is drawn from. It is used to update the head of the deck on the client side.
        Kingdom headDeck = Controller.getInstance().getHeadDeck(gold);

        // Broadcast the drawn card information
        NetworkHandler.getInstance().sendDrawnCardBroadcast(nickname, newCardId, headDeck, gold, onTableOrDeck);

        // Check if the game has ended
        if(Controller.getInstance().isEndGame()){
            // Broadcast the end game information
            NetworkHandler.getInstance().sendEndGameBroadcast();
        }
        // Return the id of the drawn card, used to update the client side.
        // It is unicast call. Other players are not able to see the drawn card.
        return Controller.getInstance().getHand(nickname);
    }

    /**
     * Implements the login method of the NetworkPlug interface.
     * This method is responsible for refreshing the user list for all connected clients.
     * It iterates over all the connections and sends a user list update to each client.
     *
     * It does not take any parameters and does not return any value.
     *
     * @catch RemoteException If a communication-related error occurs during the execution of a remote method call.
     */
    @Override
    public void refreshUsers(){
        HashMap<String, Color> playersAndPins = Controller.getInstance().getPlayersAndPins();
        //TODO thread
        for (RMIClientInterface connection : connections.values()){
            //send the updated user list to the client
            try  {
                connection.refreshUsers(playersAndPins);
            } catch (RemoteException e) {
                    //TODO
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
     * @throws RemoteException If a communication-related error occurs during the execution of a remote method call.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    @Override
    public void gameIsStarting() {
        Integer[] resourceCards = new Integer[2];
        resourceCards[0] = Controller.getInstance().getResourceCards(0);
        resourceCards[1] = Controller.getInstance().getResourceCards(1);

        Integer[] goldCard = new Integer[2];
        goldCard[0] = Controller.getInstance().getGoldCard(0);
        goldCard[1] = Controller.getInstance().getGoldCard(1);

        Kingdom goldCardOnDeck = Controller.getInstance().getHeadDeck(true);
        Kingdom resourceCardOnDeck = Controller.getInstance().getHeadDeck(false);

        for(String nicknameRefresh : connections.keySet()){
            new Thread(() -> {
                try {
                    //send the resource cards and gold card to the client
                    connections.get(nicknameRefresh).sendInfoOnTable(resourceCards, goldCard, resourceCardOnDeck, goldCardOnDeck);
                } catch (RemoteException e) {
                    //TODO
                }
            }).start();

            new Thread(() -> {
                try {
                    //send the starting card to the client, based on the player's nickname. It is  unicast method call.
                    connections.get(nicknameRefresh).showStartingCard(Controller.getInstance().getStartingCard(nicknameRefresh));
                } catch (RemoteException e) {
                    //TODO
                } catch (NoNameException e) {
                    //TODO
                }
            }).start();
        }
    }
    //TODO javadoc and comment

    @Override
    public void finalizingNumberOfPlayers() {
        for(String nickname : connections.keySet()){
            new Thread(() -> {
                if(Controller.getInstance().isAdmitted(nickname)){
                    try {
                        connections.get(nickname).stopWaiting();
                    } catch (RemoteException e) {
                        //TODO
                    }
                }else{
                    //TODO eliminare dalle disconnesioni tabella.
                    try {
                        connections.get(nickname).disconnect();
                    } catch (RemoteException e) {
                        connections.remove(nickname);
                        //TODO
                    }
                    connections.remove(nickname);
                }
            }).start();
        }
    }

    //TODO thread, è corretto?
    /**
     * It implements the sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow method of the NetworkPlug interface.
     *
     * This method is responsible for broadcasting the player's hand and the game start signal.
     * If all players have chosen their secret objective card, it broadcasts the information of Common and Hidden hands.
     * Sends the hand of the player whose nickname is given as a parameter, to all other players sends the kingdom hand(hidden).
     *
     * @param nickname The nickname of the player whose hand is being sent.
     * @param allWithSecretObjectiveCardChosen A boolean indicating whether all players have chosen their secret objective cards.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    @Override
    public void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname, boolean allWithSecretObjectiveCardChosen)
             {
        //Get the player's hand from nickname. It sends
                 try {
                     Integer[] hand = Controller.getInstance().getHand(nickname);
                 } catch (NoNameException e) {
                     throw new RuntimeException(e);
                 }
                 try {
                     Pair<Kingdom, Boolean>[]  HiddenHand = Controller.getInstance().getHiddenHand(nickname);
                 } catch (NoNameException e) {
                     throw new RuntimeException(e);
                 }

                 for(String nicknameRefresh : connections.keySet()){

            if(nickname.equals(nicknameRefresh)){
                try {
                    // Send the player's hand to the client. It is unicast call, only the player can see their hand.
                    connections.get(nicknameRefresh).showHand(nickname, Controller.getInstance().getHand(nickname));
                } catch (RemoteException e) {
                    //TODO
                } catch (NoNameException e) {
                    throw new RuntimeException(e);
                }
            }else{
                //TODO THREAD
                try {
                    // Send the hidden hand of the player to all other clients.
                    // It is a broadcast call, all other players can see the hidden hand of the player.
                    try {
                        connections.get(nicknameRefresh).showHiddenHand(nicknameRefresh, Controller.getInstance().getHiddenHand(nickname));
                    } catch (NoNameException e) {
                        throw new RuntimeException(e);
                    }
                } catch (RemoteException e) {
                    //TODO
                }
            }
            // If all players have chosen their secret objective card, broadcast the game start signal
            if(allWithSecretObjectiveCardChosen){
                try {
                    // Send the game start signal to the client. It is a broadcast call, all players can see the game start signal.
                    // The game start signal includes the information of the first player.
                    connections.get(nicknameRefresh).getIsFirstAndStartGame(Controller.getInstance().getFirstPlayer());
                } catch (RemoteException e) {
                    //TODO
                }
            }
        }
    }

    //TODO thread, è corretto?
    /**
     * It implements the sendingPlacedRootCardAndWhenCompleteObjectiveCards method of the NetworkPlug interface.
     *
     * This method is responsible for broadcasting the information of the side of Starting Card.
     * If all players have placed their root card, broadcast this information.
     *
     * @param nickname The nickname of the player who has placed the card.
     * @param side The side chosen by the player. True for one side, false for the other.
     * @param cardId The id of the card that has been placed.
     * @param allWithRootCardPlaced A boolean indicating whether all players have placed their root cards.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    @Override
    public void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId, boolean allWithRootCardPlaced) {
        //Get the player's resources and points from nickname, so it is the same information for all players.
        try {
            HashMap<Sign, Integer> playerResources = Controller.getInstance().getPlayerResources(nickname);
        } catch (NoNameException e) {
            throw new RuntimeException(e);
        }
        //get the player's points from nickname, so it is the same information for all players.
        try {
            int playerPoints = Controller.getInstance().getPlayerPoints(nickname);
        } catch (NoNameException e) {
            throw new RuntimeException(e);
        }
        //TODO sistemare controller.
        for(String nicknameRefresh : connections.keySet()){
            new Thread(() -> {
                try {
                    //Broadcast the information of the side of Starting Card as a normal PlacedCard
                    connections.get(nicknameRefresh).placeCard(nickname, cardId, new Point(0, 0), side,
                            Controller.getInstance().getPlayerResources(nickname), Controller.getInstance().getPlayerPoints(nickname));
                } catch (RemoteException e) {
                    //TODO
                } catch (NoNameException e) {
                    //TODO
                }
            }).start();

            //If all players have placed their root card, broadcast the common objective cards and the secret objective cards to choose
            if(allWithRootCardPlaced){
                //TODO THREAD
                try {
                    //Broadcast the common objective cards to all clients
                    connections.get(nicknameRefresh).
                            sendCommonObjectiveCards(Controller.getInstance().getCommonObjectiveCards());
                } catch (RemoteException e) {
                    //TODO;
                }
                try {
                    //Broadcast the secret objective cards to choose to the player
                    try {
                        connections.get(nicknameRefresh).
                                sendSecretObjectiveCardsToChoose(Controller.getInstance().getSecretObjectiveCardsToChoose(nicknameRefresh));
                    } catch (NoNameException e) {
                        throw new RuntimeException(e);
                    }
                } catch (RemoteException e) {
                    //TODO
                }
            }
        }
    }

    /**
     *
     * It implements the sendPlacedCard method of the NetworkPlug interface.
     *
     * This method is responsible for broadcasting the information of the drawn card.
     * It is called when a player draws a card successfully.
     * It broadcasts the information of the new card on the table and the head of the deck.
     *
     * @param nickname The nickname of the player who has drawn the card.
     * @param newCardId The id of the new card on the table.
     * @param headDeck The head of the deck, the new card is drawn from.
     * @param gold A boolean indicating whether the card is a gold card.
     * @param onTableOrDeck An integer indicating whether the card is on the table or deck.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    //TODO thread
    @Override
    public void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) {
        for(String nicknameRefresh : connections.keySet()){
            //if is not the player who has drawn the card, send the hidden hand of the player.
            if(!nickname.equals(nicknameRefresh)){
                try {
                    try {
                        connections.get(nicknameRefresh).showHiddenHand(nicknameRefresh, Controller.getInstance().getHiddenHand(nickname));
                    } catch (NoNameException e) {
                        throw new RuntimeException(e);
                    }
                } catch (RemoteException e) {
                    //TODO
                }
            }

            //send the information of the drawn card to the client. Broadcast call, all players can see the drawn card.
            try {
                //send the information of the drawn card to the client. It is a card on the table and the new on deck.
                //if a player draws a onDeck card, the headDeckParameter is -1, and client don't update that information.
                connections.get(nicknameRefresh).moveCard(newCardId, headDeck, gold, onTableOrDeck);
            } catch (RemoteException e) {
                //TODO
            }
            try {
                //send the updated turn information to the client. It is a broadcast call, all players can see the updated turn information.
                connections.get(nicknameRefresh).
                        refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
            } catch (RemoteException e) {
                //TODO
            }
        }
    }
    /**
     * It implements the sendPlacedCard method of the NetworkPlug interface.
     *
     * This method is responsible for broadcasting the information of the placed card.
     * It is called when a player places a card successfully.
     * It broadcasts the information of the placed card, the player's resources and points.
     *
     * @param nickname The nickname of the player who has placed the card.
     * @param cardId The id of the card that has been placed.
     * @param position The position where the card has been placed.
     * @param side The side chosen by the player. True for one side, false for the other.
     * @throws NoNameException If a player with the given nickname does not exist.
     */
    //TODO thread
    @Override
    public void sendPlacedCard(String nickname, int cardId, Point position, boolean side) {
        for(String nicknameRefresh : connections.keySet()){
            try {
                // Broadcast the placed card information, the player's resources and points
                try {
                    connections.get(nicknameRefresh).placeCard(nickname, cardId, position, side, Controller.getInstance().getPlayerResources(nickname), Controller.getInstance().getPlayerPoints(nickname));
                } catch (NoNameException e) {
                    throw new RuntimeException(e);
                }
            } catch (RemoteException e) {
                //TODO
            }
            try {
                // Refresh the turn information
                connections.get(nicknameRefresh).
                        refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
            } catch (RemoteException e) {
                //TODO;
            }
        }
    }

    //TODO è corretto
    /**
     * It implements the sendEndGame method of the NetworkPlug interface.
     *
     * This method is responsible for broadcasting the end game information.
     * The game information are the extra points of each player and the final ranking.
     * It is called when the game has ended.
     * It broadcasts the extra points of each player and the final ranking.
     *
     */
    @Override
    public void sendEndGame(){
        HashMap<String, Integer> extraPoints = Controller.getInstance().getExtraPoints();
        ArrayList<Player> ranking = Controller.getInstance().getRanking();

        for(String playerConnection : connections.keySet()){
            new Thread(() -> {
                try {
                    //send the end game information to the client. It sends the extra points and the final ranking.
                    connections.get(playerConnection).
                            showEndGame(extraPoints, ranking);
                } catch (RemoteException e) {
                    //TODO
                }
            }).start();
        }
    }
}
