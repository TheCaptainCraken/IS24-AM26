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
        //TODO metodo che implementa il mostrare scelta objectivecards
    }
}
