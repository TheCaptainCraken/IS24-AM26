package it.polimi.ingsw.server.client;

import it.polimi.ingsw.server.server.Loggable;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI {
    static int PORT = 1234;

    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello, World!");

        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
            Loggable stub = (Loggable) registry.lookup("Loggable");
            Boolean connected = stub.ping();
            System.out.println("Remote method invoke: " + connected);
        } catch (RemoteException e) {
            System.out.println("Client exception: " + e.toString());
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.out.println("Client exception: " + e.toString());
            throw new RuntimeException(e);
        }
    }
}
