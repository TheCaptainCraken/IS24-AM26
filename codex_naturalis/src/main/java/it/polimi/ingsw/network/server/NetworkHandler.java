package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.server.Controller;

import java.rmi.RemoteException;
import java.util.HashMap;

public class NetworkHandler {
    private static final NetworkHandler INSTANCE = new NetworkHandler();
    boolean lobbyIsReady = false;

    protected static HashMap<NetworkPlug, HashMap<String, Object>> networkInterfacesAndConnections;

    /**
     * Gets the instance of the controller.
     * @return The instance of the controller.
     */
    public static NetworkHandler getInstance(){
        return INSTANCE;
    }

    public NetworkHandler() {
        networkInterfacesAndConnections = new HashMap<>();
    }

    public void addNetworkPlug(NetworkPlug networkPlug) {
        networkInterfacesAndConnections.put(networkPlug, new HashMap<>());
    }

    public void addConnection(NetworkPlug networkPlug, String nickname, Object connection) {
        networkInterfacesAndConnections.get(networkPlug).put(nickname, connection);
    }

    public HashMap<String, Object> getConnections(NetworkPlug networkPlug) {
        return networkInterfacesAndConnections.get(networkPlug);
    }

    public void finalizingNumberOfPlayersBroadcast(int numberOfPlayers) {
        //TODO do a method that choose from lists of connections in each NetworkPlug`s HashMap
        lobbyIsReady = true;
    }

    public boolean lobbyIsReady() throws RemoteException {
        return lobbyIsReady;
    }

    //TODO broadcast methods
}
