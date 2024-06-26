package it.polimi.ingsw.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class represents the deck of cards.
 */
public class Deck implements Serializable {
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
    public Deck(InputStream cardsFile) throws FileNotFoundException, IOException, ParseException, IllegalArgumentException {
        cards = new ArrayList<Card>();
        this.generateDeck(cardsFile);
        this.shuffle();
    }

    public Deck(String cardsFile) throws FileNotFoundException, IOException, ParseException, IllegalArgumentException {
        cards = new ArrayList<Card>();
        this.generateDeck(cardsFile);
        this.shuffle();
    }

    private void generateDeck(String cardsFile)
            throws IOException, ParseException, IllegalArgumentException {
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
     * This constructor generates the deck of cards from a JSON file and shuffles
     * it.
     *
     * @param cardsFile the path of the JSON file containing the cards.
     * @param toFile    a boolean that is true if the deck is generated to be saved
     *                  in a file.
     * @throws FileNotFoundException    if the file is not found.
     * @throws IOException              if an I/O error occurs.
     * @throws ParseException           if the JSON file is not valid.
     * @throws IllegalArgumentException if the card prototype is invalid.
     */
    public Deck(String cardsFile, boolean toFile) throws IOException, ParseException {
        cards = new ArrayList<Card>();
        this.generateDeck(cardsFile);
    }

    /**
     * This constructor generates the deck of cards from a JSON file and shuffles
     * it.
     *
     * @param cardsFile the path of the JSON file containing the cards.
     * @param toFile    a boolean that is true if the deck is generated to be saved
     *                  in a file.
     * @throws FileNotFoundException    if the file is not found.
     * @throws IOException              if an I/O error occurs.
     * @throws ParseException           if the JSON file is not valid.
     * @throws IllegalArgumentException if the card prototype is invalid.
     */
    public Deck(InputStream cardsFile, boolean toFile) throws IOException, ParseException {
        cards = new ArrayList<Card>();
        this.generateDeck(cardsFile);
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
    private void generateDeck(InputStream cardsFile)
            throws IOException, ParseException, IllegalArgumentException {
        JSONParser parser = new JSONParser();
        Reader reader = new InputStreamReader(cardsFile, StandardCharsets.UTF_8);

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
        int id = extractInteger(cardObject, "id");
        Kingdom kingdom = kingdomOrNull((String) cardObject.get("kingdom"));
        ObjectiveType objectiveType = ObjectiveType.valueOf((String) cardObject.get("objective-type"));
        int multiplier = extractInteger(cardObject, "multiplier");
        this.insert(new ObjectiveCard(id, kingdom, objectiveType, multiplier));
    }

    /**
     * This method inserts a {@link ResourceCard} into the deck.
     * 
     * @param cardObject the JSON object representing the card.
     */
    private void insertResourceCard(JSONObject cardObject) {
        int id = extractInteger(cardObject, "id");

        int points = extractInteger(cardObject, "points");
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
        int id = extractInteger(cardObject, "id");

        Kingdom kingdom = Kingdom.valueOf((String) cardObject.get("kingdom"));
        int points = extractInteger(cardObject, "points");

        JSONObject corners = (JSONObject) cardObject.get("corners");

        this.insert(new GoldCard(id, kingdom, getCorners(corners), points, getRequirements(cardObject)));
    }

    /**
     * This method inserts a {@link SpecialGoldCard} into the deck.
     * 
     * @param cardObject the JSON object representing the card.
     */
    private void insertSpecialGoldCard(JSONObject cardObject) {
        int id = extractInteger(cardObject, "id");

        Kingdom kingdom = Kingdom.valueOf((String) cardObject.get("kingdom"));
        int points = extractInteger(cardObject, "points");

        JSONObject corners = (JSONObject) cardObject.get("corners");

        Countable thingToCount = Countable.valueOf((String) cardObject.get("thing-to-count"));

        this.insert(new SpecialGoldCard(id, kingdom, getCorners(corners), points, getRequirements(cardObject),
                thingToCount));
    }

    /**
     * This method inserts a {@link StartingCard} into the deck.
     * 
     * @param cardObject the JSON object representing the card.
     */
    private void insertStartingCard(JSONObject cardObject) {
        int id = extractInteger(cardObject, "id");

        Kingdom kingdom = kingdomOrNull((String) cardObject.get("kingdom"));

        JSONObject frontCorners = (JSONObject) cardObject.get("front-corners");
        JSONObject backCorners = (JSONObject) cardObject.get("back-corners");

        JSONArray bonusResources = (JSONArray) cardObject.get("bonus-resources");

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

        int mushroom = extractInteger(requirementsObject, "MUSHROOM");
        int leaf = extractInteger(requirementsObject, "LEAF");
        int wolf = extractInteger(requirementsObject, "WOLF");
        int butterfly = extractInteger(requirementsObject, "BUTTERFLY");
        int quill = extractInteger(requirementsObject, "QUILL");
        int inkwell = extractInteger(requirementsObject, "INKWELL");
        int scroll = extractInteger(requirementsObject, "SCROLL");

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

        Sign topLeft = signOrNull((String) cornersObject.get("TOP_LEFT"));
        Sign topRight = signOrNull((String) cornersObject.get("TOP_RIGHT"));
        Sign bottomLeft = signOrNull((String) cornersObject.get("BOTTOM_LEFT"));
        Sign bottomRight = signOrNull((String) cornersObject.get("BOTTOM_RIGHT"));

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
     * @throws IndexOutOfBoundsException if the deck is empty.
     *
     */
    public Kingdom getKingdomFirstCard() throws IndexOutOfBoundsException {
        return cards.get(0).getKingdom();
    }

    /**
     * Returns the card at the specified position in the deck.
     *
     * @param position The position of the card in the deck.
     * @return The card at the specified position.
     * @throws IndexOutOfBoundsException if the position is out of range
     *         (position < 0 || position >= size()).
     */
    public Card getCard(int position) throws IndexOutOfBoundsException {
        return cards.get(position);
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
        try {
            return cards.remove(0);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("The deck is empty");// just to add the message
        }
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

    /**
     * This method converts a string to a {@link Sign}.
     * 
     * @param sign the string to convert.
     * @return the {@link Sign} corresponding to the string, or null if the string
     *         is not a valid sign.
     */
    private Sign signOrNull(String sign) {
        try {
            return Sign.valueOf(sign);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * This method extracts an integer from a JSON object.
     * 
     * @param object the JSON object.
     * @param field  the field to extract.
     * @return the integer extracted from the JSON object.
     */
    private int extractInteger(JSONObject object, String field) {
        return Math.toIntExact((long) object.get(field));
    }
}
