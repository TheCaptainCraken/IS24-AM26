package it.polimi.ingsw.model;
import java.awt.*;
import java.util.Stack;

public class GameMaster {
    private int numberOfTurn=0;
    private Lobby community;
    String currentPlayer;
    Deck deckResource;
    Deck deckGold;
    Deck deckStart;
    Deck deckObjective;
    ResourceCard[] onTableResourceCard;
    GoldCard[] onTableGoldCard;
    ObjectiveCard[] onTableObjectiveCard;



    void play(){
        gameSetup();
        while(notLastTurn){
            numberOfTurn++;
            for(i=0; i<numberOfPlayers; i++){
                notLastTurn=turn();
            }
        }
        for(i=0; i<numberOfPlayers; i++){
            calculatePointsOfObjective()
        }
    }


 /*   public void gameSetup(){
    //using src
        deckResource = new Deck (...)// mettiamo direttamente il nome del json e poi calcoliamo quanto è grande?
        deckGold = new Deck(...)
    //setup players


    //setupTable
        setOnTableResource(deckResource.draw(), 0)
        setOnTableResource(deckResource.draw(), 1)
        setOnTableGold(deck)



    }
    //player.drawPlayer(deck.draw())

    //placeCard
    public int placeCard(String player, PlayableCard cardToPlace, Point position){
        if(player.equals(currentPlayer)){//TODO capire come identificare user
            PlayedCard newPlayedCard;
            if(isPositionable(cardToPlace, )){
                newPlayedCard=new PlayedCard();

                if(newPlayedCard.getPlayableCard() instanceof SpecialGoldCard){
                    if(goldSpecialCard.getCountable()=='conta risorse'){
                        newResourceCard.pointsToAdd(PlayedCard newPlayedCard)
                    }else{
                        pointsToAdd(Dictionary currentPlayer.getResources())
                    }
                }else{
                    newPlayedCard.getPlayableCard()
                }
                //rimuovi carta dalla mano dell-utente
                player.
                return 0;
            }
            return 2;
        }
        return 1;
    }

    public int drawCard(boolean goldOrNot,){
        if(goldOrNot){

        }else{

        }
        //aggiungi a giocatore corrente nello slot vuoto delle carte
    }

    private isPositionable(PlayedCard baseCard, Corner, PlayableCard toPlaceCard){

    }
*/

    private PlayedCard findCard(PlayedCard card, Point coordinates){
        Stack<PlayedCard> stack = new Stack<PlayedCard>();//Anche Arraylist è uguale
        return recursiveFindCard(card, coordinates, stack);
    }

    private PlayedCard recursiveFindCard(PlayedCard card, Point coordinates, Stack stack) {
        if (stack.search(card) > 0 || card == null) {
            return null;
        } else if (card.getCoordinates().x == coordinates.x && card.getCoordinates().y == coordinates.y) {
            return card;
        }
        for (Corner corner : Corner.values()) {
            PlayedCard found = recursiveFindCard(card.getAttachmentCorners().get(corner), coordinates, stack);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private boolean isPositionable(PlayedCard baseCard, Corner angleOfBaseCard, PlayableCard ){

    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setOnTableResourceCard(ResourceCard[] onTableResourceCard) {
        this.onTableResourceCard = onTableResourceCard;
    }

    public void setOnTableGoldCard(GoldCard[] onTableGoldCard) {
        this.onTableGoldCard = onTableGoldCard;
    }

    public void setOnTableObjectiveCard(ObjectiveCard[] onTableObjectiveCard) {
        this.onTableObjectiveCard = onTableObjectiveCard;
    }
}
