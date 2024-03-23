package it.polimi.ingsw.model;

/**
 * This class represents an objective card.
 */
public class ObjectiveCard extends Card {
    /**
     * The objective card's type.
     */
    private final ObjectiveType type;

    /**
     * The objective card's multiplier.
     */
    private final int multiplier;

    /**
     * Class constructor.
     *
     * @param id         The objective card's id.
     * @param kingdom    The objective card's kingdom.
     * @param type       The objective card's type.
     * @param multiplier The objective card's multiplier.
     */
    public ObjectiveCard(int id, Kingdom kingdom, ObjectiveType type, int multiplier) {
        super(id, kingdom);
        this.type = type;
        this.multiplier = multiplier;
    }

    /**
     * Returns the objective card's type.
     *
     * @return The objective card's type.
     */
    public ObjectiveType getType() {
        return type;
    }

    /**
     * Returns the objective card's multiplier.
     *
     * @return The objective card's multiplier.
     */
    public int getMultiplier() {
        return multiplier;
    }
}
