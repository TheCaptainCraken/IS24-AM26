package it.polimi.ingsw.model;

import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;

public class PlayedCard {
    private PlayableCard playableCard;
    private Dictionary<Corner, PlayedCard> attachmentCorners;
    private boolean flagCountedForObjective;//o int
    private boolean side;
    private int turnOfPositioning;
    private Point coordinates;//x e y

    public PlayedCard(PlayableCard playableCard, Dictionary<Corner, PlayedCard> toAttach, boolean flagCountedForObjective, boolean side, int turnNumber, Point coordinates) {//
        this.playableCard = playableCard;
        attachmentCorners = new Hashtable<>();
        //corner of toAttach ar reffered to corner of this object
        for (Corner corner : Corner.values()) {//https://stackoverflow.com/questions/1104975/a-for-loop-to-iterate-over-an-enum-in-java
            //this.attachmentCorners.put(corner, toAttach.get(corner)); //TODO
            attachCard(corner, toAttach.get(corner));
            switch(corner){
                case Corner.TOP_LEFT:{
                    //toAttach.get(Corner.BOTTOM_RIGHT).getAttachmentCorners().put(Corner.BOTTOM_RIGHT, this);//sarebbe sbagliata
                    toAttach.get(corner).attachCard(Corner.BOTTOM_RIGHT, this);
                }
                case Corner.TOP_RIGHT:{
                    toAttach.get(corner).attachCard(Corner.BOTTOM_LEFT, this);
                }
                case Corner.BOTTOM_LEFT:{
                    toAttach.get(corner).attachCard(Corner.TOP_RIGHT, this);
                }
                case Corner.BOTTOM_RIGHT:{
                    toAttach.get(corner).attachCard(Corner.TOP_LEFT, this);
                }
            }
        }
        this.flagCountedForObjective = false;
        this.side = side;
        this.turnOfPositioning = turnNumber;
        this.coordinates = coordinates;
    }

    public void attachCard(Corner corner, PlayedCard playedCard){
        getAttachmentCorners().put(corner, this);
    }

    public Dictionary<Corner, PlayedCard> getAttachmentCorners() {
        return attachmentCorners;
    }

    public Point getCoordinates() {
        return coordinates;
    }
}
