package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.network.messages.server.ServerMessage;

public class ShowTable extends ServerMessage {
    private final int resourceCard_0;
    private final int resourceCard_1;
    private final int goldCard_0;
    private final int goldCard_1;

    private final Kingdom resourceDeck;
    private final Kingdom goldDeck;

    public ShowTable(int resourceCard_0, int resourceCard_1, int goldCard_0, int goldCard_1, Kingdom resourceDeck, Kingdom goldDeck) {
        this.resourceCard_0 = resourceCard_0;
        this.resourceCard_1 = resourceCard_1;
        this.goldCard_0 = goldCard_0;
        this.goldCard_1 = goldCard_1;
        this.resourceDeck = resourceDeck;
        this.goldDeck = goldDeck;
    }

    public int getResourceCard_0() {
        return resourceCard_0;
    }

    public int getResourceCard_1() {
        return resourceCard_1;
    }

    public int getGoldCard_0() {
        return goldCard_0;
    }

    public int getGoldCard_1() {
        return goldCard_1;
    }

    public Kingdom getResourceDeck() {
        return resourceDeck;
    }

    public Kingdom getGoldDeck() {
        return goldDeck;
    }

    @Override
    public void callController(Controller controller) {
        controller.sendInfoOnTable(); //metodo ha probabilmente una signature incompleta
    }
}
