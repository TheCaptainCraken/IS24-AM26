package it.polimi.ingsw.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/** Class is used to represent the card placed onto the board, it's role is to update the board for every move the player makes
 * *@author Arturo*/
public class PlayedCard {
    private PlayableCard card;
    private HashMap<Corner, PlayedCard> attachmentCorners;
    private boolean flagCountedForObjective;
    private boolean isFacingUp; // true per fronte, false per retro
    private int turnOfPositioning;
    private Point position;

    public PlayedCard(PlayableCard playableCard, HashMap<Corner,PlayedCard> toAttach,boolean side, int turnNumber, Point position) {
        //usual constructor operations
        this.card = playableCard;
        attachmentCorners = new HashMap<>(); // these are the corners of the new PlayedCard that will be attached to the cards present in toAttach
        this.flagCountedForObjective = false;
        this.isFacingUp = side;
        this.turnOfPositioning = turnNumber;
        this.position = position;

        /*qui completiamo attachmentcorners con tutte le carte a cui la nuova PlayedCard è collegata
        il ciclo itera per tutte le carte da considerare,collega prima la vecchia carta a quella nuova, poi fà il contrario
        tenendo in conto quale angolo stiamo considerando, si noti che il controllo sulla disponibilità dell'angolo è stata fatta prima dal GameMaster*/

        if(!toAttach.isEmpty()){
            for(Corner c: Corner.values()){
                attachCard(c,toAttach.get(c));
                switch (c) {
                    case TOP_LEFT:
                        toAttach.get(c).attachCard(Corner.BOTTOM_RIGHT,this);
                        break;
                    case TOP_RIGHT:
                        toAttach.get(c).attachCard(Corner.BOTTOM_LEFT,this);
                        break;
                    case BOTTOM_LEFT:
                        toAttach.get(c).attachCard(Corner.TOP_RIGHT,this);
                        break;
                    case BOTTOM_RIGHT:
                        toAttach.get(c).attachCard(Corner.TOP_LEFT,this);
                        break;
                }
            }
        }
    }

    /**returns true if card has already been used for calculating Objective scores*/
    public boolean isFlagCountedForObjective() {
        return flagCountedForObjective;
    }
    /**returns true if card was played on its front*/
    public boolean isFacingUp() {
        return isFacingUp;
    }

    /**returns the PlayableCard related to the PlayedCard*/
    public PlayableCard getCard() {
        return card;
    }
    /**returns the turn in which the card was played*/
    public int getTurnOfPositioning() {
        return turnOfPositioning;
    }

    /** updates the status of a PlayedCard's corners in case it gets attached to a new card*/
    public void attachCard (Corner corner, PlayedCard playedCard){
        getAttachmentCorners().put(corner, playedCard);

    }

    /**returns the map in which the information on the status of the PlayedCard's corners is stored*/
    public HashMap<Corner, PlayedCard> getAttachmentCorners() {//TODO Capire se vogliamo passare l'oggetto o è usiamo un metodo che ritorna l'id e poi risaliamo alla carta, altrimenti così passiamo un oggetto per valore e si può modificare
        return attachmentCorners;
    }
    /**returns the PlayedCard that has been attached to the corner given to this */
    public PlayedCard getAttached(Corner corner){
        return attachmentCorners.get(corner);
    }
    /**returns the coordinates corresponding to the place,related to the StartingCard, in which the card was played*/
    public Point getPosition() {
        return position;
    }
    /**records the use of the card in calculating Objective scores*/
    public void flagWasCountedForObjective() {
        this.flagCountedForObjective = true;
    }
}
