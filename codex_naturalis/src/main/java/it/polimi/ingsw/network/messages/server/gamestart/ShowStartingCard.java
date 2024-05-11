package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;

public class ShowStartingCard extends ServerMessage {
    private final int id;

    public ShowStartingCard(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void callController(Controller controller) {
        controller.showStartingCard(id);
    }
}
