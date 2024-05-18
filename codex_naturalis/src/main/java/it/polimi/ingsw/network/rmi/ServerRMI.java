package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.server.NetworkHandler;
import it.polimi.ingsw.network.server.NetworkPlug;

import java.awt.*;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ServerRMI implements LoggableServer, NetworkPlug {
    static int PORT = 1099; //TODO porta dinamica
    HashMap<String, RMIClientInterface> connections = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerRMI obj = new ServerRMI();
//        NetworkServerSocket networkServerSocket = new NetworkServerSocket(0);
//        networkServerSocket.start();
    }

    public ServerRMI() throws RemoteException {
        NetworkHandler.getInstance().addNetworkPlug("RMI", this);
        LoggableServer serverSkeleton = null;//https://www.baeldung.com/java-rmi
        Registry registry = null;

        try {
            serverSkeleton = (LoggableServer) UnicastRemoteObject.exportObject(this, PORT);
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

    @Override
    public boolean loginAndIsFirst(RMIClientInterface clientRMI, String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
        Controller.getInstance().addPlayer(nickname);
        connections.put(nickname, clientRMI);

        NetworkHandler.getInstance().refreshUsersBroadcast();
        return Controller.getInstance().getIsFirst(nickname);
    }

    @Override
    public boolean lobbyIsReady() throws RemoteException {
        return Controller.getInstance().isLobbyLocked();
    }

    @Override
    public void insertNumberOfPlayers(int numberOfPlayers) throws
            RemoteException, ClosingLobbyException, SameNameException, LobbyCompleteException, NoNameException {

        Controller.getInstance().initializeLobby(numberOfPlayers);
        //TODO remove all players before
        //Deletes all other connections that are not in the lobby

        NetworkHandler.getInstance().finalizingNumberOfPlayersBroadcast(numberOfPlayers);
        //NetworkHandler.getInstance().refreshUsersBroadcast();
    }

    @Override
    public void finalizingNumberOfPlayers() {
        for(String nickname : connections.keySet()){
            try {
                connections.get(nickname).stopWaiting();
            } catch (RemoteException e) {
                //TODO
            }
        }
    }

    @Override
    public void chooseColor(String nickname, Color color) throws RemoteException,
            ColorAlreadyTakenException, NoNameException {
        if(Controller.getInstance().setColour(nickname, color)){
            NetworkHandler.getInstance().refreshUsersBroadcast();
            NetworkHandler.getInstance().gameIsStartingBroadcast();
        }
    }

    @Override
    public void gameIsStarting() throws NoNameException {
        Integer[] resourceCards = new Integer[2];
        resourceCards[0] = Controller.getInstance().getResourceCards(0);
        resourceCards[1] = Controller.getInstance().getResourceCards(1);

        Integer[] goldCard = new Integer[2];
        goldCard[0] = Controller.getInstance().getGoldCard(0);
        goldCard[1] = Controller.getInstance().getGoldCard(1);

        Kingdom goldCardOnDeck = Controller.getInstance().getHeadDeck(true);
        Kingdom resourceCardOnDeck = Controller.getInstance().getHeadDeck(false);

        for(String nicknameRefresh : connections.keySet()){
            try {
                connections.get(nicknameRefresh).sendInfoOnTable(resourceCards, goldCard, resourceCardOnDeck, goldCardOnDeck);
            } catch (RemoteException e) {
                //TODO
            }
            try {
                connections.get(nicknameRefresh).showStartingCard(Controller.getInstance().getStartingCard(nicknameRefresh));
            } catch (RemoteException e) {
                //TODO
            }
        }
    }

    @Override
    public void refreshUsers(){
        HashMap<String, Color> playersAndPins = Controller.getInstance().getPlayersAndPins();
        for (RMIClientInterface connection : connections.values()){
            try {
                connection.refreshUsers(playersAndPins);
            } catch (RemoteException e) {
                //TODO
            }
        }
    }

    //GAME START

    //We could optimize and use a unique function without any
    @Override
    public void chooseSideStartingCard(String nickname, boolean side)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        int cardId = Controller.getInstance().placeRootCard(nickname, side);
        boolean allWithRootCardPlaced = Controller.getInstance().areAllRootCardPlaced();
        NetworkHandler.getInstance().
                sendingPlacedRootCardAndWhenCompleteObjectiveCardsBroadcast(nickname, side, cardId, allWithRootCardPlaced);
    }

    @Override
    public void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId, boolean allWithRootCardPlaced) throws NoNameException {
        for(String nicknameRefresh : connections.keySet()){
            try {
                connections.get(nicknameRefresh).placeCard(nickname, cardId, new Point(0,0), side, Controller.getInstance().getPlayerResources(nickname), Controller.getInstance().getPlayerPoints(nickname));
            } catch (RemoteException e) {
                //TODO
            }
            if(allWithRootCardPlaced){
                try {
                    connections.get(nicknameRefresh).
                            sendCommonObjectiveCards(Controller.getInstance().getCommonObjectiveCards());
                } catch (RemoteException e) {
                   //TODO;
                }
                try {
                    connections.get(nicknameRefresh).
                            sendSecretObjectiveCardsToChoose(Controller.getInstance().getSecretObjectiveCardsToChoose(nicknameRefresh));
                } catch (RemoteException e) {
                    //TODO
                }
            }
        }
    }

    public void chooseSecretObjectiveCard(String nickname, int indexCard)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        Controller.getInstance().chooseObjectiveCard(nickname, indexCard);
        boolean allWithSecretObjectiveCardChosen = Controller.getInstance().areAllSecretObjectiveCardChosen();
        NetworkHandler.getInstance().
                sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlowBroadcast(nickname, allWithSecretObjectiveCardChosen);
    }

    @Override
    public void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname, boolean allWithSecretObjectiveCardChosen) throws NoNameException {
        for(String nicknameRefresh : connections.keySet()){
            if(nickname.equals(nicknameRefresh)){
                try {
                    connections.get(nicknameRefresh).showHand(nickname, Controller.getInstance().getHand(nickname));
                } catch (RemoteException e) {
                    //TODO
                }
            }else{
                try {
                    connections.get(nicknameRefresh).showHiddenHand(nicknameRefresh, Controller.getInstance().getHiddenHand(nickname));
                } catch (RemoteException e) {
                    //TODO
                }
            }
            if(allWithSecretObjectiveCardChosen){
                try {
                    connections.get(nicknameRefresh).getIsFirstAndStartGame(Controller.getInstance().getFirstPlayer());
                } catch (RemoteException e) {
                    //TODO
                }
            }
        }
    }

    @Override
    public void placeCard(String nickname, int indexHand, Point position, boolean side)
            throws WrongGamePhaseException, NoTurnException,
            NotEnoughResourcesException, NoNameException, CardPositionException {

        int cardId = Controller.getInstance().placeCard(nickname, indexHand, position, side);
        NetworkHandler.getInstance().sendPlacedCardBroadcast(nickname, cardId, position, side);

        if(Controller.getInstance().isEndGame()){
            NetworkHandler.getInstance().sendEndGameBroadcast();
        }
    }

    @Override
    public void sendPlacedCard(String nickname, int cardId, Point position, boolean side) throws NoNameException {
        for(String nicknameRefresh : connections.keySet()){
            try {
                connections.get(nicknameRefresh).placeCard(nickname, cardId, position, side, Controller.getInstance().getPlayerResources(nickname), Controller.getInstance().getPlayerPoints(nickname));
            } catch (RemoteException e) {
                //TODO
            }
            try {
                connections.get(nicknameRefresh).
                        refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
            } catch (RemoteException e) {
                //TODO;
            }
        }
    }

    @Override
    public int drawCard(String nickname, boolean gold, int onTableOrDeck)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        int cardId = Controller.getInstance().drawCard(nickname, gold, onTableOrDeck);
        int newCardId = Controller.getInstance().newCardOnTable(gold, onTableOrDeck);
        Kingdom headDeck = Controller.getInstance().getHeadDeck(gold);

        NetworkHandler.getInstance().sendDrawnCardBroadcast(nickname, newCardId, headDeck, gold, onTableOrDeck);

        if(Controller.getInstance().isEndGame()){
            NetworkHandler.getInstance().sendEndGameBroadcast();
        }
        return cardId;
    }

    @Override
    public void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) throws NoNameException {
        for(String nicknameRefresh : connections.keySet()){
            if(!nickname.equals(nicknameRefresh)){
                try {
                    connections.get(nicknameRefresh).showHiddenHand(nicknameRefresh, Controller.getInstance().getHiddenHand(nickname));
                } catch (RemoteException e) {
                    //TODO
                }
            }
            try {
                connections.get(nicknameRefresh).moveCard(newCardId, headDeck, gold, onTableOrDeck);
            } catch (RemoteException e) {
                //TODO
            }
            try {
                connections.get(nicknameRefresh).
                        refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
            } catch (RemoteException e) {
                //TODO
            }
        }
    }

    @Override
    public void sendEndGame(){
        for(String nicknameRefresh : connections.keySet()){
            try {
                connections.get(nicknameRefresh).
                        showEndGame(Controller.getInstance().getExtraPoints(), Controller.getInstance().getRanking());
            } catch (RemoteException e) {
                //TODO
            }
        }
    }
}
