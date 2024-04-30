package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Color;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface LoggableClient extends Remote {
    void disconnect(String issue) throws RemoteException;
    void stopWaiting(String nickname) throws RemoteException;
    void refreshUsers(HashMap<String, Color> users) throws RemoteException;
    void startGame() throws RemoteException;
}
