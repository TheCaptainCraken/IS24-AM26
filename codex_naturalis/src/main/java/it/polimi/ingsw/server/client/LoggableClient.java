package it.polimi.ingsw.server.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoggableClient extends Remote {
    void stopWaiting() throws RemoteException;
}
