package it.polimi.ingsw.model;

import java.util.Dictionary;
import java.util.Hashtable;

public abstract class PlayableCard extends Card{
    private Dictionary<Corner, Sign> corners= new Hashtable<>();//topleft, topright, botleft, botright
    //TODO enumeration or vocabulary as python
    //TODO va qua la def?
    private Sign sign;

    PlayableCard(int id, Sign cornerTopLeft, Sign cornerTopRight, Sign cornerBotLeft, Sign cornerBotRight, Sign sign) {
        super(id);
        for (Direction dir : Direction.values()) {//https://stackoverflow.com/questions/1104975/a-for-loop-to-iterate-over-an-enum-in-java
            // do what you want
        }
        this.corners.put(Corner, cornerTopLeft); //il costruttore va già con gli oggetti fatti, poi se i Sign non sono già pronti non è affar della Card
        this.corners[1] = cornerTopRight;
        this.corners[2] = cornerBotLeft;
        this.corners[3] = cornerBotRight;
        this.sign=sign;//TODO
    }

    public Sign[] getCorners() {
        return corners;
    }

    public Sign getSign() {
        return sign;
    }

}
