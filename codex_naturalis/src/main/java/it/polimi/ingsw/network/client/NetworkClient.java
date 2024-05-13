package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;

import java.awt.*;
import java.rmi.RemoteException;

public abstract class NetworkClient {

    //metodi che il client chiama il server.
    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, ClosingLobbyException, SameNameException, LobbyCompleteException, NoNameException {
    }

    public void chooseColor(Color color) throws ColorAlreadyTakenException, RemoteException,  NoNameException {
    }

    public void chooseSideStartingCard(boolean side) throws WrongGamePhaseException, NoTurnException,  NoNameException {
    }

    public void chooseSecretObjectiveCard(int indexCard) throws WrongGamePhaseException, NoTurnException,  NoNameException {
    }

    public void playCard(int indexHand, Point position, boolean side) throws WrongGamePhaseException, NoTurnException,  NotEnoughResourcesException, NoNameException, CardPositionException {
    }

    public void login(String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
    }

    public void drawCard(String nickname, boolean gold, int onTableOrDeck) throws WrongGamePhaseException, NoTurnException,  NoNameException {
    }
}

