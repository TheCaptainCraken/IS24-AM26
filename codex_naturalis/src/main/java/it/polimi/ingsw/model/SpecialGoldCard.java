package it.polimi.ingsw.model;

import java.util.HashMap;

/**
 * This class represents a special gold card (A gold card that gives points
 * based on counting something like the number of resources the player has at
 * that moment).
 */
public class SpecialGoldCard extends GoldCard {

    /**
     * The thing to count.
     */
    private final Countable thingToCount;

    /**
     * Class constructor.
     *
     * @param id           The special gold card's id.
     * @param kingdom      The special gold card's kingdom.
     * @param frontCorners The special gold card's corners.
     * @param points       The special gold card's points.
     * @param requirements The special gold card's requirements.
     * @param thingToCount The special gold card's thing to count.
     */
    public SpecialGoldCard(int id, Kingdom kingdom, HashMap<Corner, Sign> frontCorners, int points,
            HashMap<Sign, Integer> requirements, Countable thingToCount) {
        super(id, kingdom, frontCorners, points, requirements);
        this.thingToCount = thingToCount;
    }

    /**
     * Returns the special gold card's thing to count.
     *
     * @return The special gold card's thing to count.
     */
    public Countable getThingToCount() {
        return thingToCount;
    }
}
