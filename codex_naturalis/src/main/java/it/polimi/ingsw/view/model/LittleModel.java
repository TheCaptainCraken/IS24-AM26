package it.polimi.ingsw.view.model;

import it.polimi.ingsw.model.*;

import it.polimi.ingsw.model.Color;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.*;

/**
 * The LittleModel class is responsible for maintaining a simplified version of
 * the game model in server.
 * It stores information about the game such as points, resources, cards, and
 * other game details.
 * The class provides methods to update and retrieve this information.
 *
 * The LittleModel class uses several HashMaps to store information about
 * points, resources, and cards.
 * It also uses arrays to store information about the player's cards and the
 * common table.
 *
 * The LittleModel class uses a Deck object for each type of card deck in the
 * game.
 * These decks are used to retrieve card information based on card IDs.
 *
 * The LittleModel class provides methods to update the game state, such as
 * updating the player's hand,
 * updating the card on the table, updating the score, updating the resources,
 * and updating the common table.
 * It also provides methods to retrieve information about the game state, such
 * as getting the player's hand,
 * getting the hidden hand of other players, getting the points, getting the
 * resources, and getting the cards on the common table.
 *
 */
public class LittleModel {
    /**
     * The points HashMap stores the points of each player in the game.
     */
    private HashMap<String, Integer> points;
    /**
     * The resources HashMap stores the resources of each player in the game.
     */
    private HashMap<String, HashMap<Sign, Integer>> resources;
    /**
     * The otherPlayersCards HashMap stores the cards of other players in the game.
     */
    private HashMap<String, Pair<Kingdom, Boolean>[]> otherPlayersCards;
    /**
     * The myCards array stores the cards of the player.
     */
    private Integer[] myCards;
    /**
     * The table HashMap stores the starting card of each player on the table.
     */
    private HashMap<String, CardClient> table;
    /**
     * The cardsToPrint ArrayList stores the cards to print.
     */
    private static ArrayList<CardClient> cardsToPrint;
    /**
     * The resourceCards array stores the resource cards on the table.
     */
    private Integer[] resourceCards;
    /**
     * The goldCards array stores the gold cards on the table.
     */
    private Integer[] goldCards;
    /**
     * The headDeckGold stores the head deck of gold cards.
     */
    private Kingdom headDeckGold;
    /**
     * The headDeckResource stores the head deck of resource cards.
     */
    private Kingdom headDeckResource;
    /**
     * The secretObjectiveCardsToChoose array stores the secret objective cards to
     * choose from.
     */
    private Integer[] secretObjectiveCardsToChoose;
    /**
     * The commonObjectiveCards array stores the common objective cards.
     */
    private Integer[] commonObjectiveCards;
    /**
     * The secretObjectiveCard stores the secret objective card.
     */
    private Integer secretObjectiveCard;

    /**
     * The startingCardsDeck stores the deck of starting cards.
     */
    private Deck startingCardsDeck;
    /**
     * The objectiveCardsDeck stores the deck of objective cards.
     */
    private Deck objectiveCardsDeck;
    /**
     * The resourceCardsDeck stores the deck of resource cards.
     */
    private Deck resourceCardsDeck;
    /**
     * The goldCardsDeck stores the deck of gold cards.
     */
    private Deck goldCardsDeck;

    String resourcePath = "/decksJSON/resourceCardsDeck.json";
    String goldPath = "/decksJSON/goldCardsDeck.json";
    String startingPath = "/decksJSON/startingCardsDeck.json";
    String objectivePath = "/decksJSON/objectiveCardsDeck.json";

    /**
     * The LittleModel constructor initializes the points, resources, myCards,
     * otherPlayersCards, and table HashMaps.
     * It also initializes the startingCardsDeck, objectiveCardsDeck,
     * resourceCardsDeck, and goldCardsDeck.
     */
    public LittleModel() {
        points = new HashMap<>();
        resources = new HashMap<>();
        myCards = new Integer[3];
        otherPlayersCards = new HashMap<>();
        table = new HashMap<>();

        try{
            startingCardsDeck = new Deck(getClass().getResourceAsStream(startingPath), true);
            objectiveCardsDeck = new Deck(getClass().getResourceAsStream(objectivePath), true);
            resourceCardsDeck = new Deck(getClass().getResourceAsStream(resourcePath), true);
            goldCardsDeck = new Deck(getClass().getResourceAsStream(goldPath), true);
        } catch (IOException e) {
            System.out.println("file for little model not found");
            System.exit(0);
        } catch (ParseException e) {
            System.out.println("Error in parsing file for little model");
            System.exit(0);
        }
    }

    /**
     * The LittleModel constructor initializes the points, resources, myCards,
     * otherPlayersCards, and table HashMaps.
     * It also initializes the startingCardsDeck, objectiveCardsDeck,
     * resourceCardsDeck, and goldCardsDeck.
     */
    public LittleModel(String startingPath, String objectivePath, String resourcePath, String goldPath) {
        points = new HashMap<>();
        resources = new HashMap<>();
        myCards = new Integer[3];
        otherPlayersCards = new HashMap<>();
        table = new HashMap<>();

        try{
            startingCardsDeck = new Deck(startingPath, true);
            objectiveCardsDeck = new Deck(objectivePath, true);
            resourceCardsDeck = new Deck(resourcePath, true);
            goldCardsDeck = new Deck(goldPath, true);
        } catch (IOException e) {
            System.out.println("file for little model not found");
            System.exit(0);
        } catch (ParseException e) {
            System.out.println("Error in parsing file for little model");
            System.exit(0);
        }
    }

    /**
     * Updates the common table in the game.
     *
     * This method is used to update the common table with the new set of resource
     * cards, gold cards, and the head decks for both types of cards.
     * The resource cards and gold cards are represented by Integer arrays where
     * each Integer is the ID of a card.
     * The head decks are represented by Kingdom objects.
     *
     * @param points             A HashMap representing the points of the players
     * @param resources         A HashMap representing the resources of the players
     * @param myCards        An array of Integers representing the IDs of the cards in the
     * @param otherPlayersCards    A Pair representing the cards of others players
     * @param table                A Hashmap representing the starting Card of players
     * @param resourceCards     A Kingdom object representing the head deck of gold
     * @param goldCards     A Kingdom object representing the head deck of gold
     * @param headDeckGold     A Kingdom object representing the head deck of gold
     * @param headDeckResource     A Kingdom object representing the head deck of gold
     * @param secretObjectiveCardsToChoose     A Kingdom object representing the head deck of gold
     * @param commonObjectiveCards     A Kingdom object representing the head deck of gold
     * @param secretObjectiveCard     A Kingdom object representing the head deck of gold
     */
    public LittleModel(HashMap<String, Integer> points, HashMap<String, HashMap<Sign, Integer>> resources,
            Integer[] myCards, HashMap<String, Pair<Kingdom, Boolean>[]> otherPlayersCards,
            HashMap<String, CardClient> table, Integer[] resourceCards, Integer[] goldCards, Kingdom headDeckGold,
            Kingdom headDeckResource, Integer[] secretObjectiveCardsToChoose, Integer[] commonObjectiveCards,
            Integer secretObjectiveCard) {
        this.points = points;
        this.resources = resources;
        this.myCards = myCards;
        this.otherPlayersCards = otherPlayersCards;
        this.table = table;
        this.resourceCards = resourceCards;
        this.goldCards = goldCards;
        this.headDeckGold = headDeckGold;
        this.headDeckResource = headDeckResource;
        this.secretObjectiveCardsToChoose = secretObjectiveCardsToChoose;
        this.commonObjectiveCards = commonObjectiveCards;
        this.secretObjectiveCard = secretObjectiveCard;

        try{
            startingCardsDeck = new Deck(getClass().getResourceAsStream(startingPath), true);
            objectiveCardsDeck = new Deck(getClass().getResourceAsStream(objectivePath), true);
            resourceCardsDeck = new Deck(getClass().getResourceAsStream(resourcePath), true);
            goldCardsDeck = new Deck(getClass().getResourceAsStream(goldPath), true);
        } catch (IOException e) {
            System.out.println("file for little model not found");
            System.exit(0);
        } catch (ParseException e) {
            System.out.println("Error in parsing file for little model");
            System.exit(0);
        }
    }

    /**
     * Updates the player's hand with the given array of card IDs.
     *
     * The cards are represented by their IDs in the form of an Integer array.
     *
     * @param hand An array of Integers representing the IDs of the cards in the
     *             player's hand.
     */
    public void updatePrivateHand(Integer[] hand) {
        myCards = hand;
    }

    /**
     * Returns the player's hand as an array of card IDs.
     *
     * The cards are represented by their IDs in the form of an Integer array.
     *
     * @return An array of Integers representing the IDs of the cards in the
     *         player's hand.
     */
    public Integer[] getHand() {
        return myCards;
    }

    /**
     * This method is used to retrieve a card from the player's hand based on its
     * index.
     * The cards in the hand are represented by an array of Integers, where each
     * Integer is the ID of a card.
     *
     * @param index The index of the card in the player's hand.
     * @return An Integer representing the ID of the card at the given index in the
     *         player's hand.
     */
    public Integer getCardInHand(int index) {
        return myCards[index];
    }

    /**
     * This method is used to set a card in the player's hand at a specific index.
     * The cards in the hand are represented by an array of Integers, where each
     * Integer is the ID of a card.
     *
     * @param index  The index at which to set the card in the player's hand.
     * @param cardId The ID of the card to be set in the player's hand.
     */
    public void setCardInHand(int index, Integer cardId) {
        // set the card at null
        myCards[index] = cardId;
    }

    /**
     * Updates the hidden hand of the specified player.
     *
     * The cards are represented by a Pair array where each Pair consists of a
     * Kingdom and a Boolean.
     *
     * @param nickname The nickname of the player whose hand is to be updated.
     * @param hand     An array of Pairs representing the Kingdom and visibility of
     *                 the cards in the player's hand.
     */
    public void updateHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand) {
        otherPlayersCards.put(nickname, hand);
    }

    /**
     * Returns the hidden hand of the specified player.
     *
     * The cards are represented by a Pair array where each Pair consists of a
     * Kingdom and a Boolean.
     *
     * @param player The nickname of the player whose hand is to be retrieved.
     * @return An array of Pairs representing the Kingdom and visibility of the
     *         cards in the player's hand.
     */
    public Pair<Kingdom, Boolean>[] getHiddenHand(String player) {
        return otherPlayersCards.get(player);
    }

    /**
     * Updates the card on the table.
     *
     * The card is represented by its ID in the form of an Integer.
     *
     * @param newCardId     The ID of the new card.
     * @param gold          A boolean indicating if the card is a gold card.
     * @param onTableOrDeck An integer indicating the position of the card on the
     *                      table or deck.
     */
    public void updateCardOnTable(Integer newCardId, boolean gold, int onTableOrDeck) {
        // if it -1, means that the card is on the deck and not on the table, thus
        // noupdate is needed
        if (onTableOrDeck != -1) {
            if (gold) {
                goldCards[onTableOrDeck] = newCardId;
            } else {
                resourceCards[onTableOrDeck] = newCardId;
            }
        }
    }

    /**
     * Updates the head deck.
     *
     * This method is used to update the head deck with the new Kingdom.
     * The Kingdom is represented by an enumeration.
     *
     * @param headDeck The new Kingdom for the head deck.
     * @param gold     A boolean indicating if the head deck is for gold cards.
     */
    public void updateHeadDeck(Kingdom headDeck, boolean gold) {
        if (gold) {
            headDeckGold = headDeck;
        } else {
            headDeckResource = headDeck;
        }
    }

    /**
     * Updates the score of the specified player.
     *
     * This method is used to update the score of a player with the new score.
     *
     * @param nickname The nickname of the player whose score is to be updated.
     * @param points   The new score for the player.
     */
    public void updateScore(String nickname, int points) {
        this.points.put(nickname, points);
    }

    /**
     * Returns the points of all players in the game.
     *
     * This method is used to retrieve the current points of all players.
     * The points are represented in a HashMap where the key is the player's
     * nickname and the value is the player's points.
     *
     * @return A HashMap representing the nicknames of the players and their
     *         corresponding points.
     */
    public HashMap<String, Integer> getPoints() {
        return points;
    }

    /**
     * Updates the placement of a card for a specific player.
     *
     * This method is used to update the placement of a card on the player's board.
     * The card is identified by its ID, and its position is represented by a Point
     * object.
     * The side of the card and the turn in which the card was placed are also
     * recorded.
     *
     * @param nickname The nickname of the player who placed the card.
     * @param id       The ID of the card.
     * @param position The position of the card on the player's board.
     * @param side     The side of the card (true for gold side, false for resource
     *                 side).
     * @param turn     The turn in which the card was placed.
     */
    public void updatePlaceCard(String nickname, int id, Point position, boolean side, int turn) {
        CardClient startingCard = table.get(nickname);
        if (position.x == 0 && position.y == 0) {
            // first card, we need to create the card link in the hashmap
            table.put(nickname, new CardClient(id, side, position, 0, new HashMap<>()));
        } else {
            CardClient newCard = new CardClient(id, side, position, turn, new HashMap<>());

            int xPlaceToCheck = 0;
            int yPlaceToCheck = 0;
            CardClient cardOnTable;

            // same function as model server, but different purpose. Given a card, we should
            // link it with the other cards.
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

                cardOnTable = getAllCards(startingCard,
                        new Point(position.x + xPlaceToCheck, position.y + yPlaceToCheck));
                if (cardOnTable != null) {
                    // need to link the cards
                    switch (corner) {
                        case TOP_LEFT: {
                            cardOnTable.setAttached(Corner.BOTTOM_RIGHT, newCard);
                            newCard.setAttached(Corner.TOP_LEFT, cardOnTable);
                            break;
                        }
                        case TOP_RIGHT: {
                            cardOnTable.setAttached(Corner.BOTTOM_LEFT, newCard);
                            newCard.setAttached(Corner.TOP_RIGHT, cardOnTable);
                            break;
                        }
                        case BOTTOM_LEFT: {
                            cardOnTable.setAttached(Corner.TOP_RIGHT, newCard);
                            newCard.setAttached(Corner.BOTTOM_LEFT, cardOnTable);
                            break;
                        }
                        case BOTTOM_RIGHT: {
                            cardOnTable.setAttached(Corner.TOP_LEFT, newCard);
                            newCard.setAttached(Corner.BOTTOM_RIGHT, cardOnTable);
                            break;
                        }

                    }
                }
            }
        }
    }

    /**
     * Updates the resources of the specified player.
     *
     * This method is used to update the resources of a player with the new set of
     * resources.
     * The resources are represented by a HashMap where the key is the Sign and the
     * value is the quantity of that resource.
     *
     * @param nickname  The nickname of the player whose resources are to be
     *                  updated.
     * @param resources A HashMap representing the resources and their quantities
     *                  for the player.
     */
    public void updateResources(String nickname, HashMap<Sign, Integer> resources) {
        this.resources.put(nickname, resources);
    }

    /**
     * Returns the resources of all players in the game.
     *
     * This method is used to retrieve the current resources of all players.
     * The resources are represented in a HashMap where the key is the player's
     * nickname
     * and the value is another HashMap representing the resources and their
     * quantities.
     *
     * @return A HashMap representing the nicknames of the players and their
     *         corresponding resources.
     */
    public HashMap<String, HashMap<Sign, Integer>> getResources() {
        return resources;
    }

    /**
     * Returns the IDs of the objective cards in the game.
     *
     * This method is used to retrieve the current IDs of the objective cards.
     * The IDs are represented in an Integer array.
     *
     * @param id The ID of the objective card.
     * @return An array of Integers representing the IDs of the objective cards.
     */
    public ObjectiveCard getObjectiveCard(int id) {
        // we got objective cards array, so I need to correct the index. ObjectiveCards
        // index are form 1 to 16.
        id = id - 1;
        return (ObjectiveCard) objectiveCardsDeck.getCard(id);
    }

    /**
     * Returns a PlayedCard object representing a starting card.
     *
     * This method is used to retrieve a starting card based on its ID and side.
     * The ID is adjusted by subtracting 97 to match the index in the
     * startingCardsDeck.
     *
     * @param id   The ID of the starting card.
     * @param side The side of the card (true for gold side, false for resource
     *             side).
     * @return A PlayedCard object representing the starting card.
     */
    public PlayedCard getStartingCard(int id, boolean side) {
        // we got starting cards array, so I need to correct the index. StartingCards
        // index are form 97 to 102.
        id = id - 97;
        HashMap<Corner, PlayedCard> cardsToAttach = new HashMap<>();
        for (Corner corner : Corner.values()) {
            cardsToAttach.put(corner, null);
        }

        return new PlayedCard((PlayableCard) startingCardsDeck.getCard(id),
                cardsToAttach, side, 0, new Point(0, 0));
    }

    /**
     * Returns a PlayedCard object representing a card.
     *
     * This method is used to retrieve a card based on its ID and side.
     * If the ID is greater than or equal to 97, it is considered a starting card
     * and the getStartingCard method is called.
     * If the ID is less than 57, it is considered a resource card and is retrieved
     * from the resourceCardsDeck.
     * Otherwise, it is considered a gold card and is retrieved from the
     * goldCardsDeck.
     *
     * @param id   The ID of the card.
     * @param side The side of the card (true for gold side, false for resource
     *             side).
     * @return A PlayedCard object representing the card.
     */
    public PlayedCard getCard(int id, boolean side) {
        PlayableCard card;
        // starting cards are form 97 to 102
        if (id >= 97) {
            return getStartingCard(id, side);
        }
        // resource cards are form 17 to 56
        else if (id < 57) {
            card = (PlayableCard) resourceCardsDeck.getCard(id - 17);
        } else {
            // gold cards are form 57 to 96
            card = (PlayableCard) goldCardsDeck.getCard(id - 57);
        }

        HashMap<Corner, PlayedCard> cardsToAttach = new HashMap<>();
        cardsToAttach.put(Corner.TOP_LEFT, null);
        cardsToAttach.put(Corner.TOP_RIGHT, null);
        cardsToAttach.put(Corner.BOTTOM_LEFT, null);
        cardsToAttach.put(Corner.BOTTOM_RIGHT, null);

        return new PlayedCard(card,
                cardsToAttach, side, 0, new Point(0, 0));
    }

    /**
     * Updates the common table in the game.
     *
     * This method is used to update the common table with the new set of resource
     * cards, gold cards, and the head decks for both types of cards.
     * The resource cards and gold cards are represented by Integer arrays where
     * each Integer is the ID of a card.
     * The head decks are represented by Kingdom objects.
     *
     * @param resourceCards      An array of Integers representing the IDs of the
     *                           resource cards on the table.
     * @param goldCards          An array of Integers representing the IDs of the
     *                           gold cards on the table.
     * @param resourceCardOnDeck A Kingdom object representing the head deck of
     *                           resource cards.
     * @param goldCardOnDeck     A Kingdom object representing the head deck of gold
     *                           cards.
     */
    public void updateCommonTable(Integer[] resourceCards, Integer[] goldCards, Kingdom resourceCardOnDeck,
            Kingdom goldCardOnDeck) {
        this.resourceCards = resourceCards;
        this.goldCards = goldCards;
        headDeckResource = resourceCardOnDeck;
        headDeckGold = goldCardOnDeck;
    }

    /**
     * Finds a card in the game based on its position.
     *
     * This method is used to find a card in the game based on its position.
     * The position is represented by a Point object.
     *
     * @param startingCard The starting card from which to start the search.
     * @param position     The position of the card to find.
     * @return A CardClient object representing the card found, or null if no card
     *         is found at the given position.
     */
    private CardClient getAllCards(CardClient startingCard, Point position) {
        Stack<CardClient> stack = new Stack<>();
        return getAllCardsRecursive(startingCard, position, stack);
    }

    /**
     * Recursively finds a card in the game based on its position.
     *
     * This method is used to recursively find a card in the game based on its
     * position.
     * The position is represented by a Point object.
     * The method uses a Stack to keep track of the cards that have been visited
     * during the search.
     *
     * @param card     The current card being checked.
     * @param position The position of the card to find.
     * @param stack    A Stack of CardClient objects representing the cards that
     *                 have been visited during the search.
     * @return A CardClient object representing the card found, or null if no card
     *         is found at the given position.
     */
    private CardClient getAllCardsRecursive(CardClient card, Point position, Stack<CardClient> stack) {
        if (card == null) {
            return null;
        } else if (stack.contains(card)) {
            return null;
        } else if (card.getPosition().x == position.x && card.getPosition().y == position.y) {
            return card;
        }
        stack.push(card);

        for (Corner corner : Corner.values()) {
            CardClient found = getAllCardsRecursive(card.getAttached(corner), position, stack);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Updates the secret objective card of the player.
     *
     * This method is used to update the secret objective card of the player.
     * The card is identified by its index in the secretObjectiveCardsToChoose
     * array.
     *
     * @param indexCard The index of the secret objective card in the
     *                  secretObjectiveCardsToChoose array.
     */
    public void updateSecretObjectiveCard(int indexCard) {
        if (!(indexCard == 0 || indexCard == 1)) {
            System.out.println("Error in the index of the secret objective card");
        } else {
            secretObjectiveCard = secretObjectiveCardsToChoose[indexCard];
        }
    }

    /**
     * Updates the secret objective cards that the player can choose from.
     *
     * This method is used to update the secret objective cards that the player can
     * choose from.
     * The cards are represented by an Integer array where each Integer is the ID of
     * a card.
     *
     * @param secretObjectiveCardsToChoose An array of Integers representing the IDs
     *                                     of the secret objective cards to choose
     *                                     from.
     */
    public void updateSecretObjectiveCardsToChoose(Integer[] secretObjectiveCardsToChoose) {
        this.secretObjectiveCardsToChoose = secretObjectiveCardsToChoose;
    }

    /**
     * Returns the secret objective cards that the player can choose from.
     *
     * This method is used to retrieve the secret objective cards that the player
     * can choose from.
     * The cards are represented by an Integer array where each Integer is the ID of
     * a card.
     *
     * @return An array of Integers representing the IDs of the secret objective
     *         cards to choose from.
     */
    public Integer[] getSecretObjectiveCardsToChoose() {
        return secretObjectiveCardsToChoose;
    }

    /**
     * Updates the common objective cards in the game.
     *
     * This method is used to update the common objective cards in the game.
     * The cards are represented by an Integer array where each Integer is the ID of
     * a card.
     *
     * @param objectiveCardIds An array of Integers representing the IDs of the
     *                         common objective cards.
     */
    public void updateCommonObjectives(Integer[] objectiveCardIds) {
        commonObjectiveCards = objectiveCardIds;
    }

    /**
     * Returns the common objective cards in the game.
     *
     * This method is used to retrieve the common objective cards in the game.
     * The cards are represented by an Integer array where each Integer is the ID of
     * a card.
     *
     * @return An array of Integers representing the IDs of the common objective
     *         cards.
     */
    public Integer[] getCommonObjectiveCards() {
        return commonObjectiveCards;
    }

    /**
     * Returns the secret objective card of the player.
     *
     * This method is used to retrieve the secret objective card of the player.
     * The card is represented by an Integer where the Integer is the ID of the
     * card.
     *
     * @return An Integer representing the ID of the secret objective card.
     */
    public Integer getSecretObjectiveCard() {
        return secretObjectiveCard;
    }

    /**
     * Updates the players in the game.
     *
     * This method is used to update the players in the game.
     * The players are represented by a HashMap where the key is the player's
     * nickname and the value is the player's color.
     *
     * @param playersAndPins A HashMap representing the nicknames of the players and
     *                       their corresponding colors.
     */
    public void updateUsers(HashMap<String, Color> playersAndPins) {
        table = new HashMap<>();
        for (String player : playersAndPins.keySet()) {
            // update the points and resources for each player
            table.put(player, table.get(player));
            // set the points to 0 for all players
            points.put(player, 0);
            // set the resources to 0 for all players
            resources.put(player, new HashMap<>());
            for (Sign sign : Sign.values()) {
                resources.get(player).put(sign, 0);
            }
        }
    }

    /**
     * Returns the gold cards on the table.
     *
     * This method is used to retrieve the gold cards on the table.
     * The cards are represented by an Integer array where each Integer is the ID of
     * a card.
     *
     * @return An array of Integers representing the IDs of the gold cards on the
     *         table.
     */
    public Integer[] getGoldCards() {
        return goldCards;
    }

    /**
     * Returns the resource cards on the table.
     *
     * This method is used to retrieve the resource cards on the table.
     * The cards are represented by an Integer array where each Integer is the ID of
     * a card.
     *
     * @return An array of Integers representing the IDs of the resource cards on
     *         the table.
     */
    public Integer[] getResourceCards() {
        return resourceCards;
    }

    /**
     * Returns the head deck of gold cards.
     *
     * This method is used to retrieve the head deck of gold cards.
     * The head deck is represented by a Kingdom object.
     *
     * @return A Kingdom object representing the head deck of gold cards.
     */
    public Kingdom getHeadDeckGold() {
        return headDeckGold;
    }

    /**
     * Returns the head deck of resource cards.
     *
     * This method is used to retrieve the head deck of resource cards.
     * The head deck is represented by a Kingdom object.
     *
     * @return A Kingdom object representing the head deck of resource cards.
     */
    public Kingdom getHeadDeckResource() {
        return headDeckResource;
    }

    /**
     * Returns the hidden hands of all other players in the game.
     *
     * This method is used to retrieve the hidden hands of all other players in the
     * game.
     * The hands are represented by a HashMap where the key is the player's
     * nickname, and the value is an array of Pairs representing the Kingdom and
     * visibility of the cards in the player's hand.
     *
     * @return A HashMap representing the nicknames of the players and their
     *         corresponding hidden hands.
     */
    public HashMap<String, Pair<Kingdom, Boolean>[]> getOtherPlayersCards() {
        return otherPlayersCards;
    }

    /**
     * Returns a list of CardClient objects for the TUI.
     *
     * This method is used to retrieve a list of CardClient objects for the TUI.
     * The list is generated by finding all the cards linked to the card with the
     * given nickname.
     *
     * @param nickname The nickname of the player whose cards are to be retrieved.
     * @return An ArrayList of CardClient objects representing the cards linked to
     *         the card with the given nickname.
     */
    public ArrayList<CardClient> getListOfCards(String nickname) {
        CardClient card = table.get(nickname);
        return getAllCards(card);
    }

    /**
     * Returns a list of CardClient objects linked to the starting card.
     *
     * This method is used to retrieve a list of CardClient objects for the TUI.
     * The list is generated by finding all the cards linked to the starting card.
     *
     * @param startingCard The starting card from which to start the search.
     * @return An ArrayList of CardClient objects representing the cards linked to
     *         the starting card.
     */
    private ArrayList<CardClient> getAllCards(CardClient startingCard) {
        Stack<CardClient> stack = new Stack<>();
        // initialize the list of cards to print
        cardsToPrint = new ArrayList<>();
        // find all the cards linked to the starting card
        getAllCardsRecursive(startingCard, stack);
        return cardsToPrint;
    }

    /**
     * Recursively finds all cards linked to the starting card.
     *
     * This method is used to recursively find all cards in the game linked to the
     * starting card.
     * The method uses a Stack to keep track of the cards that have been visited
     * during the search.
     *
     * @param card  The current card being checked.
     * @param stack A Stack of CardClient objects representing the cards that have
     *              been visited during the search.
     */
    private void getAllCardsRecursive(CardClient card, Stack<CardClient> stack) {
        if (card == null || stack.contains(card)) {
            return;
        } else {
            // add the card to the list of cards to print
            cardsToPrint.add(card);
        }
        stack.push(card);
        // check all the corners of the card, to see if there are other cards attached
        for (Corner corner : Corner.values()) {
            getAllCardsRecursive(card.getAttached(corner), stack);
        }
    }

    /**
     * This method returns the table of the game.
     * The table is represented by a HashMap where the key is the player's nickname
     * and the value is a CardClient object representing the starting card of the
     * player.
     *
     * @return the table of the game.
     */
    public HashMap<String, CardClient> getTable() {
        return table;
    }
}
