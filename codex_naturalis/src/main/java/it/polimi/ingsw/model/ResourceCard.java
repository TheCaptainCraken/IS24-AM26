package it.polimi.ingsw.model;

public class ResourceCard extends PlayableCard {
    private int points;

    public ResourceCard(int id, Sign cornerTopLeft, Sign cornerTopRight, Sign cornerBotLeft, Sign cornerBotRight, Sign sign, int punti) {
        super(id, cornerTopLeft, cornerTopRight, cornerBotLeft, cornerBotRight, sign);
        this.points = punti;
    }

    public int getPoints() {
        return points;
    }
}
