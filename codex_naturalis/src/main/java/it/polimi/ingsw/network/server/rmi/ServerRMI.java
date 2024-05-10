package it.polimi.ingsw.network.server.rmi;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.client.ClientRMI;
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
    static int PORT = 1234; //TODO porta dinamica
    int numberOfPlayers = 0;//TODO I can do a method to get it
   //HashMap<String, ClientRMI> connections = new HashMap<>();
    //Controller.getInstance is used directly in it
    //TODO move all connections here again
    //TODO getConnections()
    //TODO call broadcast methods



    public static void main(String[] args) throws RemoteException {
    }

    public ServerRMI() throws RemoteException {
        super();
        NetworkHandler.getInstance().addNetworkPlug(this);
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
        Controller.getInstance().addPlayer(nickname);
        NetworkHandler.getInstance().addConnection(this, nickname, clientRMI);
        refreshUsers();
        return Controller.getInstance().getIsFirst(nickname);
    }

    @Override
    public void insertNumberOfPlayers(int numberOfPlayers) throws
            RemoteException, ClosingLobbyException, SameNameException, LobbyCompleteException, NoNameException {

        Controller.getInstance().initializeLobby(numberOfPlayers);
        //TODO remove all players before
        //Deletes all other connections that are not in the lobby
        HashMap<String, Color> playersInLobby = null;
        for(String nickname : connections.keySet()){
            if(Controller.getInstance().getPlayer(nickname) == null){
                connections.get(nickname).disconnect();
                connections.remove(nickname);
            }else{
                Controller.getInstance().addPlayer(nickname);
                connections.get(nickname).stopWaiting(nickname);
            }
        }
        finalizingNumberOfPlayers();
        refreshUsers();
    }

    @Override
    protected void finalizingNumberOfPlayers(){

    }

    @Override
    public void chooseColor(String nickname, Color color) throws RemoteException,
            ColorAlreadyTakenException, NoNameException {
        if(Controller.getInstance().setColour(nickname, color)){
            gameIsStarting();
        }
        refreshUsers();
    }

    @Override
    protected void gameIsStarting(){
        for(String nicknameRefresh : NetworkHandler.getInstance().getConnections(this).keySet()){
            ((ClientRMI) NetworkHandler.getInstance().getConnections(this).get(nicknameRefresh)).sendInfoOnTable();//TODO
            ((ClientRMI) NetworkHandler.getInstance().getConnections(this).get(nicknameRefresh)).showStartingCard(controller.getStartingCard(nicknameRefresh));
        }
    }

    @Override
    protected void refreshUsers(){
        HashMap<String, Color> playersAndPins = Controller.getInstance().getPlayersAndPins();
        for (Object connection : NetworkHandler.getInstance().getConnections(this).values()){
            ((ClientRMI) connection).refreshUsers(playersAndPins);
        }
    }

    //GAME START

    //We could optimize and use a unique function without any
    @Override
    public void chooseSideStartingCard(String nickname, boolean side)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        int cardId = Controller.getInstance().placeRootCard(nickname, side);
        boolean allWithRootCardPlaced = Controller.getInstance().areAllRootCardPlaced();
        //commento...
        sendingPlacedRootCardAndWhenCompleteObjectiveCards(nickname, side, cardId, allWithRootCardPlaced);
    }

    @Override
    public void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId, boolean allWithRootCardPlaced) throws NoNameException {
        for(String nicknameRefresh : NetworkHandler.getInstance().getConnections(this).keySet()){
            ClientRMI connection = (ClientRMI) NetworkHandler.getInstance().getConnections(this).get(nicknameRefresh);
            connection.placeCard(nickname, cardId, new Point(0,0), side, Controller.getInstance().getPlayerResources(nickname), controller.getPlayerPoints(nickname));
            if(allWithRootCardPlaced){
                connection.showObjectiveCards(Controller.getInstance().getCommonObjectiveCards());
                connection.showSecretObjectiveCards(Controller.getInstance().getSecretObjectiveCardsToChoose(nicknameRefresh));
            }
        }
    }

    public void chooseSecretObjectiveCard(String nickname, int indexCard)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        Controller.getInstance().chooseObjectiveCard(nickname, indexCard);
        boolean allWithSecretObjectiveCardChosen = Controller.getInstance().areAllSecretObjectiveCardChosen();
        sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(nickname, allWithSecretObjectiveCardChosen);
    }

    @Override
    protected void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname, boolean allWithSecretObjectiveCardChosen) throws NoNameException {
        for(String nicknameRefresh : NetworkHandler.getInstance().getConnections(this).keySet()){
            ClientRMI connection = (ClientRMI) (ClientRMI) NetworkHandler.getInstance().getConnections(this).get(nicknameRefresh);
            if(nickname.equals(nicknameRefresh)){
                connection.showHand(nickname, Controller.getInstance().getHand(nickname));
            }else{
                connection.showHiddenHand(nicknameRefresh, Controller.getInstance().getHiddenHand(nickname));
            }
            if(allWithSecretObjectiveCardChosen){
                connection.getIsFirst(Controller.getInstance().getFirstPlayer());
                connection.refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
            }
        }
    }

    @Override
    public void placeCard(String nickname, int indexHand, Point position, boolean side)
            throws WrongGamePhaseException, NoTurnException,
            NotEnoughResourcesException, NoNameException, CardPositionException {

        int cardId = Controller.getInstance().placeCard(nickname, indexHand, position, side);
        sendPlacedCard(nickname, cardId, position, side);

        if(Controller.getInstance().isEndGame()){
            sendEndGame();
        }
    }

    @Override
    protected void sendPlacedCard(String nickname, int cardId, Point position, boolean side){
        for(String nicknameRefresh : connections.keySet()){
            connections.get(nicknameRefresh).placeCard(nickname, cardId, position, side, Controller.getInstance().getPlayerResources(nickname), Controller.getInstance().getPlayerPoints(nickname));
            connections.get(nicknameRefresh).refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
        }
    }

    @Override
    public int drawCard(String nickname, boolean gold, int onTableOrDeck)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        int cardId = Controller.getInstance().drawCard(nickname, gold, onTableOrDeck);
        int newCardId = Controller.getInstance().newCardOnTable(gold, onTableOrDeck);
        Kingdom headDeck = Controller.getInstance().getHeadDeck(gold);

        sendDrawnCard(nickname, newCardId, headDeck, gold, onTableOrDeck);

        if(Controller.getInstance().isEndGame()){
            sendEndGame();
        }
        return cardId;
    }

    @Override
    protected void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck){
        for(String nicknameRefresh : connections.keySet()){
            if(!nickname.equals(nicknameRefresh)){
                connections.get(nicknameRefresh).showHiddenHand(nicknameRefresh, Controller.getInstance().getHiddenHand(nickname));///TODO add to client
            }
            connections.get(nicknameRefresh).moveCard(newCardId, headDeck, gold, onTableOrDeck);
            connections.get(nicknameRefresh).refreshTurnInfo(Controller.getInstance().getCurrentPlayer(), Controller.getInstance().getGameState());
        }
    }

    @Override
    protected void sendEndGame(){
        for(String nicknameRefresh : connections.keySet()){
            connections.get(nicknameRefresh).showEndGame(Controller.getInstance().getExtraPoints(), Controller.getInstance().getRanking());
        }
    }
}
