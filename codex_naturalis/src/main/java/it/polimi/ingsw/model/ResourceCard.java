package it.polimi.ingsw.model;

import java.util.HashMap;

/**
 * This class represents a resource card.
 */
public class ResourceCard extends PlayableCard {
    /**
     * The resource card's points.
     */
    private final int points;

    /**
     * Class constructor.
     *
     * @param id           The resource card's id.
     * @param kingdom      The resource card's kingdom.
     * @param frontCorners The resource card's corners.
     * @param points       The resource card's points.
     */
    public ResourceCard(int id, Kingdom kingdom, HashMap<Corner, Sign> frontCorners, int points) {
        super(id, kingdom, frontCorners);
        this.points = points;
    }

    /**
     * Returns the resource card's points.
     *
     * @return The resource card's points.
     */
    public int getPoints() {
        return points;
    }
}
