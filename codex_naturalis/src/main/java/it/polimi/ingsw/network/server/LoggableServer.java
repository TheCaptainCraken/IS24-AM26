package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.PinNotAvailableException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoggableServer extends Remote {
    boolean ping(String nickname, String ip) throws RemoteException, SameNameException, LobbyCompleteException;
    void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException;
    void chooseColor(String nickname, String color) throws RemoteException, NoSuchFieldException, PinNotAvailableException;
}
