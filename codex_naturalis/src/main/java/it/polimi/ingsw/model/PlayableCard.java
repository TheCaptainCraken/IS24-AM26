package it.polimi.ingsw.model;

import java.util.HashMap;

/**
 * This class represents a playable card.
 */
public abstract class PlayableCard extends Card {
    /**
     * The playable card's corners.
     */
    private HashMap<Corner, Sign> corners = new HashMap<>();

    /**
     * Class constructor.
     *
     * @param id      The playable card's id.
     * @param kingdom The playable card's kingdom.
     * @param corners The playable card's corners.
     */
    PlayableCard(int id, Kingdom kingdom, HashMap<Corner, Sign> corners) {
        super(id, kingdom);
        this.corners = corners;
    }

    /**
     * Returns the playable card's corners.
     *
     * @return The playable card's corners.
     */
    public HashMap<Corner, Sign> getCorners() {
        return corners;
    }
}