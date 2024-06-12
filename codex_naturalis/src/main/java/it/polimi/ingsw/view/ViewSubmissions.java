package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;

import java.awt.*;
/**
 * This class is responsible for handling the submissions from the view to the controller.
 * It is a singleton class, meaning there is only one instance of this class throughout the application.
 * The instance can be accessed using the getInstance() method. The View(gui, tui) should control the input.
 */
public class ViewSubmissions {
    /**
     * The instance of the ViewSubmissions.
     */
    private static final ViewSubmissions INSTANCE = new ViewSubmissions();
    /**
     * The controller that handles the submissions.
     */
    Controller controller;
    /**
     * Gets the instance of the ViewSubmissions.
     *
     * @return The instance of the ViewSubmissions.
     */
    public static ViewSubmissions getInstance() {
        return INSTANCE;
    }
    /**
     * Sets the controller that handles the submissions.
     *
     * @param controller The controller that handles the submissions.
     */
    public void setController(Controller controller){
        this.controller = controller;
    }
    /**
     * This method is responsible for sending the chosen number of players to the controller.
     * The controller will then check the input.
     *
     * @param numberOfPlayers The chosen number of players.
     */
    public void chooseNumberOfPlayers(int numberOfPlayers){
        controller.insertNumberOfPlayers(numberOfPlayers);
    }
    /**
     * This method is responsible for sending the chosen nickname to the controller.
     *
     * @param nickname The chosen nickname.
     */
    public void chooseNickname(String nickname){
        controller.login(nickname);
    }
    /**
     * This method is responsible for sending the chosen color to the controller.
     * The controller will then check the input and ensure the color is unique.
     *
     * @param color The chosen color. The color can be RED, BLUE, GREEN, YELLOW.
     */
    public void chooseColor(Color color){
        controller.chooseColor(color);
    }
    /**
     * This method is responsible for sending the chosen side of the starting card to the controller.
     *
     * @param isFacingUp The chosen side of the starting card. The side can be true or false.
     */
    public void chooseStartingCard(boolean isFacingUp){
        controller.chooseSideStartingCard(isFacingUp);
    }
    /**
     * This method is responsible for sending the chosen secret objective card to the controller.
     * The controller will then check the input.
     *
     * @param indexCard The index of the chosen secret objective card.
     */
    public void chooseSecretObjectiveCard(int indexCard){
        controller.chooseSecretObjectiveCard(indexCard);
    }
    /**
     * This method is responsible for sending the chosen card to the controller.
     * @param indexHand The index of the card in the player's hand.
     * @param position The position on the table where the card will be placed.
     * @param isFacingUp The side of the card.
     */
    public void placeCard(int indexHand, Point position, boolean isFacingUp) {
        controller.playCard(indexHand, position, isFacingUp);
    }
    /**
     * This method is responsible for sending the chosen card to the controller.
     * @param gold A boolean indicating whether the card is gold.
     * @param onTableOrDeck An integer indicating whether the card is drawn from the table or the deck.
     */
    public void drawCard(boolean gold, int onTableOrDeck){
        controller.drawCard(gold, onTableOrDeck);
    }
    /**
     * Retrieves the nickname of the client player.
     *
     * This method is used to get the nickname of the player from the controller.
     *
     * @return The nickname of the current player as a String.
     */
    public String getNickname(){
        return controller.getNickname();
    }

    /**
     * Send a message from the player to the others
     *
     * @param message the message to send
     */
    public void sendChatMessage(String message){
        controller.sendChatMessage(message);
    }
}
