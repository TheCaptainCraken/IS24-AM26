package it.polimi.ingsw.model;


import java.util.Dictionary;
import java.util.Hashtable;

public class StartingCard extends PlayableCard {
    Dictionary<Corner, Sign> cornersBSide= new Hashtable<>();
    //TODO Set da capire bene
    private int[] extraSignResource;//funghi,...

    public StartingCard(int id, Sign cornerTopLeft, Sign cornerTopRight, Sign cornerBotLeft, Sign cornerBotRight, Sign sign, Dictionary<Corner, Sign> cornersBSide, int[] extraSignResource) {
        super(id, cornerTopLeft, cornerTopRight, cornerBotLeft, cornerBotRight, sign);
        this.cornersBSide = cornersBSide;
        this.extraSignResource = extraSignResource;
    }
    //o arraylist Sign in effetti... è uguale
}
