package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exception.*;
import java.awt.*;
import java.util.*;
import java.util.HashMap;

public class GameMaster {
    private int globalTurn;
    private int flagTurnRemained;
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
    public GameMaster(Lobby lobby, String jsonResourceCardFileName, String jsonGoldCardFileName, String jsonObjectiveCardFileName, String jsonObjectiveStartFileName) {
        this.globalTurn = 0;
        this.flagTurnRemained = 2;
        this.turnType = TurnType.PLAYING;

        this.lobby = lobby;
        this.lobby.setLock();
        this.gameState = GameState.CHOOSING_ROOT_CARD;

        this.resourceDeck = new Deck(jsonResourceCardFileName);// mettiamo direttamente il nome del json e poi calcoliamo quanto è grande?
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
     * First turn cicle in which every player decides in which side place its StartingCard
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
            for (Corner corner : Corner.values()) {
                defaultAttachments.put(corner, null);
            }
            StartingCard rootCard = startingCardToPosition[getOrderPlayer(currentPlayer.getName())];
            PlayedCard rootCardPlaced = new PlayedCard(rootCard, defaultAttachments, side, 0, new Point(0, 0));
            currentPlayer.setRootCard(rootCardPlaced);
            if(!side){
                for(Sign sign : rootCard.getBonusResources()) {
                    currentPlayer.addResource(sign, 1);
                }
            }
            nextGlobalTurn();
            if (getOrderPlayer(getCurrentPlayer().getName()) == 0) {
                gameState = GameState.CHOOSING_OBJECTIVE_CARD;
            }
        }
    }

    /**
     * Second turn cicle in which every player decides which of the two ObjectiveCard pick
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
            NoSuchFieldException, IllegalArgumentException, NoTurnException, WrongGamePhaseException, NotEnoughResourcesException {

        Player currentPlayer = getCurrentPlayer();
        if (!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new NoTurnException();
        }
        if (gameState != GameState.PLACING_PHASE) {
            throw new WrongGamePhaseException();
        }

        HashMap<Corner, PlayedCard> attachments = isPositionable(currentPlayer.getRootCard(), position);
        //the player positions the card in the back front. The card is one resource and 4 empty corners.
        if (!side) {
            new PlayedCard(cardToPlace, attachments, side, getTurn(), position);
            currentPlayer.addResource(fromKingdomToSign(cardToPlace.getKingdom()), 1);
        } else {
            if (cardToPlace instanceof GoldCard) {
                GoldCard goldCard = (SpecialGoldCard) cardToPlace;
                if (!requirementsSatisfied(currentPlayer, goldCard)) {
                    throw new NotEnoughResourcesException();
                }
            }
            //the attachments are of the graph of the player who is playing so there isn-t any reference to Player class in the constructor
            new PlayedCard(cardToPlace, attachments, side, getTurn(), position);
            for (Corner corner : Corner.values()) {
                currentPlayer.addResource(cardToPlace.getCorners().get(corner), 1);
            }
        }
        //remove resources from counter
        for (Corner corner : Corner.values()) {
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
        //At the end because I need to know resources values at the end and how many attachments when I've found them
        if (cardToPlace instanceof SpecialGoldCard) {
            SpecialGoldCard specialGoldCard = (SpecialGoldCard) cardToPlace;
            currentPlayer.addPoints(calculatesSpecialGoldPoints(currentPlayer, specialGoldCard, attachments));
        } else {
            currentPlayer.addPoints(cardToPlace.getPoints());
        }
        currentPlayer.giveCard(cardToPlace);
        gameState = GameState.DRAWING_PHASE;
    }

    /**
     * Allows the Player to draw a card from the table or decks (there are 6 different possibilities based on goldOrNot and onTableOrDeck)
     *
     * @param namePlayer    Player who sent the request
     * @param goldOrNot     If the type of the resourceCard that wants to be drawn is gold or not
     * @param onTableOrDeck If the card is taken from the table or not: 2 means from deck, 0 and 1 are the position onTable array
     * @return
     */
    public int drawCard(String namePlayer, boolean goldOrNot, int onTableOrDeck) throws NullPointerException, WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        //onTableOrDeck has 0, 1 for position of array of cards on table and 2 for draw from deck
        Player currentPlayer = getCurrentPlayer();
        if (isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new NoTurnException();
        }
        if (gameState != GameState.DRAWING_PHASE) {
            throw new WrongGamePhaseException();
        }
        ResourceCard cardDrawn;
        if (goldOrNot) {
            if (onTableOrDeck == 2) {//TODO FOR THE VIEW la view richiede il nuovo retro di quella in cima e non solo di quella spostata
                cardDrawn = (ResourceCard) goldDeck.draw();
                currentPlayer.takeCard(cardDrawn);
            } else {
                cardDrawn = onTableGoldCards[onTableOrDeck];
                currentPlayer.takeCard(cardDrawn);
                onTableGoldCards[onTableOrDeck] = (GoldCard) goldDeck.draw();
            }
        } else {
            if (onTableOrDeck == 2) {//TODO FOR THE VIEW la view richiede il nuovo retro di quella in cima e non solo di quella spostata
                cardDrawn = (ResourceCard) resourceDeck.draw();
                currentPlayer.takeCard(cardDrawn);
            } else {
                cardDrawn = onTableResourceCards[onTableOrDeck];
                currentPlayer.takeCard(cardDrawn);
                onTableResourceCards[onTableOrDeck] = (ResourceCard) resourceDeck.draw();
            }
        }
        //next player will play?
        if (flagTurnRemained == 1 && getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
            //if it is the last player in second-last turn cicle, say the next is the last turn
            flagTurnRemained = 0;
            gameState = GameState.PLACING_PHASE;
        } else if (flagTurnRemained == 0 && getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
            //if it is the last player in last turn cicle, go to end mode
            gameState = GameState.END;
        } else if (currentPlayer.getPoints() >= 20) {//if a player reached 20 points set this turn cicle as the second-last
            flagTurnRemained = 1;
            gameState = GameState.PLACING_PHASE;
        } else {
            gameState = GameState.PLACING_PHASE;
        }
        nextGlobalTurn();

        return cardDrawn.getId(); //TODO Check ritornare solo l'id alla view
    }

    /**
     * It runes the final part of the game calculating points of objective players and calculate the ranking list
     *
     * @param namePlayer Player who sent the request
     * @return
     */
    //TODO OBJECTIVE CARD POINTS calcolaObiettiviAllaFine
    public void endGame(String namePlayer) throws WrongGamePhaseException { //Player will click a button to calculate their points
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
                /*player.addPoints(calculateEndGamePoints(currentPlayer.getSecretObjective().getType(), currentPlayer.getSecretObjective().getMultiplier()));
                //TODO OBJECTIVE CARD POINTS finire, qua fare con l'overload dell'interfaccia e poi in questo metodo si chiameranno i metodi di ricerca di this
                for(ObjectiveCard objectiveCard : onTableObjectiveCards){
                    currentPlayer.addPoints(objectiveCard.effect(calculateEndGamePoints(objectiveCard.getType(), objectiveCard.getMultiplier())));//cosa vuoi fare non esiste effect

                }*/
            }
        }
    }

    //Finding methods

    /**
     * Given a position it gives attachments to the card, the Corner keys are referred to the Corner of the new card
     * Notation is x and y based on cartesian axes system rotated of 45 degrees counterclockwise, every card represents a dot with natural coordinates
     * Example: starting card is always 0,0 so TOP_LEFT would be 0;1, TOP_RIGHT
     *
     * @param startingCard A card where
     * @param position  Position that identifies where the next card should be placed
     * @return Hashmap<Corner, PlayedCard> of the attachments for the card to cardToPlace
     * @throws NoSuchFieldException
     */
    private HashMap<Corner, PlayedCard> isPositionable(PlayedCard startingCard, Point position) throws NoSuchFieldException, IllegalStateException {
        HashMap<Corner, PlayedCard> attachments = new HashMap<>();
        PlayedCard cardToCheck;
        boolean cardToPlaceIsAttached = false;
        int xPlaceToCheck = 0, yPlaceToCheck = 0;
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
            cardToCheck = findCard(startingCard, new Point(position.x + xPlaceToCheck, position.y + yPlaceToCheck));
            if (cardToCheck != null) {
                Sign cornerToCheck = null;
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
                if (cornerToCheck == null) {
                    throw new IllegalStateException("Cannot attach to an hidden corner");
                }
                cardToPlaceIsAttached = true;
            }
            attachments.put(corner, cardToCheck);
        }
        if (!cardToPlaceIsAttached) {
            throw new NoSuchFieldException("It's not possible place a card unattached from the other cards");
            //TODO cambiare eccezioni
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
    private PlayedCard findCard(PlayedCard startingCard, Point position) {//TODO OBJECTIVE CARD POINTS METTERLO A DISPOSIZIONE DEL MODULE DI GOLDCARD E OBJECTCARD
        Stack<PlayedCard> stack = new Stack<>();//Anche Arraylist è uguale
        return recursiveFindCard(startingCard, position, stack);
    }

    /**
     * Called by findCard
     * Recursive looking at all graph of PlayedCard to find a PlayedCard identified by position
     *
     * @param playedCard PlayedCard that I'm visiting
     * @param position   Position of the card that I-m looking for
     * @param stack      Stack in which I save already visited cards
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

    /**
     * Converts Kingdom enum to Sign enum
     * @param kingdom Kingdom to convert
     * @return Sign in which the Kingdom has been converted
     */
    private Sign fromKingdomToSign(Kingdom kingdom) throws IllegalArgumentException {
        switch (kingdom) {
            case PLANT:
                return Sign.PLANT;
            case ANIMAL:
                return Sign.ANIMAL;
            case FUNGI:
                return Sign.FUNGI;
            case INSECT:
                return Sign.INSECT;
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
                return Sign.PLANT;
            case INKWELL:
                return Sign.ANIMAL;
            case SCROLL:
                return Sign.FUNGI;
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
            if (player.getResources().get(sign) < goldCard.getRequirements().get(sign)) {
                return false;
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
        int numberOfAttachments;
        switch (specialGoldCard.getThingToCount()) {
            case CORNER: {
                numberOfAttachments = 0;
                for (PlayedCard playedCard : attachments.values()) {
                    if (playedCard != null) {
                        numberOfAttachments++;
                    }
                }
                return specialGoldCard.getPoints() * numberOfAttachments;
            }
            default: {
                return specialGoldCard.getPoints() * player.getResources().get(fromCountableToSign(specialGoldCard.getThingToCount()));
            }
        }
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
        return globalTurn / lobby.getPlayers().length - 1;//non conta giro rootCard e giro ObjectiveCard
    }

    /**
     * Calculates the number of the current player who has the right to play
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {//TODO offset
        return lobby.getPlayers()[globalTurn % lobby.getPlayers().length];
    }

    /**
     * Get in which number the player plays respectively in the turn cycle
     *
     * @param name player who sent the request
     * @return get order of player
     */
    private int getOrderPlayer(String name) throws NotExistsPlayerException {//TODO offset
        for (int i = 0; i < lobby.getPlayers().length; i++) {
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

}
