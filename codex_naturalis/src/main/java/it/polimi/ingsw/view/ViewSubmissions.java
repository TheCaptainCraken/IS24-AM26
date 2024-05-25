package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;

import java.awt.*;

public class ViewSubmissions {
    private static final ViewSubmissions INSTANCE = new ViewSubmissions();

    Controller controller;

    /**
     * Gets the instance of the ViewSubmissions.
     *
     * @return The instance of the ViewSubmissions.
     */
    public static ViewSubmissions getInstance() {
        return INSTANCE;
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    //Client to Server

    //Input the number of players. The number of players can be 2, 3 or 4. The controller will check the input.
    public void chooseNumberOfPlayers(int numberOfPlayers){
        controller.insertNumberOfPlayers(numberOfPlayers);
    }
    //Input the nickname of the player. The controller will check the input.
    public void chooseNickname(String nickname){
        controller.login(nickname);
    }
    //Input the color of the player. The color can be RED, BLUE, GREEN, YELLOW. The controller will check the input.
    public void chooseColor(Color color){
        controller.chooseColor(color);
    }
    //Input the side of the card. The side can be true or false. The controller will check the input.
    public void chooseStartingCard(boolean isFacingUp){
        controller.chooseSideStartingCard(isFacingUp);
    }
    //Input the index of the card. The index can be 0 or 1. The controller will check the input.
    public void chooseSecretObjectiveCard(int indexCard){
        controller.chooseSecretObjectiveCard(indexCard);
    }
    //Input which card to play and where to play it. The controller will check the input.
    public void placeCard(int indexHand, Point position, boolean isFacingUp) {
        controller.playCard(indexHand, position, isFacingUp);
    }
    //Input where to draw the card. The controller will check the input.
    public void drawCard(boolean gold, int onTableOrDeck){
        controller.drawCard(gold, onTableOrDeck);
    }
}
