package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class NetworkClient {

    public void disconnect() {
    }

    public void stopWaiting(String nickname) {
    }

    public void refreshUsers(HashMap<String, Color> playersAndPins) {
    }

    public void sendInfoOnTable() {
        //TODO
    }

    public void showStartingCard(int startingCardId) {
    }

    public void showObjectiveCards(Integer[] objectiveCardIds) {
    }

    public void showSecretObjectiveCards(Integer[] objectiveCardIds) {
    }

    public void showHand(String nickname, Integer[] hand) {
    }

    public void showHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand) {
    }

    public void refreshTurnInfo(String currentPlayer, GameState gameState) {
    }

    public void placeCard(String nickname, int id, Point position, boolean side, HashMap<Sign, Integer> resources, int points) {
    }

    public void moveCard(int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) {
    }

    public void showEndGame(HashMap<String, Integer> extraPoints, ArrayList<Player> ranking) {
    }

    //metodi che il client chiama il server.
    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, ClosingLobbyException, SameNameException, LobbyCompleteException, NoNameException {
    }

    public void chooseColor(Color color) throws ColorAlreadyTakenException, RemoteException,  NoNameException {
    }

    public void chooseSideStartingCard(boolean side) throws WrongGamePhaseException, NoTurnException,  NoNameException {
    }

    public void chooseSecretObjectiveCard(int indexCard) throws WrongGamePhaseException, NoTurnException,  NoNameException {
    }

    public void playCard(int indexHand, Point position, boolean side) throws WrongGamePhaseException, NoTurnException,  NotEnoughResourcesException, NoNameException, CardPositionException {
    }

    public void login(String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
    }

    public void drawCard(String nickname, boolean gold, int onTableOrDeck) throws WrongGamePhaseException, NoTurnException,  NoNameException {
    }
}

