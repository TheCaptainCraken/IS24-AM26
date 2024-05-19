package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.client.NetworkClient;
import it.polimi.ingsw.view.Phase;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


public class ClientRMI extends NetworkClient implements RMIClientInterface {
    static int PORT = 1099;
    Controller controller;
    RMIClientInterface exportedClient = null;
    LoggableServer stub = null;
    Registry registry = null;

    public ClientRMI(Controller controller) throws RemoteException, SameNameException, LobbyCompleteException, NotBoundException {
        this.controller = controller;

        // Esportazione dell'oggetto ClientRMI come oggetto remoto
         exportedClient = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);

        // Cerca il registro RMI
        registry = LocateRegistry.getRegistry("127.0.0.1", PORT);

        // Cerca l'oggetto remoto del server
        stub = (LoggableServer) registry.lookup("Loggable");

    }

    public void login(String name) throws RemoteException, SameNameException, LobbyCompleteException {
        boolean isFirst = false;
        try{
            isFirst = stub.loginAndIsFirst(exportedClient, name);
            Controller.setNickname(name);
        }catch(RemoteException e){
            controller.noConnection();
        }catch (LobbyCompleteException e){
            //TODO
        }catch (SameNameException e){
            Controller.phase = Phase.LOGIN;
            controller.sameName(name);
        }
        if (isFirst) {
            Controller.setPhase(Phase.NUMBER_OF_PLAYERS);
            controller.askNumberOfPlayer();
        }else{
            if(stub.lobbyIsReady()){
                Controller.setPhase(Phase.COLOR);
            }else{
                Controller.setPhase(Phase.WAIT_NUMBER_OF_PLAYERS);
                controller.waitLobby();
            }
        }
    }

    public void insertNumberOfPlayers(int numberOfPlayers) {
        try {
            Controller.phase = Phase.WAIT;
            stub.insertNumberOfPlayers(numberOfPlayers);
            controller.correctNumberOfPlayers(numberOfPlayers);
        } catch (RemoteException e) {
           controller.noConnection();
        } catch (ClosingLobbyException e) {
            //TODO
        } catch (SameNameException e) {
            controller.sameName(controller.getNickname());
        } catch (LobbyCompleteException e) {
            //TODO
        } catch (NoNameException e) {
           controller.noName();
        }
    }


    public void chooseColor(Color color)  {
        try{
            Controller.phase = Phase.WAIT;
            stub.chooseColor(controller.getNickname(), color);
            System.out.println(Controller.getPhase());
        }
        catch (RemoteException e){
            controller.noConnection();
        } catch (ColorAlreadyTakenException e) {
            Controller.phase = Phase.COLOR;
            controller.colorAlreadyTaken();
        } catch (NoNameException e) {
           controller.noName();
        }
    }

    public void chooseSideStartingCard(boolean side){
        try {
            Controller.phase = Phase.WAIT;
            stub.chooseSideStartingCard(controller.getNickname(), side);
        } catch (RemoteException e) {
            Controller.phase = Phase.CHOOSE_SIDE_STARTING_CARD; //TODO
            controller.noConnection();
        } catch (WrongGamePhaseException e) {
            Controller.phase = Phase.CHOOSE_SIDE_STARTING_CARD;
            controller.wrongPhase();
        } catch (NoTurnException e) {
            Controller.phase = Phase.CHOOSE_SIDE_STARTING_CARD;
            controller.noTurn();
        } catch (NoNameException e) {
            controller.noName();
        }
    }

    public void chooseSecretObjectiveCard(int indexCard)
    {
        try {
            Controller.phase = Phase.WAIT;
            stub.chooseSecretObjectiveCard(controller.getNickname(), indexCard);
            controller.updateAndShowSecretObjectiveCard(indexCard);
        } catch (RemoteException e) {
            Controller.phase = Phase.CHOOSE_SECRET_OBJECTIVE_CARD;
            controller.noConnection();
        } catch (WrongGamePhaseException e) {
            Controller.phase = Phase.CHOOSE_SECRET_OBJECTIVE_CARD;
           controller.wrongPhase();
        } catch (NoTurnException e) {
            Controller.phase = Phase.CHOOSE_SECRET_OBJECTIVE_CARD;
            controller.noTurn();
        } catch (NoNameException e) {
            controller.noName(); //TODO
        }
    }

    public void playCard(int indexHand, Point position, boolean side) {
        try {
            stub.placeCard(controller.getNickname(), indexHand, position, side);
        } catch (RemoteException e) {
            controller.noConnection();
        } catch (WrongGamePhaseException e) {
            controller.wrongPhase();
        } catch (NoTurnException e) {
             controller.noTurn();
        } catch (NotEnoughResourcesException e) {
            controller.notEnoughResources();
        } catch (NoNameException e) {
            controller.NoName();
        } catch (CardPositionException e) {
           controller.cardPosition();
        }
    }

    public void drawCard(String nickname, boolean gold, int onTableOrDeck)
    {
        int cardId;
        try {
            cardId = stub.drawCard(nickname, gold, onTableOrDeck);
            controller.updateDrawCard(nickname, cardId);
        } catch (RemoteException e) {
            controller.noConnection();
        } catch (WrongGamePhaseException e) {
            controller.wrongPhase();
        } catch (NoTurnException e) {
            controller.noTurn();
        } catch (NoNameException e) {
            controller.noName();
        }
    }

    public void disconnect() {
        controller.disconnect();
    }

    public void stopWaiting() {
        Controller.setPhase(Phase.COLOR);
    }

    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        controller.refreshUsers(playersAndPins);
        //TODO logica opposta
    }

    @Override
    public void sendInfoOnTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck, Kingdom goldCardOnDeck){
        controller.cardsOnTable(resourceCards, goldCard, resourceCardOnDeck, goldCardOnDeck);
    }

    @Override
    public void showStartingCard(int startingCardId){
        controller.updateAndShowStartingCard(startingCardId);
    }

    @Override
    public void sendCommonObjectiveCards(Integer[] objectiveCardIds){
        controller.showObjectiveCards(objectiveCardIds);
    }

    @Override
    public void sendSecretObjectiveCardsToChoose(Integer[] objectiveCardIds){
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
        controller.turnInfo(currentPlayer, gameState);
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
    public void getIsFirstAndStartGame(String firstPlayer) {
        Controller.setPhase(Phase.GAMEFLOW);
        controller.showIsFirst(firstPlayer);
    }

}
