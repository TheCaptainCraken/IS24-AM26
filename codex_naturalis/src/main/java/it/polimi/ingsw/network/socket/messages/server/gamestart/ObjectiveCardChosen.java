package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

public class ObjectiveCardChosen extends ServerMessage {
    private final int objectiveCardId;

    public ObjectiveCardChosen(int objectiveCardId) {
        this.objectiveCardId = objectiveCardId;
    }

    public int getObjectiveCardId() {
        return objectiveCardId;
    }

    @Override
    public void callController(Controller controller) {
        controller.updateAndShowSecretObjectiveCard(objectiveCardId);
    }
}
