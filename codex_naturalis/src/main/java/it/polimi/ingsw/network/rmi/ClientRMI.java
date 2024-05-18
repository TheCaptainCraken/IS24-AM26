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

    public void login(String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
        boolean isFirst = false;
        try{
            isFirst = stub.loginAndIsFirst(exportedClient, nickname);
            Controller.setNickname(nickname);
        }catch(RemoteException e){
            controller.noConnection();
        }catch (LobbyCompleteException e){
            //TODO
        }catch (SameNameException e){
            controller.sameName();
        }
        if (isFirst) {
            Controller.setPhase(Phase.NUMBER_OF_PLAYERS);
            controller.askNumberOfPlayer();
        }else{
            if(stub.lobbyIsReady()){
                Controller.setPhase(Phase.COLOR);
            }else{
                //TODO
                controller.waitLobby();
            }
        }
    }

    public void insertNumberOfPlayers(int numberOfPlayers) {
        try {
            stub.insertNumberOfPlayers(numberOfPlayers);
            controller.correctNumberOfPlayers(numberOfPlayers);
        } catch (RemoteException e) {
           controller.noConnection();
        } catch (ClosingLobbyException e) {
            //TODO
        } catch (SameNameException e) {
            controller.sameName();
        } catch (LobbyCompleteException e) {
            //TODO
        } catch (NoNameException e) {
           controller.noName();
        }
    }


    public void chooseColor(Color color)  {
        try{
            stub.chooseColor(controller.getNickname(), color);
            if(Controller.getPhase() == Phase.COLOR) {
                Controller.setPhase(Phase.WAIT);
            }else{
                Controller.setPhase(Phase.CHOOSE_SIDE_STARTING_CARD);
            }
        }
        catch (RemoteException e){
            controller.noConnection();
        } catch (ColorAlreadyTakenException e) {
            controller.colorAlreadyTaken();
        } catch (NoNameException e) {
           controller.noName();
        }
    }

    public void chooseSideStartingCard(boolean side){
        try {
            stub.chooseSideStartingCard(controller.getNickname(), side);
            if(Controller.getPhase() == Phase.CHOOSE_SIDE_STARTING_CARD) {
                Controller.setPhase(Phase.WAIT);
            }else{
                Controller.setPhase(Phase.CHOOSE_SECRET_OBJECTIVE_CARD);
            }

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

    public void chooseSecretObjectiveCard(int indexCard)
    {
        try {
            stub.chooseSecretObjectiveCard(controller.getNickname(), indexCard);
            if(Controller.getPhase() == Phase.CHOOSE_SECRET_OBJECTIVE_CARD) {
                Controller.setPhase(Phase.WAIT);
            }else {
                Controller.setPhase(Phase.GAMEFLOW);
            }
            controller.updateSecretObjectiveCard(indexCard);
            controller.showSecretObjectiveCard(indexCard);
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
