package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Sign;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface RMIClientInterface extends Remote {
    void refreshUsers(HashMap<String, Color> playersAndPins) throws RemoteException;

    void sendInfoOnTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck, Kingdom goldCardOnDeck) throws RemoteException;

    void showStartingCard(int startingCardId) throws RemoteException;

    void showObjectiveCards(Integer[] objectiveCardIds) throws RemoteException;

    void showSecretObjectiveCards(Integer[] objectiveCardIds) throws RemoteException;

    void showHand(String nickname, Integer[] hand) throws RemoteException;

    void showHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand) throws RemoteException;

    void refreshTurnInfo(String currentPlayer, GameState gameState) throws RemoteException;

    void placeCard(String nickname, int id, Point position, boolean side, HashMap<Sign, Integer> resources, int points) throws RemoteException;

    void moveCard(int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) throws RemoteException;

    void showEndGame(HashMap<String, Integer> extraPoints, ArrayList<Player> ranking) throws RemoteException;

    void getIsFirst(String firstPlayer) throws RemoteException;

    void nicknameCorrect(String nickname) throws RemoteException;
}
