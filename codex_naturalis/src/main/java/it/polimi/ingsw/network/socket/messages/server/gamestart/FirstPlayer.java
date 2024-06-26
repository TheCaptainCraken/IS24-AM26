package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.view.model.Phase;

/**
 * This class represents a server message that shows the first player to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the first player.
 * The `callController` method is used to update the client's view of the first player.
 *
 * @see ServerMessage
 */
public class FirstPlayer extends ServerMessage {
    /**
     * The nickname of the first player.
     */
    private final String nickname;
    /**
     * Constructor for FirstPlayer.
     *
     * @param nickname The nickname of the first player.
     */
    public FirstPlayer(String nickname) {
        this.nickname = nickname;
    }
    /**
     * Gets the nickname of the first player.
     *
     * @return The nickname of the first player.
     */
    public String getNickname() {
        return nickname;
    }
    /**
     * Calls the `showIsFirst` method on the controller with the nickname of the first player.
     *
     * @param controller The controller on which the `showIsFirst` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        Controller.setPhase(Phase.GAME_FLOW);
        controller.showIsFirst(nickname);
    }
}
