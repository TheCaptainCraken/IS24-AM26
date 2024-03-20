package it.polimi.ingsw.model;

import java.util.Dictionary;
import java.util.Hashtable;

public class GoldCard extends ResourceCard {
    Dictionary<Kingdom, Sign> requirements= new Hashtable<>();

    public GoldCard(int id, Kingdom kingdom,int points) {
        super(id,kingdom,points);
        //this.requirements = requirements;//TODO
    }
}