package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ShowObjectiveCards extends ServerMessage {
    ArrayList<Integer> cards;

    public ShowObjectiveCards(ArrayList<Integer> cards) {
        this.cards = cards;
    }

    public ArrayList<Integer> getCards() {
        return cards;
    }

    @Override
    public void callController(Controller controller) {
        Integer[] common = cards.toArray(new Integer[0]); //guarda PlayersAndPins
        controller.showObjectiveCards(common);
    }
}
