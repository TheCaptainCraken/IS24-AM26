package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a starting card.
 */
public class StartingCard extends PlayableCard {
    private HashMap<Corner, Sign> backsideCorners = new HashMap<>();
    private ArrayList<Sign> bonusResources = new ArrayList<>();

    /**
     * Class constructor.
     *
     * @param id              The starting card's id.
     * @param kingdom         The starting card's kingdom.
     * @param frontCorners    The starting card's corners.
     * @param backsideCorners The starting card's backside corners.
     * @param bonusResources  The starting card's bonus resources.
     */
    public StartingCard(int id, Kingdom kingdom, HashMap<Corner, Sign> frontCorners,
            HashMap<Corner, Sign> backsideCorners,
            ArrayList<Sign> bonusResources) {
        super(id, kingdom, frontCorners);
        this.backsideCorners = backsideCorners;
        this.bonusResources = bonusResources;
    }

    /**
     * Returns the starting card's backside corners.
     *
     * @return The starting card's backside corners.
     */
    public HashMap<Corner, Sign> getBacksideCorners() {
        return backsideCorners;
    }

    /**
     * Returns the starting card's bonus resources.
     *
     * @return The starting card's bonus resources.
     */
    public ArrayList<Sign> getBonusResources() {
        return bonusResources;
    }
}
