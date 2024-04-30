package it.polimi.ingsw.server.server;

import it.polimi.ingsw.model.exception.FullLobbyException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoggableServer extends Remote {
    int pong() throws RemoteException, FullLobbyException;
    void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException;
}
