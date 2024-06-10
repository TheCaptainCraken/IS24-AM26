package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.view.Phase;

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
        Controller.setPhase(Phase.CHOOSE_SIDE_STARTING_CARD);
        controller.updateAndShowStartingCard(id);
    }
}
