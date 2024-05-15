package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * This class represents the Text User Interface (TUI) of the game.
 * It is responsible for displaying game information to the player and receiving player input.
 */
public class Tui {
    private final Controller controller;
    private final LittleModel model;

    /**
     * Constructor for the TUI.
     * Initializes the TUI with default values.
     */
    public Tui(LittleModel model, Controller controller){
        this.model = model;
        this.controller = controller;
    }

    /**
     * Displays to show the first player to enter the number of players.
     */
    public void showInsertNumberOfPlayer() {
        System.out.println("You are the first player. Please enter the number of players)");
        System.out.println("The number of players must be between 2 and 4");

        Scanner scanner = new Scanner(System.in);
        int numberOfPlayers = scanner.nextInt();
        controller.insertNumberOfPlayers(numberOfPlayers);
    }
    /**
     * Informs the player that they are connected to the server and waiting for the first player to choose the number of players.
     */
    public void waitLobby() {
        System.out.println("You are connected to the server. Please wait for the first player to choose the number of players");
    }

    /**
     * Informs the player that the game is starting.
     */
    public void stopWaiting() {
        System.out.println("The game is starting");
        askColor();
    }

    private void askColor() {
        System.out.println("Choose your color");
        System.out.println("1 - Blue\n" +
                "2 - Yellow\n" +
                "3 - Green\n" +
                "4 - Red\n");
        Scanner scanner = new Scanner(System.in);
        int color = scanner.nextInt();
        switch (color){
            case 1:
                controller.chooseColor(Color.BLUE);
                break;
            case 2:
                controller.chooseColor(Color.YELLOW);
                break;
            case 3:
                controller.chooseColor(Color.GREEN);
                break;
            case 4:
                controller.chooseColor(Color.RED);
                break;
            default:
                System.out.println("Invalid input");
        }
    }


    /**
     * Informs the player that the lobby has been filled with the number of players chosen by the first player.
     */
    public void disconnect() {
        System.out.println("Lobby has been fulled with number of parameters chosen by the first player");
    }

    /**
     * Displays the players in the lobby and their associated colors.
     * @param playersAndPins A HashMap containing the player nicknames as keys and their associated colors as values.
     */
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        System.out.println("The players in the lobby are:");
        for (String nickname : playersAndPins.keySet()) {
            Color color = playersAndPins.get(nickname);
            System.out.println(nickname + " - " + Objects.requireNonNullElse(color, "no color"));
        }
        //TODO ask daniel, per salvataggio dei nomi
    }

    /**
     * Displays the starting card to the player.
     * @param startingCardId The ID of the starting card.
     */
    public void showStartingCard(int startingCardId) {
        PlayedCard card = model.getStartingCard(startingCardId);
        ArrayList<String[]> cards = new ArrayList<>();
        int i;

        //Convert the PlayedCard into a string to print it
        for(i = 0; i < 2; i++) {
            cards.add(createCardToPrint(card));
        }

        System.out.println("Choose the side of the staring Card. The one on the left is the top, the one on the right is the back");
        int size = cards.get(0).length;
        for(i = 0; i < size; i++) {
            for(String[] cardsToPrint : cards) {
                System.out.print(cardsToPrint[i]);
                System.out.print("    ");
            }
        }
    }

    /**
     * Displays the common table to the player.
     * @param resourceCards The resource cards on the table.
     * @param goldCard The gold cards on the table.
     * @param resourceCardOnDeck The resource card on deck.
     * @param goldCardOnDeck The gold card on deck.
     */
    public void showCommonTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck, Kingdom goldCardOnDeck) {
        ArrayList<String[]> resourceCardsToPrint = new ArrayList<>();
        ArrayList<String[]> goldCardsToPrint = new ArrayList<>();

        for(Integer cardId: resourceCards){
            resourceCardsToPrint.add(createCardToPrint(model.getCard(cardId)));
        }
        resourceCardsToPrint.add(createCardToPrint(model.getCardFromKingdom()));

        for(Integer cardId: goldCard){
            goldCardsToPrint.add(createCardToPrint(model.getCard(cardId)));
        }
        goldCardsToPrint.add(createCardToPrint(model.getCardFromKingdom()));

        int i;
        int size = resourceCardsToPrint.get(0).length;

        System.out.println("These are the resource cards deck:");
        for(i = 0; i < size; i++) {
            for(String[] card: resourceCardsToPrint) {
                System.out.print(card[i]);
                System.out.print("    ");
            }
        }
        System.out.println();
        System.out.println("The gold card deck is:");
        for(i = 0; i < size; i++) {
            for(String[] card: goldCardsToPrint) {
                System.out.print(card[i]);
                System.out.print("    ");
            }
        }
        System.out.println();
    }

    /**
     * Displays the common objective cards to the player.
     * @param objectiveCardIds The IDs of the objective cards.
     */
    public void showCommonObjectives(Integer[] objectiveCardIds) {
        List<String[]> cards = new ArrayList<>();

        for(Integer cardId: objectiveCardIds){
            cards.add(createObjectiveCardToPrint(cardId));
        }

        int size = cards.get(0).length;
        int i;

        System.out.println("These are the common objective cards:");
        for(i = 0; i < size; i++) {
            for(String[] row : cards) {
                System.out.print(row[i]);
                System.out.print("    ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Prints the secret objective card of the player.
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

    /**
     * Displays the secret objective cards that the player can choose from.
     * This method retrieves the secret objective cards from the model, converts them into a printable format,
     * and then prints them to the console for the player to view and choose from.
     *
     * @param objectiveCardIds An array of IDs of the secret objective cards that the player can choose from.
     */
    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {
        List<String[]> cards = new ArrayList<>();

        for(Integer cardId: objectiveCardIds){
            cards.add(createObjectiveCardToPrint(cardId));
        }

        int size = cards.get(0).length;
        int i;

        System.out.println("These are the secret objective cards you can choose. Please choose one");
        //TODO numbers.
        for(i = 0; i < size; i++) {
            for(String[] row : cards) {
                System.out.print(row[i]);
                System.out.print("    ");
            }
            System.out.println();
        }
        System.out.println();
    }
    /**
     * Created the objective card of the player.
     * This method takes an index of a card as input, retrieves the corresponding card
     * and return the representation of the card on the console. The representation of the card
     * is an array of strings, with each string representing a row of the card.
     *
     * @param indexCard The index of the objective card to print.
     */
    public String[] createObjectiveCardToPrint(int indexCard) {
        ObjectiveCard card = model.getObjectiveCard(indexCard);
        String[] objective = null;

        //TODO ricontrolla con agnoli
        switch(card.getType()){
            case STAIR:
                if(card.getKingdom() == Kingdom.FUNGI || card.getKingdom() == Kingdom.ANIMAL) {
                    objective = new String[]{
                            "      ***",
                            "   ***",
                            "***"
                    };

                }else {
                    objective = new String[]{
                            "***      ",
                            "   ***   ",
                            "      ***"
                    };
                }
                    break;
            case L_FORMATION:
                if(card.getKingdom() == Kingdom.FUNGI) {
                    objective = new String[]{
                            "F",
                            "F",
                            "   P"
                    };
                }else if(card.getKingdom() == Kingdom.ANIMAL) {
                    objective = new String[]{
                            "   F",
                            "A",
                            "A"
                    };
                }else if(card.getKingdom() == Kingdom.INSECT) {
                    objective = new String[]{
                            "A",
                            "  I",
                            "  I"
                    };
                }else {
                    objective = new String[]{
                            "  P",
                            "  P",
                            "I"
                    };
                }
                break;
            case FREE_RESOURCES:
                //TODO cosa si intende
                break;
            case TRIS:
                if(card.getKingdom() == Kingdom.FUNGI) {
                    objective = new String[]{
                            "  F  ",
                            "F   F",
                            "     "
                    };
                }else if(card.getKingdom() == Kingdom.ANIMAL) {
                    objective = new String[]{
                            "  A  ",
                            "A   A",
                            "     "
                    };

                } else if (card.getKingdom() == Kingdom.PLANT) {
                    objective = new String[]{
                            "  P  ",
                            "P   P",
                            "     "
                    };
                } else if (card.getKingdom() == Kingdom.INSECT) {
                    objective = new String[]{
                            "  I  ",
                            "I   I",
                            "     "
                    };
                }
                break;
            default:
        }
        return new String[]{
                "┌---------------------------┐",
                "|            "+card.getMultiplier()+"              |",
                "|          "+card.getKingdom()+"            |",
                "|                           |",
                "|          "+objective[0]+"            |",
                "|          "+objective[1]+"            |",
                "|          "+objective[2]+"            |",
                "|                           |",
                "└---------------------------┘",
        };
    }

    /**
     * Return the card of the player.
     * This method takes an index of a card as input, retrieves the corresponding card
     * and prints the representation of the card on the console. The representation of the card
     * is an array of strings, with each string representing a row of the card.
     *
     * @param card The playedCardToPrint of the card to print.
     */
    //TODO sistemare
    public String[] createCardToPrint(PlayedCard card) {
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
                "┌--------┐---------------┌--------┐",
                "|  "+corner.get(Corner.TOP_LEFT)+" |               |  "+corner.get(Corner.TOP_RIGHT)+" |",
                "└--------┘               └--------┘",
                "|           "+midSignsString+"          |",
                "┌--------┐               ┌--------┐",
                "|  "+corner.get(Corner.BOTTOM_LEFT)+" |               |  "+corner.get(Corner.BOTTOM_RIGHT)+" |",
                "└--------┘---------------└--------┘",
        };
    }

    /**
     * Displays the turn information to the player.
     * @param currentPlayer The current player.
     * @param gameState The current game state.
     */
    public void showTurnInfo(String currentPlayer, GameState gameState) {
        System.out.println("It's " + currentPlayer + "'s turn");
        System.out.println("The game phase is: " + gameState);
    }

    /**
     * Displays the extra points to the player.
     * @param extraPoints The extra points of the players.
     */
    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
        System.out.println("The points made by ObjectiveCards are:");
        for(String player: extraPoints.keySet()) {
            System.out.println(player + " - " + extraPoints.get(player));
        }
    }

    /**
     * Displays the ranking to the player.
     * @param ranking The ranking of the players.
     */
    public void showRanking(ArrayList<Player> ranking) {
        System.out.println("The ranking is:");
        for(Player player: ranking) {
            System.out.println(player);
        }
    }

    public void printTableAreaOfPlayer(String nickname){
        printTableArea(getTable(nickname));
    }

    private void printTableArea(PlayedCard card){
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
            attachmentsStrings.put(c, createCardToPrint(attachments.get(c)));
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


    public PlayedCard getTable(String nickname) {
        //TODO get the table (RootCard) of the player with the given nickname
        return null;
    }

    /**
     * Prints the default menu, with all the options to choose from.
     */
    public void defaultMenu(){
        System.out.println("please insert a number for choosing the option");
        System.out.println("" +
                "1 - place a card\n" +
                "2 - draw a card\n" +
                "3 - show the table of a player\n" +
                "4 - show my resources\n" +
                "5 - show all players resources\n" +
                "6 - show the points of the players\n" +
                "7 - show my hand\n" +
                "8 - show the hidden hand of a player\n" );
    }

    public void startPhase(){
        System.out.println("please insert a number for choosing the option");
        System.out.println("" +
                "9 - login\n" +
                "10 - chooseColor\n" +
                "11 - choose Side StartingCard \n" +
                "12 - choose Objective Card\n");

    }

    /**
     * Shows the hidden hand of a player.
     * @param nickname The nickname of the player.
     */
    public void showHiddenHand(String nickname) {
        Pair<Kingdom, Boolean>[] hand = model.getHiddenHand(nickname);
        ArrayList<String[]> cards = new ArrayList<>();
        int i;

        for(i = 0; i < hand.length; i++){
            cards.add(null); //TODO LOGICA
        }

        int size = cards.get(0).length;
        System.out.println("These are the hidden cards of " + nickname + ":");
        for(i = 0; i < size; i++) {
            for (String[] card : cards) {
                System.out.print(card[i]);
                System.out.print("   ");
            }
        }
        System.out.println();
    }

    /**
     * Shows the hand of the client.
     */
    public void showHand() {
        Integer[] myCards = model.getHand();
        ArrayList<String[]> cards = new ArrayList<>();

        for(Integer cardId: myCards){
            cards.add(createCardToPrint(model.getCard(cardId)));
        }

        System.out.println("These are your cards:");
        int size = cards.get(0).length;
        for (int i = 0; i < size; i++) {
            for (String[] carte : cards) {
                System.out.print(carte[i]);
                System.out.print("   ");
            }
        }
        System.out.println();
    }

    /**
     * Shows the resources of the client.
     * @param name The name of client.
     */
    public void showResourcesPlayer(String name) {
        HashMap<String, HashMap<Sign, Integer>> resources = model.getResources();

        System.out.println("You have the following resources:");
        for(Sign sign: resources.get(name).keySet()){
            System.out.println(sign + " - " + resources.get(name).get(sign));
        }
    }

    /**
     * Shows the resources of all players.
     */
    public void showResourcesAllPlayers() {
        HashMap<String, HashMap<Sign, Integer>> resources = model.getResources();
        for(String player: resources.keySet()){
            System.out.println(player + " has:");
            for(Sign sign: resources.get(player).keySet()){
                System.out.println(sign + " - " + resources.get(player).get(sign));
            }
        }
    }


    public void showPoints(HashMap<String, Integer> points) {
        System.out.println("The points of the players are:");
        for(String player: points.keySet()){
            System.out.println(player + " has " + points.get(player));
        }
        System.out.println();
    }

    /**
     * Informs the player that the chosen color is already taken.
     */
    public void colorAlreadyTaken() {
        System.out.println("The color you chose is already taken. Please choose another one");
    }

    /**
     * Informs the player that the chosen nickname is already taken.
     * @param nickname The chosen nickname.
     */
    public void sameName(String nickname) {
        System.out.println("The nickname " + nickname + " is already taken. Please choose another one");
    }

    /**
     * Informs the player that it's not their turn.
     */
    public void noTurn() {
        System.out.println("It's not your turn. You can't perform this action");
    }

    /**
     * Informs the player that they don't have enough resources.
     * @param name The name of the player.
     */
    public void notEnoughResources(String name) {
        System.out.println("You don't have enough resources to perform this action");
    }

    /**
     * Informs the player that they are not connected to the server.
     */
    public void noConnection() {
        System.out.println("You are not connected to the server. Please retry\n");
    }

    /**
     * Informs the player that they can't position the card there.
     */
    public void cardPositionError() {
        System.out.println("You can't position the card there. Please try another position");
    }

    /**
     * Informs the player that the lobby is full.
     */
    public void lobbyComplete() {
        System.out.println("The lobby is full. No other players can join");
    }

    /**
     * Informs the player that he can't perform the action in this game phase.
     */
    public void wrongGamePhase() {
        System.out.println("You can't perform this action in this game phase");
    }
    /**
     * Informs the client that the name given doesn't exist.
     */
    public void noPlayer() {
        System.out.println("The player doesn't exist");
    }

    /**
     * Informs the player that the lobby is closed. A game already started.
     */
    public void closingLobbyError() {
        System.out.println("The input is not correct. Please retry");
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


    public void showTableOfPlayer(Object tableOfPlayer) {
        //TODO
    }

    public void printCard(int id) {
        String[] card = createCardToPrint(model.getCard(id));
        for(String row: card){
            System.out.println(row);
        }

    }

    public void askInput() {
            while(true){
                defaultMenu();
                startPhase();
                Scanner scanner = new Scanner(System.in);
                Integer input = scanner.nextInt();
                switch (input){
                    case 1:
                        Integer indexHand = scanner.nextInt();
                        Point position = new Point();
                        position.x = scanner.nextInt();
                        position.y = scanner.nextInt();
                        boolean isFacingUp = scanner.nextBoolean();
                        controller.playCard(indexHand, position, isFacingUp);
                        break;
                    case 2:
                        boolean gold = scanner.nextBoolean();
                        int onTableOrDeck = scanner.nextInt();
                        controller.drawCard(gold, onTableOrDeck);
                        break;
                    case 3:
                        controller.showTableOfPlayer(scanner.nextLine());
                        break;
                    case 4:
                        controller.showResources();
                        break;
                    case 5:
                        controller.showResourcesAllPlayers();
                        break;
                    case 6:
                        controller.showPoints();
                        break;
                    case 7:
                        controller.showHand();
                        break;
                    case 8:
                        controller.showHiddenHand(scanner.nextLine());
                        break;
                    case 9:
                        System.out.println("Please enter your nickname");
                        String name = scanner.nextLine();
                        controller.login(name);
                        break;
                    case 10:
                        controller.chooseColor(Color.valueOf(scanner.nextLine()));
                    case 11:
                        controller.chooseSideStartingCard(scanner.nextBoolean());
                        break;
                    case 12:
                        controller.chooseSecretObjectiveCard(scanner.nextInt());
                        break;
                    default:
                        System.out.println("Invalid input");
                }
            }

        }

    public void start() {
        askInput();
    }

    public void showIsFirst(String firstPlayer) {
        System.out.println("The fist player is" + firstPlayer +  ". Please enter the number of players" );
    }
}




