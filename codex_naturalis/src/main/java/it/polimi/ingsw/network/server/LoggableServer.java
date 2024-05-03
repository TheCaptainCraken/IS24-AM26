package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.client.ClientRMI;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.PinNotAvailableException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoggableServer extends Remote {

    boolean isFirst(ClientRMI clientRMI, String nickname) throws RemoteException, SameNameException, LobbyCompleteException;

    void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, NoSuchFieldException;

    void chooseColor(String nickname, Color color) throws RemoteException, NoSuchFieldException, PinNotAvailableException;

    void chooseSideStartingCard(String nickname, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException ;

    void chooseSecretObjectiveCard(String nickname, int indexCard) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException;

    void placeCard(String nickname, int indexHand, Point position, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException, NotEnoughResourcesException ;

    void drawCard(String nickname, boolean gold, int onTableOrDeck) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException ;
}
