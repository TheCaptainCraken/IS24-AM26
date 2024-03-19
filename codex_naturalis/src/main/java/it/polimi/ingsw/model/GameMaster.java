package it.polimi.ingsw.model;
import java.awt.*;
import java.util.HashTable;
import java.util.Hashtable;
import java.util.Stack;

public class GameMaster {
    private int globalTurn=0;//starts from 0
    //turn /lobby.getPlayers().length-1
    //currentPlayer %lobby.getPlayers().length
    private int turnRemained;
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
    private StartingCard[] startingCardToPosition;
    private ObjectiveCard[][] objectiveCardToChoose;

    public GameMaster(Lobby lobby, String jsonResourceCardFileName,  String jsonGoldCardFileName,  String jsonObjectiveCardFileName,  String jsonObjectiveStartFileName) {
        this.globalTurn = 0;
        this.turnRemained = 2; //is a value to say is not the last, it could be substituted with ENUM: PLAYING, SECOND-LAST_TURN, LAST_TURN
        this.lobby = lobby;
        this.gameState = GameState.CHOOSING_ROOT_CARD;
        this.deckResource = new Deck (jsonResourceCardFileName);// mettiamo direttamente il nome del json e poi calcoliamo quanto è grande?
        this.deckGold = new Deck(jsonGoldCardFileName);
        this.deckObjective = new Deck(jsonObjectiveCardFileName);
        this.deckStart = new Deck(jsonObjectiveStartFileName);
        //setupTable
        setOnTableResourceCard(deckResource.draw(), 0);
        setOnTableResourceCard(deckResource.draw(), 1);
        setOnTableGoldCard(deckGold.draw(), 0);
        setOnTableGoldCard(deckGold.draw(), 1);
        for(Player player : lobby.getPlayers()){
            player.setStartingHand();
        }
        for(int i=0; i<lobby.getPlayers().length; i++){
            startingCardToPosition[i]=(StartingCard) deckStart.draw();
        }
        for(int i=0; i<lobby.getPlayers().length; i++){
            for(int j=0; j<2; j++){
                objectiveCardToChoose[i][j]=(ObjectiveCard) deckObjective.draw();
            }
        }
    }

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


    //TODO pick starting cart e objective card
    public int placeRootCard(String nicknamePlayer, boolean side){//TODO devo pescare la carta fuori da qua e mostrarla con un-altra funzione e poi settarla qui, capire dove metterla nel mentre, due array (un array e una matrice alta due e num gioc)
        Player currentPlayer = getCurrentPlayer();
        if(!isCurrentPlayer(nicknamePlayer, currentPlayer)) {
            throw new IllegalStateException ("It's not the turn of this player");//is not their turn
        }else if(gameState != GameState.CHOOSING_ROOT_CARD) {
            throw new IllegalStateException ("It's not the right phase to choose how to place the card");
        }else{
            HashTable<Corner, PlayedCard>  defaultAttachments = new HashTable<>();
            for(Corner corner : Corner.values()){
                defaultAttachments.put(corner, null);
            }
            currentPlayer.setRootCard(new PlayedCard(startingCardToPosition[getOrderOfPlayOfThePlayer(currentPlayer.getNickname())], defaultAttachments, side, 0, new Point(0, 0)));
            nextGlobalTurn();
            if(getOrderOfPlayOfThePlayer(getCurrentPlayer().getNickname())==0){
                gameState=GameState.CHOOSING_OBJECTIVE_CARD;
            }
            return 0;
        }
        return 1;
    }

    public int chooseObjectiveCard(String nicknamePlayer, int whichCard){
        Player currentPlayer = getCurrentPlayer();
        if(!isCurrentPlayer(nicknamePlayer, currentPlayer)) {
            throw new IllegalStateException ("It's not the turn of this player");//is not their turn
        }else if(gameState != GameState.CHOOSING_OBJECTIVE_CARD) {
            throw new IllegalStateException ("It's not the right phase to choose how to place the card");
        }else{
            currentPlayer.setSecretObjective(objectiveCardToChoose[getOrderOfPlayOfThePlayer(currentPlayer.getNickname())][whichCard]);
            nextGlobalTurn();
            if(getOrderOfPlayOfThePlayer(getCurrentPlayer().getNickname())==0){
                gameState=GameState.PLACING_PHASE;
            }
            return 0;
        }
        return 1;//is not their turn
    }

    //placeCard
    public int placeCard(String nicknamePlayer, PlayableCard cardToPlace, Point coordinates, boolean side){
        Player currentPlayer = getCurrentPlayer();
        if(isCurrentPlayer(nicknamePlayer, currentPlayer)){
            if(gameState==GameState.PLACING_PHASE) {
                HashTable<Corner, PlayedCard> attachments = isPositionable(currentPlayer.getRootCard(), cardToPlace, coordinates);
                if (attachments != null) {
                    if(side){
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
                                currentPlayer.addPoints(specialGoldCard.pointsToAdd(newPlayedCard));//cannot create PlayedCard then because to use the graph and calculate effects I need first to place it here
                            } else {
                                currentPlayer.addPoints(specialGoldCard.pointsToAdd(currentPlayer.getResources()));
                            }
                        } else {
                            ResourceCard resourceCard = (ResourceCard) newPlayedCard.getPlayableCard();
                            currentPlayer.addPoints(resourceCard.getPoints());
                        }
                        for(Corner corner : Corner.values()){//
                            currentPlayer.addToSimbolCounter(cardToPlace.getCorners().get(corner));;
                        }
                    }else{
                        PlayedCard newPlayedCard = new PlayedCard(cardToPlace, attachments, side, getTurn(), coordinates);
                        currentPlayer.addToSimbolCounter(fromKingdomToSign(cardToPlace.getKingdom()));
                    }
                    for(Corner corner : Corner.values()){//remove from counter
                        switch(corner){
                            case TOP_LEFT:{
                                currentPlayer.removeFromSimbolCounter(attachments.get(corner).getPlayableCard().getCorners().get(Corner.BOTTOM_RIGHT));
                                break;
                            }
                            case TOP_RIGHT:{
                                currentPlayer.removeFromSimbolCounter(attachments.get(corner).getPlayableCard().getCorners().get(Corner.BOTTOM_LEFT));
                                break;
                            }
                            case BOTTOM_LEFT:{
                                currentPlayer.removeFromSimbolCounter(attachments.get(corner).getPlayableCard().getCorners().get(Corner.TOP_RIGHT));
                                break;
                            }
                            case BOTTOM_RIGHT:{
                                currentPlayer.removeFromSimbolCounter(attachments.get(corner).getPlayableCard().getCorners().get(Corner.TOP_LEFT));
                                break;
                            }
                        }
                    }
                    //rimuovi carta dalla mano dell-utente
                    currentPlayer.takeCard(cardToPlace.getId());
                    gameState=GameState.DRAWING_PHASE;
                    return 0;
                }
                return 3;//is not positionable
            }
            return 2;//is not the right phase
        }
        return 1;//is not their turn
    }

    private Sign fromKingdomToSign(Kingdom kingdom) {
        switch(kingdom){
            case PLANT: return Sign.PLANT;
            case ANIMAL: return Sign.ANIMAL;
            case FUNGI: return Sign.FUNGI;
            case INSECT: return Sign.INSECT;
        }
        return null;//TODO excpetion
    }

    public int drawCard(String nicknamePlayer, boolean goldOrNot, int onTableOrDeck){ //onTableOrDeck has 0, 1 for position of array of cards on table and 2 for draw from deck
        Player currentPlayer = getCurrentPlayer();
        if(isCurrentPlayer(nicknamePlayer, currentPlayer)) {//TODO mazzo finito
            if(gameState==GameState.DRAWING_PHASE){
                if (goldOrNot) {
                    if (onTableOrDeck == 2) {//TODO la view richiede il nuovo retro di quella in cima e non solo di quella spostata
                        currentPlayer.giveCard((PlayableCard) deckGold.draw());
                    }else{
                        currentPlayer.giveCard(onTableGoldCard[onTableOrDeck]);
                        onTableGoldCard[onTableOrDeck]=deckGold.draw();
                    }
                } else {
                    if (onTableOrDeck == 2) {//TODO la view richiede il nuovo retro di quella in cima e non solo di quella spostata
                        currentPlayer.giveCard(deckResource.draw());
                    }else{
                        currentPlayer.giveCard(onTableResourceCard[onTableOrDeck]);
                        onTableResourceCard[onTableOrDeck]=deckResource.draw();
                    }
                }
                //next player will play?
                //TODO maybe it could be written in a better way
                if(turnRemained==1&&getOrderOfPlayOfThePlayer(currentPlayer.getNickname())+1==lobby.getPlayers().length){//if it is the last player in second-last turn cicle, say the next is the last turn
                    turnRemained=0;
                    gameState = GameState.PLACING_PHASE;
                } else if (turnRemained==0&&getOrderOfPlayOfThePlayer(currentPlayer.getNickname())+1==lobby.getPlayers().length) {//if it is the last player in last turn cicle, go to end mode
                    gameState=GameState.END;
                }else if(currentPlayer.getPoints()>=20){//if a player reached 20 points set this turn cicle as the second-last
                    turnRemained=1;
                    gameState = GameState.PLACING_PHASE;
                }else{
                    gameState = GameState.PLACING_PHASE;
                }
                nextGlobalTurn();
                return 0;
            }
            return 2;//is not the right phase
        }
        return 1;//is not their turn
    }

    //TODO calcolaObiettiviAllaFine
    public int endGame(String nicknamePlayer){ //Player will click a button to calculate their points
        Player currentPlayer = getCurrentPlayer();
        if(!isCurrentPlayer(nicknamePlayer, currentPlayer)) {
            throw new IllegalStateException ("It's not the turn of this player");//is not their turn
        }else if(gameState != GameState.END) {
            throw new IllegalStateException ("It's not the right phase to choose how to place the card");
        }else{
            currentPlayer.addPoints(currentPlayer.getSecretObjective().effect(currentPlayer.getRootCard()));//TODO finire, qua fare con l'overload dell'interfaccia e poi in questo metodo si chiameranno i metodi di ricerca di this
            for(ObjectiveCard objectiveCard : onTableObjectiveCard){
                currentPlayer.addPoints(onTableObjectiveCard.effect(currentPlayer.getRootCard()));//
            }
            return currentPlayer.getPoints();
        }
        return -1;
    }


    //notation is x and y based on cartesian axes system rotated of 45 degrees counterclockwise, every card represents a dot with natural coordinates
    //example: starting card is always 0,0 so TOP_LEFT would be 0;1, TOP_RIGHT
    private HashTable<Corner, PlayedCard> isPositionable(PlayedCard startingCard, PlayableCard cardToPlace, Point coordinates/*, Corner cornerOfCardToPlace*/){
        HashTable<Corner, PlayedCard> attachments = new Hashtable<>();
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
                Sign cornerToCheck = null;//IDE gives error in cornerToCheck==null because it could be not initialized but is the purpose of this check
                if(cardToCheck.isFacingUp()){
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
                }else if(!cardToCheck.isFacingUp()&&(cardToCheck.getPlayableCard() instanceof StartingCard)){//it's a starting card upside down
                    switch(corner){
                        case TOP_LEFT:{
                            cornerToCheck=((StartingCard) cardToCheck.getPlayableCard()).getBackSideCorners().get(Corner.BOTTOM_RIGHT);
                            break;
                        }
                        case TOP_RIGHT:{
                            cornerToCheck=((StartingCard) cardToCheck.getPlayableCard()).getBackSideCorners().get(Corner.BOTTOM_LEFT);
                            break;
                        }
                        case BOTTOM_LEFT:{
                            cornerToCheck=((StartingCard) cardToCheck.getPlayableCard()).getBackSideCorners().get(Corner.TOP_RIGHT);
                            break;
                        }
                        case BOTTOM_RIGHT:{
                            cornerToCheck=((StartingCard) cardToCheck.getPlayableCard()).getBackSideCorners().get(Corner.TOP_LEFT);
                            break;
                        }
                    }
                }else{
                    cornerToCheck=Sign.EMPTY;
                }
                if(cornerToCheck==null){
                    return null;
                }
            }
            attachments.put(corner, cardToCheck);
        }
        return attachments;
    }

    private PlayedCard findCard(PlayedCard card, Point coordinates){//TODO METTERLO A DISPOSIZIONE DEL MODULE DI GOLDCARD E OBJECTCARD
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
            PlayedCard found = recursiveFindCard(card.getAttached(corner), coordinates, stack);
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

    public Player getPlayerFromNickname(String nickname){
        for(Player player : lobby.getPlayers()){
            if(player.getNickname().equals(nickname)){
                return player;
            }
        }
        return null;//TODO add a NoSuchFieldException
    }

    public int getOrderOfPlayOfThePlayer(String nickname){
        for(int i=0; i<lobby.getPlayers().length; i++){
            if(lobby.getPlayers()[i].getNickname().equals(nickname)){
                return i;
            }
        }
        return -1;//TODO add a NoSuchFieldException
    }

    public boolean isCurrentPlayer(String nicknamePlayer, Player currentPlayer){
        nicknamePlayer.equals(currentPlayer.getNickname());
    }

    public void nextGlobalTurn() {
        this.globalTurn++;
    }

    public void setOnTableResourceCard(ResourceCard resourceCard, int place) {
        this.onTableResourceCard[place] = resourceCard;
    }

    public void setOnTableGoldCard(GoldCard goldCard, int place) {
        this.onTableGoldCard[place] = goldCard;
    }

    public void setOnTableObjectiveCard(ObjectiveCard[] onTableObjectiveCard) {
        this.onTableObjectiveCard = onTableObjectiveCard;
    }

    public int getPlayerPoints(String nickname){
        return getPlayerFromNickname(nickname).getPoints();
    }

//    public StartingCard[] getStartingCardToPosition() {
//        return startingCardToPosition;
//    }
//
//    public ObjectiveCard[][] getObjectiveCardToChoose(String nickname) {
//        return objectiveCardToChoose[];
//    }
//
//    public HashTable<Sign, Integer> getPlayerResources(){
//
//    }
//
//    public ResourceCard[] getPlayerHand(String nickname){
//        get
//    }
}
