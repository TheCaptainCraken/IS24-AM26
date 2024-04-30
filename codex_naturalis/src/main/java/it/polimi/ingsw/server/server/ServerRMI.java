package it.polimi.ingsw.server.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.FullLobbyException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.server.client.LoggableClient;

import java.rmi.AlreadyBoundException;
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
    public int ping() throws RemoteException, FullLobbyException{
        //TODO controllo lobby piena e
        if(portAvailable == 42000 + controller.getNumberOfPlayers() &&
                controller.getNumberOfPlayers() != 0 &&
                portAvailable != 42003){
            //TODO unico metodo lato controller
            throw new FullLobbyException();
        }else if(controller.lobbyLocked()){
            throw new FullLobbyException();
        }
        portAvailable++;
        //add to
        return (portAvailable != 42000 &&
                numberOfPlayers >0? portAvailable:-portAvailable);
        //To not create an obj with boolean and 0-65525 value
    }

    @Override
    public void insertNumberOfPlayers(int numberOfPlayers) throws RemoteException {
        //TODO create the lobby
        controller.initializeLobby(numberOfPlayers);
        for(Object connection : connections.values()){
            connection.stopWaiting();
        }
    }

    @Override
    public Hashmap<Color, Boolean> login(String nickname) throws SameNameException, RemoteException {

        controller.addPlayer(nickname, null);//TODO change constructor of player
        //TODO add to hashmap or registry

        registryToConnect = LocateRegistry.getRegistry("127.0.0.1", PORT);
        stub = (LoggableServer) registry.lookup("Loggable");
        refreshUsers();
        return controller.getAvailableColors();// buttons that can click
    }

    @Override
    public void chooseColor(String nickname, Color color) throws RemoteException {
        if(controller.isColorAlreadyTaken(color)){
            throw new ColorAlreadyTakenException();
        }
//        if(controller.getUsers().get(nickname).getColor() == color){
//            controller.getUsers().get(nickname).setColor(null);
//        }
        controller.setColor(nickname, color);//TODO add method to player
        controller.chooseColor(color);//TODO change constructor of player
        refreshUsers();
    }

    public void refreshUsers(){
        for (Object connection : connections.values()){
            connection.refreshUsers(controller.getPlayersInLobby());//Color could be null
        }
    }

    @Override
    public void start(){
        //TODO all checks and exceptions
        controller.start();
        for (Object connection : connections.values()){
            connection.startGame();
        }
    }
}
