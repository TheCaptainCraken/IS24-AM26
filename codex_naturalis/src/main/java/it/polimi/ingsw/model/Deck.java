package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;
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
                case "ObjectiveCard":
                    insertObjectiveCard(cardObject);
                    break;
                case "ResourceCard":
                    insertResourceCard(cardObject);
                    break;
                // TODO: add the case for the GoldCard, SpecialCard and StartingCard.
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
        int id = (int) cardObject.get("id");
        Kingdom kingdom = Kingdom.valueOf((String) cardObject.get("kingdom"));
        ObjectiveType objectiveType = ObjectiveType.valueOf((String) cardObject.get("objectiveType"));
        this.insert(new ObjectiveCard(id, kingdom, objectiveType));
    }

    /**
     * This method inserts a {@link ResourceCard} into the deck.
     * 
     * @param cardObject the JSON object representing the card.
     */
    private void insertResourceCard(JSONObject cardObject) {
        int id = (int) cardObject.get("id");
        Sign cornerTopLeft = Sign.valueOf((String) cardObject.get("cornerTopLeft"));
        Sign cornerTopRight = Sign.valueOf((String) cardObject.get("cornerTopRight"));
        Sign cornerBotLeft = Sign.valueOf((String) cardObject.get("cornerBotLeft"));
        Sign cornerBotRight = Sign.valueOf((String) cardObject.get("cornerBotRight"));
        Sign sign = Sign.valueOf((String) cardObject.get("sign"));
        int points = (int) cardObject.get("points");
        this.insert(new ResourceCard(id, cornerTopLeft, cornerTopRight, cornerBotLeft, cornerBotRight, sign, points));
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
     */
    public Card draw() {
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
}
