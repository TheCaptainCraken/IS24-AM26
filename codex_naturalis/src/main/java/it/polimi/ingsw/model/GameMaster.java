package it.polimi.ingsw.model;
import java.awt.*;
import java.util.HashMap;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Stack;

public class GameMaster {
    private int globalTurn;//starts from 0
    //turn /lobby.getPlayers().length-1
    //currentPlayer %lobby.getPlayers().length
    private int flagTurnRemained;
    private Lobby lobby;
    //String currentPlayer;
    private GameState gameState;//TODO non ho capito i gamestate, aggiungere CHOOSING_ROOT_CARD, CHOOSING_OBJECTIVE_CARD
    private Deck resourceDeck;//TODO usare i parametri
    private Deck goldDeck;
    private Deck startingDeck;
    private Deck objectiveDeck;
    private ResourceCard[] onTableResourceCards;
    private GoldCard[] onTableGoldCards;
    private ObjectiveCard[] onTableObjectiveCards;
    private StartingCard[] startingCardToPosition;
    private ObjectiveCard[][] objectiveCardToChoose;

    public GameMaster(Lobby lobby, String jsonResourceCardFileName,  String jsonGoldCardFileName,  String jsonObjectiveCardFileName,  String jsonObjectiveStartFileName) {
        this.globalTurn = 0;
        this.flagTurnRemained = 2; //is a value to say is not the last, it could be substituted with ENUM: PLAYING, SECOND-LAST_TURN, LAST_TURN
        this.lobby = lobby;
        this.gameState = GameState.CHOOSING_ROOT_CARD;
        this.resourceDeck = new Deck (jsonResourceCardFileName);// mettiamo direttamente il nome del json e poi calcoliamo quanto è grande?
        this.goldDeck = new Deck(jsonGoldCardFileName);
        this.objectiveDeck = new Deck(jsonObjectiveCardFileName);
        this.startingDeck = new Deck(jsonObjectiveStartFileName);
        //setupTable
        setOnTableResourceCard((ResourceCard) resourceDeck.draw(), 0);
        setOnTableResourceCard((ResourceCard) resourceDeck.draw(), 1);
        setOnTableGoldCard((GoldCard) goldDeck.draw(), 0);
        setOnTableGoldCard((GoldCard) goldDeck.draw(), 1);
        for(Player player : lobby.getPlayers()){
            player.setRootCard(null); //cosa vuoi passare?
        }
        for(int i=0; i<lobby.getPlayers().length; i++){
            startingCardToPosition[i]=(StartingCard) startingDeck.draw();
        }
        for(int i=0; i<lobby.getPlayers().length; i++){
            for(int j=0; j<2; j++){
                objectiveCardToChoose[i][j]=(ObjectiveCard) objectiveDeck.draw();
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


    /**First turn cicle in which every player decides in which side place its StartingCard
     * @param namePlayer player who sent the request
     * @param side which side the StartingCard has been want placed
     */
    //TODO pick starting cart e objective card
    public void placeRootCard(String namePlayer, boolean side){//TODO devo pescare la carta fuori da qua e mostrarla con un-altra funzione e poi settarla qui, capire dove metterla nel mentre, due array (un array e una matrice alta due e num gioc)
        Player currentPlayer = getCurrentPlayer();
        if(!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new IllegalStateException ("It's not the turn of this player");//is not their turn
        }else if(gameState != GameState.CHOOSING_ROOT_CARD) {
            throw new IllegalStateException ("It's not the right phase to choose how to place the card");
        }else{
            HashMap<Corner, PlayedCard>  defaultAttachments = new HashMap<>();
            for(Corner corner : Corner.values()){
                defaultAttachments.put(corner, null);
            }
            currentPlayer.setRootCard(new PlayedCard(startingCardToPosition[getOrderOfPlayOfThePlayer(currentPlayer.getName())], defaultAttachments, side, 0, new Point(0, 0)));
            nextGlobalTurn();
            if(getOrderOfPlayOfThePlayer(getCurrentPlayer().getName())==0){
                gameState=GameState.CHOOSING_OBJECTIVE_CARD;
            }
        }
    }

    /**Second turn cicle in which every player decides which of the two ObjectiveCard pick
     * @param namePlayer player who sent the request
     * @param whichCard which of the two ObjectiveCard wants to be used
     */
    public int chooseObjectiveCard(String namePlayer, int whichCard){
        Player currentPlayer = getCurrentPlayer();
        if(!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new IllegalStateException ("It's not the turn of this player");//is not their turn
        }else if(gameState != GameState.CHOOSING_OBJECTIVE_CARD) {
            throw new IllegalStateException ("It's not the right phase to choose how to place the card");
        }else{
            currentPlayer.setSecretObjective(objectiveCardToChoose[getOrderOfPlayOfThePlayer(currentPlayer.getName())][whichCard]);
            nextGlobalTurn();
            if(getOrderOfPlayOfThePlayer(getCurrentPlayer().getName())==0){
                gameState = GameState.PLACING_PHASE;
            }
            return 0;
        }
    }


    /**Let the Player capsule in a PlacedCard connected to the rootCard graph of the Player
     * @param namePlayer Player who sent the request
     * @param cardToPlace Which card wants to place
     * @param position In which position of the table the player wants to be place the card
     * @param side To which side wants the player to place the card
     */
    public int placeCard(String namePlayer, PlayableCard cardToPlace, Point position, boolean side){
        Player currentPlayer = getCurrentPlayer();
        if(!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new IllegalStateException("not current player"); //not current turn
        }

        if(gameState != GameState.PLACING_PHASE) {
            throw new IllegalStateException("not corret phase game"); //
        }

        HashMap<Corner, PlayedCard> attachments = isPositionable(currentPlayer.getRootCard(), cardToPlace, position);
        if (attachments == null) {
            throw new IllegalStateException("the card is not positionable");
        }

        if(side){ //cosa serve questo if?
            if (cardToPlace instanceof GoldCard) {
                GoldCard goldCard = (SpecialGoldCard) cardToPlace;
                if (requirementsSatisfied(currentPlayer.getResources(), goldCard.getRequirements())) {
                     throw new IllegalStateException("not enough resources");
                    }
                }

                PlayedCard newPlayedCard = new PlayedCard(cardToPlace, attachments, side, getTurn(), position);//the attachments are of the graph of the player who is playing so there isn-t any reference to Player class in the constructor
                if (cardToPlace instanceof SpecialGoldCard) {
                    SpecialGoldCard specialGoldCard = (SpecialGoldCard) cardToPlace;
                    if (specialGoldCard.getThingToCount() == Countable.CORNER) {
                        currentPlayer.addPoints(specialGoldCard.pointsToAdd(newPlayedCard));//cannot create PlayedCard then because to use the graph and calculate effects I need first to place it here
                    } else {
                        currentPlayer.addPoints(specialGoldCard.pointsToAdd(currentPlayer.getResources()));
                    }
                }else {
                    ResourceCard resourceCard = (ResourceCard) newPlayedCard.getCard();
                    currentPlayer.addPoints(resourceCard.getPoints());
                }

                for (Corner corner : Corner.values()) {//
                    currentPlayer.addResource(cardToPlace.getAttached().get(corner), 1);
                }
                currentPlayer.addPoints(0); //cosa devo passare
            }else{
                PlayedCard newPlayedCard = new PlayedCard(cardToPlace, attachments, side, getTurn(), position);
                currentPlayer.addResource(fromKingdomToSign(cardToPlace.getKingdom()), 1);
            }

            for (Corner corner : Corner.values()) {//remove from counter
                switch (corner) {
                    case TOP_LEFT: {
                        currentPlayer.removeResources(attachments.get(corner).getCard().getCorners().get(Corner.BOTTOM_RIGHT), 1);
                        break;
                    }
                    case TOP_RIGHT: {
                        currentPlayer.removeResources(attachments.get(corner).getCard().getCorners().get(Corner.BOTTOM_LEFT), 1);
                        break;
                    }
                    case BOTTOM_LEFT: {
                        currentPlayer.removeResources(attachments.get(corner).getCard().getCorners().get(Corner.TOP_RIGHT), 1);
                        break;
                    }
                    case BOTTOM_RIGHT: {
                        currentPlayer.removeResources(attachments.get(corner).getCard().getCorners().get(Corner.TOP_LEFT), 1);
                        break;
                    }
                }
            }
            //rimuovi carta dalla mano dell-utente
            currentPlayer.giveCard((ResourceCard) cardToPlace);
            gameState = GameState.DRAWING_PHASE;
            return 0;
        }

    /**
     * @param namePlayer
     * @param goldOrNot
     * @param onTableOrDeck
     * @return
     */
    public ResourceCard drawCard(String namePlayer, boolean goldOrNot, int onTableOrDeck){ //onTableOrDeck has 0, 1 for position of array of cards on table and 2 for draw from deck
        Player currentPlayer = getCurrentPlayer();
        if(isCurrentPlayer(namePlayer, currentPlayer)) {//TODO mazzo finito
            return null;
        }
        if(gameState != GameState.DRAWING_PHASE) {
            return null;
        }
        if (goldOrNot) {
            if (onTableOrDeck == 2) {//TODO la view richiede il nuovo retro di quella in cima e non solo di quella spostata
                currentPlayer.takeCard((ResourceCard)goldDeck.draw());
            }else{
                currentPlayer.takeCard(onTableGoldCards[onTableOrDeck]);
                onTableGoldCards[onTableOrDeck]=(GoldCard) goldDeck.draw();
            }
        } else {
            if (onTableOrDeck == 2) {//TODO la view richiede il nuovo retro di quella in cima e non solo di quella spostata
                        currentPlayer.takeCard((ResourceCard) resourceDeck.draw());
            }else{
                currentPlayer.takeCard(onTableResourceCards[onTableOrDeck]);
                onTableResourceCards[onTableOrDeck]=(ResourceCard) resourceDeck.draw();
            }
        }

        //next player will play?
        //TODO maybe it could be written in a better way
        if(flagTurnRemained ==1 && getOrderOfPlayOfThePlayer(currentPlayer.getName())+1==lobby.getPlayers().length){//if it is the last player in second-last turn cicle, say the next is the last turn
            flagTurnRemained =0;
            gameState = GameState.PLACING_PHASE;
        }else if (flagTurnRemained ==0 && getOrderOfPlayOfThePlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {//if it is the last player in last turn cicle, go to end mode
            gameState=GameState.END;
        }else if(currentPlayer.getPoints() >= 20){//if a player reached 20 points set this turn cicle as the second-last
            flagTurnRemained = 1;
            gameState = GameState.PLACING_PHASE;
        }else{
            gameState = GameState.PLACING_PHASE;
        }
        nextGlobalTurn();
        return null;
    }


    //TODO calcolaObiettiviAllaFine
    public int endGame(String namePlayer){ //Player will click a button to calculate their points
        Player currentPlayer = getCurrentPlayer();
        if(!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new IllegalStateException ("It's not the turn of this player");//is not their turn
        }else if(gameState != GameState.END) {
            throw new IllegalStateException ("It's not the right phase to choose how to place the card");
        }else{
            currentPlayer.addPoints(calculateEndGamePoints(currentPlayer.getSecretObjective().getType(), currentPlayer.getSecretObjective().getMultiplier()));//TODO finire, qua fare con l'overload dell'interfaccia e poi in questo metodo si chiameranno i metodi di ricerca di this
            for(ObjectiveCard objectiveCard : onTableObjectiveCards){
                currentPlayer.addPoints(objectiveCard.effect(calculateEndGamePoints(objectiveCard.getType(), objectiveCard.getMultiplier())));//cosa vuoi fare non esiste effect
            }
            return currentPlayer.getPoints();
        }
        return -1;
    }


    //notation is x and y based on cartesian axes system rotated of 45 degrees counterclockwise, every card represents a dot with natural coordinates
    //example: starting card is always 0,0 so TOP_LEFT would be 0;1, TOP_RIGHT
    private HashMap<Corner, PlayedCard> isPositionable(PlayedCard startingCard, PlayableCard cardToPlace, Point coordinates/*, Corner cornerOfCardToPlace*/){
        HashMap<Corner, PlayedCard> attachments = new HashMap<>();
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
                            cornerToCheck=cardToCheck.getCard().getCorners().get(Corner.BOTTOM_RIGHT);
                            break;
                        }
                        case TOP_RIGHT:{
                            cornerToCheck=cardToCheck.getCard().getCorners().get(Corner.BOTTOM_LEFT);
                            break;
                        }
                        case BOTTOM_LEFT:{
                            cornerToCheck=cardToCheck.getCard().getCorners().get(Corner.TOP_RIGHT);
                            break;
                        }
                        case BOTTOM_RIGHT:{
                            cornerToCheck=cardToCheck.getCard().getCorners().get(Corner.TOP_LEFT);
                            break;
                        }
                    }
                }else if(!cardToCheck.isFacingUp()&&(cardToCheck.getCard() instanceof StartingCard)){//it's a starting card upside down
                    switch(corner){
                        case TOP_LEFT:{
                            cornerToCheck=((StartingCard) cardToCheck.getCard()).getBacksideCorners().get(Corner.BOTTOM_RIGHT);
                            break;
                        }
                        case TOP_RIGHT:{
                            cornerToCheck=((StartingCard) cardToCheck.getCard()).getBacksideCorners().get(Corner.BOTTOM_LEFT);
                            break;
                        }
                        case BOTTOM_LEFT:{
                            cornerToCheck=((StartingCard) cardToCheck.getCard()).getBacksideCorners().get(Corner.TOP_RIGHT);
                            break;
                        }
                        case BOTTOM_RIGHT:{
                            cornerToCheck=((StartingCard) cardToCheck.getCard()).getBacksideCorners().get(Corner.TOP_LEFT);
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

    /**Looking at all graph of PlayedCard to find a PlayedCard identified by position
     * @param startingCard Where the recursion will start to find the required PlayedCard
     * @param position
     * @return method recursiveFindCard //TODO è giusto?
     */
    private PlayedCard findCard(PlayedCard startingCard, Point position){//TODO METTERLO A DISPOSIZIONE DEL MODULE DI GOLDCARD E OBJECTCARD
        Stack<PlayedCard> stack = new Stack<>();//Anche Arraylist è uguale
        return recursiveFindCard(startingCard, position, stack);
    }

    /**Called by findCard
     * Recursive looking at all graph of PlayedCard to find a PlayedCard identified by position
     * @param playedCard PlayedCard that I'm visiting
     * @param position Position of the card that I-m looking for
     * @param stack Stack in which I save already visited cards
     * @return PlayedCard if exists else null//TODO needs the logic?
     */
    private PlayedCard recursiveFindCard(PlayedCard playedCard, Point position, Stack<PlayedCard> stack) {
        if (stack.search(playedCard) > 0 || playedCard == null) {
            return null;
        } else if (playedCard.getPosition().x == position.x && playedCard.getPosition().y == position.y) {
            return playedCard;
        }
        for (Corner corner : Corner.values()) {
            PlayedCard found = recursiveFindCard(playedCard.getAttached(corner), position, stack);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**Converts Kingdom enum to Sign enum
     * @param kingdom Kingdom to comvert
     * @return Sign in which the Kingdom has been converted
     */
    private Sign fromKingdomToSign(Kingdom kingdom) {
        switch(kingdom){
            case PLANT: return Sign.PLANT;
            case ANIMAL: return Sign.ANIMAL;
            case FUNGI: return Sign.FUNGI;
            case INSECT: return Sign.INSECT;
        }
        return null;//TODO excpetion
    }

    private int calculateEndGamePoints(ObjectiveType type,int multiplier){
        return 0;
    }

    public int getTurn(){
        return globalTurn / lobby.getPlayers().length - 1 ;//non conta giro rootCard e giro ObjectiveCard
    }

    public Player getCurrentPlayer(){
        return lobby.getPlayers()[globalTurn % lobby.getPlayers().length];
    }

    public int getOrderOfPlayOfThePlayer(String name){
        for(int i=0; i<lobby.getPlayers().length; i++){
            if(lobby.getPlayers()[i].getName().equals(name)){
                return i;
            }
        }
        return -1;//TODO add a NoSuchFieldException
    }

    public boolean isCurrentPlayer(String namePlayer, Player currentPlayer){
        return namePlayer.equals(currentPlayer.getName());
    }

    public void nextGlobalTurn() {
        this.globalTurn++;
    }

    public void setOnTableResourceCard(ResourceCard resourceCard, int place) {
        this.onTableResourceCards[place] = resourceCard;
    }

    public void setOnTableGoldCard(GoldCard goldCard, int place) {
        this.onTableGoldCards[place] = goldCard;
    }

    public void setOnTableObjectiveCards(ObjectiveCard[] onTableObjectiveCards) {//TODO modificare come quelli prima
        this.onTableObjectiveCards = onTableObjectiveCards;
    }

    public int getPlayerPoints(String name){
        return lobby.getPlayerFromName(name).getPoints();
    }

    public boolean requirementsSatisfied(HashMap<Sign, Integer> resources, HashMap<Sign, Integer> requirements){
        for(Sign sign : Sign.values()){
            if(resources.get(sign)<requirements.get(sign)){
                return false;
            }
        }
        return true;
    }



//    public StartingCard[] getStartingCardToPosition() {
//        return startingCardToPosition;
//    }
//
//    public ObjectiveCard[][] getObjectiveCardToChoose(String name) {
//        return objectiveCardToChoose[];
//    }
//
//    public HashMap<Sign, Integer> getPlayerResources(){
//
//    }
//
//    public ResourceCard[] getPlayerHand(String name){
//        get
//    }
}
