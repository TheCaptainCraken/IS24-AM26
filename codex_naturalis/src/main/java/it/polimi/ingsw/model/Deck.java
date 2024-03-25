package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class represents the deck of cards.
 */
public class Deck {
    /**
     * The list of cards in the deck.
     */
    private ArrayList<Card> cards;

    /**
     * This constructor generates the deck of cards from a JSON file and shuffles
     * it.
     * 
     * @param cardsFile the path of the JSON file containing the cards.
     * @throws FileNotFoundException    if the file is not found.
     * @throws IOException              if an I/O error occurs.
     * @throws ParseException           if the JSON file is not valid.
     * @throws IllegalArgumentException if the card prototype is invalid.
     */
    public Deck(String cardsFile) throws FileNotFoundException, IOException, ParseException, IllegalArgumentException {
        cards = new ArrayList<Card>();
        this.generateDeck(cardsFile);
        this.shuffle();
    }

    /**
     * This method generates the deck of cards from a JSON file.
     * 
     * @param cardsFile the path of the JSON file containing the cards.
     * @throws FileNotFoundException    if the file is not found.
     * @throws IOException              if an I/O error occurs.
     * @throws ParseException           if the JSON file is not valid.
     * @throws IllegalArgumentException if the card prototype is invalid.
     */
    private void generateDeck(String cardsFile)
            throws FileNotFoundException, IOException, ParseException, IllegalArgumentException {
        JSONParser parser = new JSONParser();
        Reader reader = new FileReader(cardsFile);

        JSONArray cards = (JSONArray) parser.parse(reader);
        for (Object card : cards) {
            JSONObject cardObject = (JSONObject) card;
            String prototype = (String) cardObject.get("prototype");

            switch (prototype) {
                case "OBJECTIVE":
                    insertObjectiveCard(cardObject);
                    break;
                case "RESOURCE":
                    insertResourceCard(cardObject);
                    break;
                case "GOLD":
                    insertGoldCard(cardObject);
                    break;
                case "SPECIAL_GOLD":
                    insertSpecialGoldCard(cardObject);
                    break;
                case "STARTING":
                    insertStartingCard(cardObject);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid card prototype");
            }
        }

    }

    /**
     * This method inserts an {@link ObjectiveCard} into the deck.
     * 
     * @param cardObject the JSON object representing the card.
     */
    private void insertObjectiveCard(JSONObject cardObject) {
        int id = Math.toIntExact((long) cardObject.get("id"));
        Kingdom kingdom = kingdomOrNull((String) cardObject.get("kingdom"));
        ObjectiveType objectiveType = ObjectiveType.valueOf((String) cardObject.get("objective-type"));
        int multiplier = Math.toIntExact((long) cardObject.get("multiplier"));
        this.insert(new ObjectiveCard(id, kingdom, objectiveType, multiplier));
    }

    /**
     * This method inserts a {@link ResourceCard} into the deck.
     * 
     * @param cardObject the JSON object representing the card.
     */
    private void insertResourceCard(JSONObject cardObject) {
        int id = (int) cardObject.get("id");

        int points = (int) cardObject.get("points");
        Kingdom kingdom = Kingdom.valueOf((String) cardObject.get("kingdom"));

        JSONObject corners = (JSONObject) cardObject.get("corners");

        this.insert(new ResourceCard(id, kingdom, getCorners(corners), points));
    }

    /**
     * This method inserts a {@link GoldCard} into the deck.
     * 
     * @param cardObject the JSON object representing the card.
     */
    private void insertGoldCard(JSONObject cardObject) {
        int id = (int) cardObject.get("id");
        Kingdom kingdom = Kingdom.valueOf((String) cardObject.get("kingdom"));
        int points = (int) cardObject.get("points");

        JSONObject corners = (JSONObject) cardObject.get("corners");

        this.insert(new GoldCard(id, kingdom, getCorners(corners), points, getRequirements(cardObject)));
    }

    /**
     * This method inserts a {@link SpecialGoldCard} into the deck.
     * 
     * @param cardObject the JSON object representing the card.
     */
    private void insertSpecialGoldCard(JSONObject cardObject) {
        int id = (int) cardObject.get("id");
        Kingdom kingdom = Kingdom.valueOf((String) cardObject.get("kingdom"));
        int points = (int) cardObject.get("points");

        JSONObject corners = (JSONObject) cardObject.get("corners");

        Countable thingToCount = Countable.valueOf((String) cardObject.get("thingToCount"));

        this.insert(new SpecialGoldCard(id, kingdom, getCorners(corners), points, getRequirements(cardObject),
                thingToCount));
    }

    /**
     * This method inserts a {@link StartingCard} into the deck.
     * 
     * @param cardObject the JSON object representing the card.
     */
    private void insertStartingCard(JSONObject cardObject) {
        int id = (int) cardObject.get("id");
        Kingdom kingdom = Kingdom.valueOf((String) cardObject.get("kingdom"));

        JSONObject frontCorners = (JSONObject) cardObject.get("frontCorners");
        JSONObject backCorners = (JSONObject) cardObject.get("backCorners");

        JSONArray bonusResources = (JSONArray) cardObject.get("bonusResources");

        ArrayList<Sign> bonusResourcesList = new ArrayList<Sign>();
        for (Object bonusResource : bonusResources) {
            bonusResourcesList.add(Sign.valueOf((String) bonusResource));
        }

        this.insert(
                new StartingCard(id, kingdom, getCorners(frontCorners), getCorners(backCorners), bonusResourcesList));
    }

    /**
     * This method returns the requirements of a card.
     * 
     * @param cardObject the JSON object representing the card.
     * @return the requirements of the card.
     */
    private HashMap<Sign, Integer> getRequirements(JSONObject cardObject) {
        JSONObject requirementsObject = (JSONObject) cardObject.get("requirements");

        int mushroom = (int) requirementsObject.get("MUSHROOM");
        int leaf = (int) requirementsObject.get("LEAF");
        int wolf = (int) requirementsObject.get("WOLF");
        int butterfly = (int) requirementsObject.get("BUTTERFLY");
        int quill = (int) requirementsObject.get("QUILL");
        int inkwell = (int) requirementsObject.get("INKWELL");
        int scroll = (int) requirementsObject.get("SCROLL");

        HashMap<Sign, Integer> requirements = new HashMap<Sign, Integer>();

        requirements.put(Sign.MUSHROOM, mushroom);
        requirements.put(Sign.LEAF, leaf);
        requirements.put(Sign.WOLF, wolf);
        requirements.put(Sign.BUTTERFLY, butterfly);
        requirements.put(Sign.QUILL, quill);
        requirements.put(Sign.INKWELL, inkwell);
        requirements.put(Sign.SCROLL, scroll);

        return requirements;
    }

    /**
     * This method returns the corners of a card.
     * 
     * @param cornersObject the JSON object representing the corners of the card.
     * @return the corners of the card.
     */
    private HashMap<Corner, Sign> getCorners(JSONObject cornersObject) {

        Sign topLeft = Sign.valueOf((String) cornersObject.get("TOP_LEFT"));
        Sign topRight = Sign.valueOf((String) cornersObject.get("TOP_RIGHT"));
        Sign bottomLeft = Sign.valueOf((String) cornersObject.get("BOTTOM_LEFT"));
        Sign bottomRight = Sign.valueOf((String) cornersObject.get("BOTTOM_RIGHT"));

        HashMap<Corner, Sign> corners = new HashMap<Corner, Sign>();

        corners.put(Corner.TOP_LEFT, topLeft);
        corners.put(Corner.TOP_RIGHT, topRight);
        corners.put(Corner.BOTTOM_LEFT, bottomLeft);
        corners.put(Corner.BOTTOM_RIGHT, bottomRight);

        return corners;
    }

    /**
     * This method shuffles the deck.
     */
    private void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * This method returns the kingdom of the first card of the deck.
     * 
     * @return the {@link Kingdom} of the first card of the deck.
     */
    public Kingdom getKingdomFirstCard() {
        return cards.get(0).getKingdom();
    }

    /**
     * This method returns the first {@link Card} of the deck and then removes it
     * from the deck.
     * 
     * @return the first {@link Card} of the deck.
     * 
     * @throws IndexOutOfBoundsException if the deck is empty.
     */
    public Card draw() throws IndexOutOfBoundsException {
        return cards.remove(0);
    }

    /**
     * This method adds a {@link Card} to the deck.
     * 
     * @param card the {@link Card} to add to the deck.
     */
    private void insert(Card card) {
        cards.add(card);
    }

    /**
     * This method converts a string to a {@link Kingdom}.
     * 
     * @param kingdom the string to convert.
     * @return the {@link Kingdom} corresponding to the string, or null if the
     *         string is not a valid kingdom.
     */
    private Kingdom kingdomOrNull(String kingdom) {
        try {
            return Kingdom.valueOf(kingdom);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
