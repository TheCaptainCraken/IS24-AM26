package it.polimi.ingsw.controller.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.network.client.ClientRMI;
import it.polimi.ingsw.network.client.NetworkInterface;

import java.awt.*;
import java.util.HashMap;

public class Controller {
    private String nickname;
    private NetworkInterface connection;

    public void createInstanceOfConnection(String typeOfConnection){
        if(typeOfConnection=="RMI"){
            connection = new ClientRMI();
        }else if(typeOfConnection=="Socket"){
            //onnection = new ClientSocket();
        }
    }

    public void askNumberOfPlayer() {
        //metodo view stampa messaggio
        //input --> controller --> server(rmi o socket)
    }
    public void waitLobby() {
    }

    public void stopWaiting() {
    }

    public void error(String issue) {
    }

    public void setNickname(String nickname) {
    }

    public void refreshUsers(HashMap<String, Color> playersAndPins) {
    }

    public String getNickname() {
        return null;
    }

    public void sendInfoOnTable() {
    }

    public void showStartingCard(int startingCardId) {
    }

    public void showObjectiveCards(Integer[] objectiveCardIds) {
    }

    public void showSecretObjectiveCards(Integer[] objectiveCardIds) {
    }

    public void setSecretObjectiveCard(int indexCard) {//It's personal
    }

    public void getHand(String nickname, Integer[] hand) {
    }

    public void getHiddenHand(Kingdom[] hand) {
    }

    public void refreshTurnInfo(String currentPlayer, GameState gameState) {
    }

    public void placeCard(String nickname, int id, Point position, boolean side) {
    }

    public void updateResources(String nickname, HashMap<Sign, Integer> resources) {
    }

    public void updateScore(String nickname, int points) {
    }

    public void drawCard(String nickname, int cardId) {
    }

    public void newCardOnTable(int newCardId, boolean gold, int onTableOrDeck) {
    }

    public void newHeadDeck(Kingdom headDeck, boolean gold, int onTableOrDeck) {
    }

    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
    }

    public void showRanking(HashMap<String, Integer> ranking) {
    }

    public void getIsFirst(String firstPlayer) {
    }
}
