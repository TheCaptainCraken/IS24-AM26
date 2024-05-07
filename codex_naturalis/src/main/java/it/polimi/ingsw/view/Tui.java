package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.PlayableCard;

import java.util.ArrayList;
import java.util.HashMap;

public class Tui {
    private final ArrayList<String> availableColors;
    private final ArrayList<Card> Cards;
    private final ArrayList<Card> StartingCards;
    private final ArrayList<Card> ObjectiveCards;
    private HashMap<String, Integer> points;


    public Tui(){
        //TODO colori
        //TODO vari deck
        availableColors = new ArrayList<>();
        availableColors.add("RED");
        availableColors.add("YELLOW");
        availableColors.add("GREEN");
        availableColors.add("BLUE");

        Cards = new ArrayList<>();
        StartingCards = new ArrayList<>();
        ObjectiveCards = new ArrayList<>();
        points = new HashMap<>();
    }

    public void askNumberOfPlayer() {
        System.out.println("You are the first player. Please enter the number of players)");
        System.out.println("The number of players must be between 2 and 4");
    }

    public void waitLobby() {
        System.out.println("You are connected to the server. Please wait for the first player to choose the number of players");
    }

    public void stopWaiting() {
        //TODO
        System.out.println("You are connected to the server. Please wait for the first player to choose the number of players");
    }

    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        System.out.println("The players in the lobby are:");
        for (String nickname : playersAndPins.keySet()) {
            //TODO color could be null
            System.out.println(nickname + " - " + playersAndPins.get(nickname));
        }
    }

    public void disconnect() {
        System.out.println("Lobby has been fulled with number of parameters chosen by the first player");
    }

    public void showStartingCard(int startingCardId) {
        //TODO get card from id
        System.out.println("The starting card is: " + startingCardId);
    }

    public void sameName(String nickname) {
        System.out.println("The nickname " + nickname + " is already taken. Please choose another one");
    }

    public void lobbyComplete() {
        System.out.println("The lobby is full. No other players can join");
    }

    public void colorAlreadyTaken() {
        System.out.println("The color you chose is already taken. Please choose another one");
        //TODO colori disponibili
    }

    public void noTurn() {
        System.out.println("It's not your turn. You can't perform this action");
    }

    public void notEnoughResources() {
        System.out.println("You don't have enough resources to perform this action");
        //TODO show resources
    }

    public void noConnection() {
        System.out.println("You are not connected to the server. Please retry");
    }

    public void wrongGamePhase() {
        System.out.println("You can't perform this action in this game phase");
        //TODO show game phase
    }

    public void noPlayer() {
        System.out.println("The player doesn't exist");
    }

    public void sendInfoOnTable() {
    }

    public void printObjectiveCards(Integer[] objectiveCardIds) {
    }

    public void printSecretObjectiveCards(Integer[] objectiveCardIds) {
    }

    public void showSecretObjectiveCard(int indexCard) {
    }

    public void showRanking(HashMap<String, Integer> ranking) {
        System.out.println("The ranking is:");
        for(String player: ranking.keySet()){
            System.out.println(player + " - " + ranking.get(player));
        }
    }

    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
        System.out.println("The points made by ObjectiveCards are:");
        for(String player: extraPoints.keySet()){
            System.out.println(player + " - " + extraPoints.get(player));
        }
    }

    public void refreshTurnInfo(String currentPlayer, GameState gameState) {
        System.out.println("It's " + currentPlayer + "'s turn");
        System.out.println("The game phase is: " + gameState);
    }

    public void updateScore(String nickname, int points) {
        //TODO salvare i vari player con i punti
    }
}
