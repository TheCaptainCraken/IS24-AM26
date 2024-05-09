package it.polimi.ingsw.network.client;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.server.LoggableServer;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

public class ClientRMI extends NetworkClient {
    int PORT = 1234;
    Controller controller;
    LoggableServer stub = null;
    Registry registry = null;
    ClientRMI obj = new ClientRMI();

    //TODO costruttore

    public void login(String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
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

        boolean isFirst = stub.isFirst(this, nickname);
        if (isFirst) {
            //This should be changed
            controller.askNumberOfPlayer();
        }else{
            if(stub.lobbyIsReady()){
                controller.waitLobby();
            }else{
                controller.stopWaiting();
            }
        }
    }

    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException,
            SameNameException, LobbyCompleteException, NoNameException, ClosingLobbyException {
        stub.insertNumberOfPlayers(numberOfPlayers);
    }


    public void chooseColor(Color color) throws RemoteException,  ColorAlreadyTakenException, NoNameException {
        //TODO call controller ok client
        stub.chooseColor(controller.getNickname(), color);
    }


    public void chooseSideStartingCard(boolean side) throws WrongGamePhaseException, NoTurnException, NoNameException {
        stub.chooseSideStartingCard(controller.getNickname(), side);
    }

    public void chooseSecretObjectiveCard(int indexCard)
            throws WrongGamePhaseException, NoTurnException,  NoNameException {
        stub.chooseSecretObjectiveCard(controller.getNickname(), indexCard);
        controller.showSecretObjectiveCard(indexCard);
    }

    public void playCard(int indexHand, Point position, boolean side) throws WrongGamePhaseException, NoTurnException, NoNameException,  NotEnoughResourcesException, NoNameException, CardPositionException {
        stub.placeCard(controller.getNickname(), indexHand, position, side);
    }

    public void drawCard(String nickname, boolean gold, int onTableOrDeck)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        int cardId = stub.drawCard(nickname, gold, onTableOrDeck);
        controller.drawCard(nickname, cardId);
    }

    @Override
    public void disconnect() {
        controller.disconnect();
    }

    @Override
    public void stopWaiting(String nickname) {
        controller.setNickname(nickname);
    }

    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        controller.refreshUsers(playersAndPins);
    }

    @Override
    public void sendInfoOnTable(){
        controller.sendInfoOnTable();
    }

    @Override
    public void showStartingCard(int startingCardId){
        controller.showStartingCard(startingCardId);
    }

    @Override
    public void showObjectiveCards(Integer[] objectiveCardIds){
        controller.showObjectiveCards(objectiveCardIds);
    }

    @Override
    public void showSecretObjectiveCards(Integer[] objectiveCardIds){
        controller.showSecretObjectiveCards(objectiveCardIds);
    }

    @Override
    public void showHand(String nickname, Integer[] hand){
        controller.updateHand(nickname, hand);
    }

    @Override
    public void showHiddenHand(String nickname, Kingdom[] hand){
        controller.updateHiddenHand(nickname, hand);
    }

    @Override
    public void refreshTurnInfo(String currentPlayer, GameState gameState){
        controller.refreshTurnInfo(currentPlayer, gameState);
    }

    @Override
    public void placeCard(String nickname, int id, Point position, boolean side, HashMap<Sign, Integer> resources, int points){
        controller.placeCard(nickname, id, position, side);
        controller.updateResources(nickname, resources);
        controller.updateScore(nickname, points);
    }

    @Override
    public void moveCard(int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck){
        controller.newCardOnTable(newCardId, gold, onTableOrDeck);
        controller.newHeadDeck(headDeck, gold, onTableOrDeck);
    }

    @Override
    public void showEndGame(HashMap<String, Integer> extraPoints, HashMap<String, Integer> ranking){
        controller.showExtraPoints(extraPoints);
        controller.showRanking(ranking);
    }

    //TODO, serve connessione
    public void getIsFirst(String firstPlayer) {
        controller.getIsFirst(firstPlayer);
    }
}
