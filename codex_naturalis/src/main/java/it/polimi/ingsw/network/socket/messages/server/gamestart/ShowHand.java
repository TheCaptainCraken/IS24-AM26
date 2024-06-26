package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
/**
 * This class represents a server message that shows the hand to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the hand.
 * The `callController` method is used to update the client's view of the hand.
 *
 * @see ServerMessage
 */
public class ShowHand extends ServerMessage {
    /**
     * The nickname of the player.
     */
    private final String nickname;
    /**
     * The cards in the player's hand.
     */
    private final Integer[] cardsInHand;
    /**
     * Constructor for ShowHand.
     *
     * @param nickname The nickname of the player.
     * @param cardsInHand The cards in the player's hand.
     */
    public ShowHand(String nickname, Integer[] cardsInHand) {
        this.nickname = nickname;
        this.cardsInHand = cardsInHand;
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
     * Gets the cards in the player's hand.
     *
     * @return The cards in the player's hand.
     */
    public Integer[] getCardsInHand() {
        return cardsInHand;
    }
    /**
     * Calls the `updateHand` method on the controller with the cards in the player's hand.
     *
     * @param controller The controller on which the `updateHand` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        //this is the private hand of the player
        controller.updateHand(cardsInHand);
    }
}
