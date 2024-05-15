package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;

import java.util.ArrayList;

public class GiveSecretObjectiveCards extends ServerMessage {
    ArrayList<Integer> choices;

    public GiveSecretObjectiveCards(ArrayList<Integer> choices) {
        this.choices = choices;
    }

    public ArrayList<Integer> getChoices() {
        return choices;
    }

    @Override
    public void callController(Controller controller) {
        Integer[] choicesArray = new Integer[choices.size()];
        for(int i = 0; i < choices.size(); i++) {
            choicesArray[i] = choices.get(i);
        }

        controller.showSecretObjectiveCardsToChoose(choicesArray);
    }
}
