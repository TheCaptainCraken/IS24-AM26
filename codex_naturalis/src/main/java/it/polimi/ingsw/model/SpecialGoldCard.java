package it.polimi.ingsw.model;

public class SpecialGoldCard extends GoldCard {
    Countable thingToCount;

    public SpecialGoldCard(int id, Sign cornerTopLeft, Sign cornerTopRight, Sign cornerBotLeft, Sign cornerBotRight, Sign sign, int punti, int[] requirements) {
        super(id, cornerTopLeft, cornerTopRight, cornerBotLeft, cornerBotRight, sign, punti, requirements);
    }
}
