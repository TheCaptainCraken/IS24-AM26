package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.view.model.Phase;

import java.util.ArrayList;
/**
 * This class represents a server message that gives the secret objective cards to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the secret objective cards.
 * The `callController` method is used to update the client's view of the secret objective cards.
 *
 * @see ServerMessage
 */
public class GiveSecretObjectiveCards extends ServerMessage {
    /**
     * The list of secret objective cards.
     */
    ArrayList<Integer> choices;
    /**
     * Constructor for GiveSecretObjectiveCards.
     *
     * @param choices The list of secret objective cards.
     */
    public GiveSecretObjectiveCards(ArrayList<Integer> choices) {
        this.choices = choices;
    }
    /**
     * Gets the list of secret objective cards.
     *
     * @return The list of secret objective cards.
     */
    public ArrayList<Integer> getChoices() {
        return choices;
    }
    /**
     * Calls the `showSecretObjectiveCardsToChoose` method on the controller with the list of secret objective cards.
     *
     * @param controller The controller on which the `showSecretObjectiveCardsToChoose` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        Integer[] choicesArray = new Integer[choices.size()];
        for(int i = 0; i < choices.size(); i++) {
            choicesArray[i] = choices.get(i);
        }
        Controller.setPhase(Phase.CHOOSE_SECRET_OBJECTIVE_CARD);
        controller.showSecretObjectiveCardsToChoose(choicesArray);
    }
}
