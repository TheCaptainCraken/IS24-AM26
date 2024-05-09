package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Tui {
    private final ArrayList<String> availableColors;
    private final ArrayList<Card> Cards;
    private final ArrayList<Card> StartingCards;
    private final ArrayList<Card> ObjectiveCards;

    private HashMap<String, Integer> score;
    private HashMap<String, HashMap<Sign, Integer>> AllResources;
    private HashMap<String, ArrayList<PlayedCard>> hiddenCard;


    public Tui(){
        //TODO colori
        //TODO vari deck
        availableColors = new ArrayList<>();

        Cards = new ArrayList<>();
        StartingCards = new ArrayList<>();
        ObjectiveCards = new ArrayList<>();
        score = new HashMap<>();
        AllResources = new HashMap<>();
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
            Color color = playersAndPins.get(nickname);
            System.out.println(nickname + " - " + Objects.requireNonNullElse(color, "no color"));
        }
        //TODO ask daniel
        for(String name : playersAndPins.keySet()){
            score.put(name, 0);
            AllResources.put(name, new HashMap<>()); //TODO controllare
        }
    }

    public void disconnect() {
        System.out.println("Lobby has been fulled with number of parameters chosen by the first player");
    }

    public void showStartingCard(int startingCardId) {
        PlayedCard card;
        ArrayList<String[]> cards = new ArrayList<>();
        int i;

        //Convert the PlayedCard into a string to print it
        for(i = 0; i < 2; i++) {
            //cards.add() //TOD0 funzione JsonCarta
        }

        System.out.println("Choose the side of the staring Card. The one on the left is the top, the one on the right is the back");
        //TODO cosa scrivere
        for(i = 0; i < 7; i++) {
            for(String[] carta : cards) {
                System.out.print(carta[i]);
                System.out.print("   ");
            }
        }
    }

    public void sameName(String nickname) {
        System.out.println("The nickname " + nickname + " is already taken. Please choose another one");
    }

    public void lobbyComplete() {
        System.out.println("The lobby is full. No other players can join");
    }

    public void colorAlreadyTaken() {
        System.out.println("The color you chose is already taken. Please choose another one");
    }

    public void noTurn() {
        System.out.println("It's not your turn. You can't perform this action");
    }

    public void notEnoughResources(String name) {
        System.out.println("You don't have enough resources to perform this action");
        showResourcesPlayer(name);
    }

    public void noConnection() {
        System.out.println("You are not connected to the server. Please retry");
    }

    public void showCommonTable() {
        //TODO
    }

    public void showCommonObjectives(Integer[] objectiveCardIds) {
        //TODO get card from index
        List<String[]> cards = new ArrayList<>();
        for(Integer cardId: objectiveCardIds){
            //TODO card fromId
            cards.add(createObjectiveCardToPrint(cardId));
        }

        int size = cards.get(0).length;
        int i;

        for(i = 0; i < size; i++) {
            for(String[] carta : cards) {
                System.out.print(carta[i]);
                System.out.print("   ");
            }
        }
        System.out.println();
    }

    /**
     * Prints the secret objective card of the player.
     *
     * This method takes an index of a card as input, retrieves the corresponding card
     * and prints the representation of the card on the console. The representation of the card
     * is an array of strings, with each string representing a row of the card.
     *
     * @param indexCard The index of the secret objective card to print.
     */
    public void showSecretObjectiveCard(int indexCard) {
        System.out.println("This is your secret objective card: ");
        String[] secretObjective = createObjectiveCardToPrint(indexCard);
        for(String row: secretObjective){
            System.out.println(row);
        }

    }

    public String[] createObjectiveCardToPrint(int indexCard) {
        ObjectiveCard card = null; //TODO get card from index
        String[] objective;

        if(indexCard == 9 || indexCard == 11) {
            objective = new String[]{
                 "      *** ",
                 "   ***    ",
                 "***       ",
            };
        }else if(indexCard == 10 || indexCard == 12) {
            objective = new String[]{
                    "***       ",
                    "   ***    ",
                    "      *** ",
            };
        }else if(indexCard == 13) {
            objective = new String[]{
                    "***       ",
                    "***       ",
                    "   ***    ",
            };
        }else if(indexCard == 14) {
            objective = new String[]{
                    "   ***    ",
                    "       ***",
                    "       ***",
            };
        }else if(indexCard == 15) {
            objective = new String[]{
                    "    ***   ",
                    "***       ",
                    "***       ",
            };
        }else if(indexCard == 16) { objective = new String[]{
                "   ***    ",
                "   ***    ",
                "***       ",
            };
        }else if(card.getKingdom() != null){
            objective = new String[]{
                    " " + card.getType() + " ",
                    " " + card.getKingdom() + " ",
                    "              "
                 };
        }else {
            objective = new String[]{
                    "  "+card.getType()+"  ",
                    "  ********  ",
                    "  ********  "
            };
        }

        //TODO sistemare layout provandolo. Da fare per ogni carta


        return new String[]{
                "┌---------------------------┐",
                "|             "+card.getMultiplier()+"              |",
                "|             "+card.getKingdom()+"                  |",
                "|                           |",
                "|             "+objective[0]+"              |",
                "|             "+objective[1]+"              |",
                "|             "+objective[2]+"              |",
                "|                           |",
                "└---------------------------┘",
        };
    }


    public void showTurnInfo(String currentPlayer, GameState gameState) {
        System.out.println("It's " + currentPlayer + "'s turn");
        System.out.println("The game phase is: " + gameState);
    }

    public void updateScore(String nickname, int points) {
        Integer newPoint = Integer.valueOf(points);
        score.put(nickname, newPoint);
        //TODO non qui
    }

    public void updateResources(String nickname, HashMap<Sign, Integer> resources){
        AllResources.put(nickname, resources);
    }

    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
        System.out.println("The points made by ObjectiveCards are:");
        for(String player: extraPoints.keySet()) {
            System.out.println(player + " - " + extraPoints.get(player));
        }
    }

    public void showRanking(ArrayList<Player> ranking) {
        System.out.println("The ranking is:");
        for(Player player: ranking) {
            System.out.println(player);
        }
    }

    public void wrongGamePhase() {
        System.out.println("You can't perform this action in this game phase");
    }

    public void noPlayer() {
        System.out.println("The player doesn't exist");
    }

    public void closingLobbyError() {
        System.out.println("The input is not correct. Please retry");
    }

    public void showResourcesAllPlayer() {
        for(String player: AllResources.keySet()){
            System.out.println(player + " has:");
            for(Sign sign: AllResources.get(player).keySet()){
                System.out.println(sign + " - " + AllResources.get(player).get(sign));
            }
        }
    }

    private void showResourcesPlayer(String nickname) {
        System.out.println("You have the following resources:");
        for(Sign sign: AllResources.get(nickname).keySet()){
            System.out.println(sign + " - " + AllResources.get(nickname).get(sign));
        }
    }

    public void printTableAreaOfPlayer(String nickname){
        printTableArea(getTable(nickname));
    }

    public void printTableArea(PlayedCard card){
        if(card == null){
            System.out.println("There is no card in this place");
            defaultMenu();
            return;
        }
        System.out.println("Arrows means that the first is on the other one, example: card1 -> card2, card1 is on card2 on the corner near the arrow");
        System.out.println(focusOnCard(card));//Useless first time
        System.out.println("Options: +\n" +
                "(top left) q   e (top right)\n" +
                "             s (quit)  \n" +
                "(bot left) z   c (bot right)\n");
        char option = 's';//TODO Scanf con prossima opzione
        switch(option){
            case 'q':
                printTableArea(card.getAttachmentCorners().get(Corner.TOP_LEFT));
                break;
            case 'e':
                printTableArea(card.getAttachmentCorners().get(Corner.TOP_RIGHT));
                break;
            case 'z':
                printTableArea(card.getAttachmentCorners().get(Corner.BOTTOM_LEFT));
                break;
            case 'c':
                printTableArea(card.getAttachmentCorners().get(Corner.BOTTOM_RIGHT));
                break;
            default:
                defaultMenu();
        }
    }

    public String focusOnCard(PlayedCard focusedCard) {
        String toPrint = "";
        HashMap<Corner, PlayedCard> attachments = focusedCard.getAttachmentCorners();
        HashMap<Corner, String[]> attachmentsStrings = new HashMap<>();
        for(Corner c : attachments.keySet()){
            attachmentsStrings.put(c, printResourceCard(attachments.get(c)));
        }
        HashMap<Corner, String[]> arrows = createArrows(attachments, focusedCard);

        for(int i = 0; i < 7; i++){
            toPrint += attachmentsStrings.get(Corner.TOP_LEFT)[i]+"                             "+attachmentsStrings.get(Corner.TOP_RIGHT)[i]+"\n";
        }
        toPrint += "                             "+arrows.get(Corner.TOP_LEFT)[0]+"                             "+arrows.get(Corner.TOP_RIGHT)[0]+"                             "+"\n";
        toPrint += "                             "+arrows.get(Corner.TOP_LEFT)[1]+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+arrows.get(Corner.TOP_RIGHT)[1]+"                             "+"\n";
        toPrint += "                             "+arrows.get(Corner.TOP_LEFT)[2]+"                             "+arrows.get(Corner.TOP_RIGHT)[2]+"                             "+"\n";

        for(int i = 0; i < 7; i++){
            toPrint += "                             "+" ~ "+attachmentsStrings.get(Corner.TOP_LEFT)[i]+" ~ "+"                             "+"\n";
        }
        toPrint += "                             "+arrows.get(Corner.BOTTOM_LEFT)[0]+"                             "+arrows.get(Corner.BOTTOM_RIGHT)[0]+"                             "+"\n";
        toPrint += "                             "+arrows.get(Corner.BOTTOM_LEFT)[1]+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+arrows.get(Corner.BOTTOM_RIGHT)[1]+"                             "+"\n";
        toPrint += "                             "+arrows.get(Corner.BOTTOM_LEFT)[2]+"                             "+arrows.get(Corner.BOTTOM_RIGHT)[2]+"                             "+"\n";
        for(int i=0; i<7; i++){
            toPrint += attachmentsStrings.get(Corner.BOTTOM_LEFT)[i]+"                             "+attachmentsStrings.get(Corner.BOTTOM_RIGHT)[i]+"\n";
        }
        return toPrint;
    }

    public String[] printResourceCard(PlayedCard card) {
        HashMap<Corner, String> corner = new HashMap<>();
        String midSignsString;
        if(card == null){
            midSignsString = "empty";
        }else{
            ArrayList<Sign> midSigns = new ArrayList<>();
            if (card.isFacingUp()) {
                for (Corner c : Corner.values()) {
                    corner.put(c, card.getCard().getCorners().get(c).toString());
                }
                midSigns.add(fromKingdomToSign(card.getCard().getKingdom()));
            } else {
                if (card.getCard() instanceof StartingCard) {
                    for (Corner c : Corner.values()) {
                        corner.put(c, ((StartingCard) card.getCard()).getBacksideCorners().get(c).toString());
                    }
                    for (Sign s : ((StartingCard) card.getCard()).getBonusResources()) {
                        midSigns.add(s);
                    }
                } else {
                    for (Corner c : Corner.values()) {
                        corner.put(c, Sign.EMPTY.toString());
                    }
                    midSigns.add(fromKingdomToSign(card.getCard().getKingdom()));
                }
            }
            switch (midSigns.size()) {
                case 1:
                    midSignsString = "  " + midSigns.get(0) + "  ";
                    break;
                case 2:
                    midSignsString = " " + midSigns.get(0) + " " + midSigns.get(1) + " ";
                    break;
                case 3:
                    midSignsString = midSigns.get(0) + " " + midSigns.get(1) + " " + midSigns.get(2);
                    break;
                default:
                    midSignsString = "     ";
                    break;
            }
        }
        return new String[]{
                "┌-----┐---------------┌-----┐",
                "|  "+corner.get(Corner.TOP_LEFT)+"  |               |  "+corner.get(Corner.TOP_RIGHT)+"  |",
                "└-----┘               └-----┘",
                "|           "+midSignsString+"          |",
                "┌-----┐               ┌-----┐",
                "|  "+corner.get(Corner.BOTTOM_LEFT)+"  |               |  "+corner.get(Corner.BOTTOM_RIGHT)+"  |",
                "└-----┘---------------└-----┘",
        };
    }

    public HashMap<Corner, String[]> createArrows(HashMap<Corner, PlayedCard> attachments, PlayedCard focusedCard){
        HashMap<Corner, String[]> arrows = new HashMap<>();
        for(Corner corner: Corner.values()){
            switch (corner){
                case TOP_LEFT:{
                    if (attachments.get(Corner.TOP_RIGHT).getTurnOfPositioning()>focusedCard.getTurnOfPositioning()){
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                " ",
                                " \\ ",
                                " -┘"
                        });
                    }else{
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                "┌- ",
                                "\\ ",
                                "   "
                        });
                    }
                }
                case TOP_RIGHT:{
                    if (attachments.get(Corner.TOP_RIGHT).getTurnOfPositioning()>focusedCard.getTurnOfPositioning()){
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                "   ",
                                " / ",
                                "└- "
                        });
                    }else{
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                " -┐",
                                " / ",
                                "   "
                        });
                    }
                }
                case BOTTOM_LEFT:{
                    if (attachments.get(Corner.BOTTOM_LEFT).getTurnOfPositioning()>focusedCard.getTurnOfPositioning()){
                        arrows.put(Corner.BOTTOM_LEFT, new String[]{
                                " -┐",
                                " / ",
                                "   "
                        });
                    }else{
                        arrows.put(Corner.BOTTOM_LEFT, new String[]{
                                "   ",
                                " / ",
                                "└- "
                        });
                    }
                }
                case BOTTOM_RIGHT:{
                    if (attachments.get(Corner.TOP_RIGHT).getTurnOfPositioning()>focusedCard.getTurnOfPositioning()){
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                "┌- ",
                                "\\ ",
                                "   "
                        });
                    }else{
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                " ",
                                " \\ ",
                                " -┘"
                        });
                    }
                }
            }
        }
        return arrows;
    }

    /**
     * Converts Kingdom enum to Sign enum
     * @param kingdom Kingdom to convert
     * @return Sign in which the Kingdom has been converted
     */
    private Sign fromKingdomToSign(Kingdom kingdom) throws IllegalArgumentException {
        switch (kingdom){
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


    public PlayedCard getTable(String nickname) {
        //TODO get the table (RootCard) of the player with the given nickname
        return null;
    }

    public void defaultMenu(){
        //TODO prints the default menu
    }


    public void cardPositionError() {
        System.out.println("You can't position the card there. Please try another position");
    }

    public void showHiddenHand(String nickname, HashMap<String, ArrayList<PlayedCard>> hiddenCards) {
        //TODO da id a playedCard, chi lo fa
        ArrayList<String[]> cards = new ArrayList<>();
        for(String player: hiddenCards.keySet()){
            System.out.println(player + " has:");
            for(PlayedCard card: hiddenCards.get(player)){
                cards.add(printResourceCard(card));
            }

            int i;
            int size = cards.get(0).length;
            for(i = 0; i < size; i++) {
                for (String[] card : cards) {
                    System.out.print(card[i]);
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

    public void showHand(String nickname, Integer[] hand) {
        PlayedCard card = null;
        ArrayList<String[]> cards = new ArrayList<>();
        for(Integer cardId: hand){
            //TODO card fromId
            cards.add(printResourceCard(card));
        }

        //TODO fare unica funzione private

        int i;
        int size = cards.get(0).length;
        for (i = 0; i < size; i++) {
            for (String[] carte : cards) {
                System.out.print(carte[i]);
                System.out.print("   ");
            }
        }
        System.out.println();
    }

    public void updateHand(String nickname, Integer[] hand) {
        //TODO mettere in memoria. Questo dovrebbe essere disponibile per TUI/GUI
    }

    public void updateHiddenHand(String nickname, Kingdom[] hand) {
        //TODO mettere in memoria
    }
}



