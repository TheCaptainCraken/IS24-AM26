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

    public void login(String nickname) throws RemoteException, SameNameException, LobbyCompleteException, NoConnectionException;


    public void disconnect() throws RemoteException;


    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException,
            NoSuchFieldException, ClosingLobbyException, SameNameException, LobbyCompleteException;


    public void stopWaiting(String nickname) throws RemoteException;

    public void refreshUsers(HashMap<String, Color> playersAndPins);

    public void chooseColor(Color color) throws ColorAlreadyTakenException, RemoteException, NoSuchFieldException, ColorAlreadyTakenException;

    //GAME START

    public void sendInfoOnTable();

    public void showStartingCard(int startingCardId);

    //Called by the user (they have .getNickname())
    public void chooseSideStartingCard(boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException;

    public void showObjectiveCards(Integer[] objectiveCardIds);

    public void showSecretObjectiveCards(Integer[] objectiveCardIds);

    //Called by the user (they have .getNickname())
    public void chooseSecretObjectiveCard(int indexCard)
            throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException;

    public void showHand(String nickname, Integer[] hand);

    public void showHiddenHand(String nickname, Kingdom[] hand);

    //Game Flow
    public void refreshTurnInfo(String currentPlayer, GameState gameState);


    //Called by the user (they have .getNickname())
    public void playCard(int indexHand, Point position, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException, NotEnoughResourcesException;


    //Called for playCard() and for chooseSideStartingCard()
    public void placeCard(String nickname, int id, Point position, boolean side, HashMap<Sign, Integer> resources, int points);

    public void drawCard(String nickname, boolean gold, int onTableOrDeck)
            throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException;

    public void moveCard(int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck);

    public void showEndGame(HashMap<String, Integer> extraPoints, HashMap<String, Integer> ranking);
}

