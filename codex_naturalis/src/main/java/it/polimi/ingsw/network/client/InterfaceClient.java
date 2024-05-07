package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.exception.NoConnectionException;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface InterfaceClient {

    void disconnect() throws RemoteException;

    void stopWaiting(String nickname) throws RemoteException;

    void refreshUsers(HashMap<String, Color> playersAndPins);

    void sendInfoOnTable();

    void showStartingCard(int startingCardId);

    void showObjectiveCards(Integer[] objectiveCardIds);

    void showSecretObjectiveCards(Integer[] objectiveCardIds);

    void showHand(String nickname, Integer[] hand);

    void showHiddenHand(String nickname, Kingdom[] hand);

    void refreshTurnInfo(String currentPlayer, GameState gameState);

    void placeCard(String nickname, int id, Point position, boolean side, HashMap<Sign, Integer> resources, int points);

    void moveCard(int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck);

    void showEndGame(HashMap<String, Integer> extraPoints, HashMap<String, Integer> ranking);
}

