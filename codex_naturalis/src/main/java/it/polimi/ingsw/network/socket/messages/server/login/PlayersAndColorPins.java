package it.polimi.ingsw.network.socket.messages.server.login;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.util.HashMap;
/**
 * This class represents a server message that contains a map of players and their associated color pins.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the received map.
 * The `callController` method is used to refresh the users' information in the controller with the received map.
 *
 * @see ServerMessage
 */
public class PlayersAndColorPins extends ServerMessage {
    /**
     * A map of player names to their associated color pins.
     */
    private final HashMap<String, Color> map;
    /**
     * Constructor for PlayersAndColorPins.
     *
     * @param map A map of player names to their associated color pins.
     */
    public PlayersAndColorPins(HashMap<String, Color> map) {
        this.map = map;
    }
    /**
     * Gets the map of players and their associated color pins.
     *
     * @return The map of players and their associated color pins.
     */
    public HashMap<String, Color> getMap() {
        return map;
    }
    /**
     * Calls the `refreshUsers` method on the controller with the map of players and their associated color pins.
     *
     * @param controller The controller on which the `refreshUsers` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        HashMap<String, Color> newMap = new HashMap<>();
        for (String p : map.keySet()) {
            newMap.put(p, map.get(p));
        }
        // Refresh the users' information in the controller with the received map.
        controller.refreshUsers(newMap);
    }
}
