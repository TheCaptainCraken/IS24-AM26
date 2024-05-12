package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NoNameException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.HashMap;

public class NetworkHandler {
    private static final NetworkHandler INSTANCE = new NetworkHandler();
    boolean lobbyIsReady = false;

    protected static HashMap<String, NetworkPlug> networkInterfacesAndConnections;

    /**
     * Gets the instance of the controller.
     * @return The instance of the controller.
     */
    public static NetworkHandler getInstance(){
        //TODO mutex
        return INSTANCE;
    }

    public NetworkHandler() {
        networkInterfacesAndConnections = new HashMap<>();
    }

    public void addNetworkPlug(String nameNetworkPlug, NetworkPlug networkPlug) {
        networkInterfacesAndConnections.put(nameNetworkPlug, networkPlug);
    }

    public void finalizingNumberOfPlayersBroadcast(int numberOfPlayers) throws SameNameException, LobbyCompleteException, NoNameException {
        //TODO do a method that choose from lists of connections in each NetworkPlug`s HashMap

//        HashMap<String, Color> playersInLobby = null;
//        for(String nickname : connections.keySet()){
//            if(Controller.getInstance().getPlayer(nickname) == null){
//                connections.get(nickname).disconnect();
//                connections.remove(nickname);
//            }else{
//                Controller.getInstance().addPlayer(nickname);
//                connections.get(nickname).stopWaiting(nickname);
//            }
//        }
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.finalizingNumberOfPlayers();
        }
        lobbyIsReady = true;
    }

    public void gameIsStartingBroadcast() throws RemoteException, NoNameException {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.gameIsStarting();
        }
    }

    public void refreshUsersBroadcast() {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.refreshUsers();
        }
    }

    public void sendingPlacedRootCardAndWhenCompleteObjectiveCardsBroadcast(String nickname, boolean side, int cardId, boolean allWithRootCardPlaced) throws NoNameException {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendingPlacedRootCardAndWhenCompleteObjectiveCards(nickname, side, cardId, allWithRootCardPlaced);
        }
    }

    public void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlowBroadcast(String nickname, boolean allWithSecretObjectiveCardChosen) throws NoNameException {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(nickname, allWithSecretObjectiveCardChosen);
        }
    }

    public void sendPlacedCardBroadcast(String nickname, int cardId, Point position, boolean side) throws NoNameException {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendPlacedCard(nickname, cardId, position, side);
        }
    }

    public void sendDrawnCardBroadcast(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) throws NoNameException {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendDrawnCard(nickname, newCardId, headDeck, gold, onTableOrDeck);
        }
    }

    public void sendEndGameBroadcast() {
        for (NetworkPlug networkPlug : networkInterfacesAndConnections.values()) {
            networkPlug.sendEndGame();
        }
    }

    //TODO broadcast methods
}
