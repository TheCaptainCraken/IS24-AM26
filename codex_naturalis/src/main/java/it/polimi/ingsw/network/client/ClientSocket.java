package it.polimi.ingsw.network.client;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientSocket extends NetworkClient {
   // String url;
   // final int port;
    Controller controller;

    /*public ClientSocket(String url, int port) {
        this.url = url;
        this.port = port;
    }  li metto qui poichè non siamo ancora sicuri di come gestire la questione IP/port */


    @Override
    public void disconnect(){

    }


    @Override
    public void stopWaiting(String nickname) {

    }

    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {

    }

    @Override
    public void sendInfoOnTable() {

    }

    @Override
    public void showStartingCard(int startingCardId) {

    }


    @Override
    public void showObjectiveCards(Integer[] objectiveCardIds) {

    }

    @Override
    public void showSecretObjectiveCards(Integer[] objectiveCardIds) {

    }

    @Override
    public void showHand(String nickname, Integer[] hand) {

    }

    @Override
    public void showHiddenHand(String nickname, Kingdom[] hand) {

    }

    @Override
    public void refreshTurnInfo(String currentPlayer, GameState gameState) {

    }


    @Override
    public void placeCard(String nickname, int id, Point position, boolean side, HashMap<Sign, Integer> resources, int points) {

    }

    @Override
    public void moveCard(int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) {

    }

    @Override
    public void showEndGame(HashMap<String, Integer> extraPoints, ArrayList<Player> ranking) {

    }
}
