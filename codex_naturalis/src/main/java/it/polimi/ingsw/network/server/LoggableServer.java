package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.client.ClientRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoggableServer extends Remote {
    boolean isFirst(ClientRMI clientRMI, String nickname) throws RemoteException, SameNameException, LobbyCompleteException, NotBoundException;
    void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, NotExistsPlayerException, NoSuchFieldException;
    void chooseColor(String nickname, Color color) throws RemoteException, NoSuchFieldException, ColorAlreadyTakenException;
}
