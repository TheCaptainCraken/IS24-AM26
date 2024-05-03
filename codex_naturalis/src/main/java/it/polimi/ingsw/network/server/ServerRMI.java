package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.client.ClientRMI;
import it.polimi.ingsw.network.client.LoggableClient;

import java.awt.*;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ServerRMI implements LoggableServer {
    static int PORT = 1234; //TODO porta dinamica
    Controller controller = Controller.getInstance();
    int numberOfPlayers = 0;//TODO I can do a method to get it
    HashMap<String, ClientRMI> connections = new HashMap<>();

    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello, World!");
        LoggableServer serverSkeleton = null;//https://www.baeldung.com/java-rmi
        ServerRMI obj = new ServerRMI();

        try{
            serverSkeleton = (LoggableServer) UnicastRemoteObject.exportObject(obj, PORT);
        }catch (RemoteException e){
            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        Registry registry = null;
        try{
            registry = LocateRegistry.createRegistry(PORT);
        }catch(RemoteException e){
            System.out.println("Registry exception: " + e.toString());
            e.printStackTrace();
        }

        try{
            registry.bind("Loggable", serverSkeleton);
        }catch (RemoteException e) {
            System.out.println("Rebind exception: " + e.toString());
            e.printStackTrace();
        }catch (AlreadyBoundException e) {
            System.out.println("Already bound exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("Server ready");
    }

    //Only because we have all client on the same machine
    //A server shouldn't pick the port for a client
    int portAvailable = 41999;

    @Override
    public boolean isFirst(ClientRMI clientRMI, String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
        controller.addPlayer(nickname);
        connections.put(nickname, clientRMI);
        refreshUsers();
        return controller.getIsFirst(nickname);
    }

    @Override
    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, NoSuchFieldException {
        controller.initializeLobby(numberOfPlayers);//TODO remove all players before
        //Deletes all other connections that are not in the lobby
        HashMap<String, Color> playersInLobby = null;
        for(String nickname : connections.keySet()){
            if(controller.getPlayer(nickname) == null){
                connections.get(nickname).disconnect("Lobby has been fulled with number of parameters chosen by the first player");
                connections.remove(nickname);
            }else{
                connections.get(nickname).stopWaiting(nickname);
            }
        }
        refreshUsers();
    }

    @Override
    public void chooseColor(String nickname, Color color) throws RemoteException, NoSuchFieldException,
             ColorAlreadyTakenException {
//        if(controller.getUsers().get(nickname).getColor() == color){
//            controller.getUsers().get(nickname).setColor(null);
//        }
        if(controller.setColour(nickname, color)){
            for(String nicknameRefresh : connections.keySet()){
                connections.get(nicknameRefresh).sendInfoOnTable();//TODO
                connections.get(nicknameRefresh).getStartingCard(controller.getRootCard(nicknameRefresh));
            }
        }
        refreshUsers();
    }

    public void refreshUsers(){
        HashMap<String, Color> playersAndPins = controller.getPlayersAndPins();
        for (ClientRMI connection : connections.values()){
            connection.refreshUsers(playersAndPins);//Color could be null
        }
    }

    //GAME START


    //We could optimize and use a unique function without any
    @Override
    public void chooseSideStartingCard(String nickname, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException {
        int cardId = controller.placeRootCard(nickname, side);
        boolean allWithRootCardPlaced = controller.areAllRootCardPlaced();
        for(String nicknameRefresh : connections.keySet()){
            connections.get(nicknameRefresh).placeCard(nickname, cardId, new Point(0,0), side, controller.getPlayerResources(nickname), controller.getPlayerPoints(nickname));
            if(allWithRootCardPlaced){
                connections.get(nicknameRefresh).showObjectiveCards(controller.getObjectiveCards());
                connections.get(nicknameRefresh).showSecretObjectiveCards(controller.getSecretObjectiveCards(nicknameRefresh));
            }
        }
    }

    public void chooseSecretObjectiveCard(String nickname, int indexCard) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        controller.chooseObjectiveCard(nickname, indexCard);
        boolean allWithSecretObjectiveCardChosen = controller.areAllSecretObjectiveCardChosen();
        for(String nicknameRefresh : connections.keySet()){
            if(nickname.equals(nicknameRefresh)){
                connections.get(nickname).showHand(nickname, controller.getHand(nickname));
            }else{
                connections.get(nicknameRefresh).showHiddenHand(nicknameRefresh, controller.getHiddenHand(nickname));
            }
            if(allWithSecretObjectiveCardChosen){
                connections.get(nicknameRefresh).refreshTurnInfo(controller.getCurrentPlayer(), controller.getGameState());
            }
        }
    }

    @Override
    public void placeCard(String nickname, int indexHand, Point position, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException, NotEnoughResourcesException {
        int cardId = controller.placeCard(nickname, indexHand, position, side);
        for(String nicknameRefresh : connections.keySet()){
            connections.get(nicknameRefresh).placeCard(nickname, cardId, position, side, controller.getPlayerResources(nickname), controller.getPlayerPoints(nickname));
            connections.get(nicknameRefresh).refreshTurnInfo(controller.getCurrentPlayer(), controller.getGameState());
        }
        if(controller.isEndGame()){
            for(String nicknameRefresh : connections.keySet()){
                connections.get(nicknameRefresh).showEndGame(controller.getExtraPoints(), controller.getRanking());
            }
        }
    }

    @Override
    public void drawCard(String nickname, boolean gold, int onTableOrDeck) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        int cardId = controller.drawCard(nickname, gold, onTableOrDeck);
        int newCardId = controller.newCardOnTable(gold, onTableOrDeck);
        Kingdom headDeck = controller.getHeadDeck(gold);
        for(String nicknameRefresh : connections.keySet()){
            if(nickname.equals(nicknameRefresh)){
                connections.get(nickname).drawnCard(nickname, cardId);//TODO maybe also gold, it depende on enumeration
            }else{
                connections.get(nicknameRefresh).showHiddenHand(nicknameRefresh, controller.getHiddenHand(nickname));//bad but easier
            }
            connections.get(nicknameRefresh).moveCard(newCardId, headDeck, gold, onTableOrDeck);
            connections.get(nicknameRefresh).refreshTurnInfo(controller.getCurrentPlayer(), controller.getGameState());
        }
        if(controller.isEndGame()){
            for(String nicknameRefresh : connections.keySet()){
                connections.get(nicknameRefresh).showEndGame(controller.getExtraPoints(), controller.getRanking());
            }
        }
    }
}
