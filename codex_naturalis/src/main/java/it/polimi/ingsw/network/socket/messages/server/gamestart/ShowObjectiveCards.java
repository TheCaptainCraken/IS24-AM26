package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.util.ArrayList;
/**
 * This class represents a server message that shows the objective cards to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the objective cards.
 * The `callController` method is used to update the client's view of the objective cards.
 *
 * @see ServerMessage
 */
public class ShowObjectiveCards extends ServerMessage {
/**
     * The list of objective cards.
     */
    ArrayList<Integer> cards;
    /**
     * Constructor for ShowObjectiveCards.
     *
     * @param cards The list of objective cards.
     */
    public ShowObjectiveCards(ArrayList<Integer> cards) {
        this.cards = cards;
    }
    /**
     * Gets the list of objective cards.
     *
     * @return The list of objective cards.
     */
    public ArrayList<Integer> getCards() {
        return cards;
    }
    /**
     * Calls the `showCommonObjectiveCards` method on the controller with the list of objective cards.
     *
     * @param controller The controller on which the `showCommonObjectiveCards` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        Integer[] common = cards.toArray(new Integer[0]);
        controller.showCommonObjectiveCards(common);
    }
}
