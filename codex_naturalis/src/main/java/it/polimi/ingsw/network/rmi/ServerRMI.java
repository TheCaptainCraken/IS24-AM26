package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.server.NetworkHandler;
import it.polimi.ingsw.network.server.NetworkPlug;

import java.awt.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ServerRMI extends NetworkPlug implements LoggableServer {
    static int PORT = 1099; //TODO porta dinamica
    int numberOfPlayers = 0;//TODO I can do a method to get it
    HashMap<String, RMIClientInterface> connections = new HashMap<>();
    //TODO call broadcast methods

    public static void main(String[] args) throws RemoteException {
        ServerRMI obj = new ServerRMI();
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

    //Only because we have all client on the same machine
    //A server shouldn't pick the port for a client
    int portAvailable = 41999;

    @Override
    public boolean loginAndIsFirst(RMIClientInterface clientRMI, String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
        Controller.getInstance().addPlayer(nickname);
        connections.put(nickname, clientRMI);
        refreshUsers();
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
        NetworkHandler.getInstance().refreshUsersBroadcast();
    }

    @Override
    protected void finalizingNumberOfPlayers() throws NoNameException, SameNameException, LobbyCompleteException {

    }

    @Override
    public void chooseColor(String nickname, Color color) throws RemoteException,
            ColorAlreadyTakenException, NoNameException {
        if(Controller.getInstance().setColour(nickname, color)){
            NetworkHandler.getInstance().gameIsStartingBroadcast();
        }
        NetworkHandler.getInstance().refreshUsersBroadcast();
    }

    @Override
    protected void gameIsStarting() throws NoNameException {
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
                throw new RuntimeException(e);
            }
            try {
                connections.get(nicknameRefresh).showStartingCard(Controller.getInstance().getStartingCard(nicknameRefresh));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void refreshUsers(){
        HashMap<String, Color> playersAndPins = Controller.getInstance().getPlayersAndPins();
        for (RMIClientInterface connection : connections.values()){
            try {
                connection.refreshUsers(playersAndPins);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }
            if(allWithRootCardPlaced){
                try {
                    connections.get(nicknameRefresh).
                            showObjectiveCards(Controller.getInstance().getCommonObjectiveCards());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                try {
                    connections.get(nicknameRefresh).
                            showSecretObjectiveCards(Controller.getInstance().getSecretObjectiveCardsToChoose(nicknameRefresh));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
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
    protected void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname, boolean allWithSecretObjectiveCardChosen) throws NoNameException {
        for(String nicknameRefresh : connections.keySet()){
            if(nickname.equals(nicknameRefresh)){
                try {
                    connections.get(nicknameRefresh).showHand(nickname, Controller.getInstance().getHand(nickname));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }else{
                try {
                    connections.get(nicknameRefresh).showHiddenHand(nicknameRefresh, Controller.getInstance().getHiddenHand(nickname));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            if(allWithSecretObjectiveCardChosen){
                try {
                    connections.get(nicknameRefresh).getIsFirst(Controller.getInstance().getFirstPlayer());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                try {
                    connections.get(nicknameRefresh).
                            refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
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
    protected void sendPlacedCard(String nickname, int cardId, Point position, boolean side) throws NoNameException {
        for(String nicknameRefresh : connections.keySet()){
            try {
                connections.get(nicknameRefresh).placeCard(nickname, cardId, position, side, Controller.getInstance().getPlayerResources(nickname), Controller.getInstance().getPlayerPoints(nickname));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            try {
                connections.get(nicknameRefresh).
                        refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
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
    protected void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) throws NoNameException {
        for(String nicknameRefresh : connections.keySet()){
            if(!nickname.equals(nicknameRefresh)){
                try {
                    connections.get(nicknameRefresh).showHiddenHand(nicknameRefresh, Controller.getInstance().getHiddenHand(nickname));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                connections.get(nicknameRefresh).moveCard(newCardId, headDeck, gold, onTableOrDeck);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            try {
                connections.get(nicknameRefresh).
                        refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void sendEndGame(){
        for(String nicknameRefresh : connections.keySet()){
            try {
                connections.get(nicknameRefresh).
                        showEndGame(Controller.getInstance().getExtraPoints(), Controller.getInstance().getRanking());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
