package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.client.ClientRMI;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoggableServer extends Remote {

    boolean isFirst(ClientRMI clientRMI, String nickname) throws RemoteException, SameNameException, LobbyCompleteException;

    boolean lobbyIsReady() throws RemoteException;

    void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, ClosingLobbyException, SameNameException, LobbyCompleteException, NoNameException;

    void chooseColor(String nickname, Color color) throws RemoteException, ColorAlreadyTakenException, NoNameException;

    void chooseSideStartingCard(String nickname, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,  NoNameException;

    void chooseSecretObjectiveCard(String nickname, int indexCard) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,  NoNameException;

    void placeCard(String nickname, int indexHand, Point position, boolean side)
            throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NotEnoughResourcesException, NoNameException, CardPositionException;

    int drawCard(String nickname, boolean gold, int onTableOrDeck) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,  NoNameException;
}
