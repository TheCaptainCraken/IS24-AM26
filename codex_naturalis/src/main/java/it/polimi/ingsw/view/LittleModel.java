package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;

import it.polimi.ingsw.model.Color;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.List;


public class LittleModel implements Serializable {
    static String basePath = "src/main/java/it/polimi/ingsw/model/decks/";

    private HashMap <String, Integer> points;
    private HashMap<String, HashMap<Sign, Integer>> resources;

    private HashMap<String, Pair<Kingdom, Boolean>[]> otherPlayersCards;
    private Integer[] myCards;

    private HashMap<String, CardClient> table;
    private HashMap<String, Integer> playersAndCardsNumber;
    private static ArrayList<CardClient> cardsToPrint;

    private Integer[] resourceCards;
    private Integer[] goldCards;
    private Kingdom headDeckGold;
    private Kingdom headDeckResource;

    private Integer[] secretObjectiveCardsToChoose;
    private Integer[] commonObjectiveCards;
    private Integer secretObjectiveCard;

    private Deck startingCardsDeck;
    private Deck objectiveCardsDeck;
    private Deck resourceCardsDeck;
    private Deck goldCardsDeck;

    public LittleModel() {
        points = new HashMap<>();
        resources = new HashMap<>();
        myCards = new Integer[3];
        otherPlayersCards = new HashMap<>();
        table = new HashMap<>();
        playersAndCardsNumber = new HashMap<>();

        try{
            startingCardsDeck = new Deck("codex_naturalis/src/main/java/it/polimi/ingsw/model/decks/startingCardsDeck.json", true);
            objectiveCardsDeck = new Deck("codex_naturalis/src/main/java/it/polimi/ingsw/model/decks/objectiveCardsDeck.json", true);
            resourceCardsDeck = new Deck("codex_naturalis/src/main/java/it/polimi/ingsw/model/decks/resourceCardsDeck.json", true);
            goldCardsDeck = new Deck("codex_naturalis/src/main/java/it/polimi/ingsw/model/decks/goldCardsDeck.json", true);
        } catch (IOException e) {
           System.out.println("file for TUI not found");
        } catch (ParseException e) {
           System.out.println("Error in parsing file for TUI");
        }
    }

    public void updateHand(String nickname, Integer[] hand){
        myCards = hand;
    }

    public Integer[] getHand() {
        return myCards;
    }

    public void updateHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand){
        otherPlayersCards.put(nickname, hand);
    }

    public Pair<Kingdom, Boolean>[] getHiddenHand(String player) {
        return otherPlayersCards.get(player);
    }

    public void updateCardOnTable(int newCardId, boolean gold, int onTableOrDeck) {
        if(newCardId != -1) {
            if (gold) {
                goldCards[onTableOrDeck] = newCardId;
            } else {
                resourceCards[onTableOrDeck] = newCardId;
            }
        }
    }

    public void updateHeadDeck(Kingdom headDeck, boolean gold) {
        if(gold){
            headDeckGold = headDeck;
        } else {
            headDeckResource = headDeck;
        }
    }

    public void updateScore(String nickname, int points) {
        this.points.put(nickname, points);
    }

    public HashMap<String, Integer> getPoints() {
        return points;
    }

    public void updateDrawCard(String nickname, int cardId) {
        //TODO capire logica
    }

    public void updatePlaceCard(String nickname, int id, Point position, boolean side) {
       CardClient startingCard = table.get(nickname);
       if(position.x == 0 && position.y == 0){
           table.put(nickname, new CardClient(id, side, position, 0, new HashMap<>()));
       }else{
           CardClient newCard = new CardClient(id, side, position, playersAndCardsNumber.put(nickname, playersAndCardsNumber.get(nickname) + 1), new HashMap<>());
           playersAndCardsNumber.put(nickname, playersAndCardsNumber.get(nickname) + 1);

           int xPlaceToCheck = 0;
           int yPlaceToCheck = 0;
           CardClient cardToCheck;

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
               if(cardToCheck != null) {
                   switch (corner) {
                       case TOP_LEFT: {
                           cardToCheck.setAttached(Corner.BOTTOM_RIGHT, newCard);
                           newCard.setAttached(Corner.TOP_LEFT, cardToCheck);
                           break;
                       }
                       case TOP_RIGHT: {
                           cardToCheck.setAttached(Corner.BOTTOM_LEFT, newCard);
                           newCard.setAttached(Corner.TOP_RIGHT, cardToCheck);
                           break;
                       }
                       case BOTTOM_LEFT: {
                           cardToCheck.setAttached(Corner.TOP_RIGHT, newCard);
                           newCard.setAttached(Corner.BOTTOM_LEFT, cardToCheck);
                           break;
                       }
                       case BOTTOM_RIGHT: {
                           cardToCheck.setAttached(Corner.TOP_LEFT, newCard);
                           newCard.setAttached(Corner.BOTTOM_RIGHT, cardToCheck);
                           break;
                       }

                   }
               }
           }
       }
    }

    public void updateResources(String nickname, HashMap<Sign, Integer> resources) {
        this.resources.put(nickname, resources);
    }

    public HashMap<String, HashMap<Sign, Integer>> getResources() {
        return resources;
    }

    public ObjectiveCard getObjectiveCard(int id){
        id = id - 1;
        return (ObjectiveCard) objectiveCardsDeck.getCard(id);
    }

    public PlayedCard getStartingCard(int id, boolean side){
        id = id - 97;
        HashMap<Corner, PlayedCard> cardsToAttach = new HashMap<>();
        for(Corner corner: Corner.values()){
            cardsToAttach.put(corner, null);
        }
        PlayedCard card = new PlayedCard((PlayableCard) startingCardsDeck.getCard(id),
                cardsToAttach, side, 0, new Point(0, 0));

        return card;
    }

    public PlayedCard getCard(int id, boolean side){
        PlayableCard card;
        if(id >= 97){
             return getStartingCard(id, side);
        }
        else if(id < 57){
           card = (PlayableCard) resourceCardsDeck.getCard(id - 17);
        }else {
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

    public Object getTableOfPlayer(String name) {
        //TODO
        return null;
    }

    public void updateCommonTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck, Kingdom goldCardOnDeck) {
        this.resourceCards = resourceCards;
        this.goldCards = goldCard;
        headDeckResource = resourceCardOnDeck;
        headDeckGold = goldCardOnDeck;
    }

    private CardClient findCard(CardClient startingCard, Point position) {
        Stack<CardClient> stack = new Stack<>();
        return recursiveFindCard(startingCard, position, stack);
    }

    private CardClient recursiveFindCard(CardClient card, Point position, Stack<CardClient> stack) {
        if (card == null) {
            return null;
        } else if (stack.contains(card)) {
            return null;
        } else if (card.getPosition().x == position.x && card.getPosition().y == position.y) {
            return card;
        }
        stack.push(card);

        for (Corner corner : Corner.values()) {
            CardClient found = recursiveFindCard(card.getAttached(corner), position, stack);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void updateSecretObjectiveCard(int indexCard) {
        secretObjectiveCard = secretObjectiveCardsToChoose[indexCard];
    }

    public void updateSecretObjectiveCardsToChoose(Integer[] secretObjectiveCardsToChoose) {
        this.secretObjectiveCardsToChoose = secretObjectiveCardsToChoose;
    }

    public Integer[] getSecretObjectiveCardsToChoose() {
        return secretObjectiveCardsToChoose;
    }

    public Integer[] getCommonObjectiveCards() {
        return commonObjectiveCards;
    }

    public Integer getSecretObjectiveCard() {
        return secretObjectiveCard;
    }

    public void updateCommonObjectiveCards(Integer[] commonObjectiveCards) {
        this.commonObjectiveCards = commonObjectiveCards;
    }

    public void updateUsers(HashMap<String, Color> playersAndPins) {
        for(String player: playersAndPins.keySet()){
            this.playersAndCardsNumber.put(player, 0);
        }
    }

    public Integer[] getGoldCards() {
        return goldCards;
    }

    public Integer[] getResourceCards() {
        return resourceCards;
    }

    public Kingdom getHeadDeckGold() {
        return headDeckGold;
    }

    public Kingdom getHeadDeckResource() {
        return headDeckResource;
    }

    public HashMap<String, Pair<Kingdom, Boolean>[]> getOtherPlayersCards() {
        return otherPlayersCards;
    }

    public ArrayList<CardClient> getListOfCardForTui(String nickname){
        ArrayList<CardClient> cards = new ArrayList<>();
        CardClient card = table.get(nickname);
        ArrayList<CardClient> carte = findCard(card);
        return carte;
    }

    private ArrayList<CardClient> findCard(CardClient startingCard) {
        Stack<CardClient> stack = new Stack<>();
        cardsToPrint = new ArrayList<>();
        CardClient card = recursiveFindCard(startingCard, stack);
        return cardsToPrint;
    }

    private CardClient recursiveFindCard(CardClient card, Stack<CardClient> stack) {
        if (card == null) {
            return null;
        } else if (stack.contains(card)) {
            return null;
        } else{
            cardsToPrint.add(card);
        }
        stack.push(card);
        for (Corner corner : Corner.values()) {
            CardClient found = recursiveFindCard(card.getAttached(corner), stack);
        }
        return null;
    }



}

