package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exception.*;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;

public class GameMaster {
    private int globalTurn;
    private TurnType turnType;
    private Lobby lobby;
    private GameState gameState;

    private Deck resourceDeck;
    private Deck goldDeck;
    private Deck startingDeck;
    private Deck objectiveDeck;

    private ResourceCard[] onTableResourceCards;
    private GoldCard[] onTableGoldCards;
    private ObjectiveCard[] onTableObjectiveCards;
    private StartingCard[] startingCardToPosition;
    private ObjectiveCard[][] objectiveCardToChoose;

    private ArrayList<Player> ranking;

    /**
     * The general functionalities of the game representing the peer point of the Model, the object is going to speak with the Controller
     *
     * @param lobby                      Lobby of user that are going to play
     * @param jsonResourceCardFileName   json file name to create the resource deck
     * @param jsonGoldCardFileName       json file name to create the gold deck
     * @param jsonObjectiveCardFileName  json file name to create the objective deck
     * @param jsonObjectiveStartFileName json file name to create the starting deck
     */
    public GameMaster(Lobby lobby, String jsonResourceCardFileName, String jsonGoldCardFileName, String jsonObjectiveCardFileName,
                      String jsonObjectiveStartFileName) throws IOException, ParseException {
        this.globalTurn = 0;
        this.turnType = TurnType.PLAYING;
        this.onTableResourceCards = new ResourceCard[2];
        this.onTableGoldCards = new GoldCard[2];
        this.onTableObjectiveCards = new ObjectiveCard[2];
        this.startingCardToPosition = new StartingCard[lobby.getPlayers().length];
        this.objectiveCardToChoose = new ObjectiveCard[lobby.getPlayers().length][2];
        this.ranking = new ArrayList<>();
        this.lobby = lobby;
        this.lobby.setLock();
        this.gameState = GameState.CHOOSING_ROOT_CARD;

        this.resourceDeck = new Deck(jsonResourceCardFileName);
        this.goldDeck = new Deck(jsonGoldCardFileName);
        this.objectiveDeck = new Deck(jsonObjectiveCardFileName);
        this.startingDeck = new Deck(jsonObjectiveStartFileName);

        //Set up of the table
        setOnTableResourceCard((ResourceCard) resourceDeck.draw(), 0);
        setOnTableResourceCard((ResourceCard) resourceDeck.draw(), 1);
        setOnTableGoldCard((GoldCard) goldDeck.draw(), 0);
        setOnTableGoldCard((GoldCard) goldDeck.draw(), 1);
        setOnTableObjectiveCards((ObjectiveCard) objectiveDeck.draw(), 0);
        setOnTableObjectiveCards((ObjectiveCard) objectiveDeck.draw(), 1);

        int i, j;
        for (i = 0; i < lobby.getPlayers().length; i++) {
            startingCardToPosition[i] = (StartingCard) startingDeck.draw();
        }
        for (i = 0; i < lobby.getPlayers().length; i++) {
            for (j = 0; j < 2; j++) {
                objectiveCardToChoose[i][j] = (ObjectiveCard) objectiveDeck.draw();
            }
        }
        for(Player player : lobby.getPlayers()){
            ResourceCard[] hand = new ResourceCard[3];
            hand[0]=(ResourceCard) resourceDeck.draw();
            hand[1]=(ResourceCard) resourceDeck.draw();
            hand[2]=(ResourceCard) goldDeck.draw();
            player.setHand(hand);
        }
    }

    /**
     * First turn cycle in which every player decides in which side place its StartingCard
     *
     * @param namePlayer player who sent the request
     * @param side       which side the StartingCard has been want placed
     */
    public void placeRootCard(String namePlayer, boolean side) throws NoTurnException, WrongGamePhaseException, NotExistsPlayerException {
        Player currentPlayer = getCurrentPlayer();

        if (!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new NoTurnException();
        } else if (gameState != GameState.CHOOSING_ROOT_CARD) {
            throw new WrongGamePhaseException();
        } else {
            HashMap<Corner, PlayedCard> defaultAttachments = new HashMap<>();
            StartingCard rootCard = startingCardToPosition[getOrderPlayer(currentPlayer.getName())];
            PlayedCard rootCardPlaced = new PlayedCard(rootCard, defaultAttachments, side, 0, new Point(0, 0));
            currentPlayer.setRootCard(rootCardPlaced);

            if(!side){
                for(Sign sign : rootCard.getBonusResources()) {
                    currentPlayer.addResource(sign, 1);
                }
            } else {
                for (Corner corner : Corner.values()) {
                    currentPlayer.addResource(rootCard.getCorners().get(corner), 1);
                }
            }

            nextGlobalTurn();
            if (getOrderPlayer(getCurrentPlayer().getName()) == 0) {
                gameState = GameState.CHOOSING_OBJECTIVE_CARD;
            }
        }
    }

    /**
     * Second turn cycle in which every player decides which of the two ObjectiveCard pick
     *
     * @param namePlayer player who sent the request
     * @param whichCard  which of the two ObjectiveCard wants to be used
     */
    public void chooseObjectiveCard(String namePlayer, int whichCard) throws NoTurnException, WrongGamePhaseException, NotExistsPlayerException {
        Player currentPlayer = getCurrentPlayer();
        if (!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new NoTurnException();
        } else if (gameState != GameState.CHOOSING_OBJECTIVE_CARD) {
            throw new WrongGamePhaseException();
        } else {
            currentPlayer.setSecretObjective(objectiveCardToChoose[getOrderPlayer(currentPlayer.getName())][whichCard]);
            nextGlobalTurn();
            if (getOrderPlayer(getCurrentPlayer().getName()) == 0) {
                gameState = GameState.PLACING_PHASE;
            }
        }
    }

    /**
     * Let the Player capsule in a PlacedCard connected to the rootCard graph of the Player
     *
     * @param namePlayer  Player who sent the request
     * @param cardToPlace Which card wants to place
     * @param position    In which position of the table the player wants to be place the card
     * @param side        To which side wants the player to place the card
     */
    public void placeCard(String namePlayer, ResourceCard cardToPlace, Point position, boolean side) throws
            NoSuchFieldException, IllegalArgumentException, NoTurnException, WrongGamePhaseException, NotEnoughResourcesException, CardPositionException, NotExistsPlayerException {

        //manage all possible exceptions
        Player currentPlayer = getCurrentPlayer();
        if (!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new NoTurnException();
        }
        if (gameState != GameState.PLACING_PHASE) {
            throw new WrongGamePhaseException();
        }

        //check if the card can be placed in the position
        HashMap<Corner, PlayedCard> attachments = isPositionable(currentPlayer.getRootCard(), position);
        //TODO deve essere gestita qua la exception di CardPositionException credo.
        //the player positions the card in the back front. The card is one resource and 4 empty corners.
        if (!side) {
            new PlayedCard(cardToPlace, attachments, side, getTurn(), position);
            currentPlayer.addResource(fromKingdomToSign(cardToPlace.getKingdom()), 1);
        } else {
            if (cardToPlace instanceof SpecialGoldCard) {
                GoldCard goldCard = (SpecialGoldCard) cardToPlace;
                if (!requirementsSatisfied(currentPlayer, goldCard)) {
                    throw new NotEnoughResourcesException();
                }
            }else if(cardToPlace instanceof GoldCard) {
                GoldCard goldCard = (GoldCard) cardToPlace;
                if (!requirementsSatisfied(currentPlayer, goldCard)) {
                    throw new NotEnoughResourcesException();
                }
            }
            //the attachments are of the graph of the player who is playing so there isn-t any reference to Player class in the constructor
            //TODO ma cosi costruisci solo per le ResourceCard in questo caso??
            new PlayedCard(cardToPlace, attachments, side, getTurn(), position);
            for (Corner corner : Corner.values()) {
                currentPlayer.addResource(cardToPlace.getCorners().get(corner), 1);
            }
        }
        //remove resources from counter
        for (Corner corner : Corner.values()) {
            //if corner points to null doesn't remove any resources, resources are subtracted only if the PlayedCards
            //present in the attachments HashMap are played on their front side or are referencing the StartingCard
            if(attachments.get(corner) != null && (attachments.get(corner).isFacingUp() || attachments.get(corner).getCard() instanceof StartingCard) ) {
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
        }
        //TODO se è girata non devi aggiungere i punti
        //At the end because I need to know resources values at the end and how many attachments when I've found them
        if(side) {
            if (cardToPlace instanceof SpecialGoldCard) {
                SpecialGoldCard specialGoldCard = (SpecialGoldCard) cardToPlace;
                currentPlayer.addPoints(calculatesSpecialGoldPoints(currentPlayer, specialGoldCard, attachments));
            } else {
                currentPlayer.addPoints(cardToPlace.getPoints());
            }
        }

        currentPlayer.giveCard(cardToPlace);
        if(areTheCardFinished()){//only when the card are fijnished and the game is in the final phase
            nextGlobalTurn();
            if (turnType == TurnType.SECOND_LAST_TURN && getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
                //if it is the last player in second-last turn cicle, say the next is the last turn
                turnType = TurnType.LAST_TURN;
            } else if (turnType == TurnType.LAST_TURN && getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
                //if it is the last player in last turn cicle, go to end mode
                gameState = GameState.END;
            }
        }else{
            gameState = GameState.DRAWING_PHASE;
        }
    }

    /**
     * Allows the Player to draw a card from the table or decks (there are 6 different possibilities based on goldOrNot and onTableOrDeck)
     *
     * @param namePlayer    Player who sent the request
     * @param Gold          If the type of the resourceCard that wants to be drawn is gold or not
     * @param CardPosition  If the card is taken from the table or not: 2 means from deck, 0 and 1 are the position onTable array
     * @return
     */
    public int drawCard(String namePlayer, boolean Gold, int CardPosition) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, IndexOutOfBoundsException{
        //CardPosition has 0, 1 for position of array of cards on table and -1 for drawing from deck
        Player currentPlayer = getCurrentPlayer();
        if (!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new NoTurnException();
        }
        if (gameState != GameState.DRAWING_PHASE) {
            throw new WrongGamePhaseException();
        }

        ResourceCard cardDrawn;
        if (Gold) {
            if (CardPosition == -1) {
                cardDrawn = (ResourceCard) goldDeck.draw();
                currentPlayer.takeCard(cardDrawn);
            } else {
                cardDrawn = onTableGoldCards[CardPosition];
                if(cardDrawn == null) {
                    throw new IllegalArgumentException("There is no card in that spot on table");
                }
                currentPlayer.takeCard(cardDrawn);
                try{
                    onTableGoldCards[CardPosition] = (GoldCard) goldDeck.draw();
                }catch (IndexOutOfBoundsException e){
                    onTableGoldCards[CardPosition] = null;
                }
            }
        } else {
            if (CardPosition == -1) {
                cardDrawn = (ResourceCard) resourceDeck.draw();
                currentPlayer.takeCard(cardDrawn);
            } else {
                cardDrawn = onTableResourceCards[CardPosition];
                if(cardDrawn == null) {
                    throw new IllegalArgumentException("There is no card in that spot on table");
                }
                currentPlayer.takeCard(cardDrawn);
                try {
                    onTableResourceCards[CardPosition] = (ResourceCard) resourceDeck.draw();
                }catch (IndexOutOfBoundsException e){
                    onTableResourceCards[CardPosition] = null;
                }
            }
        }

        //next player will play?
        if (turnType == TurnType.SECOND_LAST_TURN && getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
            //if it is the last player in second-last turn cicle, say the next is the last turn
            turnType = TurnType.LAST_TURN;
            gameState = GameState.PLACING_PHASE;
        } else if (turnType == TurnType.LAST_TURN && getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
            //if it is the last player in last turn cicle, go to end mode
            gameState = GameState.END;
            //TODO fine gioco
        } else if (currentPlayer.getPoints() >= 20 || areTheCardFinished()) {
            //if a player reached 20 points set this turn cicle as the second-last
            //TODO fine mazzi e fine partita
            turnType = TurnType.SECOND_LAST_TURN;
            gameState = GameState.PLACING_PHASE;
        } else {
            gameState = GameState.PLACING_PHASE;
        }

        nextGlobalTurn();
        return cardDrawn.getId();
    }

    /**
     * It runes the final part of the game calculating points of objective players and calculate the ranking list
     *
     * @param namePlayer Player who sent the request
     * @return
     */
    //TODO OBJECTIVE CARD POINTS calcolaObiettiviAllaFine
    public void endGame(String namePlayer) throws WrongGamePhaseException {
        //Player will click a button to calculate their points. No we can do in drawCard.
        //TODO OBJECTIVE CARD POINTS rifare senza il calcolo degli stati

        ArrayList<Integer> numberOfObjectiveForPlayer = new ArrayList<>();//With a linkedhashmap I can't put before or after an element
        if (gameState != GameState.END) {//It just has an anti-cheat purpose
            throw new WrongGamePhaseException();
        } else {
            for (Player player : lobby.getPlayers()) {
                int numberOfObjective = calculateBestCombination();
                //TODO OBJECTIVE CARD POINTS quelli in altro modo
                //numberOfObjective+=;
                boolean toInsert = true;
                int i;
                for (i = 0; i < ranking.size() && toInsert; i++) {
                    if (ranking.get(i).getPoints() < player.getPoints() || (ranking.get(i).getPoints() == player.getPoints()
                            && numberOfObjectiveForPlayer.get(i) > numberOfObjective)) {
                        ranking.add(i, player);//aggiungi prima
                        numberOfObjectiveForPlayer.add(i, numberOfObjective);
                        toInsert = false;
                    }
                }
                if (toInsert) {
                    ranking.add(ranking.size(), player);//aggiungi prima
                    numberOfObjectiveForPlayer.add(numberOfObjectiveForPlayer.size(), numberOfObjective);
                }
                //TODO OBJECTIVE CARD POINTS finire, qua fare con l'overload dell'interfaccia e poi in questo metodo
                // si chiameranno i metodi di ricerca di this
            }
        }
    }

    //Finding methods

    /**
     * Given a position it gives attachments to the card, the Corner keys are referred to the Corner of the new card
     * Notation is x and y based on cartesian axes system rotated of 45 degrees counterclockwise, every card represents a dot with natural coordinates
     * Example: starting card is always 0,0 so TOP_LEFT would be 0;1, TOP_RIGHT //TODO sistema meglio questo commento
     *
     * @param startingCard A card where the recursion will start to find the required PlayedCard
     * @param position  Position that identifies where the next card should be placed
     * @return Hashmap<Corner, PlayedCard> of the attachments for the card to cardToPlace
     * @throws NoSuchFieldException
     */
    private HashMap<Corner, PlayedCard> isPositionable(PlayedCard startingCard, Point position) throws CardPositionException {
        HashMap<Corner, PlayedCard> attachments = new HashMap<>();
        PlayedCard cardToCheck, newCard;
        boolean validPosition = false;
        int xPlaceToCheck = 0, yPlaceToCheck = 0;

        //For each corner of the card to be placed, it checks if there is a possible card to attach it. The switch case refers to the new Card.
        for (Corner corner : Corner.values()) {
            switch (corner) {
                case TOP_LEFT: {
                    xPlaceToCheck = 0;
                    yPlaceToCheck = 1;
                    break;
                }
                case TOP_RIGHT: {
                    xPlaceToCheck = 1;
                    yPlaceToCheck = 0;
                    break;
                }
                case BOTTOM_LEFT: {
                    xPlaceToCheck = -1;
                    yPlaceToCheck = 0;
                    break;
                }
                case BOTTOM_RIGHT: {
                    xPlaceToCheck = 0;
                    yPlaceToCheck = -1;
                    break;
                }
            }

            //TODO carta già posizionata, si potrebbe fare matematicamente.
            newCard = findCard(startingCard, position);
            if(newCard != null){
                throw new CardPositionException();
            }
            cardToCheck = findCard(startingCard, new Point(position.x + xPlaceToCheck, position.y + yPlaceToCheck));

            if (cardToCheck != null) {
                Sign cornerToCheck = null;
                /*Checks if the corner is valid:
                * if the card is facing up, it checks the corner of the card to be placed
                * if the card is facing down, it checks the backside corner of the card to be placed(only for starting card).
                */
                if (cardToCheck.isFacingUp()) {
                    switch (corner) {
                        case TOP_LEFT: {
                            cornerToCheck = cardToCheck.getCard().getCorners().get(Corner.BOTTOM_RIGHT);
                            break;
                        }
                        case TOP_RIGHT: {
                            cornerToCheck = cardToCheck.getCard().getCorners().get(Corner.BOTTOM_LEFT);
                            break;
                        }
                        case BOTTOM_LEFT: {
                            cornerToCheck = cardToCheck.getCard().getCorners().get(Corner.TOP_RIGHT);
                            break;
                        }
                        case BOTTOM_RIGHT: {
                            cornerToCheck = cardToCheck.getCard().getCorners().get(Corner.TOP_LEFT);
                            break;
                        }
                    }
                } else if (!cardToCheck.isFacingUp() && (cardToCheck.getCard() instanceof StartingCard)) {
                    //it's a starting card upside down
                    switch (corner) {
                        case TOP_LEFT: {
                            cornerToCheck = ((StartingCard) cardToCheck.getCard()).getBacksideCorners().get(Corner.BOTTOM_RIGHT);
                            break;
                        }
                        case TOP_RIGHT: {
                            cornerToCheck = ((StartingCard) cardToCheck.getCard()).getBacksideCorners().get(Corner.BOTTOM_LEFT);
                            break;
                        }
                        case BOTTOM_LEFT: {
                            cornerToCheck = ((StartingCard) cardToCheck.getCard()).getBacksideCorners().get(Corner.TOP_RIGHT);
                            break;
                        }
                        case BOTTOM_RIGHT: {
                            cornerToCheck = ((StartingCard) cardToCheck.getCard()).getBacksideCorners().get(Corner.TOP_LEFT);
                            break;
                        }
                    }
                } else {
                    cornerToCheck = Sign.EMPTY;
                }
                //It verifies if the corner of the card to be placed is compatible with the corner of the card to which it is attached
                if (cornerToCheck == null) {
                    throw new CardPositionException();
                }
                validPosition = true;
            }
            attachments.put(corner, cardToCheck);
        }

        if (!validPosition) {
            throw new CardPositionException();
        }
        return attachments;
    }

    /**
     * Looking at all graph of PlayedCard to find a PlayedCard identified by position
     *
     * @param startingCard Where the recursion will start to find the required PlayedCard
     * @param position
     * @return method recursiveFindCard //TODO è giusto?
     */
    private PlayedCard findCard(PlayedCard startingCard, Point position) {
        Stack<PlayedCard> stack = new Stack<>();
        return recursiveFindCard(startingCard, position, stack);
    }

    /**
     * Called by findCard
     * Recursive looking at all graph of PlayedCard to find a PlayedCard identified by position
     *
     * @param playedCard PlayedCard that I'm visiting
     * @param position   Position of the card that I'm looking for
     * @param stack      Stack in which I save already visited cards
     * @return PlayedCard if exists else null
     */
    private PlayedCard recursiveFindCard(PlayedCard playedCard, Point position, Stack<PlayedCard> stack) {
        if (playedCard == null) {
            return null;
        } else if(stack.contains(playedCard)) {
            return null;
        }
        else if (playedCard.getPosition().x == position.x && playedCard.getPosition().y == position.y) {
            return playedCard;
        }
        stack.push(playedCard);
        for (Corner corner : Corner.values()) {
            PlayedCard found = recursiveFindCard(playedCard.getAttached(corner), position, stack);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Converts Kingdom enum to Sign enum
     * @param kingdom Kingdom to convert
     * @return Sign in which the Kingdom has been converted
     */
    private Sign fromKingdomToSign(Kingdom kingdom) throws IllegalArgumentException {
        switch (kingdom) {
            case PLANT:
                return Sign.LEAF;
            case ANIMAL:
                return Sign.WOLF;
            case FUNGI:
                return Sign.MUSHROOM;
            case INSECT:
                return Sign.BUTTERFLY;
        }
        throw new IllegalArgumentException("It's not a right Kingdom to convert");
    }

    /**
     * Converts Countable enum to Sign enum
     *
     * @param countable Countable to convert
     * @return Sign in which the Kingdom has been converted
     */
    private Sign fromCountableToSign(Countable countable) throws IllegalArgumentException {
        switch (countable) {
            case QUILL:
                return Sign.QUILL;
            case INKWELL:
                return Sign.INKWELL;
            case SCROLL:
                return Sign.SCROLL;
        }
        throw new IllegalArgumentException("It's not a right Countable to convert");
    }

    /**
     * Controls if the resources of a Player are enough for the requirements of a GoldCard
     *
     * @param player   Player about we want to know if they can place the GoldCard
     * @param goldCard GoldCard that wants to be placed and has certain requirements
     * @return
     */
    public boolean requirementsSatisfied(Player player, GoldCard goldCard) {
        for (Sign sign : Sign.values()) {
            if(sign != Sign.EMPTY) {
                if (player.getResources().get(sign) < goldCard.getRequirements().get(sign)) {
                    return false;
                }
            }
        }
        return true;
    }

    //Calculate points methods

    /**
     * Calculate the number of points given by the effect of SpecialGoldCard that is positioned
     *
     * @param player          who is trying to position the card to see their resource count
     * @param specialGoldCard specialGoldCard given to find the effect
     * @param attachments     other card corners used to calculate the corners covered for Countable.CORNER type
     * @return number of points to add to the player points
     * @throws IllegalArgumentException
     */
    private int calculatesSpecialGoldPoints(Player player, SpecialGoldCard specialGoldCard, HashMap<Corner, PlayedCard> attachments){
        if (specialGoldCard.getThingToCount() == Countable.CORNER) {
            int numberOfAttachments = 0;
            for (PlayedCard playedCard : attachments.values()) {
                if (playedCard != null) {
                    numberOfAttachments++;
                }
            }
            return specialGoldCard.getPoints() * numberOfAttachments;
        } else
            return specialGoldCard.getPoints() * player.getResources().get(fromCountableToSign(specialGoldCard.getThingToCount()));


    }

    //TODO OBJECTIVE CARD POINTS
    private int calculateEndGamePoints(ObjectiveType type, int multiplier) {
        return 0;
    }

    /**
     * Calculates the best combination of ObjectiveCard effects to get major increase of points with more application of
     * effects (In case of draw points between players)
     *
     * @return ..//TODO OBJECTIVE CARD POINTS
     */
    private int calculateBestCombination() {
        return 0;
    }

    //Turn methods

    /**
     * Pass to the next turn consequentially switching player
     */
    private void nextGlobalTurn() {
        this.globalTurn++;
    }

    /**
     * Calculates the number of cicles made by players, the cicle of setup are not counted
     *
     * @return the cycle of turns made in total
     */
    public int getTurn() {
        return globalTurn / lobby.getPlayers().length - 1;
        //non conta giro rootCard (turn = -1) e giro ObjectiveCard (turn = 0)
    }

    /**
     * Calculates the number of the current player who has the right to play
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return lobby.getPlayers()[globalTurn % (lobby.getPlayers().length)];
    }

    /**
     * Get in which number the player plays respectively in the turn cycle
     *
     * @param name player who sent the request
     * @return get order of player
     */
    private int getOrderPlayer(String name) throws NotExistsPlayerException {
        int i;
        for (i = 0; i < lobby.getPlayers().length; i++) {
            if (lobby.getPlayers()[i].getName().equals(name)) {
                return i;
            }
        }
        throw new NotExistsPlayerException();
    }

    /**
     * Compares the name of the player who sent the request and who is the turn right now and says if it's their turn
     *
     * @param name          player who sent the request
     * @param currentPlayer the player who is the turn right now
     * @return
     */
    private boolean isCurrentPlayer(String name, Player currentPlayer) {
        return name.equals(currentPlayer.getName());
    }


    /**
     * Place card on the spot on table
     *
     * @param resourceCard ResourceCard to position on table
     * @param place        id of the spot where to place, 0 and 1 are the position onTable array
     */
    public void setOnTableResourceCard(ResourceCard resourceCard, int place) {
        this.onTableResourceCards[place] = resourceCard;
    }

    /**
     * Place card on the spot on table
     *
     * @param goldCard GoldCard to position on table
     * @param place    id of the spot where to place, 0 and 1 are the position onTable array
     */
    public void setOnTableGoldCard(GoldCard goldCard, int place) {
        this.onTableGoldCards[place] = goldCard;
    }

    /**
     * Place card on the spot on table (It's usable only in GameSetup constructor)
     *
     * @param objectiveCard ObjectiveCard to position on table
     * @param place         id of the spot where to place, 0 and 1 are the position onTable array
     */
    public void setOnTableObjectiveCards(ObjectiveCard objectiveCard, int place) {
        this.onTableObjectiveCards[place] = objectiveCard;
    }

    //Info methods for the view

    /**
     * Request info about the points of a certain player
     *
     * @param namePlayer name of the player about is wanted to get info
     * @return points of the player
     */
    public int getPlayerPoints(String namePlayer) throws NoSuchFieldException {
        return lobby.getPlayerFromName(namePlayer).getPoints();
    }

    /**
     * Request info about the resources of a certain player
     *
     * @param namePlayer name of the player about is wanted to get info
     * @return resources of the player
     */
    public HashMap<Sign, Integer> getPlayerResources(String namePlayer) throws NoSuchFieldException {
        return lobby.getPlayerFromName(namePlayer).getResources();
    }

    /**
     * Give the ranking at the end of the match
     *
     * @return List of winners
     */
    public ArrayList<Player> getRanking() {
        return ranking;
    }

    private boolean areTheCardFinished(){
        return getKingdomNextCardResourceDeck()==null && getKingdomNextCardGoldDeck()==null && getResourceCardOnTable(0)==null && getResourceCardOnTable(1)==null && getGoldCardOnTable(0)==null && getGoldCardOnTable(1)==null;
    }

    /**
     * Give the resource card on the table
     *
     * @param position position of the card on the table
     * @return ResourceCard on the table
     */
    public ResourceCard getResourceCardOnTable(int position) {
        return onTableResourceCards[position];
    }

    /**
     * Give the gold card on the table
     *
     * @param position position of the card on the table
     * @return ResourceCard on the table
     */
    public GoldCard getGoldCardOnTable(int position) {
       return onTableGoldCards[position];
    }

    /**
     * Give the Kingdom of the next card of the resourceDeck
     *
     * @return Kingdom of the next card of the deck
     */
    public Kingdom getKingdomNextCardResourceDeck() {
        try {
            return resourceDeck.getKingdomFirstCard();
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Give the Kingdom of the next card of the goldDeck
     *
     * @return Kingdom of the next card of the deck
     */
    public Kingdom getKingdomNextCardGoldDeck() {
        try {
            return goldDeck.getKingdomFirstCard();
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }
}
