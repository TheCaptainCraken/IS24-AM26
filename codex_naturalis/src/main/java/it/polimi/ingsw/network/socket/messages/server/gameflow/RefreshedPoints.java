package it.polimi.ingsw.network.socket.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
/**
 * This class represents a server message that updates the points of a player.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the updated points.
 * The `callController` method is used to update the client's view of the player's points.
 *
 * @see ServerMessage
 */
public class RefreshedPoints extends ServerMessage {
    /**
     * The nickname of the player.
     */
    private final String nickname;

    /**
     * The updated points of the player.
     */
    private final int points;

    /**
     * Constructor for RefreshedPoints.
     *
     * @param nickname The nickname of the player.
     * @param points The updated points of the player.
     */
    public RefreshedPoints(String nickname, int points) {
        this.nickname = nickname;
        this.points = points;
    }

    /**
     * Gets the nickname of the player.
     *
     * @return The nickname of the player.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Gets the updated points of the player.
     *
     * @return The updated points of the player.
     */
    public int getPoints() {
        return points;
    }
    /**
     * Calls the `updateScore` method on the controller with the nickname and updated points.
     *
     * @param controller The controller on which the `updateScore` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        // this is the updated score of the player
        controller.updateScore(nickname, points);
    }
}
