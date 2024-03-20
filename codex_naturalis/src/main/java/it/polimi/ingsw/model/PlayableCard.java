package it.polimi.ingsw.model;


import java.util.Dictionary;
import java.util.Hashtable;

public abstract class PlayableCard extends Card{
    private Dictionary<Corner, Sign> corners= new Hashtable<>();//topleft, topright, botleft, botright
    //TODO enumeration or vocabulary as python
    //TODO va qua la def?
    private Sign sign;

    PlayableCard(int id, Kingdom kingdom ) {
        super(id,kingdom);
    }

    /*public Sign[] getCorners() {
        return corners;
    }*/

    /*public Sign getSign() {
        return sign;
    }*/
}