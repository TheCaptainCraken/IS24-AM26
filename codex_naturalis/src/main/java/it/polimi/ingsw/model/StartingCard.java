package it.polimi.ingsw.model;


import java.util.Dictionary;
import java.util.Hashtable;

public class StartingCard extends PlayableCard {
    Dictionary<Corner, Sign> cornersBSide= new Hashtable<>();
    //TODO Set da capire bene
    private int[] extraSignResource;//funghi,...

    public StartingCard(int id, Kingdom kingdom) {
        super(id, kingdom);
    }

    //o arraylist Sign in effetti... è uguale
}
