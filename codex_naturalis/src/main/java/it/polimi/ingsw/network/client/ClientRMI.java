package it.polimi.ingsw.network.client;

import it.polimi.ingsw.controller.Controller;//TODO lato client
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NotExistsPlayerException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.network.exception.NoConnectionException;
import it.polimi.ingsw.network.server.LoggableServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ClientRMI{
    int PORT = 1234;
    Controller controller;//TODO check if is to do a singletone
    LoggableServer stub = null;
    Registry registry = null;
    //Serving stuffs
    LoggableClient clientSkeleton =  null;
    ClientRMI obj = new ClientRMI();//
    boolean lobbyIsReady = false;


    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello, World!");
    }

    public void login(String nickname) throws
            RemoteException, SameNameException, LobbyCompleteException, NoConnectionException {

        try {
            registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
            stub = (LoggableServer) registry.lookup("Loggable");
        } catch (RemoteException e) {
            System.out.println("Client exception: " + e.toString());
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.out.println("Client exception: " + e.toString());
            throw new RuntimeException(e);
        }
        if(stub == null){
            throw new NoConnectionException();
        }
        boolean isFirst = stub.isFirst(this, nickname);
        //chiama chiedi numberOfPlayer o waiting
        System.out.println("Is first: " + isFirst);
        if (isFirst) {//This should be changed
            controller.askNumberOfPlayer();
        }else{
            if(lobbyIsReady){
                controller.waitLobby();
            }else{
                controller.stopWaiting();
            }
        }
    }

    public void disconnect(String issue) throws RemoteException {
        controller.error(issue);
    }

    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, NotExistsPlayerException, NoSuchFieldException {
        stub.insertNumberOfPlayers(numberOfPlayers);
        //TODO try/catch

        lobbyIsReady = true;
    }

    public void stopWaiting(String nickname) throws RemoteException {
        controller.setNickname(nickname);
    }

    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        controller.refreshUsers(playersAndPins);
    }

    public void pickColor(Color color) {
        //TODO call controller ok client
        stub.chooseColor(controller.getNickname(), color);
    }

    //GAME START

    public void sendInfoOnTable(){
        controller.sendInforOnTable();
    }

    public void chooseSideStartingCard(boolean side){
        stub.chooseSideStartingCard();
        controller.setSideStartingCard(side);
    }

    public void getStartingCard(int rootCard) {
        //TODO
    }
}
