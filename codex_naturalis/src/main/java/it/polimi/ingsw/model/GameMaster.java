package it.polimi.ingsw.model;

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



/*    void play(){
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


    public void gameSetup(){
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
    public int placeCard(String player, Point position){
        if(player.equals(currentPlayer)){
            PlayedCard newPlayedCard;
            if(isPositionable(cardId, )){
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

    private PlayedCard findCard(PlayedCard card, Point coordinates){
        if(card==null){
            return null;
        }else if(card.getCoordinates().x==coordinates.x && card.getCoordinates().y==coordinates.y){
            return card;
        }
        for(Corner corner : Corner.values())
            PlayedCard found = findCard(card.get, targetCoord);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
*/
    private boolean isPositionable(PlayedCard playedCard ){

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


