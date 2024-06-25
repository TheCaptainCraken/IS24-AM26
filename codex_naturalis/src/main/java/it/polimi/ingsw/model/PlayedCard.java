package it.polimi.ingsw.model;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;


/** The class is used to represent the card placed onto the board, it's role is to keep track of the moves that a player makes
 * during the match
 * *@author Arturo*/
public class PlayedCard implements Serializable {
    /**It's the PlayableCard that this instance of PlayedCard references
     * */
    private final PlayableCard card;

    /** Keeps track of the cards that are attached to the PlayedCard instance
     * */
    private final HashMap<Corner, PlayedCard> attachmentCorners;

    /** Is True if the card has already been used for calculating Objective Scores
     * */
    private boolean flagCountedForObjective;

    /** Identifies the side on which the card has been played, true if it's on its front
     * */
    private final boolean isFacingUp;

    /**
     * Records in which turn the card has been played
     * */
    private final int turnOfPositioning;
    /**
     *  Identifies the position in which the card is on the board
     * */
    private final Point position;

    /**Constructor for the PlayedCard class
     *
     * @param cardsToAttach is the map which contains the cards that will have to be attached to our new PlayedCard
     * @param playableCard is the PlayableCard that the PlayedCard will reference
     * @param position is the position in which the card has been played
     * @param side is true if the card has been played on its front
     * @param turnNumber is the turn in which the card has been played
     * */
    public PlayedCard(PlayableCard playableCard, HashMap<Corner, PlayedCard> cardsToAttach, boolean side, int turnNumber, Point position) {
        this.card = playableCard;
        attachmentCorners = new HashMap<>();
        this.flagCountedForObjective = false;
        this.isFacingUp = side;
        this.turnOfPositioning = turnNumber;
        this.position = position;

        // This ForEach cycle iterates on the Keys of the cardsToAttach given in order to fill out attachmentCorners with the PlayedCards
        // that are attached to this instance of PlayedCard,the newest card is attached first.
        //
        for(Corner c : cardsToAttach.keySet()){
            this.attachCard(c, cardsToAttach.get(c));
            if(cardsToAttach.get(c) != null) {
                //if the card is not null, we attach the card to the corner of the card that is already attached to this
                switch (c) {
                    case TOP_LEFT:
                        cardsToAttach.get(c).attachCard(Corner.BOTTOM_RIGHT, this);
                        break;
                    case TOP_RIGHT:
                        cardsToAttach.get(c).attachCard(Corner.BOTTOM_LEFT, this);
                        break;
                    case BOTTOM_LEFT:
                        cardsToAttach.get(c).attachCard(Corner.TOP_RIGHT, this);
                        break;
                    case BOTTOM_RIGHT:
                        cardsToAttach.get(c).attachCard(Corner.TOP_LEFT, this);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * @return true if card has already been used for calculating Objective scores
     * */
    public boolean isFlagCountedForObjective() {
        return flagCountedForObjective;
    }

    /**
     * @return true if card was played on its front
     * */
    public boolean isFacingUp() {
        return isFacingUp;
    }

    /**
     * @return the PlayableCard related to the PlayedCard
     * */
    public PlayableCard getCard() {
        return card;
    }

    /**
     * @return the turn in which the card was played
     * */
    public int getTurnOfPositioning() {
        return turnOfPositioning;
    }

    /** updates the status of a PlayedCard's corners in case it gets attached to a new card
     * */
    public void attachCard (Corner corner, PlayedCard playedCard){
        this.attachmentCorners.put(corner, playedCard);
    }

    /**
     * @return the map in which the information on the status of the PlayedCard's corners is stored
     * */
    public HashMap<Corner, PlayedCard> getAttachmentCorners() {
        HashMap<Corner, PlayedCard> attachmentCornersCopy = new HashMap<>();
        for(Corner corner : Corner.values()){
            attachmentCornersCopy.put(corner, attachmentCorners.get(corner));
        }
        return attachmentCornersCopy;
    }
    /**
     * @return the PlayedCard that has been attached to the corner given to this
     * */
    public PlayedCard getAttached(Corner corner){
        return attachmentCorners.get(corner);
    }

    /**
     * @return the coordinates corresponding to the place,related to the StartingCard, in which the card was played
     * */
    public Point getPosition() {
        return new Point(position.x, position.y);
    }

    /**
     * records the use of the card in calculating Objective scores by setting the parameter to true
     * */
    public void flagWasCountedForObjective() {
        this.flagCountedForObjective = true;
    }
}
