package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.network.server.LoggableServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ClientRMI implements LoggableClient{
    static int PORT = 1234;
    Controller controller;//TODO check if is to do a singletone
    LoggableServer stub = null;
    Registry registry = null;
    //Serving stuffs
    LoggableClient clientSkeleton =  null;
    ClientRMI obj = new ClientRMI();//

    String nickname = null;


    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello, World!");
    }

    public void connect(String nickname) throws RemoteException, SameNameException, LobbyCompleteException {
        if(stub == null){
            throw new NoConnectionException();
        }
        String ip="127.0.0.1";//TODO get from machine

        boolean isFirst = stub.ping(nickname, ip);
        //chiama chiedi numberOfPlayer o waiting

        System.out.println("Remote method invoke: " + port);
        if (isFirst) {//This should be changed
            controller.askNumberOfPlayer();
        }else
            controller.wait();
        }
        clientSkeleton = (LoggableClient) UnicastRemoteObject.exportObject(obj, port);//TODO choose port

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

    }

    @Override
    public void disconnect(String issue) throws RemoteException {
        controller.error(issue);
    }

    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException {
        stub.insertNumberOfPlayers(numberOfPlayers);
    }

    @Override
    public void stopWaiting(String nickname) throws RemoteException {
        this.nickname = nickname;
        controller.setNickname();
    }

    @Override
    public void refreshUsers(HashMap<String, Color> users) {
        controller.refreshUsers(users);
    }

    public void pickColor(Color color) {
        //TODO call controller ok client
        stub.chooseColor(controller.getNickname(), color);
    }

    @Override
    public void startGame() {
        controller.startGame();
    }
}
