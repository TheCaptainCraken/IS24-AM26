package it.polimi.ingsw.model;

import java.util.HashMap;
import java.lang.Math;

/**
 * This is the entity of player.
 * It should have a unique name, a color for the pin and a counter of points. It has also a hashmap for tracking resources.
 * For the game, it has the rootCard(the starting card) and secretObjective.
 * At the start phase, the game master should set rootCard, SecretObjective and the hand of the player(a fixed array of 3 cards).
 * At the beginning of the game, the game master set rootCard, secretObjective and the hand.
 */
public class Player implements java.io.Serializable{
    private final String name;
    private int points;
    private int objectivePoints;
    private Color color;
    private PlayedCard rootCard;
    private final HashMap<Sign, Integer> resources;
    private ObjectiveCard secretObjective;
    private ResourceCard[] hand;

    /**
     * It is the constructor of Player, it set the name and the color of the pin
     * @param name the name of the player
     */
    public Player(String name) {
        this.name = name;
        this.points = 0;
        this.objectivePoints = 0;
        resources = new HashMap<>();
        for(Sign sign : Sign.values()){
            this.resources.put(sign, 0);
        }
        int i;
        this.hand = new ResourceCard[3];
        for(i = 0; i < hand.length; i++){
            hand[i] = null;
        }
        this.secretObjective = null;
    }

    /**
     *   getter of player's name
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     *  getter of player's points
     *
     * @return the points of the player
     */
    public int getPoints() {
        return points;
    }

    /**
     * getter of the color of the pin
     *
     * @return the color of the pin
     */
    public Color getColor() {
        return color;
    }

    /**
     * getter of root card, which is the starting card of the game
     *
     * @return the root card
     */
    public PlayedCard getRootCard() {
        return rootCard;
    }

    /**
     * Getter of symbol counter. It tracks the resources for gold card and special objects.
     *
     * @return the hashmap of resources
     */
    public HashMap<Sign, Integer> getResources() {
        return resources;
    }

    /**
     * getter of the secret objective.
     *
     * @return the secret objective
     */
    public ObjectiveCard getSecretObjective() {
        return secretObjective;
    }

    /**
     * getter of the hand of the player, a fixed array of three resources cards
     *
     * @return the hand of the player
     */
    public ResourceCard[] getHand() {
        return hand;
    }

    /**
     * setter of the root of the player. The game master should pass the front or back of the card, chosen by the player
     * @param rootCard the front or back of the card, chosen by the player. It's the starting card.
     */
    public void setRootCard(PlayedCard rootCard) {
        this.rootCard = rootCard;
    }

    /**
     * It is the setter of secret objective card, which will randomly be given by the game master
     * @param secretObjective the secret objective, that gives extra points at the ending of the game
     */
    public void setSecretObjective(ObjectiveCard secretObjective) {
        this.secretObjective = secretObjective;
    }

    /**
     * At the beginning of the game, the game master set the hand of each player
     * @param hand an array of free cards
     */
    public void setHand(ResourceCard[] hand){
        this.hand = hand;
    }

    /**
     * this function updates the resources given by a new card put on table
     * @param sign the type of resource to update
     * @param numResources how many resources a card gives
     */
    public void addResource(Sign sign, Integer numResources){
        if(sign != null){
            resources.put(sign, resources.get(sign) + numResources);
        }
    }

    /**
     * this function updates the resources. It subtracts the resources covered by a new card played.
     * @param sign the type of resource to update
     * @param numResources how many resources to delete
     */
    public void removeResources(Sign sign, Integer numResources){
        if(sign != null){
            resources.put(sign, resources.get(sign) - numResources);
        }
    }

    /**
     * This function deletes the card given by input. The game Master should call this method to eliminate a card, played on table, from player's hand.
     * @param card card played
     */
    public void giveCard(ResourceCard card){
        int i;
        for(i = 0; i < 3; i++){
            if(hand[i] == card){
                hand[i] = null;
                break;
            }
        }
    }
    /**
     * This function gives to player a new card. The game Master should call this method to give a card to player's hand. The new card will be put in the only position
     * in the array where there is value == null. There is only one null position at every player's round.
     * @param card the new card drawn
     */
    public void takeCard(ResourceCard card){
        int i;
        for(i = 0; i < 3; i++){
            if(hand[i] == null){
                hand[i] = card;
                break;
            }
        }
    }
    /**
     * this function sums the new points given by a new played card
     * @param new_points the points possibly given by a new card or a secret objective
     */
    public void addPoints(int new_points){
        //the player in the game can earn maximum 29 points, no more, see slack
        points = Math.min(29, this.points + new_points);
    }

    /**
     * slack professor's comment: Grazie per la segnalazione! Ho cambiato la mia risposta 1 di conseguenza: ci si ferma a 29 punti prima di sommare le carte obiettivo.
     * A questo punto si procede con la somma degli obiettivi a fine partita e si segue la regola alla risposta 3 in caso di parità
     *
     * Sums the extra points given by the objectiveCard
     * @param new_points  special points given at the end of the game by ObjectiveCard
     */
    public void addObjectivePoints(int new_points){
            objectivePoints += new_points;
    }

    /**
     * This method returns the objective points of the player.
     * Objective points are special points that are added at the end of the game based on the player's secret objective card.
     *
     * @return the objective points of the player.
     */
    public int getObjectivePoints() {
        return objectivePoints;
    }

    /**
     * This method sets the color of the player's pin.
     *
     * @param colour the color to set for the player's pin.
     */
    public void setColour(Color colour) {
        this.color = colour;
    }
}
