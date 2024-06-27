package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exception.*;
import it.polimi.ingsw.view.model.CardClient;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * The GameMaster class represents the main game logic in the application.
 * It manages the game state, player turns, card decks, and scoring.
 * It also handles the placement of cards and the drawing of cards from the deck
 * or table.
 * The GameMaster interacts with the Lobby to manage the players and with the
 * Deck to manage the cards.
 * It also uses various helper methods to calculate scores, check if a card can
 * be placed, and find cards in the game graph.
 * The GameMaster class throws various exceptions to handle invalid game
 * actions.
 */
public class GameMaster implements Serializable {
    /**
     * The global turn counter.
     */
    private int globalTurn;
    /**
     * The type of turn.
     */
    private TurnType turnType;
    /**
     * The lobby of players.
     */
    private Lobby lobby;
    /**
     * The current game state.
     */
    private GameState gameState;

    /**
     * The resource deck.
     */
    private Deck resourceDeck;
    /**
     * The gold deck.
     */
    private Deck goldDeck;
    /**
     * The starting deck.
     */
    private Deck startingDeck;
    /**
     * The objective deck.
     */
    private Deck objectiveDeck;

    /**
     * The resource cards on the table.
     */
    private ResourceCard[] onTableResourceCards;
    /**
     * The gold cards on the table.
     */
    private GoldCard[] onTableGoldCards;
    /**
     * The objective cards on the table.
     */
    private ObjectiveCard[] onTableObjectiveCards;
    /**
     * The starting cards to position.
     */
    private StartingCard[] startingCardToPosition;
    /**
     * The objective cards to choose.
     */
    private ObjectiveCard[][] objectiveCardToChoose;
    /**
     * The ranking of players.
     */
    private ArrayList<Player> ranking;


    /**
     * The general functionalities of the game representing the peer point of the
     * Model, the object is going to speak with the Controller
     *
     * @param lobby                      Lobby of user that are going to play
     * @param jsonResourceCardFileName   json file name to create the resource deck
     * @param jsonGoldCardFileName       json file name to create the gold deck
     * @param jsonObjectiveCardFileName  json file name to create the objective deck
     * @param jsonStartingCardFileName json file name to create the starting deck
     *
     * @throws IOException    if the file is not found or can't be read
     * @throws ParseException if the file is not a valid JSON file
     */
    public GameMaster(Lobby lobby, InputStream jsonResourceCardFileName, InputStream jsonGoldCardFileName,
                      InputStream jsonObjectiveCardFileName,
                      InputStream jsonStartingCardFileName) throws IOException, ParseException {
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
        this.startingDeck = new Deck(jsonStartingCardFileName);

        // Set up of the table
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
        for (Player player : lobby.getPlayers()) {
            ResourceCard[] hand = new ResourceCard[3];
            hand[0] = (ResourceCard) resourceDeck.draw();
            hand[1] = (ResourceCard) resourceDeck.draw();
            hand[2] = (ResourceCard) goldDeck.draw();
            player.setHand(hand);
        }
    }

    /**
     * The general functionalities of the game representing the peer point of the
     * Model, the object is going to speak with the Controller
     *
     * @param lobby                      Lobby of user that are going to play
     * @param jsonResourceCardFileName   json file name to create the resource deck
     * @param jsonGoldCardFileName       json file name to create the gold deck
     * @param jsonObjectiveCardFileName  json file name to create the objective deck
     * @param jsonStartingCardFileName json file name to create the starting deck
     *
     * @throws IOException    if the file is not found or can't be read
     * @throws ParseException if the file is not a valid JSON file
     */
    public GameMaster(Lobby lobby, String jsonResourceCardFileName, String jsonGoldCardFileName,
                      String jsonObjectiveCardFileName,
                      String jsonStartingCardFileName) throws IOException, ParseException {
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
        this.startingDeck = new Deck(jsonStartingCardFileName);

        // Set up of the table
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
        for (Player player : lobby.getPlayers()) {
            ResourceCard[] hand = new ResourceCard[3];
            hand[0] = (ResourceCard) resourceDeck.draw();
            hand[1] = (ResourceCard) resourceDeck.draw();
            hand[2] = (ResourceCard) goldDeck.draw();
            player.setHand(hand);
        }

    }

    /**
     * Creates a new instance from a saved GameMaster on file.
     * 
     * @param path path of the file to load.
     * @return the loaded GameMaster.
     * @throws IOException            if the file is not found or can't be read.
     * @throws ClassNotFoundException if the class is not found.
     */
    public static GameMaster tryLoadingGameMaster(String path) throws IOException, ClassNotFoundException {
        FileInputStream saveFile = new FileInputStream(path);
        ObjectInputStream save = new ObjectInputStream(saveFile);

        GameMaster gameMaster = (GameMaster) save.readObject();

        save.close();
        saveFile.close();

        return gameMaster;
    }

    /**
     * This method is used to delete a file from the file system.
     * It takes as input the path of the file to be deleted.
     * If the file exists and is successfully deleted, no exception is thrown.
     * If the file does not exist or cannot be deleted due to a system error, no exception is thrown.
     *
     * @param savePath A string representing the path of the file to be deleted.
     */
    public static void cancelFile(String savePath) {
        File file = new File(savePath);
        if(file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * First turn cycle in which every player decides in which side place its
     * StartingCard
     *
     * @param namePlayer player who sent the request
     * @param side       which side the StartingCard has been want placed
     * @throws NoTurnException         if the player is not the current player
     * @throws WrongGamePhaseException if the game is not in the right phase
     * @throws NoNameException         if the player name is not found
     *
     * @return the id of the card placed
     */
    public int placeRootCard(String namePlayer, boolean side)
            throws NoTurnException, WrongGamePhaseException, NoNameException {
        Player currentPlayer = getCurrentPlayer();

        if (!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new NoTurnException();
        } else if (gameState != GameState.CHOOSING_ROOT_CARD) {
            throw new WrongGamePhaseException();
        } else {
            // first card, all the corners are not linked to any card
            HashMap<Corner, PlayedCard> defaultAttachments = new HashMap<>();
            for (Corner corner : Corner.values()) {
                defaultAttachments.put(corner, null);
            }
            StartingCard rootCard = startingCardToPosition[getOrderPlayer(currentPlayer.getName())];
            PlayedCard rootCardPlaced = new PlayedCard(rootCard, defaultAttachments, side, 0, new Point(0, 0));
            currentPlayer.setRootCard(rootCardPlaced);

            if (!side) {
                // add bonus resources
                for (Sign sign : rootCard.getBonusResources()) {
                    currentPlayer.addResource(sign, 1);
                }
                // add back side corners
                for (Corner corner : Corner.values()) {
                    currentPlayer.addResource(rootCard.getBacksideCorners().get(corner), 1);
                }
            } else {
                // if the card is placed on the top side, the player gets the resources of the
                // corner. Each corner has a resource
                for (Corner corner : Corner.values()) {
                    currentPlayer.addResource(rootCard.getCorners().get(corner), 1);
                }
            }

            nextGlobalTurn();
            if (getOrderPlayer(getCurrentPlayer().getName()) == 0) {
                gameState = GameState.CHOOSING_OBJECTIVE_CARD;
            }
            return rootCard.getId();
        }
    }

    /**
     * Second turn cycle in which every player decides which of the two
     * ObjectiveCard pick
     *
     * @param namePlayer player who sent the request
     * @param whichCard  which of the two ObjectiveCard wants to be used (0, 1)
     *
     *  @throws NoTurnException         if the player is not the current player
     *  @throws WrongGamePhaseException if the game is not in the right phase
     *  @throws NoNameException         if the player name is not found
     */
    public void chooseObjectiveCard(String namePlayer, int whichCard)
            throws NoTurnException, WrongGamePhaseException, NoNameException {
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
     * Let the Player capsule in a PlacedCard connected to the rootCard graph of the
     * Player
     *
     * @param namePlayer Player who sent the request
     * @param index      Which card wants to place
     * @param position   In which position of the table the player wants to be
     *                   place the card
     * @param side       To which side wants the player to place the card
     *
     * @throws NoTurnException         if the player is not the current player
     * @throws WrongGamePhaseException if the game is not in the right phase
     * @throws NotEnoughResourcesException if the player doesn't have enough resources
     * @throws CardPositionException if the card can't be placed in the position
     * @throws NoNameException         if the player name is not found
     *
     * @return the id of the card placed
     */
    public int placeCard(String namePlayer, int index, Point position, boolean side)
            throws NoTurnException, WrongGamePhaseException,
            NotEnoughResourcesException, CardPositionException, NoNameException {

        // manage all possible exceptions
        Player currentPlayer = getCurrentPlayer();
        if (!isCurrentPlayer(namePlayer, currentPlayer)) {
            throw new NoTurnException();
        }
        if (gameState != GameState.PLACING_PHASE) {
            throw new WrongGamePhaseException();
        }

        // check if the card can be placed in the position
        ResourceCard cardToPlace = currentPlayer.getHand()[index];

        HashMap<Corner, PlayedCard> attachments = isPositionable(currentPlayer.getRootCard(), position);
        // the player positions the card in the back front. The card is one resource and
        // 4 empty corners.
        if (!side) {
            new PlayedCard(cardToPlace, attachments, side, getTurn(), position);
            currentPlayer.addResource(fromKingdomToSign(cardToPlace.getKingdom()), 1);
        } else {
            if (cardToPlace instanceof SpecialGoldCard) {
                GoldCard goldCard = (SpecialGoldCard) cardToPlace;
                if (!requirementsSatisfied(currentPlayer, goldCard)) {
                    throw new NotEnoughResourcesException();
                }
            } else if (cardToPlace instanceof GoldCard) {
                GoldCard goldCard = (GoldCard) cardToPlace;
                if (!requirementsSatisfied(currentPlayer, goldCard)) {
                    throw new NotEnoughResourcesException();
                }
            }
            // the attachments are of the graph of the player who is playing so there isn-t
            // any reference to Player class in the constructor
            new PlayedCard(cardToPlace, attachments, side, getTurn(), position);
            for (Corner corner : Corner.values()) {
                currentPlayer.addResource(cardToPlace.getCorners().get(corner), 1);
            }
        }
        // remove resources from counter
        for (Corner corner : Corner.values()) {
            // if corner points to null doesn't remove any resources, resources are
            // subtracted only if the PlayedCards
            // present in the attachments HashMap are played on their front side or are
            // referencing the StartingCard
            if (attachments.get(corner) != null && attachments.get(corner).isFacingUp()) {
                switch (corner) {
                    case TOP_LEFT: {
                        currentPlayer.removeResources(
                                attachments.get(corner).getCard().getCorners().get(Corner.BOTTOM_RIGHT), 1);
                        break;
                    }
                    case TOP_RIGHT: {
                        currentPlayer.removeResources(
                                attachments.get(corner).getCard().getCorners().get(Corner.BOTTOM_LEFT), 1);
                        break;
                    }
                    case BOTTOM_LEFT: {
                        currentPlayer.removeResources(
                                attachments.get(corner).getCard().getCorners().get(Corner.TOP_RIGHT), 1);
                        break;
                    }
                    case BOTTOM_RIGHT: {
                        currentPlayer.removeResources(
                                attachments.get(corner).getCard().getCorners().get(Corner.TOP_LEFT), 1);
                        break;
                    }
                }
            } else if (attachments.get(corner) != null && attachments.get(corner).getCard() instanceof StartingCard) {
                switch (corner) {
                    case TOP_LEFT: {
                        currentPlayer.removeResources(
                                ((StartingCard) attachments.get(corner).getCard()).getBacksideCorners()
                                        .get(Corner.BOTTOM_RIGHT),
                                1);
                        break;
                    }
                    case TOP_RIGHT: {
                        currentPlayer.removeResources(
                                ((StartingCard) attachments.get(corner).getCard()).getBacksideCorners()
                                        .get(Corner.BOTTOM_LEFT),
                                1);
                        break;
                    }
                    case BOTTOM_LEFT: {
                        currentPlayer.removeResources(
                                ((StartingCard) attachments.get(corner).getCard()).getBacksideCorners()
                                        .get(Corner.TOP_RIGHT),
                                1);
                        break;
                    }
                    case BOTTOM_RIGHT: {
                        currentPlayer.removeResources(
                                ((StartingCard) attachments.get(corner).getCard()).getBacksideCorners()
                                        .get(Corner.TOP_LEFT),
                                1);
                        break;
                    }
                }
            }
        }

        // At the end because I need to know resources values at the end and how many
        // attachments when I've found them
        if (side) {
            if (cardToPlace instanceof SpecialGoldCard) {
                SpecialGoldCard specialGoldCard = (SpecialGoldCard) cardToPlace;
                currentPlayer.addPoints(calculatesSpecialGoldPoints(currentPlayer, specialGoldCard, attachments));
            } else {
                currentPlayer.addPoints(cardToPlace.getPoints());
            }
        }

        currentPlayer.giveCard(cardToPlace);

        // if card are not finished, we check in draw phase if is last/second last turn
        if (areTheCardFinished()) {
            // no need to check for GameFlow: drawPhase check if all card are finished. If
            // so, change the phase to SECOND_LAST_TURN.
            if (turnType == TurnType.SECOND_LAST_TURN
                    && getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
                // if it is the last player in second-last turn cycle, say the next is the last
                // turn
                turnType = TurnType.LAST_TURN;
                // remains in placing phase if and only if the card are finished
                gameState = GameState.PLACING_PHASE;
            } else if (turnType == TurnType.LAST_TURN
                    && getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
                // if it is the last player in last turn cycle, go to end mode
                gameState = GameState.END;
                endGame(); // game transitions into the calculating phase
            }
            // only when the card are finished and the game is in the final phase
            // normally, the turn is updated in draw Phase,but since areTheCardFinished() is
            // true we need to update it here.
            nextGlobalTurn();
        } else {
            gameState = GameState.DRAWING_PHASE;
        }

        return cardToPlace.getId();
    }

    /**
     * Allows the Player to draw a card from the table or decks (there are 6
     * different possibilities based on goldOrNot and onTableOrDeck)
     *
     * @param namePlayer   Player who sent the request
     * @param Gold         If the type of the resourceCard that wants to be drawn is
     *                     gold or not
     * @param CardPosition If the card is taken from the table or not: -1 means from
     *                     deck, 0 and 1 are the position onTable array
     *
     * @throws WrongGamePhaseException if the game is not in the right phase
     * @throws NoTurnException         if the player is not the current player
     * @throws NoNameException         if the player name is not found
     * @throws CardPositionException   if the card can't be drawn from the position
     *
     * @return the id of the card drawn
     */
    public int drawCard(String namePlayer, boolean Gold, int CardPosition)
            throws WrongGamePhaseException, NoTurnException, NoNameException, CardPositionException {
        // CardPosition has 0, 1 for position of array of cards on table and -1 for
        // drawing from deck
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
                try {
                    cardDrawn = (ResourceCard) goldDeck.draw();
                } catch (IndexOutOfBoundsException e) {
                    throw new CardPositionException();
                }
                currentPlayer.takeCard(cardDrawn);
            } else {
                cardDrawn = onTableGoldCards[CardPosition];
                // if the card is not present in the table, it throws an exception
                if (cardDrawn == null) {
                    throw new CardPositionException();
                }
                // remove the card from the model
                currentPlayer.takeCard(cardDrawn);
                // update the card on table
                try {
                    onTableGoldCards[CardPosition] = (GoldCard) goldDeck.draw();
                } catch (IndexOutOfBoundsException e) {
                    onTableGoldCards[CardPosition] = null;
                }
            }
        } else {
            if (CardPosition == -1) {
                try {
                    cardDrawn = (ResourceCard) resourceDeck.draw();
                } catch (IndexOutOfBoundsException e) {
                    throw new CardPositionException();
                }
                currentPlayer.takeCard(cardDrawn);
            } else {
                cardDrawn = onTableResourceCards[CardPosition];
                if (cardDrawn == null) {
                    throw new CardPositionException();
                }
                currentPlayer.takeCard(cardDrawn);
                try {
                    onTableResourceCards[CardPosition] = (ResourceCard) resourceDeck.draw();
                } catch (IndexOutOfBoundsException e) {
                    onTableResourceCards[CardPosition] = null;
                }
            }
        }

        gameState = GameState.PLACING_PHASE;

        // next player will play?
        if (turnType == TurnType.PLAYING && (currentPlayer.getPoints() >= 20 || areTheCardFinished())) {
            if (getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
                turnType = TurnType.LAST_TURN;
            } else {
                turnType = TurnType.SECOND_LAST_TURN;
            }
        } else if (turnType != TurnType.PLAYING
                && getOrderPlayer(currentPlayer.getName()) + 1 == lobby.getPlayers().length) {
            if (turnType == TurnType.SECOND_LAST_TURN) {
                turnType = TurnType.LAST_TURN;
            } else if (turnType == TurnType.LAST_TURN) {
                gameState = GameState.END;
                endGame();
            }
        }

        nextGlobalTurn();

        return cardDrawn.getId();
    }

    /**
     * Represents the last part of the game, in which the points gained
     * from fulfilling the objectives are calculated.
     *
     * @throws WrongGamePhaseException if the game is not in the right phase
     */
    public void endGame() throws WrongGamePhaseException {
        if (gameState != GameState.END) {
            throw new WrongGamePhaseException();
        } else {
            for (Player player : lobby.getPlayers()) {
                ObjectiveCard secret = player.getSecretObjective();
                // secret objective is calculated first
                int newPoints = calculateEndGamePoints(secret.getType(), secret.getMultiplier(), player,
                        secret.getKingdom());
                player.addObjectivePoints(newPoints);
                for (ObjectiveCard card : onTableObjectiveCards) {
                    newPoints = calculateEndGamePoints(card.getType(), card.getMultiplier(), player, card.getKingdom());
                    player.addObjectivePoints(newPoints);
                }
                // add player to ranking after insert all objective points
                ranking.add(player);
            }

            // Anonymous function to sort the ranking according to rules of the game
            // this function does not return anything. The controller will ask for the
            // ranking
            ranking.sort(new Comparator<Player>() {
                @Override
                public int compare(Player p1, Player p2) {
                    // total score of a player: normal points during the game and objective points
                    int sum1 = p1.getPoints() + p1.getObjectivePoints();
                    int sum2 = p2.getPoints() + p2.getObjectivePoints();
                    if (sum1 == sum2) {
                        // if the points are equal, the player with the most objective points wins
                       return Integer.compare(p2.getObjectivePoints(), p1.getObjectivePoints());

                    } else if (sum1 > sum2) {
                        // sum is insert before than player 2
                        return -1;
                    } else {
                        // sum is insert after than player 2
                        return 1;
                    }
                }
            });
        }
    }

    // Finding methods
    /**
     * Given a position it gives attachments to the card, the Corner keys are
     * referred to the Corner of the new card
     * Notation is x and y based on cartesian axes system rotated of 45 degrees
     * counterclockwise, every card represents a dot with natural coordinates
     * Example: starting card is always 0,0 so TOP_LEFT would be 0;1, TOP_RIGHT
     *
     * @param startingCard A card where the recursion will start to find the
     *                     required PlayedCard
     * @param position     Position that identifies where the next card should be
     *                     placed
     * @return Hashmap<Corner, PlayedCard> of the attachments for the card to
     *         cardToPlace
     */
    private HashMap<Corner, PlayedCard> isPositionable(PlayedCard startingCard, Point position)
            throws CardPositionException {
        HashMap<Corner, PlayedCard> attachments = new HashMap<>();
        PlayedCard cardToCheck, newCard;
        boolean validPosition = false;
        int xPlaceToCheck = 0, yPlaceToCheck = 0;

        // For each corner of the card to be placed, it checks if there is a possible
        // card to attach it. The switch case refers to the new Card.

        newCard = findCard(startingCard, position);
        if (newCard != null) {
            throw new CardPositionException();
        }

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
                /*
                 * Checks if the corner is valid:
                 * if the card is facing up, it checks the corner of the card to be placed
                 * if the card is facing down, it checks the backside corner of the card to be
                 * placed(only for starting card).
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
                    // it's a starting card upside down
                    switch (corner) {
                        case TOP_LEFT: {
                            cornerToCheck = ((StartingCard) cardToCheck.getCard()).getBacksideCorners()
                                    .get(Corner.BOTTOM_RIGHT);
                            break;
                        }
                        case TOP_RIGHT: {
                            cornerToCheck = ((StartingCard) cardToCheck.getCard()).getBacksideCorners()
                                    .get(Corner.BOTTOM_LEFT);
                            break;
                        }
                        case BOTTOM_LEFT: {
                            cornerToCheck = ((StartingCard) cardToCheck.getCard()).getBacksideCorners()
                                    .get(Corner.TOP_RIGHT);
                            break;
                        }
                        case BOTTOM_RIGHT: {
                            cornerToCheck = ((StartingCard) cardToCheck.getCard()).getBacksideCorners()
                                    .get(Corner.TOP_LEFT);
                            break;
                        }
                    }
                } else {
                    cornerToCheck = Sign.EMPTY;
                }
                // It verifies if the corner of the card to be placed is compatible with the
                // corner of the card to which it is attached
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
     * Looking at all graph of PlayedCard to find a PlayedCard identified by
     * position
     *
     * @param startingCard Where the recursion will start to find the required
     *                     PlayedCard
     * @param position     position that identifies where the next card should be
     * @return method recursiveFindCard
     */
    private PlayedCard findCard(PlayedCard startingCard, Point position) {
        Stack<PlayedCard> stack = new Stack<>();
        return recursiveFindCard(startingCard, position, stack);
    }

    /**
     * Called by findCard
     * Recursive looking at all graph of PlayedCard to find a PlayedCard identified
     * by position
     *
     * @param playedCard PlayedCard that I'm visiting
     * @param position   Position of the card that I'm looking for
     * @param stack      Stack in which I save already visited cards
     * @return PlayedCard if exists else null
     */
    private PlayedCard recursiveFindCard(PlayedCard playedCard, Point position, Stack<PlayedCard> stack) {
        if (playedCard == null) {
            return null;
        } else if (stack.contains(playedCard)) {
            return null;
        } else if (playedCard.getPosition().x == position.x && playedCard.getPosition().y == position.y) {
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
     *
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
     *
     * @throws IllegalArgumentException if the Countable is not a valid Countable
     */
    public Sign fromCountableToSign(Countable countable) throws IllegalArgumentException {
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
     * Controls if the resources of a Player are enough for the requirements of a
     * GoldCard
     *
     * @param player   Player about we want to know if they can place the GoldCard
     * @param goldCard GoldCard that wants to be placed and has certain requirements
     * @return true if the player has enough resources to place the GoldCard, false
     *         otherwise
     */
    public boolean requirementsSatisfied(Player player, GoldCard goldCard) {
        for (Sign sign : Sign.values()) {
            if (sign != Sign.EMPTY && sign != Sign.NULL) {
                if (player.getResources().get(sign) < goldCard.getRequirements().get(sign)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Calculate points methods

    /**
     * Calculate the number of points given by the effect of SpecialGoldCard that is
     * positioned
     *
     * @param player          who is trying to position the card to see their
     *                        resource count
     * @param specialGoldCard specialGoldCard given to find the effect
     * @param attachments     other card corners used to calculate the corners
     *                        covered for Countable corner type
     * @return number of points to add to the player points
     */
    private int calculatesSpecialGoldPoints(Player player, SpecialGoldCard specialGoldCard,
            HashMap<Corner, PlayedCard> attachments) {
        if (specialGoldCard.getThingToCount() == Countable.CORNER) {
            int numberOfAttachments = 0;
            for (PlayedCard playedCard : attachments.values()) {
                if (playedCard != null) {
                    numberOfAttachments++;
                }
            }
            return specialGoldCard.getPoints() * numberOfAttachments;
        } else
            return specialGoldCard.getPoints()
                    * player.getResources().get(fromCountableToSign(specialGoldCard.getThingToCount()));

    }

    /**
     * Convert the graph of PlayedCard of a player in a list of PlayedCard
     *
     * @param player player to explore
     * @return list of played cards
     */
    public ArrayList<PlayedCard> getPlayersCards(Player player) {
        PlayedCard rootCard = player.getRootCard();
        return exploreGraph(rootCard);
    }

    /**
     * Explore the graph of played cards.
     *
     * @param startingCard starting card of the player
     * @return list of played cards
     */
    private ArrayList<PlayedCard> exploreGraph(PlayedCard startingCard) {
        ArrayList<PlayedCard> cards = new ArrayList<>();
        Stack<PlayedCard> stack = new Stack<>();
        stack.push(startingCard);
        while (!stack.isEmpty()) {
            PlayedCard card = stack.pop();
            cards.add(card);
            for (Corner corner : Corner.values()) {
                PlayedCard attached = card.getAttached(corner);
                // if the attachment is not null and the card is not yet in the list, it is
                // added to the stack
                // remember that cards are double linked, so we need to check if the card is
                // already in the list
                if (attached != null && !cards.contains(attached)) {
                    stack.push(attached);
                }
            }
        }
        // Sort the cards by ascending x + y position
        Collections.sort(cards, Comparator.comparing((PlayedCard card) -> card.getPosition().x + card.getPosition().y));
        return cards;
    }

    // Turn methods
    /**
     * Pass to the next turn consequentially switching player
     */
    private void nextGlobalTurn() {
        this.globalTurn++;
    }

    /**
     * Calculates the number of cycles made by players, the cycle of setup are not
     * counted
     *
     * @return the cycle of turns made in total
     */
    public int getTurn() {
        return globalTurn / lobby.getPlayers().length - 1;
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
     * @throws NoNameException if the player name is not found
     *
     * @return get order of player
     */
    public int getOrderPlayer(String name) throws NoNameException {
        int i;
        for (i = 0; i < lobby.getPlayers().length; i++) {
            if (lobby.getPlayers()[i].getName().equals(name)) {
                return i;
            }
        }
        throw new NoNameException();
    }

    /**
     * Compares the name of the player who sent the request and who is the turn
     * right now and says if it's their turn
     *
     * @param name          player who sent the request
     * @param currentPlayer the player who is the turn right now
     * @return true if it's the turn of the player who sent the request
     */
    private boolean isCurrentPlayer(String name, Player currentPlayer) {
        return name.equals(currentPlayer.getName());
    }

    /**
     * Place card on the spot on table
     *
     * @param resourceCard ResourceCard to position on table
     * @param place        id of the spot where to place, 0 and 1 are the position
     *                     onTable array
     */
    public void setOnTableResourceCard(ResourceCard resourceCard, int place) {
        this.onTableResourceCards[place] = resourceCard;
    }

    /**
     * Place card on the spot on table
     *
     * @param goldCard GoldCard to position on table
     * @param place    id of the spot where to place, 0 and 1 are the position
     *                 onTable array
     */
    public void setOnTableGoldCard(GoldCard goldCard, int place) {
        this.onTableGoldCards[place] = goldCard;
    }

    /**
     * Place card on the spot on table (It's usable only in GameSetup constructor)
     *
     * @param objectiveCard ObjectiveCard to position on table
     * @param place         id of the spot where to place, 0 and 1 are the position
     *                      onTable array
     */
    public void setOnTableObjectiveCards(ObjectiveCard objectiveCard, int place) {
        this.onTableObjectiveCards[place] = objectiveCard;
    }

    // Info methods for the view

    /**
     * Request info about the points of a certain player
     *
     * @param namePlayer name of the player about is wanted to get info
     *
     * @throws NoNameException if the player name is not found
     * @return points of the player
     */
    public int getPlayerPoints(String namePlayer) throws NoNameException {
        return lobby.getPlayerFromName(namePlayer).getPoints();
    }

    /**
     * Request info about the resources of a certain player
     *
     * @param namePlayer name of the player about is wanted to get info
     * @throws NoNameException if the player name is not found
     * @return resources of the player
     */
    public HashMap<Sign, Integer> getPlayerResources(String namePlayer) throws NoNameException {
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

    /**
     * It returns if the cards on the table and in the deck are finished
     *
     * @return true if the cards are finished, false otherwise
     */
    private boolean areTheCardFinished() {
        return getKingdomNextCardResourceDeck() == null &&
                getKingdomNextCardGoldDeck() == null &&
                getResourceCardOnTable(0) == null && getResourceCardOnTable(1) == null &&
                getGoldCardOnTable(0) == null && getGoldCardOnTable(1) == null;
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
        } catch (IndexOutOfBoundsException e) {
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
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Retrieves the next gold card from the deck.
     * This method is used only for testing purposes and should not be used during
     * normal gameplay.
     *
     * @return The next gold card from the deck.
     */
    public Card getGoldCardDeckTestOnly() {
        return goldDeck.draw();
    }

    /**
     * Retrieves the next resource card from the deck.
     * This method is used only for testing purposes and should not be used during
     * normal gameplay.
     *
     * @return The next gold card from the deck.
     */
    public Card getResourceCardDeckTestOnly() {
        return resourceDeck.draw();
    }

    /**
     * Retrieves the current state of the game.
     *
     * This method is used to check the current phase of the game, which can be
     * useful for determining
     * the valid actions that can be performed at any given time.
     *
     * @return The current state of the game as a GameState enum.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Retrieves the card from the table or deck based on the provided parameters.
     *
     * @param gold          Indicates if the card to be retrieved is a gold card. If
     *                      true, a gold card is retrieved, otherwise a resource
     *                      card is retrieved.
     * @param onTableOrDeck Specifies the location of the card. If it's a number
     *                      between 0 and 1, it refers to the position on the table.
     *                      If it's -1, the card is drawn from the deck.
     * @return The ID of the retrieved card. If the specified card does not exist,
     *         null is returned.
     * @throws NullPointerException If the specified card position does not exist.
     */
    public Integer getCard(boolean gold, int onTableOrDeck) {
        if (gold) {
            try {
                return onTableGoldCards[onTableOrDeck].getId();
            } catch (NullPointerException e) {
                return null;
            }
        } else {
            try {
                return onTableResourceCards[onTableOrDeck].getId();
            } catch (NullPointerException e) {
                return null;
            }
        }
    }

    /**
     * Retrieves an objective card from the table in the game.
     *
     * This method is used to retrieve an objective card from the table in the game.
     *
     * @param i The index of the objective card on the table.
     * @return The objective card at the specified index on the table.
     */
    public Card getObjectiveCard(int i) {
        return onTableObjectiveCards[i];
    }

    /**
     * Retrieves an objective card that a player can choose.
     *
     * This method is used to retrieve an objective card from the set of cards that
     * a player can choose from.
     *
     * @param i The index of the player in the game.
     * @param j The index of the objective card in the player's set of choosable
     *          cards.
     * @return The objective card at the specified indices.
     */
    public ObjectiveCard getObjectiveCardToChoose(int i, int j) {
        return objectiveCardToChoose[i][j];
    }

    /**
     * Retrieves the kingdom of the first card in the specified deck.
     *
     * This method is used to peek at the first card in the deck without drawing it.
     * It can be used to plan ahead and make strategic decisions based on the
     * upcoming card.
     *
     * @param gold If true, the method checks the gold deck. If false, it checks the
     *             resource deck.
     * @return The kingdom of the first card in the specified deck. If the deck is
     *         empty, returns null.
     * @throws IndexOutOfBoundsException If the specified deck is empty.
     */
    public Kingdom getHeadDeck(boolean gold) {
        try {
            if (gold) {
                return goldDeck.getKingdomFirstCard();
            } else {
                return resourceDeck.getKingdomFirstCard();
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Retrieves the resource card at the specified position on the table.
     *
     * This method is used to retrieve a specific resource card from the table in
     * the game.
     * The position is based on the order in which the cards are placed on the
     * table.
     *
     * @param position The position of the resource card on the table.
     * @return The resource card at the specified position. If the position is
     *         invalid, null is returned.
     */
    public Card getResourceCard(int position) {
        return onTableResourceCards[position];
    }

    /**
     * Retrieves the gold card at the specified position on the table.
     *
     * This method is used to retrieve a specific gold card from the table in the
     * game.
     * The position is based on the order in which the cards are placed on the
     * table.
     *
     * @param position The position of the gold card on the table.
     * @return The gold card at the specified position. If the position is invalid,
     *         null is returned.
     */
    public Card getGoldCard(int position) {
        return onTableGoldCards[position];
    }

    /**
     * Retrieves the starting card assigned to a player.
     *
     * This method is used to retrieve the starting card that has been assigned to a
     * player based on their nickname.
     *
     * @param nickname The nickname of the player.
     * @return The starting card assigned to the player.
     * @throws NoNameException If the player with the given nickname does not exist.
     */
    public StartingCard getStartingCardToPosition(String nickname) throws NoNameException {
        return startingCardToPosition[getOrderPlayer(nickname)];
    }

    /**
     * Retrieves the current turn type of the game.
     *
     * This method is used to check the current turn type of the game, which can be
     * useful for determining
     * the valid actions that can be performed at any given time.
     *
     * @return The current turn type of the game as a TurnType enum.
     */
    public TurnType getTurnType() {
        return turnType;
    }

    /**
     * Calculate the number of points given by an objective card.
     *
     * @param type       type of the objective card.
     * @param multiplier multiplier of the objective card.
     * @param player     player who is getting targeted.
     * @param kingdom    kingdom of the objective card.
     * @return number of points to add to the player points
     */
    public int calculateEndGamePoints(ObjectiveType type, int multiplier, Player player, Kingdom kingdom) {
        int points = 0;

        switch (type) {
            case TWO_QUILLS:
                points = player.getResources().get(Sign.QUILL) / 2;
                break;
            case TWO_INKS:
                points = player.getResources().get(Sign.INKWELL) / 2;
                break;
            case TWO_SCROLLS:
                points = player.getResources().get(Sign.SCROLL) / 2;
                break;
            case FREE_RESOURCES:
                points = Math.min(player.getResources().get(Sign.QUILL),
                        Math.min(player.getResources().get(Sign.INKWELL), player.getResources().get(Sign.SCROLL)));
                break;
            case TRIS:
                points = player.getResources().get(fromKingdomToSign(kingdom)) / 3;
                break;
            case L_FORMATION: {
                ArrayList<PlayedCard> usedCards = new ArrayList<>();
                switch (kingdom) {
                    case FUNGI:
                        // i start from top card
                        for (PlayedCard card : getPlayersCards(player)) {
                            if (!usedCards.contains(card) && card.getCard().getKingdom() == Kingdom.FUNGI) {
                                // the top card
                                Point position = card.getPosition();

                                Point lowerPosition = (Point) position.clone();
                                lowerPosition.translate(-1, -1);
                                PlayedCard lower = findCard(player.getRootCard(), lowerPosition);

                                // the card below on the right, linked to lower
                                PlayedCard lowerRight;
                                try {
                                    lowerRight = lower.getAttached(Corner.BOTTOM_RIGHT);
                                } catch (NullPointerException e) {
                                    lowerRight = null;
                                }

                                if (lower != null && lowerRight != null &&
                                        !usedCards.contains(lower) && !usedCards.contains(lowerRight)
                                        && lower.getCard().getKingdom() == Kingdom.FUNGI
                                        && lowerRight.getCard().getKingdom() == Kingdom.PLANT) {
                                    points++;
                                    usedCards.add(card);
                                    usedCards.add(lowerRight);
                                    usedCards.add(lower);
                                }
                            }
                        }
                        break;
                    case ANIMAL:
                        // i start from the below card
                        for (PlayedCard card : getPlayersCards(player)) {
                            if (!usedCards.contains(card) && card.getCard().getKingdom() == Kingdom.ANIMAL) {
                                // take the card on top
                                Point position = card.getPosition();
                                Point topPosition = (Point) position.clone();
                                topPosition.translate(+1, +1);
                                PlayedCard top = findCard(player.getRootCard(), topPosition);
                                // take the card on top right
                                PlayedCard topRight;
                                try {
                                    topRight = top.getAttached(Corner.TOP_RIGHT);
                                } catch (NullPointerException e) {
                                    topRight = null;
                                }

                                if (top != null && topRight != null &&
                                        !usedCards.contains(top) && !usedCards.contains(topRight)
                                        && top.getCard().getKingdom() == Kingdom.ANIMAL
                                        && topRight.getCard().getKingdom() == Kingdom.FUNGI) {
                                    points++;
                                    usedCards.add(card);
                                    usedCards.add(topRight);
                                    usedCards.add(top);
                                }
                            }
                        }
                        break;
                    case PLANT:
                        // i start from the card on top
                        for (PlayedCard card : getPlayersCards(player)) {
                            if (!usedCards.contains(card) && card.getCard().getKingdom() == Kingdom.PLANT) {
                                // take the card on top
                                Point position = card.getPosition();
                                Point belowPosition = (Point) position.clone();
                                belowPosition.translate(-1, -1);
                                PlayedCard below = findCard(player.getRootCard(), belowPosition);
                                // take the card on top right
                                PlayedCard belowLeft;
                                try {
                                    belowLeft = below.getAttached(Corner.BOTTOM_LEFT);
                                } catch (NullPointerException e) {
                                    belowLeft = null;
                                }

                                if (below != null && belowLeft != null &&
                                        !usedCards.contains(below) && !usedCards.contains(belowLeft)
                                        && below.getCard().getKingdom() == Kingdom.PLANT
                                        && belowLeft.getCard().getKingdom() == Kingdom.INSECT) {
                                    points++;
                                    usedCards.add(card);
                                    usedCards.add(belowLeft);
                                    usedCards.add(below);
                                }
                            }
                        }
                        break;
                    case INSECT:
                        // i start from the below card
                        for (PlayedCard card : getPlayersCards(player)) {
                            if (!usedCards.contains(card) && card.getCard().getKingdom() == Kingdom.INSECT) {
                                // take the card on top
                                Point position = card.getPosition();
                                Point topPosition = (Point) position.clone();
                                topPosition.translate(+1, +1);
                                PlayedCard top = findCard(player.getRootCard(), topPosition);

                                // take the card on top right
                                PlayedCard topLeft;
                                try {
                                    topLeft = top.getAttached(Corner.TOP_LEFT);
                                } catch (NullPointerException e) {
                                    topLeft = null;
                                }

                                if (top != null && topLeft != null &&
                                        !usedCards.contains(top) && !usedCards.contains(topLeft)
                                        && top.getCard().getKingdom() == Kingdom.INSECT
                                        && topLeft.getCard().getKingdom() == Kingdom.ANIMAL) {
                                    points++;
                                    usedCards.add(card);
                                    usedCards.add(topLeft);
                                    usedCards.add(top);
                                }
                            }
                        }
                        break;
                }// last parenthesis for kingdom switch
            }
                break;
            case STAIR: {
                // I put the card used to give points.
                ArrayList<PlayedCard> usedCards = new ArrayList<>();
                switch (kingdom) {
                    // stair formation top right: crescente: I start from the bottom one
                    case FUNGI:
                    case ANIMAL:
                        for (PlayedCard card : getPlayersCards(player)) {
                            if (card.getCard().getKingdom() == kingdom && !usedCards.contains(card)) {
                                PlayedCard firstUpper = card.getAttached(Corner.TOP_RIGHT);

                                // if there is no card attached to the first one, I have a null pointer
                                // exception
                                PlayedCard secondUpper;
                                try {
                                    secondUpper = firstUpper.getAttached(Corner.TOP_RIGHT);
                                } catch (NullPointerException e) {
                                    secondUpper = null;
                                }

                                // controll that are not null and that the cards are not already used and that
                                // the cards are of the right kingdom
                                if (firstUpper != null && secondUpper != null &&
                                        !usedCards.contains(firstUpper) && !usedCards.contains(secondUpper)
                                        && firstUpper.getCard().getKingdom() == kingdom
                                        && secondUpper.getCard().getKingdom() == kingdom) {
                                    points++;
                                    usedCards.add(card);
                                    usedCards.add(firstUpper);
                                    usedCards.add(secondUpper);
                                }
                            }
                        }
                        break;
                    // stair formation bottom right: decrescente: I start from the top one
                    case PLANT:
                    case INSECT:
                        for (PlayedCard card : getPlayersCards(player)) {
                            if (card.getCard().getKingdom() == kingdom && !usedCards.contains(card)) {
                                PlayedCard firstDown = card.getAttached(Corner.BOTTOM_RIGHT);

                                // if there is no card attached to the first one, I have a null pointer
                                // exception
                                PlayedCard secondDown;
                                try {
                                    secondDown = firstDown.getAttached(Corner.BOTTOM_RIGHT);
                                } catch (NullPointerException e) {
                                    secondDown = null;
                                }

                                // control that are not null and that the cards are not already used and that
                                // the cards are of the right kingdom
                                if (firstDown != null && secondDown != null &&
                                        !usedCards.contains(firstDown) && !usedCards.contains(secondDown)
                                        && firstDown.getCard().getKingdom() == kingdom
                                        && secondDown.getCard().getKingdom() == kingdom) {
                                    points++;
                                    usedCards.add(card);
                                    usedCards.add(firstDown);
                                    usedCards.add(secondDown);
                                }
                            }
                        }
                        break;
                }
            }
                break;
        }

        return points * multiplier;
    }

    /**
     * Retrieves the lobby of players in the game.
     *
     * This method is used to access the lobby of players in the game, which contains
     * information about all the players currently participating in the game.
     *
     * @return The lobby of players in the game.
     */
    public Lobby getLobby() {
        return lobby;
    }
}
