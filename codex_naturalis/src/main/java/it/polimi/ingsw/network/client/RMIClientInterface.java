package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Sign;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;

public interface RMIClientInterface extends Remote{
    void refreshUsers(HashMap<String, Color> playersAndPins);

    void sendInfoOnTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck, Kingdom goldCardOnDeck);

    void showStartingCard(int startingCardId);

    void showObjectiveCards(Integer[] objectiveCardIds);

    void showSecretObjectiveCards(Integer[] objectiveCardIds) ;

    void showHand(String nickname, Integer[] hand);

    void showHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand);

    void refreshTurnInfo(String currentPlayer, GameState gameState);

    void placeCard(String nickname, int id, Point position, boolean side, HashMap<Sign, Integer> resources, int points);

    void moveCard(int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck);

    void showEndGame(HashMap<String, Integer> extraPoints, ArrayList<Player> ranking);

    void getIsFirst(String firstPlayer);
}
