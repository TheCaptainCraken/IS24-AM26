package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
/**
 * This class represents a server message that indicates the objective card chosen by the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the chosen objective card.
 * The `callController` method is used to update the client's view of the chosen objective card.
 *
 * @see ServerMessage
 */
public class ObjectiveCardChosen extends ServerMessage {
    /**
     * The id of the chosen objective card.
     */
    private final int objectiveCardId;
    /**
     * Constructor for ObjectiveCardChosen.
     *
     * @param objectiveCardId The id of the chosen objective card.
     */
    public ObjectiveCardChosen(int objectiveCardId) {
        this.objectiveCardId = objectiveCardId;
    }
    /**
     * Gets the id of the chosen objective card.
     *
     * @return The id of the chosen objective card.
     */
    public int getObjectiveCardId() {
        return objectiveCardId;
    }
    /**
     * Calls the `updateAndShowSecretObjectiveCard` method on the controller with the id of the chosen objective card.
     *
     * @param controller The controller on which the `updateAndShowSecretObjectiveCard` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        controller.updateAndShowSecretObjectiveCard(objectiveCardId);
    }
}
