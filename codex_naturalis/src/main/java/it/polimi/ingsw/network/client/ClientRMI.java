package it.polimi.ingsw.network.client;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.exception.NoConnectionException;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NotExistsPlayerException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.network.exception.NoConnectionException;
import it.polimi.ingsw.network.server.LoggableServer;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ClientRMI{
    int PORT = 1234;
    Controller controller;//TODO check if is to do a singletone
    LoggableServer stub = null;
    Registry registry = null;
    //Serving stuffs
    LoggableClient clientSkeleton =  null;
    ClientRMI obj = new ClientRMI();//
    boolean lobbyIsReady = false;


    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello, World!");
    }

    public void login(String nickname) throws RemoteException, SameNameException, LobbyCompleteException, NoConnectionException {
        try {
            registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
            stub = (LoggableServer) registry.lookup("Loggable");
        } catch (RemoteException e) {
            System.out.println("Client exception: " + e.toString());
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.out.println("Client exception: " + e.toString());
            throw new RuntimeException(e);
        }
        if(stub == null){
            throw new NoConnectionException();
        }
        boolean isFirst = stub.isFirst(this, nickname);
        //chiama chiedi numberOfPlayer o waiting
        System.out.println("Is first: " + isFirst);
        if (isFirst) {//This should be changed
            controller.askNumberOfPlayer();
        }else{
            if(lobbyIsReady){
                controller.waitLobby();
            }else{
                controller.stopWaiting();
            }
        }
    }

    public void disconnect(String issue) throws RemoteException {
        controller.error(issue);
    }

    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, NotExistsPlayerException, NoSuchFieldException {
        stub.insertNumberOfPlayers(numberOfPlayers);
        //TODO try/catch

        lobbyIsReady = true;
    }

    public void stopWaiting(String nickname) throws RemoteException {
        controller.setNickname(nickname);
    }

    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        controller.refreshUsers(playersAndPins);
    }

    public void pickColor(Color color) throws PinNotAvailableException, RemoteException, NoSuchFieldException {
        //TODO call controller ok client
        stub.chooseColor(controller.getNickname(), color);
    }

    //GAME START

    public void sendInfoOnTable(){
        controller.sendInfoOnTable();
    }

    public void showStartingCard(int startingCardId){
        controller.showStartingCard(startingCardId);
    }

    //Called by the user (they have .getNickname())
    public void chooseSideStartingCard(boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException {
        stub.chooseSideStartingCard(controller.getNickname(), side);
        //It places it from the server
    }

    public void showObjectiveCards(Integer[] objectiveCardIds){
        controller.showObjectiveCards(objectiveCardIds);
    }

    public void showSecretObjectiveCards(Integer[] objectiveCardIds){
        controller.showSecretObjectiveCards(objectiveCardIds);
    }

    //Called by the user (they have .getNickname())
    public void chooseSecretObjectiveCard(int indexCard) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {//0 or 1
        stub.chooseSecretObjectiveCard(controller.getNickname(), indexCard);
        controller.setSecretObjectiveCard(indexCard);
    }

    public void showHand(String nickname, Integer[] hand){
        controller.getHand(nickname, hand);
    }

    public void showHiddenHand(String nickname, Kingdom[] hand){
        controller.getHiddenHand(hand);
    }

//Game Flow
    public void refreshTurnInfo(String currentPlayer, GameState gameState){
        controller.refreshTurnInfo(currentPlayer, gameState);
    }


    //Called by the user (they have .getNickname())
    public void playCard(int indexHand, Point position, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException, NotEnoughResourcesException {
        stub.placeCard(controller.getNickname(), indexHand, position, side);
    }


    //Called for playCard() and for chooseSideStartingCard()
    public void placeCard(String nickname, int id, Point position, boolean side, HashMap<Sign, Integer> resources, int points){
        controller.placeCard(nickname, id, position, side);//TODO is unique for type of card?
        controller.updateResources(nickname, resources);
        controller.updateScore(nickname, points);
    }

    public void drawCard(String nickname, boolean gold, int onTableOrDeck) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        stub.drawCard(nickname, gold, onTableOrDeck);
    }

    //Different methods so client doesn't receive id of an hidden card
    public void drawnCard(String nickname, int cardId){
        controller.drawCard(nickname, cardId);//it should understand where to place it
    }

//    public void showHiddenDrawnCardHand(String nickname, Kingdom[] hand){
//        controller.getHiddenHand(hand);
//    }

    public void moveCard(int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck){
        controller.newCardOnTable(newCardId, gold, onTableOrDeck);
        controller.newHeadDeck(headDeck, gold, onTableOrDeck);
    }

    public void showEndGame(HashMap<String, Integer> extraPoints, HashMap<String, Integer> ranking){
        controller.showExtraPoints(extraPoints);
        controller.showRanking(ranking);
    }
}
