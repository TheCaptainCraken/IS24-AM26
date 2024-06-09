package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
/**
 * This class represents a server message that shows the table state to the client.
 * It is called when the game is starting, and the table state is updated.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the table state.
 * The `callController` method is used to update the client's view of the table state.
 *
 * @see ServerMessage
 */
public class ShowTable extends ServerMessage {
    /**
     * The index of the first resource card on the table.
     */
    private final int resourceCard_0;
    /**
     * The index of the second resource card on the table.
     */
    private final int resourceCard_1;
    /**
     * The index of the first gold card on the table.
     */
    private final int goldCard_0;
    /**
     * The index of the second gold card on the table.
     */
    private final int goldCard_1;
    /**
     * The resource deck.
     */
    private final Kingdom resourceDeck;
    /**
     * The gold deck.
     */
    private final Kingdom goldDeck;
    /**
     * Class constructor that initializes the table state.
     *
     * @param resourceCard_0 The index of the first resource card on the table.
     * @param resourceCard_1 The index of the second resource card on the table.
     * @param goldCard_0 The index of the first gold card on the table.
     * @param goldCard_1 The index of the second gold card on the table.
     * @param resourceDeck The resource deck.
     * @param goldDeck The gold deck.
     */
    public ShowTable(int resourceCard_0, int resourceCard_1, int goldCard_0, int goldCard_1, Kingdom resourceDeck, Kingdom goldDeck) {
        this.resourceCard_0 = resourceCard_0;
        this.resourceCard_1 = resourceCard_1;
        this.goldCard_0 = goldCard_0;
        this.goldCard_1 = goldCard_1;
        this.resourceDeck = resourceDeck;
        this.goldDeck = goldDeck;
    }
    /**
     * This method is used to get the index of the first resource card on the table.
     *
     * @return The index of the first resource card on the table.
     */
    public int getResourceCard_0() {
        return resourceCard_0;
    }
    /**
     * This method is used to get the index of the second resource card on the table.
     *
     * @return The index of the second resource card on the table.
     */
    public int getResourceCard_1() {
        return resourceCard_1;
    }
    /**
     * This method is used to get the index of the first gold card on the table.
     *
     * @return The index of the first gold card on the table.
     */
    public int getGoldCard_0() {
        return goldCard_0;
    }
    /**
     * This method is used to get the index of the second gold card on the table.
     *
     * @return The index of the second gold card on the table.
     */
    public int getGoldCard_1() {
        return goldCard_1;
    }
    /**
     * This method is used to get the resource deck.
     *
     * @return The resource deck.
     */
    public Kingdom getResourceDeck() {
        return resourceDeck;
    }
    /**
     * This method is used to get the gold deck.
     *
     * @return The gold deck.
     */
    public Kingdom getGoldDeck() {
        return goldDeck;
    }
    /**
     * This method is used to invoke the `cardsOnTable` method on the provided controller.
     *
     * It is called when the `ShowTable` message is received by the client.
     * The method is overridden from the `ServerMessage` class.
     *
     * @param controller The controller on which the `cardsOnTable` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        Integer[] resourceCards = new Integer[2];
        Integer[] goldCards = new Integer[2];
        resourceCards[0] = resourceCard_0;
        resourceCards[1] = resourceCard_1;
        goldCards[0] = goldCard_0;
        goldCards[1] = goldCard_1;

        // Update the client's view of the table state.
        controller.cardsOnTable(resourceCards, goldCards, resourceDeck, goldDeck);
    }
}
