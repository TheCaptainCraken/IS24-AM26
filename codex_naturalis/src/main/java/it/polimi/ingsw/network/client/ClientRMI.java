package it.polimi.ingsw.network.client;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.server.rmi.LoggableServer;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientRMI extends NetworkClient implements RMIClientInterface {
    int PORT = 1234;
    Controller controller;
    LoggableServer stub = null;
    Registry registry = null;

    //TODO costruttore

    public void login(String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
        try {
            registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
            stub = (LoggableServer) registry.lookup("Loggable");
        }
        catch (NotBoundException e) {
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
        controller.updateDrawCard(nickname, cardId);
    }

    public void disconnect() {
        controller.disconnect();
    }

    public void stopWaiting(String nickname) {
        controller.setNickname(nickname);
    }

    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        controller.refreshUsers(playersAndPins);
    }

    @Override
    public void sendInfoOnTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck, Kingdom goldCardOnDeck){
        controller.sendInfoOnTable(resourceCards, goldCard, resourceCardOnDeck, goldCardOnDeck);
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
        controller.showSecretObjectiveCardsToChoose(objectiveCardIds);
    }

    @Override
    public void showHand(String nickname, Integer[] hand){
        controller.updateHand(nickname, hand);
    }

    @Override
    public void showHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand){
        controller.updateHiddenHand(nickname, hand);
    }

    @Override
    public void refreshTurnInfo(String currentPlayer, GameState gameState){
        controller.refreshTurnInfo(currentPlayer, gameState);
    }

    @Override
    public void placeCard(String nickname, int id, Point position, boolean side, HashMap<Sign, Integer> resources, int points){
        controller.updatePlaceCard(nickname, id, position, side);
        controller.updateResources(nickname, resources);
        controller.updateScore(nickname, points);
    }
    @Override
    public void moveCard(int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck){
        controller.updateCardOnTable(newCardId, gold, onTableOrDeck);
        controller.updateHeadDeck(headDeck, gold);
    }
    @Override
    public void showEndGame(HashMap<String, Integer> extraPoints, ArrayList<Player> ranking){
        controller.showExtraPoints(extraPoints);
        controller.showRanking(ranking);
    }

    @Override
    public void getIsFirst(String firstPlayer) {
        controller.getIsFirst(firstPlayer);
    }
}
