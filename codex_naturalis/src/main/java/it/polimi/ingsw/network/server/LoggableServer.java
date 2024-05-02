package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.ClientRMI;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.PinNotAvailableException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoggableServer extends Remote {
    boolean isFirst(ClientRMI clientRMI, String nickname) throws RemoteException, SameNameException, LobbyCompleteException;
    void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException;
    void chooseColor(String nickname, String color) throws RemoteException, NoSuchFieldException, PinNotAvailableException;
}
