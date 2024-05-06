package it.polimi.ingsw.controller.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.*;

import it.polimi.ingsw.network.client.ClientRMI;
import it.polimi.ingsw.network.client.InterfaceClient;
import it.polimi.ingsw.network.exception.NoConnectionException;
import it.polimi.ingsw.view.Tui;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.HashMap;

public class Controller {
    private String nickname;
    private InterfaceClient connection;
    private Tui view;

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
            //onnection = new ClientSocket();
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

    //TODO
    public void setNickname(String nickname) {

    }

    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        view.refreshUsers(playersAndPins);
    }

    public void disconnect() {
        view.disconnect();
    }

    public String getNickname() {
        return null;
    }

    //TODO also for controller server
    public void sendInfoOnTable() {
    }

    public void showStartingCard(int startingCardId) {
        view.showStartingCard(startingCardId);
    }

    //TODO quelle di tutti
    public void showObjectiveCards(Integer[] objectiveCardIds) {
    }

    //TODO per scegliere le due
    public void showSecretObjectiveCards(Integer[] objectiveCardIds) {
    }

    public void setSecretObjectiveCard(int indexCard) {//It's personal
    }

    //TODO quelle di tutti
    public void getHand(String nickname, Integer[] hand) {
    }

    public void getHiddenHand(Kingdom[] hand) {
    }

    //TODO current player e fase di gioco
    public void refreshTurnInfo(String currentPlayer, GameState gameState) {
    }

    //TODO generica per tutti i giocatori
    public void placeCard(String nickname, int id, Point position, boolean side) {
    }

    //TODO generica per tutti i giocatori
    public void updateResources(String nickname, HashMap<Sign, Integer> resources) {
    }
    //TODO generica per tutti i giocatori
    public void updateScore(String nickname, int points) {
    }
    //TODO generica per tutti i giocatori
    public void drawCard(String nickname, int cardId) {
    }
    //TODO generica per tutti i giocatori
    public void newCardOnTable(int newCardId, boolean gold, int onTableOrDeck) {
    }
    //TODO generica per tutti i giocatori
    public void newHeadDeck(Kingdom headDeck, boolean gold, int onTableOrDeck) {
    }
    //TODO generica per tutti i giocatori
    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
    }
    //TODO generica per tutti i giocatori
    public void showRanking(HashMap<String, Integer> ranking) {
    }
    //TODO generica per tutti i giocatori
    public void getIsFirst(String firstPlayer) {
    }


    //TODO metodi chiamata server
    public void login(String nickname) {
        try{
            connection.login(nickname);
        } catch (SameNameException e) {
            view.sameName(nickname);
        }catch (LobbyCompleteException e){
            view.lobbyComplete();
        }catch (NoConnectionException e){
            //TODO
        } catch (RemoteException e) {
            //TODO;
        }
    }

    public void insertNumberOfPlayers(int numberOfPlayers) {
        try{
            connection.insertNumberOfPlayers(numberOfPlayers);
        } catch (RemoteException e) {
            //TODO
        } catch (NoSuchFieldException e) {
            //TODO
        } catch (ClosingLobbyException e) {
            //TODO
        } catch (SameNameException e) {
            //TODO
        } catch (LobbyCompleteException e) {
            //TODO
        }
    }

    public void chooseColor(Color color) {
        try{
            connection.chooseColor(color);
        } catch (RemoteException e) {
            //TODO
        } catch (NoSuchFieldException e) {
            //TODO
        } catch (ColorAlreadyTakenException e) {
            view.colorAlreadyTaken();
        }
    }

    public void chooseSideStartingCard(boolean side) {
        try{
            connection.chooseSideStartingCard(side);
        } catch (WrongGamePhaseException e) {
            //TODO
        } catch (NoTurnException e) {
            //TODO
        } catch (NotExistsPlayerException e) {
            //TODO
        } catch (NoSuchFieldException e) {
            //TODO
        }
    }

    public void chooseSecretObjectiveCard(int indexCard) {
        try{
            connection.chooseSecretObjectiveCard(indexCard);
        } catch (WrongGamePhaseException e) {
            //TODO
        } catch (NoTurnException e) {
            //TODO
        } catch (NotExistsPlayerException e) {
            //TODO
        } catch (NoSuchFieldException e) {
            //TODO
        }
    }

    public void playCard(int indexHand, Point position, boolean side) {
        try{
            connection.playCard(indexHand, position, side);
        } catch (WrongGamePhaseException e) {
            //TODO
        } catch (NoTurnException e) {
            //TODO
        } catch (NotExistsPlayerException e) {
            //TODO
        } catch (NoSuchFieldException e) {
            //TODO
        } catch (NotEnoughResourcesException e) {
            //TODO
        }
    }

    //TODO drawCard
}
