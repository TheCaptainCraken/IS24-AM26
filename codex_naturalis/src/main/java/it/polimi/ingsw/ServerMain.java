package it.polimi.ingsw;

import it.polimi.ingsw.network.RMI.ServerRMI;
import it.polimi.ingsw.network.socket.NetworkServerSocket;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        ServerRMI obj = new ServerRMI();
        NetworkServerSocket networkServerSocket = new NetworkServerSocket(0);

        new Thread(()-> {
            try {
                networkServerSocket.start();
            } catch (IOException e) {
                System.out.println("Cannot start the socket server. Please restart the server.");
            }
        }).start();
    }

}