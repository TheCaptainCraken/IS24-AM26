package it.polimi.ingsw.model;

import java.util.HashMap;

public class Player {
    private final String nickname;
    private int points;
    private final ColorPin pin;
    private final boolean isFirst;
    private PlayedCard rootCard;
    private HashMap<Sign, Integer> simbolCounter;
    private ObjectiveCard hiddenObjectiveCard;
    private ResourceCard[] hand = new ResourceCard[3];
    private int stateTurn;

    public Player(String nickname, ColorPin pin, boolean isFirst) {
        this.nickname = nickname;
        this.pin = pin;
        this.isFirst = isFirst;
        this.points = 0;
        for(Sign sign : Sign.values()){
            if(sign != null) {
                this.simbolCounter.put(sign, 0);
            }
        }
        for(int i = 0; i < hand.length; i++){
            hand[i] = null;
        }
        this.hiddenObjectiveCard = null;
        this.hand = null;
    }

    public String getNickname() {
        return nickname;
    }

    public int getPoints() {
        return points;
    }

    public ColorPin getPin() {
        return pin;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public PlayedCard getRootCard() {
        return rootCard;
    }

    public HashMap<Sign, Integer> getSymbolCounter() {
        return simbolCounter;
    }

    public ObjectiveCard getHiddenObjectiveCard() {
        return hiddenObjectiveCard;
    }

    public ResourceCard[] getHand() {
        return hand;
    }

    public int getStateTurn() {
        return stateTurn;
    }

    public void setRootCard(PlayedCard rootCard) {
        this.rootCard = rootCard;
    }

    public void setHiddenObjectiveCard(ObjectiveCard hiddenObjectiveCard) {
        this.hiddenObjectiveCard = hiddenObjectiveCard;
    }

    /**
     *
     * @param sign the type of resource to update
     * @param num_resources how many resouces a card gives
     *        this function update the resources given by a new card put on table
     */
    public void addSymbolCounter(Sign sign, Integer num_resources){
        simbolCounter.put(sign, simbolCounter.get(sign) + num_resources);
    }
    /**
     *
     * @param sign the type of resource to update
     * @param num_resources how many resouces to delate
     *        this function update the resources. It subtracts the resources covered by a new card played
     */
    public void removeSymbolCounter(Sign sign, Integer num_resources){
        simbolCounter.put(sign, simbolCounter.get(sign) - num_resources);
    }

    /**
     *
     * @param card the new card drawn from the deck
     *        this function update the resources. It subtracts the resources covered by a new card played
     */
    public void cardInHand(ResourceCard card){
        for(int i = 0; i < 3; i++){
            //it updates the hand in the place null, where a card is missing
            if(hand[i] == null){
                hand[i] = card;
                break;
            }
        }
    }
    /**
     *
     * @param card the card put on table, thus a played card
     *        this function update the resources. It subtracts the resources covered by a new card played
     */
    public void cardOnTable(ResourceCard card){
        for(int i = 0; i < 3; i++){
            if(hand[i] == card){
                hand[i] = null;
                break;
            }
        }
    }

    /**
     *
     * @param new_points the points possibly given by a new card
     *                   this function updates the points of a player
     */
    public void updatePoints(int new_points){
        points += new_points;
    }
}
