package it.polimi.ingsw.model;

import java.util.HashMap;

/**
 * This class represents a gold card.
 */
public class GoldCard extends ResourceCard {
    /**
     * The gold card's requirements.
     */
    private HashMap<Sign, Integer> requirements = new HashMap<>();

    /**
     * Class constructor.
     *
     * @param id           The gold card's id.
     * @param kingdom      The gold card's kingdom.
     * @param frontCorners The gold card's corners.
     * @param points       The gold card's points.
     * @param requirements The gold card's requirements.
     */
    public GoldCard(int id, Kingdom kingdom, HashMap<Corner, Sign> frontCorners, int points,
            HashMap<Sign, Integer> requirements) {
        super(id, kingdom, frontCorners, points);
        this.requirements = requirements;
    }

    /**
     * Returns the gold card's requirements.
     *
     * @return The gold card's requirements.
     */
    public HashMap<Sign, Integer> getRequirements() {
        return requirements;
    }
}