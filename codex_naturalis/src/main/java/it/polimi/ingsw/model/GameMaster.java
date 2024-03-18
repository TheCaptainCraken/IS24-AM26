package it.polimi.ingsw.model;
import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Stack;

public class GameMaster {
    private int globalTurn=0;//starts from 0
    //turn /lobby.getPlayers().length-1
    //currentPlayer %lobby.getPlayers().length
    private Lobby lobby;
    //String currentPlayer;
    private GameState gameState;//TODO non ho capito i gamestate, aggiungere CHOOSING_ROOT_CARD, CHOOSING_OBJECTIVE_CARD
    private Deck deckResource;//TODO usare i parametri
    private Deck deckGold;
    private Deck deckStart;
    private Deck deckObjective;
    private ResourceCard[] onTableResourceCard;
    private GoldCard[] onTableGoldCard;
    private ObjectiveCard[] onTableObjectiveCard;



//    void play(){
//        gameSetup();
//        while(notLastTurn){
//            numberOfTurn++;
//            for(i=0; i<numberOfPlayers; i++){
//                notLastTurn=turn();
//            }
//        }
//        for(i=0; i<numberOfPlayers; i++){
//            calculatePointsOfObjective()
//        }
//    }


    public void gameSetup(){//TODO
    //using src
        /*deckResource = new Deck (...)// mettiamo direttamente il nome del json e poi calcoliamo quanto è grande?
        deckGold = new Deck(...)
    //setup players
        deckObjective = new Deck(...);
        deckStart = new Deck(...);
        for(Player player : lobby.getPlayers()){
            //player.setRootCard(deckStart.draw());
        }
        for(Player player : lobby.getPlayers()){
            player.setRootCard(deckStart.draw());
        }

    //setupTable
        setOnTableResource(deckResource.draw(), 0);
        setOnTableResource(deckResource.draw(), 1);
        setOnTableGold(deck);*/



    }
    //player.drawPlayer(deck.draw())

    //TODO pick starting cart e objective card
    public int placeRootCard(String nicknamePlayer){//TODO devo pescare la carta fuori da qua e mostrarla con un-altra funzione e poi settarla qui, capire dove metterla nel mentre, due array (un array e una matrice alta due e num gioc)
        Player currentPlayer = getCurrentPlayer();
        if(isCurrentPlayer(nicknamePlayer, currentPlayer)) {
            if (gameState == GameState.CHOOSING_ROOT_CARD) {

                currentPlayer.setRootCard(deckStart.draw());
            }
        }
        return 1;//is not their turn
    }

    public int chooseObjectiveCard(String nicknamePlayer){
        Player currentPlayer = getCurrentPlayer();
        if(isCurrentPlayer(nicknamePlayer, currentPlayer)) {
            if (gameState == GameState.CHOOSING_OBJECTIVE_CARD) {

            }
        }
        return 1;//is not their turn
    }

    //placeCard
    public int placeCard(String nicknamePlayer, PlayableCard cardToPlace, Point coordinates, boolean side){
        Player currentPlayer = getCurrentPlayer();
        if(isCurrentPlayer(nicknamePlayer, currentPlayer)){
            if(gameState==GameState.PLACING_PHASE) {
                Dictionary<Corner, PlayedCard> attachments = isPositionable(currentPlayer.getRootCard(), cardToPlace, coordinates);
                if (attachments != null) {
                    if (cardToPlace instanceof GoldCard) {
                        GoldCard goldCard = (SpecialGoldCard) cardToPlace;
                        if (!goldCard.requirementsSatisfied(currentPlayer.getSimbolCounter())) {
                            return 4;//not enough resources
                        }
                    }
                    PlayedCard newPlayedCard = new PlayedCard(cardToPlace, attachments, side, getTurn(), coordinates);//the attachments are of the graph of the player who is playing so there isn-t any reference to Player class in the constructor
                    if (cardToPlace instanceof SpecialGoldCard) {
                        SpecialGoldCard specialGoldCard = (SpecialGoldCard) cardToPlace;
                        if (specialGoldCard.getCountable() == Countable.CORNER) {
                            currentPlayer.addPoints(specialGoldCard.pointsToAdd(newPlayedCard));
                        } else {
                            currentPlayer.addPoints(specialGoldCard.pointsToAdd(currentPlayer.getResources()));
                        }
                    } else {
                        ResourceCard resourceCard = (ResourceCard) newPlayedCard.getPlayableCard();
                        currentPlayer.addPoints(resourceCard.getPoints());
                    }
                    //rimuovi carta dalla mano dell-utente
                    currentPlayer.removePlayedCard(cardToPlace.getId());
                    gameState=GameState.DRAWING_PHASE;
                    return 0;
                }
                return 3;//is not positionable
            }
            return 2;//is not the right phase
        }
        return 1;//is not their turn
    }

    public int drawCard(String nicknamePlayer, boolean goldOrNot, int onTableOrDeck){ //onTableOrDeck has 0, 1 for position of array of cards on table and 2 for draw from deck
        Player currentPlayer = getCurrentPlayer();
        if(isCurrentPlayer(nicknamePlayer, currentPlayer)) {//TODO mazzo finito
            if(gameState==GameState.DRAWING_PHASE){
                if (goldOrNot) {
                    if (onTableOrDeck == 2) {//TODO la view richiede il nuovo retro di quella in cima e non solo di quella spostata
                        currentPlayer.draw(deckGold.draw());
                    }else{
                        currentPlayer.draw(onTableGoldCard[onTableOrDeck]);
                        onTableGoldCard[onTableOrDeck]=deckGold.draw();
                    }
                } else {
                    if (onTableOrDeck == 2) {//TODO la view richiede il nuovo retro di quella in cima e non solo di quella spostata
                        currentPlayer.draw(deckResource.draw());
                    }else{
                        currentPlayer.draw(onTableResourceCard[onTableOrDeck]);
                        onTableResourceCard[onTableOrDeck]=deckResource.draw();
                    }
                }
                gameState=GameState.PLACING_PHASE;
                nextGlobalTurn();
                return 0;
            }
            return 2;//is not the right phase
        }
        return 1;//is not their turn
    }

    //TODO calcolaObiettiviAllaFine


    //notation is x and y based on cartesian axes system rotated of 45 degrees counterclockwise, every card represents a dot with natural coordinates
    //example: starting card is always 0,0 so TOP_LEFT would be 0;1, TOP_RIGHT
    private Dictionary<Corner, PlayedCard> isPositionable(PlayedCard startingCard, PlayableCard cardToPlace, Point coordinates/*, Corner cornerOfCardToPlace*/){
        Dictionary<Corner, PlayedCard> attachments = new Hashtable<>();
        PlayedCard cardToCheck;
//        int xBaseToPlace=0, yBaseToPlace=0;
        int xPlaceToCheck=0, yPlaceToCheck=0;
//        switch(cornerOfCardToPlace){
//            case TOP_LEFT:{
//                xBaseToPlace=0;
//                yBaseToPlace=-1;
//                break;
//            }
//            case TOP_RIGHT:{
//                xBaseToPlace=-1;
//                yBaseToPlace=0;
//                break;
//            }
//            case BOTTOM_LEFT:{
//                xBaseToPlace=1;
//                yBaseToPlace=0;
//                break;
//            }
//            case BOTTOM_RIGHT:{
//                xBaseToPlace=0;
//                yBaseToPlace=1;
//                break;
//            }
//        }
        for(Corner corner : Corner.values()){
            switch(corner){
                case TOP_LEFT:{
                    xPlaceToCheck=0;
                    yPlaceToCheck=1;
                    break;
                }
                case TOP_RIGHT:{
                    xPlaceToCheck=1;
                    yPlaceToCheck=0;
                    break;
                }
                case BOTTOM_LEFT:{
                    xPlaceToCheck=-1;
                    yPlaceToCheck=0;
                    break;
                }
                case BOTTOM_RIGHT:{
                    xPlaceToCheck=0;
                    yPlaceToCheck=-1;
                    break;
                }
            }
            cardToCheck = findCard(startingCard, new Point(coordinates.x+xPlaceToCheck, coordinates.y+yPlaceToCheck));
            //if(corner!=cornerOfCardToPlace){
            //    cardToCheck = findCard(baseCard, new Point(baseCard.getCoordinates().x+xBaseToPlace+xPlaceToCheck, baseCard.getCoordinates().y+yBaseToPlace+yPlaceToCheck));
            //}else{
            //    cardToCheck = baseCard;
            //}
            if(cardToCheck!=null){
                Sign cornerToCheck;
                switch(corner){
                    case TOP_LEFT:{
                       cornerToCheck=cardToCheck.getPlayableCard().getCorners().get(Corner.BOTTOM_RIGHT);
                        break;
                    }
                    case TOP_RIGHT:{
                        cornerToCheck=cardToCheck.getPlayableCard().getCorners().get(Corner.BOTTOM_LEFT);
                        break;
                    }
                    case BOTTOM_LEFT:{
                        cornerToCheck=cardToCheck.getPlayableCard().getCorners().get(Corner.TOP_RIGHT);
                        break;
                    }
                    case BOTTOM_RIGHT:{
                        cornerToCheck=cardToCheck.getPlayableCard().getCorners().get(Corner.TOP_LEFT);
                        break;
                    }
                }
                if(cornerToCheck==null){
                    return null;
                }
            }
            attachments.put(corner, cardToCheck);
        }
        return attachments;
    }

    private PlayedCard findCard(PlayedCard card, Point coordinates){
        Stack<PlayedCard> stack = new Stack<>();//Anche Arraylist è uguale
        return recursiveFindCard(card, coordinates, stack);
    }

    private PlayedCard recursiveFindCard(PlayedCard card, Point coordinates, Stack<PlayedCard> stack) {
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

    public int getTurn(){
        return globalTurn / lobby.getPlayers().length - 1 ;//non conta giro rootCard e giro ObjectiveCard
    }

    public Player getCurrentPlayer(){
        return lobby.getPlayers()[globalTurn % lobby.getPlayers().length];
    }

    public boolean isCurrentPlayer(String nicknamePlayer, Player currentPlayer){
        nicknamePlayer.equals(currentPlayer.getNickname());
    }

    public void nextGlobalTurn() {
        this.globalTurn++;
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

    public int getPlayerPoints(){

    }

    public Dictionary<Sign, Integer> getPlayerResources(){

    }

    public ResourceCard[] getPlayerHand(){

    }
}
