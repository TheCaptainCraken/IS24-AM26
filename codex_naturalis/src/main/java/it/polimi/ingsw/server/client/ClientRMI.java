package it.polimi.ingsw.server.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.server.server.LoggableServer;

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


    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello, World!");
    }

    public void connect() throws RemoteException {
        if(stub==null){
            throw new NoConnectionException();
        }
        int port = stub.ping();
        System.out.println("Remote method invoke: " + port);
        if (port==42000) {//This should be changed
            controller.askNumberOfPlayer();
        }else if(port<0){
            port=-port;
            controller.wait();
        } else {
            controller.askNickname();
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

    public void insertNumberOfPlayers(int numberOfPlayers, String ip int port) throws RemoteException {
        ip="127.0.0.1";//TODO get from machine
        stub.insertNumberOfPlayers(numberOfPlayers, ip, port);
    }

    @Override
    public void stopWaiting() throws RemoteException {
        if(controller.getNickname() == null){//TODO create local String final
            controller.askNickname();
        }
    }

    public void sendNickname(String nickname){
        HashMap<Color, Boolean> availableColors = stub.login(nickname);
        controller.setNickname(nickname);
        controller.showAvailableColors(availableColors);
    }

    @Override
    public void refreshUsers(HashMap<String, Color> users) {
        controller.refreshUsers(users);
    }

    public void pickColor(Color color) {
        //TODO call controller ok client
        stub.chooseColor(controller.getNickname(), color);
        //refresh of users arrives to every client
    }

    public void start(){//TODO add to the diagram
        stub.start();
    }

    @Override
    public void startGame() {
        controller.startGame();
    }
}
