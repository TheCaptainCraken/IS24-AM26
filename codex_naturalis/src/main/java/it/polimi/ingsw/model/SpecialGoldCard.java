package it.polimi.ingsw.model;

public class SpecialGoldCard extends GoldCard {
    Countable thingToCount;

    public SpecialGoldCard(int id, Kingdom kingdom, int points) {
        super(id, kingdom, points);
    }
}
