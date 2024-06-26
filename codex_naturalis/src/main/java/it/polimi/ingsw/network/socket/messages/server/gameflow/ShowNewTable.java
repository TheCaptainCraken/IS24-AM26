package it.polimi.ingsw.network.socket.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
/**
 * This class represents a server message that shows a new table to the client.
 *
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the new table.
 * The `callController` method is used to update the client's view of the new table.
 *
 * @see ServerMessage
 */
public class ShowNewTable extends ServerMessage {
    //it is -1, if the client draw from the deck, a real id  if the client draw from the table.
    /**
     * The id of the card.
     */
    private final Integer idCard;
    /**
     * The gold status of the card.
     */
    private final boolean gold;
    /**
     * The top card of the kingdom.
     */
    private final Kingdom topCard;
    /**
     * The status of the card, whether it's on table or deck. If it is -1, we don't update the card on the table.
     */
    private final int onTableOrDeck;
    /**
     * Constructor for ShowNewTable.
     *
     * @param idCard The id of the card.
     * @param gold The gold status of the card.
     * @param onTableOrDeck The status of the card, whether it's on table or deck.
     * @param topCard The top card of the kingdom.
     */
    public ShowNewTable(Integer idCard, boolean gold, int onTableOrDeck, Kingdom topCard) {
        this.idCard = idCard;
        this.gold = gold;
        this.onTableOrDeck = onTableOrDeck;
        this.topCard = topCard;
    }
    /**
     * Gets the id of the card.
     *
     * @return The id of the card.
     */
    public int getIdCard() {
        return idCard;
    }
    /**
     * Gets the gold status of the card.
     *
     * @return The gold status of the card.
     */
    public boolean isGold() {
        return gold;
    }
    /**
     * Gets the status of the card, whether it's on table or deck.
     *
     * @return The status of the card.
     */
    public int getOnTableOrDeck() {
        return onTableOrDeck;
    }
    /**
     * Gets the top card of the kingdom.
     *
     * @return The top card of the kingdom.
     */
    public Kingdom getTopCard() {
        return topCard;
    }
    /**
     * Calls the `updateAndShowCommonTable` method on the controller with the id of the card, gold status, status of the card, and the top card.
     *
     * @param controller The controller on which the `updateAndShowCommonTable` method will be invoked.
     */
    @Override
    public void callController(Controller controller) {
        controller.updateAndShowCommonTable(idCard, gold, onTableOrDeck, topCard);
    }
}
