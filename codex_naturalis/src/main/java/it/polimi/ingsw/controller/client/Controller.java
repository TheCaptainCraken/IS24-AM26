package it.polimi.ingsw.controller.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.network.client.ClientRMI;
import it.polimi.ingsw.network.client.NetworkInterface;
import it.polimi.ingsw.view.Tui;

import java.awt.*;
import java.util.HashMap;

public class Controller {
    private String nickname;
    private NetworkInterface connection;
    private Tui view;

    public void setView(String typeOfView) {
        if(typeOfView.equals("TUI")){
            view = new Tui();
        }else if(typeOfView.equals("GUI")){
            //TODO
            //view = new GUI();
        }
    }

    public void createInstanceOfConnection(String typeOfConnection){
        if(typeOfConnection.equals("RMI")){
            connection = new ClientRMI();
        }else if(typeOfConnection.equals("Socket")){
            //TODO
            //onnection = new ClientSocket();
        }
    }

    public void askNumberOfPlayer() {
        view.askNumberOfPlayer();
    }

    public void waitLobby() {
        view.waitLobby();
    }

    public void stopWaiting() {
        view.stopWaiting();
    }


    public void setNickname(String nickname) {

    }

    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        view.refreshUsers(playersAndPins);
    }

    public String getNickname() {
        return null;
    }

    public void sendInfoOnTable() {
    }

    public void showStartingCard(int startingCardId) {
        view.showStartingCard(startingCardId);
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

    public void disconnect() {
        view.disconnect();
    }
}
