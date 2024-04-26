package it.polimi.ingsw.server.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerRMI implements Loggable{
    static int PORT = 1234;

    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello, World!");
        Loggable stub = null;
        ServerRMI obj = new ServerRMI();

        try{
            stub = (Loggable) UnicastRemoteObject.exportObject(obj, PORT);
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
            registry.bind("Loggable", stub);
        }catch (RemoteException e) {
            System.out.println("Rebind exception: " + e.toString());
            e.printStackTrace();
        }catch (AlreadyBoundException e) {
            System.out.println("Already bound exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("Server ready");
    }

    @Override
    public boolean ping() throws RemoteException {

        return true;
    }
}
