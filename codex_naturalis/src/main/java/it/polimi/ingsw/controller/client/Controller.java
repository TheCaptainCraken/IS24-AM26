package it.polimi.ingsw.controller.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.*;

import it.polimi.ingsw.network.client.ClientRMI;
import it.polimi.ingsw.network.client.NetworkClient;
import it.polimi.ingsw.view.Tui;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.HashMap;

public class Controller {
    private String nickname;
    private NetworkClient connection;
    private Tui view;

    public Controller(){
        //TODO
    }

    public void setView(String typeOfView) {
        if(typeOfView.equals("TUI")){
            view = new Tui();
        }else if(typeOfView.equals("GUI")){
            //TODO
            //view = new GUI();
        }
    }

    public void createInstanceOfConnection(String typeOfConnection){
        if(typeOfConnection.equals("RMI")){
            connection = new ClientRMI();
        }else if(typeOfConnection.equals("Socket")){
            //TODO
            //connection = new ClientSocket();
        }
    }

    public void askNumberOfPlayer() {
        view.askNumberOfPlayer();
    }

    public void waitLobby() {
        view.waitLobby();
    }

    public void stopWaiting() {
        view.stopWaiting();
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        view.refreshUsers(playersAndPins);
    }

    public void disconnect() {
        view.disconnect();
    }

    public void sendInfoOnTable() {
        view.sendInfoOnTable();
    }

    public void showStartingCard(int startingCardId) {
        view.showStartingCard(startingCardId);
    }

    public void showObjectiveCards(Integer[] objectiveCardIds) {
        view.printObjectiveCards(objectiveCardIds);
    }

    public void showSecretObjectiveCards(Integer[] objectiveCardIds) {
        view.printSecretObjectiveCards(objectiveCardIds);
    }

    public void showSecretObjectiveCard(int indexCard) {
        view.showSecretObjectiveCard(indexCard);
    }

    public void getHand(String nickname, Integer[] hand) {
        //TODO no print immediata
    }

    public void getHiddenHand(Kingdom[] hand) {
        //TODO print immediata
    }

    public void refreshTurnInfo(String currentPlayer, GameState gameState) {
        view.refreshTurnInfo(currentPlayer, gameState);
    }

    public void placeCard(String nickname, int id, Point position, boolean side) {
        //TODO no print immediata, valuta cosa succede
    }

    public void updateResources(String nickname, HashMap<Sign, Integer> resources) {
        view.updateResources(nickname, resources);
    }

    public void updateScore(String nickname, int points) {
        view.updateScore(nickname, points);
    }

    public void drawCard(String nickname, int cardId) {
        //TODO capire se fare print immediata o meno
    }

    public void newCardOnTable(int newCardId, boolean gold, int onTableOrDeck) {
        //TODO no print immediata
    }

    public void newHeadDeck(Kingdom headDeck, boolean gold, int onTableOrDeck) {
        //TODO no print immediata
    }

    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
        view.showExtraPoints(extraPoints);
    }

    public void showRanking(HashMap<String, Integer> ranking) {
        view.showRanking(ranking);
    }
    //TODO generica per tutti i giocatori
    public void getIsFirst(String firstPlayer) {
    }

    //parte client connect server. Chiamata dal client
    //TODO metodi chiamata server
    public void login(String nickname) {
        try{
            connection.login(nickname);
        } catch (SameNameException e) {
            view.sameName(nickname);
        } catch (LobbyCompleteException e){
            view.lobbyComplete();
        } catch (RemoteException e) {
            view.noConnection();
        }
    }

    public void insertNumberOfPlayers(int numberOfPlayers) {
        //TODO correct player to ask for number of players. First player?
        try{
            connection.insertNumberOfPlayers(numberOfPlayers);
        } catch (RemoteException e) {
            view.noConnection();
        } catch (ClosingLobbyException e) {
            view.closingLobbyError();
        } catch (SameNameException e) {
            //TODO
        } catch (LobbyCompleteException e) {
            //TODO
        } catch (NoNameException e) {
            throw new RuntimeException(e);
        }
    }

    public void chooseColor(Color color) {
        try{
            connection.chooseColor(color);
        } catch (RemoteException e) {
            view.noConnection();
        } catch (ColorAlreadyTakenException e) {
            view.colorAlreadyTaken();
        } catch (NoNameException e) {
            throw new RuntimeException(e);
        }
    }

    public void chooseSideStartingCard(boolean side) {
        try{
            connection.chooseSideStartingCard(side);
        } catch (WrongGamePhaseException e) {
            view.wrongGamePhase();
        } catch (NoTurnException e) {
            view.noTurn();
        } catch (NotExistsPlayerException e) {
            view.noPlayer();
        } catch (NoNameException e) {
            throw new RuntimeException(e);
        }
    }

    public void chooseSecretObjectiveCard(int indexCard) {
        try{
            connection.chooseSecretObjectiveCard(indexCard);
        } catch (WrongGamePhaseException e) {
            view.wrongGamePhase();
        } catch (NoTurnException e) {
            view.noTurn();
        } catch (NotExistsPlayerException e) {
            view.noPlayer();
        } catch (NoNameException e) {
            throw new RuntimeException(e);
        }
    }

    public void playCard(int indexHand, Point position, boolean side) {
        try{
            connection.playCard(indexHand, position, side);
        } catch (WrongGamePhaseException e) {
            view.wrongGamePhase();
        } catch (NoTurnException e) {
            view.noTurn();
        } catch (NotExistsPlayerException e) {
            view.noPlayer();
        } catch (NotEnoughResourcesException e) {
            view.notEnoughResources(nickname);
        } catch (NoNameException e) {
            //TODO
        } catch (CardPositionException e) {
           //TODO
        }
    }

    public void setSecretObjectiveCard(int indexCard) {
        try {
            connection.chooseSecretObjectiveCard(indexCard);
        } catch (WrongGamePhaseException e) {
            view.wrongGamePhase();
        } catch (NoTurnException e) {
            view.noTurn();
        } catch (NotExistsPlayerException e) {
            view.noPlayer();
        } catch (NoNameException e) {
            throw new RuntimeException(e);
        }

    }

    public void drawCard(String nickname, boolean gold, int onTableOrDeck) {
        try{
            connection.drawCard(nickname, gold, onTableOrDeck);
           // drawCard(nickname, cardId);
        } catch (WrongGamePhaseException e) {
            view.wrongGamePhase();
        } catch (NoTurnException e) {
            view.noTurn();
        } catch (NotExistsPlayerException e) {
            view.noPlayer();
        } catch (NoNameException e) {
            throw new RuntimeException(e);
        }
    }

}
