package it.polimi.ingsw.network.socket.messages.server.endgame;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.util.ArrayList;
/**
 * This class represents a server message that shows the ranking of players to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the ranking.
 * The `callController` method is used to update the client's view of the ranking.
 *
 * @see ServerMessage
 */
public class ShowRanking extends ServerMessage {
    /**
     * The ranking of players.
     */
    private final ArrayList<Player> ranking;

    /**
     * Constructor for ShowRanking.
     *
     * @param ranking The ranking of players.
     */
    public ShowRanking(ArrayList<Player> ranking) {
        this.ranking = ranking;
    }

    /**
     * Gets the ranking of players.
     *
     * @return The ranking of players.
     */
    public ArrayList<Player> getRanking() {
        return ranking;
    }
    /**
     * Calls the `showRanking` method on the controller with the ranking.
     *
     * @param controller The controller on which the `showRanking` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        controller.showRanking(ranking);
    }
}
