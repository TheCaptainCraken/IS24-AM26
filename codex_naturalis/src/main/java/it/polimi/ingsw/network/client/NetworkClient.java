package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;

import java.awt.*;
import java.rmi.RemoteException;

public abstract class NetworkClient {

    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, ClosingLobbyException, SameNameException, LobbyCompleteException, NoNameException {
    }

    public void chooseColor(Color color) throws  ColorAlreadyTakenException, RemoteException,  NoNameException {
    }

    public void chooseSideStartingCard(boolean side) throws RemoteException, WrongGamePhaseException, NoTurnException,  NoNameException {
    }

    public void chooseSecretObjectiveCard(int indexCard){
    }

    public void playCard(int indexHand, Point position, boolean side) throws RemoteException, WrongGamePhaseException, NoTurnException,  NotEnoughResourcesException, NoNameException, CardPositionException {
    }

    public void login(String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
    }

    public void drawCard(String nickname, boolean gold, int onTableOrDeck) throws RemoteException, WrongGamePhaseException, NoTurnException,  NoNameException {
    }
}

