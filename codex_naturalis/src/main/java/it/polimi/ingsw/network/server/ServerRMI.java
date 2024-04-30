package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.client.LoggableClient;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ServerRMI implements LoggableServer {
    static int PORT = 1234;
    Controller controller = Controller.getInstance();
    int numberOfPlayers = 0;//TODO I can do a method to get it
    HashMap<String, LoggableClient> connections = new HashMap<>();//TODO understand RMI

    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello, World!");
        LoggableServer serverSkeleton = null;//https://www.baeldung.com/java-rmi
        ServerRMI obj = new ServerRMI();

        try{
            serverSkeleton = (LoggableServer) UnicastRemoteObject.exportObject(obj, PORT);
        }catch (RemoteException e){
            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        Registry registry = null;
        try{
            registry = LocateRegistry.createRegistry(PORT);
        }catch(RemoteException e){
            System.out.println("Registry exception: " + e.toString());
            e.printStackTrace();
        }

        try{
            registry.bind("Loggable", serverSkeleton);
        }catch (RemoteException e) {
            System.out.println("Rebind exception: " + e.toString());
            e.printStackTrace();
        }catch (AlreadyBoundException e) {
            System.out.println("Already bound exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("Server ready");
    }

    //Only because we have all client on the same machine
    //A server shouldn't pick the port for a client
    int portAvailable = 41999;

    @Override
    public boolean ping(String nickname, String ip) throws RemoteException, SameNameException, LobbyCompleteException, NotBoundException {
        controller.addPlayer(nickname);
        Registry registryForSingleClient = LocateRegistry.getRegistry(ip, 0);
        LoggableClient stub = (LoggableClient) registryForSingleClient.lookup("Loggable");
        connections.put(nickname, stub);
        refreshUsers();
        return controller.getIsFirst();
    }

    @Override
    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException, NoSuchFieldException {
        controller.initializeLobby(numberOfPlayers);//TODO remove all players before
        //Deletes all other connections that are not in the lobby
        HashMap<String, Color> playersInLobby = null;
        for(String nickname : connections.keySet()){
            if(controller.getPlayer(nickname) == null){
                connections.get(nickname).disconnect(nickname, "Lobby has been fulled with number of parameters chosen by the first player");
                connections.remove(nickname);
            }else{
                connections.get(nickname).stopWaiting(nickname);
            }
        }
        refreshUsers();
    }

    @Override
    public void chooseColor(String nickname, Color color) throws RemoteException, NoSuchFieldException, PinNotAvailableException {
//        if(controller.getUsers().get(nickname).getColor() == color){
//            controller.getUsers().get(nickname).setColor(null);
//        }
        if(controller.setColour(nickname, color)){
            for(LoggableClient connection : connections.values()){
                connection.startGame();
            }
        }
        refreshUsers();
    }

    public void refreshUsers(){
        for (LoggableClient connection : connections.values()){
            connection.refreshUsers(controller.getPlayersAndPins());//Color could be null
        }
    }
}
