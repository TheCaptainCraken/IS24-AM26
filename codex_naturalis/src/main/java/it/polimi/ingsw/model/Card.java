package it.polimi.ingsw.model;

public abstract class Card {
    private int id;
    private Kingdom kingdom;

    Card(int id, Kingdom kingdom){
        this.id=id;
        this.kingdom=kingdom;
    }

    public int getId() {
        return id;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }
}


