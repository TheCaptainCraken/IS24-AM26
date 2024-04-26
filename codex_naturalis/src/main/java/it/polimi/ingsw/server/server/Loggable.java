package it.polimi.ingsw.server.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Loggable extends Remote {
    Boolean ping() throws RemoteException;
}
