package it.polimi.ingsw.model;


/**
 * This class represents a generic card.
 */
public abstract class Card implements java.io.Serializable {
    /**
     * The card's id.
     */
    private final int id;

    /**
     * The card's kingdom.
     */
    private final Kingdom kingdom;

    /**
     * Class constructor.
     *
     * @param id      The card's id.
     * @param kingdom The card's kingdom.
     */
    Card(int id, Kingdom kingdom) {
        this.id = id;
        this.kingdom = kingdom;
    }

    /**
     * Returns the card's id.
     *
     * @return The card's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the card's kingdom.
     *
     * @return The card's kingdom.
     */
    public Kingdom getKingdom() {
        return kingdom;
    }
}
