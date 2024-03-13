package it.polimi.ingsw.model;

import java.util.Dictionary;
import java.util.Hashtable;

public class GoldCard extends ResourceCard {
    Dictionary<Kingdom, Sign> requirements= new Hashtable<>();

    public GoldCard(int id, Sign cornerTopLeft, Sign cornerTopRight, Sign cornerBotLeft, Sign cornerBotRight, Sign sign, int punti, int[] requirements) {
        super(id, cornerTopLeft, cornerTopRight, cornerBotLeft, cornerBotRight, sign, punti);
        //this.requirements = requirements;//TODO
    }
}
