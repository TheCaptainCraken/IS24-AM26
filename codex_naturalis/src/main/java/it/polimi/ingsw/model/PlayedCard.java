package it.polimi.ingsw.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**@author Arturo
 * class used to represent the card placed onto the board, it's role is to update the board for every move the player makes*/
public class PlayedCard {
    private PlayableCard card;
    private HashMap<Corner, PlayedCard> attachmentCorners;
    private boolean flagCountedForObjective;
    private boolean isFacingUp; // true per fronte, false per retro
    private int turnOfPositioning;
    private Point position;//x e y, alla fine Point lo usiamo oppure no? (Arturo)

    public PlayedCard(PlayableCard playableCard, ArrayList<DifferentPair<Corner,PlayedCard>> cardsToBeAttached,boolean side, int turnNumber, Point position) {
        //usual constructor operations
        this.card = playableCard;

        attachmentCorners = new HashMap<>(); // these are the corners of the new PlayedCard that will be attached to the cards present in toAttach
        for(Corner corner : Corner.values()){
            attachmentCorners.put(corner,null);
        }
        this.flagCountedForObjective = false;
        this.isFacingUp = side;
        this.turnOfPositioning = turnNumber;
        this.position = position;

        /*qui completiamo attachmentcorners con tutte le carte a cui la nuova PlayedCArd è collegata
        il ciclo itera per tutte le carte da considerare,collega prima la nuova carta a quella vecchia, poi fà il contrario
        tenendo in conto quale angolo stiamo considerando, si noti che il controllo sulla disponibilità dell'angolo è fatta dal GameMaster*/

        for(DifferentPair <Corner,PlayedCard> toAttach : cardsToBeAttached){

            toAttach.getSecond().attachCard(toAttach.getFirst(),this);
            switch (toAttach.getFirst()) {
                case TOP_LEFT:
                    this.attachCard(Corner.BOTTOM_RIGHT,toAttach.getSecond());
                    break;
                case TOP_RIGHT:
                    this.attachCard(Corner.BOTTOM_LEFT,toAttach.getSecond());
                    break;
                case BOTTOM_LEFT:
                    this.attachCard(Corner.TOP_RIGHT,toAttach.getSecond());
                    break;
                case BOTTOM_RIGHT:
                    this.attachCard(Corner.TOP_LEFT,toAttach.getSecond());
                    break;
            }
        }
    }

    /**returns true if card has already been used for calculating Objective scores*/
    public boolean isFlagCountedForObjective() {
        return flagCountedForObjective;
    }
    /**returns true if card was played on it's front*/
    public boolean isFacingUp() {
        return isFacingUp;
    }

    public PlayableCard getCard() {
        return card;
    }
    /**returns the turn in which the card was played*/
    public int getTurnOfPositioning() {
        return turnOfPositioning;
    }

    /** updates the status of a PlayedCard's corners in case it gets attached to a new card*/
    public void attachCard(Corner corner, PlayedCard playedCard){
        getAttachmentCorners().replace(corner, this);
        // replace poichè usiamo una hashmap, con voci per ogni angolo già create
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
