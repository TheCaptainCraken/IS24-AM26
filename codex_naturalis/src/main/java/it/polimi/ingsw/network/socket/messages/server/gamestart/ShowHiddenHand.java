package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import javafx.util.Pair;

/**
 * This class represents a server message that shows the hidden hand to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the hidden hand.
 * The `callController` method is used to update the client's view of the hidden hand.
 *
 * @see ServerMessage
 */
public class ShowHiddenHand extends ServerMessage {
    /**
     * The nickname of the player.
     */
    private final String nickname;
    /**
     * The hidden hand of the player.
     */
    private final Pair<Kingdom,Boolean>[] hand;
    /**
     * Constructor for ShowHiddenHand.
     *
     * @param nickname The nickname of the player.
     * @param hand The hidden hand of the player.
     */
    public ShowHiddenHand(String nickname, Pair<Kingdom,Boolean>[] hand) {
        this.nickname = nickname;
        this.hand = hand;
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
     * Gets the hidden hand of the player.
     *
     * @return The hidden hand of the player.
     */
    public Pair<Kingdom,Boolean>[] getHand() {
        return hand;
    }
    /**
     * Calls the `updateHiddenHand` method on the controller with the nickname and the hidden hand.
     *
     * @param controller The controller on which the `updateHiddenHand` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        controller.updateHiddenHand(nickname, hand);
    }
}
