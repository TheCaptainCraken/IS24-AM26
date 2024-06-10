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
 * The TUI communicates with the game's controller to send player actions and receive updates about the game state.
 * It also uses the game's model to retrieve information about the game state for display.
 * The TUI is designed to be used in a console environment.

 * It implements the ViewInterface interface, which defines the methods that every type of view should implement.
 *
 * @author PietroBenecchi
 */
public class TUI implements ViewInterface {
    /**
     * Controller of the game. It is used to send player actions to the game.
     */
    private final Controller controller;
    /**
     * Model of the game. It is used to retrieve information about the game state
     */
    private final LittleModel model;

    /**
     * Constructor for the TUI.
     * Initializes the TUI with default values.
     */
    public TUI(LittleModel model, Controller controller) {
        this.model = model;
        this.controller = controller;
    }

    /**
     * Informs the player that they are connected to the server and waiting for the first player to choose the number of players.
     */
    @Override
    public void waitLobby() {
        System.out.println("You are connected to the server. Please wait for all players to join the game");
    }

    /**
     * Informs the player that the game is starting.
     */
    @Override
    public void stopWaiting() {
        System.out.println("The game is starting");
    }

    @Override
    public synchronized void correctNumberOfPlayers(int numberOfPlayers) {
        System.out.println("You have correctly set the number of players");
        System.out.println("The number of players are " + numberOfPlayers);
    }

    /**
     * Informs the player that the lobby has been filled with the number of players chosen by the first player.
     */
    @Override
    public void disconnect() {
        System.out.println("Lobby has been fulled with number of parameters chosen by the first player");
    }

    /**
     * Displays the players in the lobby and their associated colors.
     *
     * @param playersAndPins A HashMap containing the player nicknames as keys and their associated colors as values.
     */
    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        System.out.println("The players in the lobby are:");
        for (String nickname : playersAndPins.keySet()) {
            Color color = playersAndPins.get(nickname);
            System.out.println(nickname + " - " + Objects.requireNonNullElse(color, "no color"));
        }
    }

    /**
     * Displays the name of the first player in the game.
     *
     * This method is called when the game is about to start and the first player has been determined.
     * It displays a message to the console indicating who the first player is.
     *
     * @param firstPlayer The name of the first player.
     */
    @Override
    public synchronized void showIsFirst(String firstPlayer) {
        System.out.println("The first player is " + firstPlayer + ". The game is starting");
    }
    /**
     * Displays the starting card to the player.
     *
     * @param startingCardId The ID of the starting card.
     */
    @Override
    public synchronized void showStartingCard(int startingCardId) {
        //same starting card, we simply change the side
        PlayedCard card = model.getStartingCard(startingCardId, true);
        PlayedCard cardBack = model.getStartingCard(startingCardId, false);
        ArrayList<String[]> cards = new ArrayList<>();
        int i;

        //Convert the PlayedCard into a string to print it
        cards.add(createCardToPrint(card));
        cards.add(createCardToPrint(cardBack));

        System.out.println("Choose the side of the starting Card. The one on the left is the top, the one on the right is the back");
        int size = cards.get(0).length;
        for (i = 0; i < size; i++) {
            for (String[] cardsToPrint : cards) {
                System.out.print(cardsToPrint[i]);
                System.out.print("   ");
            }
            System.out.println();
        }
    }
    /**
     * Displays the common table of cards to the player. 2 resource cards, 2 gold card and the cards on the deck.
     *
     * This method is called to show the current state of the common table of cards in the game.
     * It retrieves the cards from the model and prints them to the console for the player to view.
     */
    @Override
    public synchronized void showCommonTable() {
        //the cards on the table
        Integer[] resourceCards = model.getResourceCards();
        Integer[] goldCard = model.getGoldCards();
        //the cards on the deck
        Kingdom resourceCardOnDeck = model.getHeadDeckResource();
        Kingdom goldCardOnDeck = model.getHeadDeckGold();

        //create the cards to print(resource cards)
        ArrayList<String[]> cardsToPrint = new ArrayList<>();
        for (Integer cardId : resourceCards) {
            cardsToPrint.add(createCardToPrint(model.getCard(cardId, true)));
        }
        cardsToPrint.add(createCardToPrint(fromKingdomToCard(resourceCardOnDeck)));

        //create the cards to print(gold cards)
        ArrayList<String[]> goldCardsToPrint = new ArrayList<>();
        for (Integer cardId : goldCard) {
            goldCardsToPrint.add(createCardToPrint(model.getCard(cardId, true)));
        }
        goldCardsToPrint.add(createCardToPrint(fromKingdomToCard(goldCardOnDeck)));

        System.out.println("This is the resource card deck:\n");
        printCardArray(cardsToPrint);
        System.out.println("This is the gold card deck:\n");
        printCardArray(goldCardsToPrint);

    }
    /**
     * Displays the common objective cards to the player.
     *
     * @param objectiveCardIds The IDs of the objective cards.
     */
    @Override
    public synchronized void showCommonObjectives(Integer[] objectiveCardIds) {
        List<String[]> cards = new ArrayList<>();

        for (Integer cardId : objectiveCardIds) {
            cards.add(createObjectiveCardToPrint(cardId));
        }

        int size = cards.get(0).length;
        int i;

        System.out.println("These are the common objective cards:");
        for (i = 0; i < size; i++) {
            for (String[] row : cards) {
                System.out.print(row[i]);
                System.out.print("    ");
            }
            System.out.println();
        }
        System.out.println();
    }
    /**
     *
     * This method is responsible for asking the user to choose a color for their player.
     * The user is presented with a list of colors to choose from, and their input is read from the console.
     * The chosen color is then sent to server.
     * If the user enters an invalid input, they are asked to choose a color again.
     *
     */
    public void askChooseColor() {
        System.out.println(Controller.getPhase());
        System.out.println("Choose your color");
        System.out.println("1 - Blue\n" +
                "2 - Yellow\n" +
                "3 - Green\n" +
                "4 - Red\n");

        Scanner scanner = new Scanner(System.in);
        int color;
        boolean validInput = false;
        do {
            try {
                color = scanner.nextInt();
                switch (color) {
                    case 1:
                        ViewSubmissions.getInstance().chooseColor(Color.BLUE);
                        validInput = true;
                        break;
                    case 2:
                        ViewSubmissions.getInstance().chooseColor(Color.YELLOW);
                        validInput = true;
                        break;
                    case 3:
                        ViewSubmissions.getInstance().chooseColor(Color.GREEN);
                        validInput = true;
                        break;
                    case 4:
                        ViewSubmissions.getInstance().chooseColor(Color.RED);
                        validInput = true;
                        break;
                    default:
                        System.out.println("Invalid input");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.next(); // discard the invalid input
            }
        } while (!validInput);
    }
    /**
     * Displays the secret objective cards that the player can choose from.
     * This method retrieves the secret objective cards from the model, converts them into a printable format,
     * and then prints them to the console for the player to view and choose from.
     *
     * @param objectiveCardIds An array of IDs of the secret objective cards that the player can choose from.
     */
    @Override
    public synchronized void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {
        List<String[]> cards = new ArrayList<>();

        for (Integer cardId : objectiveCardIds) {
            cards.add(createObjectiveCardToPrint(cardId));
        }

        int size = cards.get(0).length;
        int i;

        System.out.println("These are the secret objective cards you can choose. Please choose one");
        for (i = 0; i < size; i++) {
            for (String[] row : cards) {
                System.out.print(row[i]);
                System.out.print("    ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Enter 0 or 1:");
    }
    /**
     * Prints the secret objective card of the player.
     * This method takes an index of a card as input, retrieves the corresponding card
     * and prints the representation of the card on the console. The representation of the card
     * is an array of strings, with each string representing a row of the card.
     *
     * @param indexCard The index of the secret objective card to print.
     */
    @Override
    public synchronized void showSecretObjectiveCard(int indexCard) {
        System.out.println("This is your secret objective card: ");
        String[] secretObjective = createObjectiveCardToPrint(indexCard);
        for (String row : secretObjective) {
            System.out.println(row);
        }
    }
    /**
     * Displays the turn information to the player.
     *
     * @param currentPlayer The current player.
     * @param gameState     The current game state.
     */
    @Override
    public synchronized void showTurnInfo(String currentPlayer, GameState gameState) {
        System.out.println("It's " + currentPlayer + "'s turn");
        System.out.println("The game phase is: " + gameState);
    }
    /**
     * Shows the resources of the client.
     *
     */
    @Override
    public synchronized void showResourcesPlayer() {
        String name = controller.getNickname();
        HashMap<String, HashMap<Sign, Integer>> resources = model.getResources();

        System.out.println("You have the following resources:");
        for (Sign sign : resources.get(name).keySet()) {
            if(sign != Sign.NULL && sign != Sign.EMPTY) {
                System.out.println(sign + " - " + resources.get(name).get(sign));
            }
        }
    }
    /**
     * Shows the resources of all players.
     */
    public synchronized void showResourcesAllPlayers() {
        HashMap<String, HashMap<Sign, Integer>> resources = model.getResources();
        for (String player : resources.keySet()) {
            System.out.println();
            System.out.println(player + " has:");
            for (Sign sign : resources.get(player).keySet()) {
                if(sign != Sign.NULL && sign != Sign.EMPTY) {
                    System.out.println(sign + " - " + resources.get(player).get(sign));
                }
            }
        }
    }
    /**
     * This method is responsible for displaying the points of all players.
     * It retrieves the points of each player from the model and prints them to the console.
     * The points are displayed in the format: player - points.
     */
    @Override
    public synchronized void showPoints() {
        HashMap<String, Integer> points = model.getPoints();
        System.out.println("The points of the players are:");
        for (String player : points.keySet()) {
            System.out.println(player + " has " + points.get(player));
        }
        System.out.println();
    }
    /**
     * Displays the extra points to the player.
     *
     * @param extraPoints The extra points of the players.
     */
    @Override
    public synchronized void showExtraPoints(HashMap<String, Integer> extraPoints) {
        System.out.println("The points made by ObjectiveCards are:");
        for (String player : extraPoints.keySet()) {
            System.out.println(player + " - " + extraPoints.get(player));
        }
    }
    /**
     * Displays the ranking to the player.
     *
     * @param ranking The ranking of the players.
     */
    @Override
    public synchronized void showRanking(ArrayList<Player> ranking) {
        System.out.println("The ranking is:");
        for (Player player : ranking) {
            System.out.println(player);
        }
    }
    /**
     * Displays the table of a specific player.
     *
     * It retrieves the table from the model and prints it to the console for the player to view.
     *
     * @param nickname The nickname of the player whose table is to be displayed.
     */
    @Override
    public void showTableOfPlayer(String nickname) {
        printTableAreaOfPlayer(nickname);
    }
    /**
     * Shows the hand of the client.
     */
    @Override
    public synchronized void showHand() {
        Integer[] myCards = model.getHand();
        ArrayList<String[]> cards = new ArrayList<>();

        for (Integer cardId : myCards) {
            cards.add(createCardToPrint(model.getCard(cardId, true)));
        }

        System.out.println("These are your cards:");
        int size = cards.get(0).length;
        for (int i = 0; i < size; i++) {
            for (String[] carte : cards) {
                System.out.print(carte[i]);
                System.out.print("   ");
            }
            System.out.println();
        }
        System.out.println();
    }
    /**
     * Informs the player that the chosen color is already taken.
     */
    @Override
    public synchronized void colorAlreadyTaken() {
        System.out.println("The color you chose is already taken. Please choose another one");
    }
    /**
     * Informs the player that the chosen nickname is already taken.
     *
     * @param nickname The chosen nickname.
     */
    @Override
    public synchronized void sameName(String nickname) {
        System.out.println("The nickname " + nickname + " is already taken. Please choose another one");
    }
    /**
     * Informs the player that it's not their turn.
     */
    @Override
    public synchronized void noTurn() {
        System.out.println("It's not your turn. You can't perform this action");
    }
    /**
     * Informs the player that they don't have enough resources.
     *
     */
    @Override
    public synchronized void notEnoughResources() {
        System.out.println("You don't have enough resources to perform this action");
    }
    /**
     * Informs the player that they are not connected to the server.
     */
    @Override
    public synchronized void noConnection() {
        System.out.println("You are not connected to the server. Game will end soon.\n");
        System.out.println("Thank you for playing. Goodbye!");
    }
    /**
     * Informs the player that they can't position the card there.
     */
    @Override
    public synchronized void cardPositionError() {
        System.out.println("You can't position the card there. Please try another position");
    }
    /**
     * Informs the player that the lobby is full.
     */
    @Override
    public synchronized void lobbyComplete() {
        System.out.println("The lobby is full. No other players can join");
    }
    /**
     * Informs the player that he can't perform the action in this game phase.
     */
    @Override
    public synchronized void wrongGamePhase() {
        System.out.println("You can't perform this action in this game phase");
    }
    /**
     * Informs the client that the name given doesn't exist.
     */
    @Override
    public synchronized void noPlayer() {
        System.out.println("The player doesn't exist");
    }
    /**
     * Informs the player that the lobby is closed. A game already started.
     */
    @Override
    public synchronized void closingLobbyError() {
        System.out.println("You haven't fill the lobby with the correct number of players. The lobby is closing");
    }

    @Override
    public void showStartingCardChosen() {
        //TODO
    }

    @Override
    public void stopGaming() {
        //TODO
    }

    /**
     * Displays to show the first player to enter the number of players.
     */
    @Override
    public void askNumberOfPlayers() {
        System.out.println("You are the first player. Please enter the number of players");
        System.out.println("The number of players must be between 2 and 4");

        Scanner scanner = new Scanner(System.in);
        int numberOfPlayers = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                numberOfPlayers = scanner.nextInt();
                if (numberOfPlayers >= 2 && numberOfPlayers <= 4) {
                    validInput = true;
                } else {
                    System.out.println("Invalid input. Please enter a number between 2 and 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 2 and 4.");
                scanner.next(); // discard the invalid input
            }
        }
        ViewSubmissions.getInstance().chooseNumberOfPlayers(numberOfPlayers);
        //TODO dividere fasi
    }
    /**
     * Displays the table of a specific player chosen by the user.
     *
     * This method asks the user to input the nickname of a player. It then retrieves the table of the player
     * with the given nickname from the model and prints it to the console for the user to view.
     *
     * If the given nickname is not valid, the method asks the user to input a valid nickname.
     */
    private void showTableOfPlayerGivenName() {
        Scanner scanner = new Scanner(System.in);
        String nickname;
        System.out.println("Please insert the nickname of the player you want to see the table of");
        for(String player: model.getOtherPlayersCards().keySet()){
            System.out.println(player);
        }
        //open the scanner for the nickname. The nickname must be valid
        nickname = scanner.nextLine();
        //if is not valid, it asks again
        while(!model.getOtherPlayersCards().containsKey(nickname)){
            System.out.println("The nickname you inserted is not valid. Please insert a valid nickname");
            nickname = scanner.nextLine();
        }
        //print the table given a correct name.
        printTableAreaOfPlayer(nickname);
    }
    /**
     * Asks the user to draw a card from the game deck.
     *
     * This method interacts with the user to draw a card from the game deck. It first displays the common table of cards
     * to the user. Then it asks the user to input whether the card is gold and the location from where the card is drawn.
     * The user's input is read from the console and then sent to the server.
     * If the user enters an invalid input, they are asked to provide the information again.
     */
    private void askDrawCard() {
        Scanner scanner = new Scanner(System.in);
        boolean gold;
        int onTableOrDeck;
        boolean validInput = false;

        showCommonTable();
        while (!validInput) {
            try {
                System.out.println("Enter true if the card is gold, false otherwise:");
                gold = scanner.nextBoolean();
                System.out.println("Enter -1, 0 or 1 if the card is on table, if it's on deck:");
                onTableOrDeck = scanner.nextInt();
                if (onTableOrDeck != -1 && onTableOrDeck != 0 && onTableOrDeck != 1) {
                    System.out.println("Invalid input. Value must be -1, 0 or 1.");
                    continue;
                }
                ViewSubmissions.getInstance().drawCard(gold, onTableOrDeck);
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter the correct values.");
                scanner.next(); // Consumes the invalid input
            }
        }
    }
    /**
     * This method is responsible for asking the user to place a card on the table.
     * The user is asked to provide the index of the card in their hand, the position on the table where they want to place the card,
     * and whether the card should be placed face up or face down.
     * The user's input is read from the console and then sent to the server.
     * If the user enters an invalid input, they are asked to provide the information again.
     */
    private void askPlaceCard() {
        Scanner scanner = new Scanner(System.in);
        Integer indexHand;
        Point position = new Point();
        boolean isFacingUp;
        boolean validInput = false;

        showHand();
        while (!validInput) {
            try {
                System.out.println("Enter index of the card in hand (1-3):");
                indexHand = scanner.nextInt();
                if (indexHand < 1 || indexHand > 3) {
                    System.out.println("Invalid input. Index must be between 1 and 3.");
                    continue;
                }
                System.out.println("Enter position x:");
                position.x = scanner.nextInt();
                System.out.println("Enter position y:");
                position.y = scanner.nextInt();
                System.out.println("Enter true if the card is facing up, false otherwise:");
                isFacingUp = scanner.nextBoolean();
                ViewSubmissions.getInstance().placeCard(indexHand - 1, position, isFacingUp);
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter the correct values.");
                scanner.next(); // Consumes the invalid input
            }
        }
    }
    /**
     * This method is responsible for asking the user to choose a nickname for their player.
     * The user's input is read from the console and then sent to the server.
     * The chosen nickname is then used to identify the player in the game.
     * If the user enters a nickname that contains spaces, they are asked to choose a nickname again.
     */
    private void askChooseNickname() {
        Scanner scanner = new Scanner(System.in);
        String nickname;

        do {
            System.out.println("Please enter your nickname (without spaces):");
            nickname = scanner.nextLine();
        } while (nickname.contains(" "));
        //sent information to the server
        ViewSubmissions.getInstance().chooseNickname(nickname);
    }
    /**
     * Shows the hidden hand of a player.
     */
    @Override
    public synchronized void showHiddenHand(String name) {
        //name is not used, but is necessary for the interface
        Set<String> players = model.getOtherPlayersCards().keySet();

        // Iterate over all players
        for (String player : players) {
            Pair<Kingdom, Boolean>[] hand = model.getHiddenHand(player);
            ArrayList<String[]> cards = new ArrayList<>();
            int i;

            // Create a printable representation for each card in the hand
            for (i = 0; i < hand.length; i++) {
                cards.add(createCardToPrint(fromKingdomToCard(hand[i].getKey())));
            }

            int size = cards.get(0).length;
            System.out.println("These are the hidden cards of " + player + ":");
            for (i = 0; i < size; i++) {
                for (String[] card : cards) {
                    System.out.print(card[i]);
                    System.out.print("   ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
    /**
     * Asks the user to choose a secret objective card.
     *
     * This method interacts with the user to choose a secret objective card. It first displays the secret objective cards
     * to the user. Then it asks the user to input the index of the card they want to choose.
     * The user's input is read from the console and then sent to the server.
     * If the user enters an invalid input, they are asked to provide the information again.
     */
    private void askChooseSecretObjectiveCard() {
        Scanner scanner = new Scanner(System.in);
        int indexCard = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                indexCard = scanner.nextInt();
                if (indexCard == 0 || indexCard == 1) {
                    ViewSubmissions.getInstance().chooseSecretObjectiveCard(indexCard);
                    validInput = true;
                } else {
                    System.out.println("Invalid input. Please enter 0 or 1.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter 0 or 1.");
                scanner.next(); // Consumes the invalid input
            }
        }
    }
    /**
     * Asks the user to choose a starting card.
     *
     * This method interacts with the user to choose a starting card. It first displays the starting cards
     * to the user. Then it asks the user to input whether the card is facing up or down.
     * The user's input is read from the console and then sent to the server.
     * If the user enters an invalid input, they are asked to provide the information again.
     */
    private void askChooseStartingCard() {
        Scanner scanner = new Scanner(System.in);
        boolean isFacingUp = false;

        String side;
        do {
            side = scanner.nextLine();
            if (side.equals("true"))
                isFacingUp = true;
            else if (side.equals("false")) {
                isFacingUp = false;}
            else
                System.out.println("Invalid input");
        } while (!side.equals("true") && !side.equals("false"));
        ViewSubmissions.getInstance().chooseStartingCard(isFacingUp);
    }
    /**
     * Starts the game's Text User Interface (TUI).
     *
     * This method is responsible for starting the game's TUI. It is called when the game is about to start.
     * It continuously runs a loop to keep the game running until it ends.
     * Inside the loop, it displays the default menu to the user and handles their input.
     */
    @Override
    public void start() {
        while (true) {
            switch (Controller.getPhase()) {
                case LOGIN:
                    askChooseNickname();
                    break;
                case COLOR:
                    askChooseColor();
                    break;
                case CHOOSE_SIDE_STARTING_CARD:
                    // server sends the starting card and the player chooses the side
                    askChooseStartingCard();
                    break;
                case CHOOSE_SECRET_OBJECTIVE_CARD:
                    //server sends the secret objective cards and the player chooses one
                    askChooseSecretObjectiveCard();
                    break;
                case WAIT:
                    //TODO
                    break;
                case WAIT_NUMBER_OF_PLAYERS:
                    // wait that the first player choice the number of players
                    //TODO
                    break;
                case GAMEFLOW:
                    // the game is started, the player can perform any action
                    defaultMenu();
                    break;
            }
        }

    }
    /**
     * Prints an array of cards.
     *
     * This method takes an ArrayList of String arrays, where each String array represents a card,
     * and prints each card to the console. Each String in the array represents a row of the card.
     * The cards are printed row by row, with each card's row printed side by side.
     *
     * @param cardsToPrint An ArrayList of String arrays, where each String array represents a card to be printed.
     */
    public void printCardArray(ArrayList<String[]> cardsToPrint) {
        int size = cardsToPrint.get(0).length;
        int i;
        for (i = 0; i < size; i++) {
            for (String[] card : cardsToPrint) {
                System.out.print(card[i]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    /**
     * It is used to create a card to print when we only know the kingdom, not the card. It is used for the deck.
     * @param kingdom the kingdom of the card
     * @return the card to print
     */
    public PlayedCard fromKingdomToCard(Kingdom kingdom) {
        switch (kingdom) {
            case PLANT:
                return model.getCard(74, false);
            case ANIMAL:
                return model.getCard(85, false);
            case FUNGI:
                return model.getCard(63, false);
            case INSECT:
                return model.getCard(92, false);
        }
        return null;
    }
    /**
     * Prints the table area of a specific player.
     *
     * This method retrieves the table area of the specified player from the model and prints it to the console.
     * The table area is represented as a list of cards, which are printed row by row.
     * Each card is represented as a String array, with each String in the array representing a row of the card.
     *
     * @param nickname The nickname of the player whose table area is to be printed.
     */
    private synchronized void printTableAreaOfPlayer(String nickname) {
        ArrayList<CardClient> cards = model.getListOfCards(nickname);
        ArrayList<String[]> cardsToPrint = new ArrayList<>();

        //order the cards by level at first, secondly by x. The level is the sum of x and y.
        Collections.sort(cards, Comparator.comparing((CardClient card) -> card.getPosition().x + card.getPosition().y, Comparator.reverseOrder())
                .thenComparing(card -> card.getPosition().x));

        int level = cards.get(0).getPosition().x + cards.get(0).getPosition().y;
        int min = cards.stream().mapToInt(card -> card.getPosition().x).min().orElse(0);
        int inizialier = min;

        if(level % 2 == 0){
            //TODO poi togliere only for debugging porpouse
            cardsToPrint.add(new String[]{
                    "┌--------┐--------------",
                    "|        |              ",
                    "└--------┘              ",
                    "|        |              ",
                    "┌--------┐              ",
                    "|        |              ",
                    "└--------┘--------------",
            });
        }
        for(CardClient card: cards){
            //level is the previous, if it's different from the current card, print the previous cards
            if(level == card.getPosition().x + card.getPosition().y){
                while(min != card.getPosition().x){
                    min++;
                    cardsToPrint.add(new String[]{
                            "┌--------┐--------------┌--------┐",
                            "|        |              |        |",
                            "└--------┘              └--------┘",
                            "|        |              |        |",
                            "┌--------┐              ┌--------┐",
                            "|        |              |        |",
                            "└--------┘--------------└--------┘",
                    });
                }
                cardsToPrint.add(createCardToPrint(model.getCard(card.getId(), card.getSide())));
            }else{
                min = inizialier;
                printCardArray(cardsToPrint);
                level = card.getPosition().x + card.getPosition().y;
                cardsToPrint = new ArrayList<>();

                //shift level
                if(level % 2 == 0){
                    cardsToPrint.add(new String[]{
                            "┌--------┐--------------",
                            "|        |              ",
                            "└--------┘              ",
                            "|        |              ",
                            "┌--------┐              ",
                            "|        |              ",
                            "└--------┘--------------",
                    });
                }
                while(min != card.getPosition().x){
                    min++;
                    cardsToPrint.add(new String[]{
                            "┌--------┐--------------┌--------┐",
                            "|        |              |        |",
                            "└--------┘              └--------┘",
                            "|        |              |        |",
                            "┌--------┐              ┌--------┐",
                            "|        |              |        |",
                            "└--------┘--------------└--------┘",

                    });
                }
                cardsToPrint.add(createCardToPrint(model.getCard(card.getId(), card.getSide())));
            }
        }
        //for the last one print the cards
        printCardArray(cardsToPrint);
    }

    /**
     * Prints the default menu, with all the options to choose from.
     */
    private void defaultMenu() {
        System.out.println("please insert a number for choosing the option");
        System.out.println("" +
                "1 - place a card\n" +
                "2 - draw a card\n" +
                "3 - show the table of a player\n" +
                "4 - show my resources\n" +
                "5 - show all players resources\n" +
                "6 - show the points of the players\n" +
                "7 - show my hand\n" +
                "8 - show the hidden hand of a player\n");
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        boolean validInput = false;
        while (!validInput) {
            try {
                choice = scanner.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Input not valid. Please insert a number.");
                scanner.next(); // consume the invalid input
            }
        }
        switch (choice) {
            case 1:
                printTableAreaOfPlayer(controller.getNickname());
                askPlaceCard();
                break;
            case 2:
                askDrawCard();
                break;
            case 3:
                showTableOfPlayerGivenName();
                break;
            case 4:
                showResourcesPlayer();
                break;
            case 5:
                showResourcesAllPlayers();
                break;
            case 6:
                showPoints();
                break;
            case 7:
                showHand();
                break;
            case 8:
                //Name is not used, but is necessary for the interface
                showHiddenHand(null);
                break;
            default:
                System.out.println("Invalid input. Please retry");
                defaultMenu();
        }

    }
    /**
     * Created the objective card of the player.
     * This method takes an index of a card as input, retrieves the corresponding card
     * and return the representation of the card on the console. The representation of the card
     * is an array of strings, with each string representing a row of the card.
     *
     * @param indexCard The index of the objective card to print.
     */
    private String[] createObjectiveCardToPrint(int indexCard) {
        ObjectiveCard card = model.getObjectiveCard(indexCard);
        String[] objective = null;

        switch (card.getType()) {
            case STAIR:
                if (card.getKingdom() == Kingdom.FUNGI || card.getKingdom() == Kingdom.ANIMAL) {
                    objective = new String[]{
                            "      ***",
                            "   ***   ",
                            "***      "
                    };

                } else {
                    objective = new String[]{
                            "***      ",
                            "   ***   ",
                            "      ***"
                    };
                }
                break;
            case L_FORMATION:
                if (card.getKingdom() == Kingdom.FUNGI) {
                    objective = new String[]{
                            "    F    ",
                            "    F    ",
                            "       P "
                    };
                } else if (card.getKingdom() == Kingdom.ANIMAL) {
                    objective = new String[]{
                            "       F ",
                            "    A    ",
                            "    A    "
                    };
                } else if (card.getKingdom() == Kingdom.INSECT) {
                    objective = new String[]{
                            "    A    ",
                            "      I  ",
                            "      I  "
                    };
                } else {
                    objective = new String[]{
                            "     P   ",
                            "     P   ",
                            "   I     "
                    };
                }
                break;
            case FREE_RESOURCES:
                objective = new String[]{
                        "  SCROLL ",
                        "  INKS   ",
                        "  QUILLS "
                };
                break;
            case TRIS:
                if (card.getKingdom() == Kingdom.FUNGI) {
                    objective = new String[]{
                            "      F  ",
                            "    F   F",
                            "         "
                    };
                } else if (card.getKingdom() == Kingdom.ANIMAL) {
                    objective = new String[]{
                            "      A  ",
                            "    A   A",
                            "         "
                    };

                } else if (card.getKingdom() == Kingdom.PLANT) {
                    objective = new String[]{
                            "     P   ",
                            "   P   P ",
                            "         "
                    };
                } else if (card.getKingdom() == Kingdom.INSECT) {
                    objective = new String[]{
                            "      I  ",
                            "    I   I",
                            "         "
                    };
                }
                break;
            case TWO_INKS:
                objective = new String[]{
                        "  INKS   ",
                        "  INKS   ",
                        "         "
                };
                break;
            case TWO_SCROLLS:
                objective = new String[]{
                        "  SCROLL ",
                        "  SCROLL ",
                        "         "
                };
                break;
            case TWO_QUILLS:
                objective = new String[]{
                        "  QUILLS ",
                        "  QUILLS ",
                        "         "
                };
                break;
            default:
                objective = new String[]{
                        "   ",
                        "   ",
                        "   "
                };

        }
        String multiplier = Integer.toString(card.getMultiplier());
        String kingdom = Optional.ofNullable(card.getKingdom())
                .map(Kingdom::toString)
                .orElse("");
        kingdom = centerStringMidSign(kingdom);
        return new String[]{
                "┌-------------------------------------┐",
                "|                " + multiplier + "                    |",
                "" + kingdom + "",
                "|                                     |",
                "|            " + objective[0] + "                |",
                "|            " + objective[1] + "                |",
                "|            " + objective[2] + "                |",
                "└-------------------------------------┘",
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
    public String[] createCardToPrint(PlayedCard card) {
        HashMap<Corner, String> corner = new HashMap<>();
        String p = " ";
        ArrayList<String> midSigns = new ArrayList<>();
        if (card.isFacingUp() && card.getCard() instanceof StartingCard) {
            for (Corner c : Corner.values()) {
                corner.put(c, Optional.ofNullable((card.getCard()).getCorners().get(c))
                        .map(Object::toString)
                        .orElse(Sign.NULL.toString()));
            }
            for (Sign s : ((StartingCard) card.getCard()).getBonusResources()) {
                midSigns.add(s.toString());
            }
        }else if(card.isFacingUp()){
            for (Corner c : Corner.values()) {
                corner.put(c, Optional.ofNullable((card.getCard()).getCorners().get(c))
                        .map(Object::toString)
                        .orElse(Sign.NULL.toString()));
            }

            if (card.getCard() instanceof ResourceCard) {
                p = Integer.toString(((ResourceCard) card.getCard()).getPoints());
            }
            //Non ha kingdom se è carta alta.
            midSigns.add(" ");
        } else {
            for (Corner c : Corner.values()) {
                corner.put(c, Sign.EMPTY.toString());
            }
            midSigns.add(Optional.ofNullable(card.getCard().getKingdom())
                    .map(Enum::name)
                    .orElse(Sign.NULL.name()));
        }
        switch (midSigns.size()) {
            case 1:
                midSigns.add(Sign.NULL.toString());
                midSigns.add(Sign.NULL.toString());
                break;
            case 2:
                midSigns.add(Sign.NULL.toString());
                break;
            default:
                break;
        }

        String a = signToEmoji(corner.get(Corner.TOP_LEFT));
        String b = signToEmoji(corner.get(Corner.TOP_RIGHT));
        String c = signToEmoji(corner.get(Corner.BOTTOM_LEFT));
        String d = signToEmoji(corner.get(Corner.BOTTOM_RIGHT));
        String e = middleSignEmoji(midSigns.get(0));
        String g = middleSignEmoji(midSigns.get(1));
        String h = middleSignEmoji(midSigns.get(2));

        return new String[]{
                "┌––––––––┐––––––––––––––┌––––––––┐",
                "    "+a+"           "+p+"          "+b+"    ",
                "└––––––––┘              └––––––––┘",
                "|     "+g+"         " + e + "         "+h+"   |",
                "┌––––––––┐              ┌––––––––┐",
                "    "+c+"                      "+d+ "    ",
                "└––––––––┘––––––––––––––└––––––––┘",
        };

    }
    /**
     * This method is responsible for converting a sign to its corresponding emoji.
     * It takes a string representation of a sign as input and returns a string representation of the corresponding emoji.
     * The method handles the following signs: MUSHROOM, WOLF, BUTTERFLY, LEAF, SCROLL, INKWELL, QUILL, EMPTY, NULL.
     * If the sign is not one of the above, the method returns a string with two spaces.
     *
     * @param sign The sign to be converted to an emoji. It is a string representation of a sign.
     * @return A string representation of the corresponding emoji. If the sign is not handled by the method, it returns a string with two spaces.
     */
    public String signToEmoji(String sign) {
        switch (sign) {
            case "MUSHROOM":
                return "\uD83C\uDF44"; // Fungus emoji
            case "WOLF":
                return "\uD83D\uDC3A"; // WOLF emoji
            case "BUTTERFLY":
                return "\uD83E\uDD8B"; // Butterfly emoji
            case "LEAF":
                return "\uD83C\uDF3F"; // Herb emoji
            case "SCROLL":
                return "\uD83D\uDCDC"; // Scroll emoji
            case "INKWELL":
                return "\uD83D\uDD8B"; // Black Nib emoji
            case "QUILL":
                return "\uD83E\uDEB6"; // Notebook With Decorative Cover emoji
            case "NULL":
                return "\uD83D\uDFE5";
            default:
                return "  ";
        }
    }

    /**
     * This method is responsible for converting a sign to its corresponding emoji.
     * It takes a string representation of a sign as input and returns a string representation of the corresponding emoji.
     * It is used to convert the middle signs (starting cards or back side resource cards)  of the cards to emojis.
     *
     * @param sign The sign to be converted to an emoji. It is a string representation of a sign.
     * @return A string representation of the corresponding emoji. If the sign is not handled by the method, it returns a string with two spaces.
     */
    private String middleSignEmoji(String sign) {
        switch (sign) {
            case "MUSHROOM":
            case "FUNGI":
                return "\uD83C\uDF44"; // Fungus emoji
            case "WOLF":
            case "ANIMAL":
                return "\uD83D\uDC3A"; // wolf emoji
            case "BUTTERFLY":
            case "INSECT":
                return "\uD83E\uDD8B"; // Butterfly emoji
            case "LEAF":
            case "PLANT":
                return "\uD83C\uDF3F"; // Herb emoji
            case "SCROLL":
                return "\uD83D\uDCDC"; // Scroll emoji
            case "INKWELL":
                return "\uD83D\uDD8B"; // Black Nib emoji
            case "QUILL":
                return "\uD83E\uDEB6"; // Notebook With Decorative Cover emoji
            case "NULL":
                return "\uD83D\uDFE5"; //red box emoji
            default:
                return "  "; // case empty
        }
    }
    /**
     * Centers a string within a fixed length.
     *
     * This method takes a string as input and centers it within a fixed length by adding spaces at the beginning and end of the string.
     * The length is fixed at 37 characters.
     * The centered string is then returned.
     *
     * @param word The string to be centered.
     * @return A string that is the centered version of the input string. The returned string has a fixed length of 37 characters.
     */
    private String centerStringMidSign(String word) {
        int length = 37;
        int wordLength = word.length();
        int startSpaces = (length - wordLength) / 2;
        int endSpaces = length - startSpaces - wordLength;
        return "|" + " ".repeat(startSpaces) + word + " ".repeat(endSpaces) + "|";
    }
}

