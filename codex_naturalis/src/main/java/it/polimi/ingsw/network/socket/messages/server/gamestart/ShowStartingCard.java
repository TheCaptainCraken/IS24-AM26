package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.view.model.Phase;
/**
 * This class represents a server message that shows the starting card to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the starting card.
 * The `callController` method is used to update the client's view of the starting card.
 *
 * @see ServerMessage
 */
public class ShowStartingCard extends ServerMessage {
    /**
     * The index of the starting card.
     */
    private final int id;
    /**
     * Class constructor that initializes the starting card.
     *
     * @param id The index of the starting card.
     */
    public ShowStartingCard(int id) {
        this.id = id;
    }
    /**
     * This method is used to get the index of the starting card.
     *
     * @return The index of the starting card.
     */
    public int getId() {
        return id;
    }
    /**
     * This method is used to invoke the appropriate method on the provided controller based on the starting card.
     *
     * @param controller The controller(singleton) on which the appropriate method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        Controller.setPhase(Phase.CHOOSE_SIDE_STARTING_CARD);
        controller.updateAndShowStartingCard(id);
    }
}
