package it.polimi.ingsw.network.client;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.client.ClientMessage;
import it.polimi.ingsw.network.messages.client.gameflow.CardToBeDrawn;
import it.polimi.ingsw.network.messages.client.gameflow.CardToBePositioned;
import it.polimi.ingsw.network.messages.client.gamestart.ChosenObjectiveCard;
import it.polimi.ingsw.network.messages.client.gamestart.ChosenStartingCardSide;
import it.polimi.ingsw.network.messages.client.login.ColorChosen;
import it.polimi.ingsw.network.messages.client.login.LoginMessage;
import it.polimi.ingsw.network.messages.client.login.NumberOfPlayersMessage;
import it.polimi.ingsw.network.messages.server.ServerMessage;



import java.awt.*;
import java.io.*;
import java.net.*;

public class ClientSocket extends NetworkClient implements Runnable{
//interface client ha una struttura adatta per utilizzo RMI, fare l'override con socket, è praticamente impossibile e poco sensato
    private final Controller controller = new Controller();
    private Socket socket;
    private InputStream inputStream;
    private final ObjectInputStream objInputStream;
    private OutputStream outputStream;
    private final ObjectOutputStream objOutputStream;
    public ClientSocket(String address, int port) {
        try{
            socket = new Socket(address,port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            objInputStream = new ObjectInputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            objOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //metodi che corrispondo alle azioni che un giocatore fa durante tutta la vita della connessione
    //hanno una corrispondeza 1:1 con metodi usati per RMI
    @Override
    public void login(String nickname) throws SameNameException, LobbyCompleteException{
        //client crea messaggio e lo invia usando objOutputStream
        ClientMessage message = new LoginMessage(nickname);
        sendMessage(message);

    }
    @Override
    public void insertNumberOfPlayers(int numberOfPlayers){
        ClientMessage message = new NumberOfPlayersMessage(numberOfPlayers);
        sendMessage(message);
    }

    @Override
    public void chooseColor(Color color) {
        ClientMessage  message = new ColorChosen(controller.getNickname(),color); //il nickname non è ancora stato fissato in controller vero?
        sendMessage(message);
    }
    @Override
    public void chooseSideStartingCard(boolean side) {
        ClientMessage message = new ChosenStartingCardSide(side);
        sendMessage(message);
    }
    @Override
    public void chooseSecretObjectiveCard(int indexCard) {
        ClientMessage message = new ChosenObjectiveCard(indexCard);
        sendMessage(message);
    }
    @Override
    public void playCard(int indexHand, Point position, boolean side) {
        ClientMessage message = new CardToBePositioned(indexHand,position,side);
        sendMessage(message);
    }

    @Override
    public void drawCard(String nickname, boolean gold, int onTableOrDeck) {
        ClientMessage message = new CardToBeDrawn(nickname,gold,onTableOrDeck); //il nickname lo devo prendere dal controller o no?
        sendMessage(message);
    }

    //metodi che riguardano il gestire aggiornamenti view/ricezione info dal server, dovrebbero essere chiamati dai metodi qui sopra
    //una volta che hanno finito la loro logica, questi metodi sono unici per Socket
    //TODO metodo che ascolta e riceve aggiornamenti da server


    @Override
    public void run() {
        while(true){
            ServerMessage serverMessage = receiveMessage();
            handleResponse(serverMessage);
        }
    }
    public void handleResponse(ServerMessage message){
        message.callController(controller); //metodo che ogni sottoclasse di ServerMessage Overrida per chiamare il metodo del controller
        //soluzione usata per snellire codice,
    }

    //metodi utility per migliorare leggibilità codice
    public ServerMessage receiveMessage(){
        ServerMessage answer;
        try {
            answer = (ServerMessage) objInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return answer;
    }

    public void sendMessage(Message message){
        try{
            objOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
