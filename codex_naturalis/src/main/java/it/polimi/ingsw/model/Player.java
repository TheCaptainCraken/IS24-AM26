package it.polimi.ingsw.model;

import java.util.HashMap;

/**
 * This is the entity of player.
 * It should have a unique name, a color for the pin and a counter of points. It has also a hashmap for tracking resources.
 * For the game, it has the rootCard(the starting card) and secretObjective.
 * At the start phase, the game master should set rootCard, SecretObjective and the hand of the player(a fixed array of 3 cards).
 * At the beginning of the game, the game master set rootCard, secretObjective and the hand.
 */
public class Player {
    private final String name;
    private int points;
    private final Color color;
    private PlayedCard rootCard;
    private HashMap<Sign, Integer> resources;
    private ObjectiveCard secretObjective;
    private ResourceCard[] hand;

    /**
     * It is the constructor of Player, it set the  name and the color of the pin
     * @param name
     * @param color
     */
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.points = 0;
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
     */
    public String getName() {
        return name;
    }

    /**
     *  getter of player's points
     */
    public int getPoints() {
        return points;
    }

    /**
     * getter of the color of the pin
     */
    public Color getColor() {
        return color;
    }

    /**
     * getter of root card, which is the starting card of the game
     */
    public PlayedCard getRootCard() {
        return rootCard;
    }

    /**
     * getter of symbol counter. It tracks the resources for gold card and special objects.
     */
    public HashMap<Sign, Integer> getResources() {
        return resources;
    }

    /**
     * getter of the secret objective.
     */
    public ObjectiveCard getSecretObjective() {
        return secretObjective;
    }

    /**
     * getter of the hand of the player, a fixed array of three resources cards
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
        resources.put(sign, resources.get(sign) + numResources);
    }

    /**
     * this function updates the resources. It subtracts the resources covered by a new card played.
     * @param sign the type of resource to update
     * @param numResources how many resources to delete
     */
    public void removeResources(Sign sign, Integer numResources){
        resources.put(sign, resources.get(sign) - numResources);
    }

    /**
     * It updates the hand of the player. It removes the card played on the table changing it with a new card drawn.
     * It a unique method for cardOnTable and pickCard, which now are useless.
     * @param cardToRemove the card played by the player, thus to be removed
     * @param newCard the new card drawn by the player
     */
    public void updateCard(ResourceCard cardToRemove, ResourceCard newCard){
        int i;
        for(i = 0; i < 3; i++){
            if(hand[i] == cardToRemove){
                hand[i] = newCard;
                break;
            }
        }
    }
    /**
     * this function updates the points of a player
     * @param new_points the points possibly given by a new card or a secret objective
     */
    public void updatePoints(int new_points){
        //the player in the game can earn maximum 29 points, no more, see slack
        if(points + new_points <= 29) {
            points += new_points;
        }else{
            points = 29;
        }
    }
}
