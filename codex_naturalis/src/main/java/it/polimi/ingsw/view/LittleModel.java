package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.*;


public class LittleModel {
    static String basePath = "src/main/java/it/polimi/ingsw/model/decks/";

    private HashMap <String, Integer> points;
    private HashMap<String, HashMap<Sign, Integer>> resources;

    private HashMap<String, Pair<Kingdom, Boolean>> otherPlayersCards; //TODO come salvarle?
    private Integer[] myCards;

    private Kingdom headDeckGold;
    private Kingdom headDeckResource;

    private Deck startingCards;
    private Deck ObjectiveCards;
    private Deck ResourceCards;

    public LittleModel(){
        points = new HashMap<>();
        resources = new HashMap<>();
        otherPlayersCards = new HashMap<>();
        myCards = new Integer[3];

        try{
            startingCards = new Deck(basePath + "startingCardsDeck.json", true);
            ObjectiveCards = new Deck(basePath + "objectiveCardsDeck.json", true);
            ResourceCards = new Deck(basePath + "resourceCardsDeck.json", true);
        } catch (IOException e) {
           System.out.println("file for TUI not found");
        } catch (ParseException e) {
           System.out.println("Error in parsing file for TUI");
        }
    }

    public void updateHand(String nickname, Integer[] hand){
        myCards = hand;
    }

    public Integer[] getMyCards() {
        return myCards;
    }

    public void updateHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand){
        //TODO ha senso, hashMap di hashMap?
    }

    public Pair<Kingdom, Boolean> getPlayerCard (String player) {
        return otherPlayersCards.get(player); //TODO
    }

    public void updateCardOnTable(int newCardId, boolean gold, int onTableOrDeck) {
        //TODO io ne ho due, vedi logica.
    }

    public void updateHeadDeck(Kingdom headDeck, boolean gold) {
        if(gold){
            headDeckGold = headDeck;
        } else {
            headDeckResource = headDeck;
        }
    }

    public void updateScore(String nickname, int points) {
        //TODO sono sempre aggiornati o va fatta la somma?
        this.points.put(nickname, points);
    }

    public void updateDrawCard(String nickname, int cardId) {
        //TODO capire logica
    }

    public void updatePlaceCard(String nickname, int id, Point position, boolean side) {
        //TODO capire logica di salvataggi carte
        //TODO fare stampa ordinata
    }

    public void updateResources(String nickname, HashMap<Sign, Integer> resources) {
        this.resources.put(nickname, resources);
    }

    public HashMap<String, HashMap<Sign, Integer>> getResources() {
        return resources;
    }

    //TODO capire come e dove salvare le carte. Vorrei avere una cosa uguale per TUI/GUI

    public ObjectiveCard getObjectiveCard(int id){
        id = id - 1;
        return (ObjectiveCard) ObjectiveCards.getCard(id);
    }

    public PlayedCard getStartingCard(int id){
        id = id - 97;
        PlayedCard card = new PlayedCard((PlayableCard) startingCards.getCard(id),
                null, false, 0, new Point(0, 0));
        //TODO
        return null;
    }

    public PlayedCard getCard(int id){
        id = id - 17;
        PlayedCard card = new PlayedCard((PlayableCard) ResourceCards.getCard(id),
                null, false, 0, new Point(0, 0));
        //TODO
        return null;
    }

    public PlayedCard getCardFromKingdom(){
        //TODO
        return null;
    }

}

