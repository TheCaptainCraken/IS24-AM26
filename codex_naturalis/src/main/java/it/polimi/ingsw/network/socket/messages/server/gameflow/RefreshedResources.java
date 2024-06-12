package it.polimi.ingsw.network.socket.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.util.HashMap;

/**
 * This class represents a server message that updates the resources of a player.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the updated resources.
 * The `callController` method is used to update the client's view of the player's resources.
 *
 * @see ServerMessage
 */
public class RefreshedResources extends ServerMessage {
    /**
     * The nickname of the player.
     */
    private final String nickname;
    /**
     * The updated resources of the player.
     */
    private final HashMap<Sign, Integer> resources;
    /**
     * Constructor for RefreshedResources.
     *
     * @param nickname The nickname of the player.
     * @param resources The updated resources of the player.
     */
    public RefreshedResources(String nickname, HashMap<Sign, Integer> resources) {
        this.nickname = nickname;
        this.resources = resources;
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
     * Gets the updated resources of the player.
     *
     * @return The updated resources of the player.
     */
    public HashMap<Sign, Integer> getResources() {
        return resources;
    }

    /**
     * Calls the `updateResources` method on the controller with the nickname and updated resources.
     *
     * @param controller The controller on which the `updateResources` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        controller.updateResources(nickname, resources);
    }
}
