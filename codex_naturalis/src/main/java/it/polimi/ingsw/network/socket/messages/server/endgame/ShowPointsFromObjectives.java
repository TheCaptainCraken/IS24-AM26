package it.polimi.ingsw.network.socket.messages.server.endgame;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.util.HashMap;
/**
 * This class represents a server message that shows the extra points from objectives to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the extra points.
 * The `callController` method is used to update the client's view of the extra points.
 *
 * @see ServerMessage
 */
public class ShowPointsFromObjectives extends ServerMessage {
    private final HashMap<String,Integer> extraPoints;

    /**
     * Constructor for ShowPointsFromObjectives.
     *
     * @param extraPoints The extra points from objectives.
     */
    public ShowPointsFromObjectives(HashMap<String, Integer> extraPoints) {
        this.extraPoints = extraPoints;
    }

    /**
     * Gets the extra points from objectives.
     *
     * @return The extra points from objectives.
     */
    public HashMap<String, Integer> getExtraPoints() {
        return extraPoints;
    }

    /**
     * Calls the `showExtraPoints` method on the controller with the extra points.
     *
     * @param controller The controller on which the `showExtraPoints` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        controller.showExtraPoints(extraPoints);
    }
}
