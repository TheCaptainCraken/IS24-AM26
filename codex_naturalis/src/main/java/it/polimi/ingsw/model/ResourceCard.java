package it.polimi.ingsw.model;

public class ResourceCard extends PlayableCard {
    private int points;

    public ResourceCard(int id, Kingdom kingdom,int punti) {
        super(id,kingdom);
        this.points = punti;
    }

    public int getPoints() {
        return points;
    }
}
