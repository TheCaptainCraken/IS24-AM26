package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.view.model.CardClient;
import it.polimi.ingsw.view.model.LittleModel;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;
//TODO
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private LittleModel model;
    /**
     * Object used for synchronization.
     */
    private final Object syncornizedObject;


    /**
     * Constructor for the TUI.
     * Initializes the TUI with default values.
     * @param model The model of the game.
     * @param controller The controller of the game.
     */
    public TUI(LittleModel model, Controller controller) {
        this.model = model;
        this.syncornizedObject = new Object();
        this.controller = controller;
    }

    /**
     * Informs the player that they are connected to the server and waiting for the first player to choose the number of players.
     */
    @Override
    public void waitLobby() {
        synchronized (syncornizedObject) {
            System.out.println("You are connected to the server. Please wait for all players to join the game");
            System.out.println();
        }
    }

    /**
     * Informs the player that the game is starting.
     */
    @Override
    public void stopWaiting() {
        synchronized (syncornizedObject) {
            System.out.println("The game is starting");
            System.out.println();
        }
    }

    @Override
    public void correctNumberOfPlayers(int numberOfPlayers) {
        synchronized (syncornizedObject) {
            System.out.println("You have correctly set the number of players");
            System.out.println("The number of players are " + numberOfPlayers);
            System.out.println();
        }
    }

    /**
     * Informs the player that the lobby has been filled with the number of players chosen by the first player.
     */
    @Override
    public void disconnect() {
        synchronized (syncornizedObject) {
            System.out.println("Lobby has been fulled with number of parameters chosen by the first player");
            System.out.println();
        }
    }

    /**
     * Displays a received chat message to the player.
     *
     * This method is called when a chat message is received from another player.
     * It takes the sender's nickname and the message content as parameters, and displays the message to the console.
     *
     * @param sender The nickname of the player who sent the message.
     * @param message The content of the message.
     */
    @Override
    public void receiveChatMessage(String sender, String message, boolean broadcast) {
        synchronized (syncornizedObject) {
            System.out.println((broadcast?"[All] ":"") + "There is a new message from " + sender);
            System.out.println(message);
        }
    }

    /**
     * Informs the player that the starting card has been chosen.
     *
     * This method is called when the starting card has been selected in the game.
     * It displays a message to the console indicating that the starting card has been chosen,
     * and then prints the player's table area to the console.
     */
    @Override
    public void showStartingCardChosen() {
        synchronized (syncornizedObject) {
            System.out.println("The starting card has been chosen");
            printTableAreaOfPlayer(controller.getNickname());
        }
    }
    /**
     * Displays the players in the lobby and their associated colors.
     *
     * @param playersAndPins A HashMap containing the player nicknames as keys and their associated colors as values.
     */
    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        synchronized (syncornizedObject) {
            System.out.println("The players in the lobby are:");
            for (String nickname : playersAndPins.keySet()) {
                Color color = playersAndPins.get(nickname);
                System.out.println(nickname + " - " + Objects.requireNonNullElse(color, "no color"));
            }
            System.out.println();
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
    public void showIsFirst(String firstPlayer) {
        synchronized (syncornizedObject) {
            System.out.println("The first player is " + firstPlayer + ". The game is starting");
            System.out.println();
        }
    }
    /**
     * Displays the starting card to the player.
     *
     * @param startingCardId The ID of the starting card.
     */
    @Override
    public void showStartingCard(int startingCardId) {
        //same starting card, we simply change the side
        PlayedCard card = model.getStartingCard(startingCardId, true);
        PlayedCard cardBack = model.getStartingCard(startingCardId, false);
        ArrayList<String[]> cards = new ArrayList<>();
        int i;

        //Convert the PlayedCard into a string to print it
        cards.add(createCardToPrint(card));
        cards.add(createCardToPrint(cardBack));

        synchronized (syncornizedObject) {
            System.out.println("Choose the side of the starting Card. The one on the left is the back, the one on the right is the top");
            System.out.println("Enter true for the top, false for the back");
            int size = cards.get(0).length;
            for (i = 0; i < size; i++) {
                for (String[] cardsToPrint : cards) {
                    System.out.print(cardsToPrint[i]);
                    System.out.print("   ");
                }
                System.out.println();
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
    public void showCommonTable() {
        synchronized (syncornizedObject) {
            //the cards on the table
            Integer[] resourceCards = model.getResourceCards();
            Integer[] goldCard = model.getGoldCards();
            //the cards on the deck
            Kingdom resourceCardOnDeck = model.getHeadDeckResource();
            Kingdom goldCardOnDeck = model.getHeadDeckGold();

            //create the cards to print(resource cards)
            ArrayList<String[]> cardsToPrint = new ArrayList<>();
            for (Integer cardId : resourceCards) {

                try {
                    cardsToPrint.add(createCardToPrint(model.getCard(cardId, true)));
                } catch (NullPointerException e) {
                    cardsToPrint.add(new String[]{
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                    });
                }
            }
            //create the resource deck to print
            try {
                cardsToPrint.add(createCardToPrint(fromKingdomToCard(resourceCardOnDeck)));
            } catch (NullPointerException e) {
                cardsToPrint.add(new String[]{
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                });
            }

            //create the cards to print(gold cards)
            ArrayList<String[]> goldCardsToPrint = new ArrayList<>();
            for (Integer cardId : goldCard) {
                try {
                    goldCardsToPrint.add(createCardToPrint(model.getCard(cardId, true)));
                } catch (NullPointerException e) {
                    goldCardsToPrint.add(new String[]{
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                    });
                }
            }
            //create the gold deck to print
            try {
                goldCardsToPrint.add(createCardToPrint(fromKingdomToCard(goldCardOnDeck)));
            } catch (NullPointerException e) {
                goldCardsToPrint.add(new String[]{
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                });
            }

            System.out.println("This is the resource cards deck:\n");
            printCardArray(cardsToPrint);
            System.out.println("This is the gold cards deck:\n");
            printCardArray(goldCardsToPrint);
            System.out.println();
        }
    }
    /**
     * Displays the common objective cards to the player.
     *
     * @param objectiveCardIds The IDs of the objective cards.
     */
    @Override
    public void showCommonObjectives(Integer[] objectiveCardIds) {
        List<String[]> cards = new ArrayList<>();

        for (Integer cardId : objectiveCardIds) {
            cards.add(createObjectiveCardToPrint(cardId));
        }

        int size = cards.get(0).length;
        int i;
        synchronized (syncornizedObject) {
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
    }
    /**
     * Displays the secret objective cards that the player can choose from.
     * This method retrieves the secret objective cards from the model, converts them into a printable format,
     * and then prints them to the console for the player to view and choose from.
     *
     * @param objectiveCardIds An array of IDs of the secret objective cards that the player can choose from.
     */
    @Override
    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {
        synchronized (syncornizedObject) {
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
            System.out.println();
        }
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
    public void showSecretObjectiveCard(int indexCard) {
        synchronized (syncornizedObject) {
            System.out.println("This is your secret objective card: ");
            String[] secretObjective = createObjectiveCardToPrint(indexCard);
            for (String row : secretObjective) {
                System.out.println(row);
            }
            System.out.println();
        }
    }
    /**
     * Displays the turn information to the player.
     *
     * @param currentPlayer The current player.
     * @param gameState     The current game state.
     */
    @Override
    public void showTurnInfo(String currentPlayer, GameState gameState) {
        synchronized (syncornizedObject) {
            System.out.println("It's " + currentPlayer + "'s turn");
            System.out.println("The game phase is: " + gameState);
            System.out.println();
        }
    }
    /**
     * Shows the resources of all clients.
     *
     */
    @Override
    public void showResourcesPlayer() {
        HashMap<String, HashMap<Sign, Integer>> resources = model.getResources();

        synchronized (syncornizedObject) {
            for(String name : resources.keySet()){
                System.out.println(name + " has the following resources:");
                for (Sign sign : resources.get(name).keySet()) {
                    if (sign != Sign.NULL && sign != Sign.EMPTY) {
                        System.out.println(sign + " - " + resources.get(name).get(sign));
                    }
                }
                System.out.println();
            }
            System.out.println();
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
        System.out.println();
    }
    /**
     * This method is responsible for displaying the points of all players.
     * It retrieves the points of each player from the model and prints them to the console.
     * The points are displayed in the format: player - points.
     */
    @Override
    public void showPoints() {
        HashMap<String, Integer> points = model.getPoints();
        synchronized (syncornizedObject) {
            System.out.println("The points of the players are:");
            for (String player : points.keySet()) {
                System.out.println(player + " has " + points.get(player));
            }
            System.out.println();
        }
    }
    /**
     * Displays the extra points to the player.
     *
     * @param extraPoints The extra points of the players.
     */
    @Override
    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
        synchronized (syncornizedObject) {
            System.out.println("The points made by ObjectiveCards are:");
            for (String player : extraPoints.keySet()) {
                System.out.println(player + " - " + extraPoints.get(player));
            }
            System.out.println();
        }
    }
    /**
     * Displays the ranking to the player.
     *
     * @param ranking The ranking of the players.
     */
    @Override
    public void showRanking(ArrayList<Player> ranking) {
        synchronized (syncornizedObject) {
            System.out.println("The ranking is:");
            for (Player player : ranking) {
                System.out.println(player.getName() + " - " + player.getPoints() + player.getObjectivePoints());
            }
            System.out.println();
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
        synchronized (syncornizedObject) {
            printTableAreaOfPlayer(nickname);
        }
    }
    /**
     * Shows the hand of the client.
     */
    @Override
    public void showHand() {
        Integer[] myCards = model.getHand();
        ArrayList<String[]> cards = new ArrayList<>();

        for (Integer cardId : myCards) {
            if(cardId == null){
                cards.add(new String[]  {
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                        "                                  ",
                });
            }else{
                cards.add(createCardToPrint(model.getCard(cardId, true)));
            }
        }
        synchronized (syncornizedObject) {
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
    }
    /**
     * Informs the player that the chosen color is already taken.
     */
    @Override
    public void colorAlreadyTaken() {
        synchronized (syncornizedObject) {
            System.out.println("The color you chose is already taken. Please choose another one");
            System.out.println();
        }
    }
    /**
     * Informs the player that the chosen nickname is already taken.
     *
     * @param nickname The chosen nickname.
     */
    @Override
    public void sameName(String nickname) {
        synchronized (syncornizedObject) {
            System.out.println("The nickname " + nickname + " is already taken. Please choose another one");
            System.out.println();
        }
    }
    /**
     * Informs the player that it's not their turn.
     */
    @Override
    public void noTurn() {
        synchronized (syncornizedObject) {
            System.out.println("It's not your turn. You can't perform this action");
            System.out.println();
        }
    }
    /**
     * Informs the player that they don't have enough resources.
     *
     */
    @Override
    public void notEnoughResources() {
        synchronized (syncornizedObject) {
            System.out.println("You don't have enough resources to perform this action");
            System.out.println();
        }
    }
    /**
     * Informs the player that they are not connected to the server.
     */
    @Override
    public void noConnection() {
        synchronized (syncornizedObject) {
            System.out.println("You are not connected to the server. Game will end soon.");
            System.out.println("Thank you for playing. Goodbye!");
            System.out.println();
        }
    }
    /**
     * Informs the player that they can't position the card there.
     */
    @Override
    public void cardPositionError() {
        synchronized (syncornizedObject) {
            System.out.println("You can't position the card there. Please try another position");
            System.out.println();
        }
    }
    /**
     * Informs the player that the lobby is full.
     */
    @Override
    public void lobbyComplete() {
        synchronized (syncornizedObject) {
            System.out.println("The lobby is full. No other players can join");
            System.out.println();
        }
    }
    /**
     * Informs the player that he can't perform the action in this game phase.
     */
    @Override
    public void wrongGamePhase() {
        synchronized (syncornizedObject) {
            System.out.println("You can't perform this action in this game phase");
            System.out.println();
        }
    }
    /**
     * Informs the client that the name given doesn't exist.
     */
    @Override
    public void noPlayer() {
        synchronized (syncornizedObject) {
            System.out.println("The player doesn't exist");
            System.out.println();
        }
    }
    /**
     * Informs the player that the lobby is closed. A game already started.
     */
    @Override
    public void closingLobbyError() {
        synchronized (syncornizedObject) {
            System.out.println("You haven't fill the lobby with the correct number of players. The lobby is closing");
            System.out.println();
        }

    }

    /**
     * Informs the player that the game has ended due to a client disconnection.
     *
     * This method is called when a client has disconnected from the game and the game cannot continue.
     * It displays a message to the console indicating that the game has ended and thanks the player for playing.
     */
    @Override
    public void stopGaming() {
        synchronized (syncornizedObject) {
            System.out.println("Some client has disconnected. You cannot continue the game");
            System.out.println("The game is over. Thank you for playing");
        }
    }

    /**
     * Displays to show the first player to enter the number of players.
     */
    @Override
    public void askNumberOfPlayers() {
        synchronized (syncornizedObject) {
            System.out.println("You are the first player. Please enter the number of players");
            System.out.println("The number of players must be between 2 and 4");
        }
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
        synchronized (syncornizedObject) {
            System.out.println("Choose your color");
            System.out.println("1 - Blue\n" +
                    "2 - Yellow\n" +
                    "3 - Green\n" +
                    "4 - Red\n");
        }

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
                synchronized (syncornizedObject) {
                    System.out.println("Invalid input. Please enter a number between 1 and 4.");
                    scanner.next(); // discard the invalid input
                }
            }
        } while (!validInput);
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

        synchronized (syncornizedObject) {
            System.out.println("Please insert the nickname of the player you want to see the table of");

            for (String player : model.getOtherPlayersCards().keySet()) {
                System.out.println(player);
            }
        }
        //open the scanner for the nickname. The nickname must be valid
        nickname = scanner.nextLine();
        //if is not valid, it asks again
        while (!model.getOtherPlayersCards().containsKey(nickname)) {
            synchronized (syncornizedObject) {
                System.out.println("The nickname you inserted is not valid. Please insert a valid nickname");
                nickname = scanner.nextLine();
            }
        }
        synchronized (syncornizedObject) {
            //print the table given a correct name.
            printTableAreaOfPlayer(nickname);
        }

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

        synchronized (syncornizedObject) {
            showCommonTable();
        }
        while (!validInput) {
            try {
                synchronized (syncornizedObject) {
                    System.out.println("Enter true if the card is gold, false otherwise:");
                }
                gold = scanner.nextBoolean();
                synchronized (syncornizedObject) {
                    System.out.println("Enter -1, 0 or 1 if the card is on table, if it's on deck:");
                }
                onTableOrDeck = scanner.nextInt();
                if (onTableOrDeck != -1 && onTableOrDeck != 0 && onTableOrDeck != 1) {
                    synchronized (syncornizedObject) {
                        System.out.println("Invalid input. Value must be -1, 0 or 1.");
                    }
                    continue;
                }
                ViewSubmissions.getInstance().drawCard(gold, onTableOrDeck);
                validInput = true;
            } catch (InputMismatchException e){
                    synchronized (syncornizedObject) {
                    System.out.println("Invalid input. Please enter the correct values.");
                    scanner.next(); // Consumes the invalid input
                }
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
                synchronized (syncornizedObject) {
                    System.out.println("Enter index of the card in hand (1-3):");
                }
                indexHand = scanner.nextInt();
                if (indexHand < 1 || indexHand > 3) {
                    synchronized (syncornizedObject) {
                        System.out.println("Invalid input. Index must be between 1 and 3.");
                    }
                    continue;
                }
                synchronized (syncornizedObject) {
                    System.out.println("Enter position x:");
                }
                position.x = scanner.nextInt();
                synchronized (syncornizedObject) {
                    System.out.println("Enter position y:");
                }
                position.y = scanner.nextInt();
                synchronized (syncornizedObject) {
                    System.out.println("Enter true if the card is facing up, false otherwise:");
                }
                isFacingUp = scanner.nextBoolean();
                ViewSubmissions.getInstance().placeCard(indexHand - 1, position, isFacingUp);
                validInput = true;
            } catch (InputMismatchException e) {
                synchronized (syncornizedObject) {
                    System.out.println("Invalid input. Please enter the correct values.");
                    scanner.next(); // Consumes the invalid input
                }
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
            synchronized (syncornizedObject) {
                System.out.println("Please enter your nickname (without spaces):");
            }
            nickname = scanner.nextLine();
        } while (nickname.contains(" "));
        //sent information to the server
        ViewSubmissions.getInstance().chooseNickname(nickname);
    }
    /**
     * Shows the hidden hand of a player.
     */
    @Override
    public void showHiddenHand(String name) {
        //name is not used, but is necessary for the interface
        Set<String> players = model.getOtherPlayersCards().keySet();

        // Iterate over all players
        for (String player : players) {
            Pair<Kingdom, Boolean>[] hand = model.getHiddenHand(player);
            ArrayList<String[]> cards = new ArrayList<>();
            int i;

            // Create a printable representation for each card in the hand
            for (i = 0; i < hand.length; i++) {
                try {
                    //if the card is not null, it creates the card to print. Card can be null
                    cards.add(createCardToPrint(fromKingdomToCard(hand[i].getKey())));
                } catch (NullPointerException e) {
                    cards.add(new String[]{
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                    });
                }
            }
            synchronized (syncornizedObject) {
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
                    synchronized (syncornizedObject) {
                        System.out.println("Invalid input. Please enter 0 or 1.");
                    }
                }
            } catch (InputMismatchException e) {
                synchronized (syncornizedObject) {
                    System.out.println("Invalid input. Please enter 0 or 1.");
                    scanner.next(); // Consumes the invalid input
                }
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
            else {
                synchronized (syncornizedObject) {
                    System.out.println("Invalid input");
                }
            }
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
    public void start() {
        while (true) {
            switch (Controller.getPhase()) {
                case LOGIN:
                    askChooseNickname();
                    break;
                case NUMBER_OF_PLAYERS:
                    chooseNumberOfPlayers();
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
                    break;
                case WAIT_NUMBER_OF_PLAYERS:
                    // wait that the first player choice the number of players
                    break;
                case GAME_FLOW:
                    // the game is started, the player can perform any action
                    defaultMenu();
                    break;
            }
        }
    }

    private void chooseNumberOfPlayers() {
        Scanner scanner = new Scanner(System.in);
        int numberOfPlayers = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                numberOfPlayers = scanner.nextInt();
                if (numberOfPlayers >= 2 && numberOfPlayers <= 4) {
                    validInput = true;
                } else {
                    synchronized (syncornizedObject) {
                        System.out.println("Invalid input. Please enter a number between 2 and 4.");
                    }
                }
            } catch (InputMismatchException e) {
                synchronized (syncornizedObject){
                    System.out.println("Invalid input. Please enter a number between 2 and 4.");
                    scanner.next(); // discard the invalid input
                }
            }
        }
        ViewSubmissions.getInstance().chooseNumberOfPlayers(numberOfPlayers);
    }

    /**
     * Allow the player to send a chat message to the others.
     */
    private void sendChatMessage(){
        Scanner scanner = new Scanner(System.in);
        String message;

        synchronized (syncornizedObject) {
            System.out.println("Please enter your message, use @nickname to send it just to those players (es:\"@nickname1 @nickname2 hi!\"):");
        }

        message = scanner.nextLine();

        //sent information to the server
        ViewSubmissions.getInstance().sendChatMessage(message);
    }

    private void menu(){
        synchronized (syncornizedObject) {
            System.out.println("Please insert a number for choosing the option");
            System.out.println("" +
                    "1 - place a card\n" +
                    "2 - draw a card\n" +
                    "3 - show the table of a player\n" +
                    "4 - show all players resources\n" +
                    "5 - show the points of the players\n" +
                    "6 - show my hand\n" +
                    "7 - show the hidden hand of a player\n" +
                    "8 - send a chat message\n");
        }
    }

    /**
     * Prints the default menu, with all the options to choose from.
     */
    private void defaultMenu() {
        menu();
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        boolean validInput = false;
        while (!validInput) {
            try {
                choice = scanner.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                synchronized (syncornizedObject) {
                    System.out.println("Input not valid. Please insert a number.");
                    scanner.next(); // consume the invalid input
                }
            }
        }
        switch (choice) {
            case 1:
                synchronized (syncornizedObject) {
                    printTableAreaOfPlayer(controller.getNickname());
                }
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
                showPoints();
                break;
            case 6:
                showHand();
                break;
            case 7:
                //Name is not used, but is necessary for the interface
                showHiddenHand(null);
                break;
            case 8:
                sendChatMessage();
                break;
            default:
                synchronized (syncornizedObject) {
                    System.out.println("Invalid input. Please retry");
                }
                defaultMenu();
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
        System.out.println("Error in the kingdom");
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
    private void printTableAreaOfPlayer(String nickname) {
        ArrayList<CardClient> cards = model.getListOfCards(nickname);
        ArrayList<String[]> cardsToPrint = new ArrayList<>();

        //order the cards by level at first, secondly by x. The level is the sum of x and y.
        Collections.sort(cards, Comparator.comparing((CardClient card) -> card.getPosition().x + card.getPosition().y, Comparator.reverseOrder())
                .thenComparing(card -> card.getPosition().x - card.getPosition().y));

        //get the maximum level of the cards
        int level = cards.get(0).getPosition().x + cards.get(0).getPosition().y;
        //get the minimum x of the cards.
        int min = cards.stream().mapToInt(card -> card.getPosition().x - card.getPosition().y).min().orElse(0);
        int inizialier = min;

        System.out.println("This is the table area of " + nickname + ":");
        System.out.println();


        for(CardClient card: cards){
            //level is the previous, if it's different from the current card, print the previous cards
            if(level == card.getPosition().x + card.getPosition().y){
                while(min != card.getPosition().x - card.getPosition().y){
                    min++;
                    cardsToPrint.add(new String[]{
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                    });
                }
                min = (card.getPosition().x - card.getPosition().y) + 1;
                cardsToPrint.add(createCardToPrint(model.getCard(card.getId(), card.getSide())));
            }else{
                min = inizialier;
                printCardArray(cardsToPrint);
                level = card.getPosition().x + card.getPosition().y;
                cardsToPrint = new ArrayList<>();

                while(min != card.getPosition().x - card.getPosition().y){
                    min++;
                    cardsToPrint.add(new String[]{
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                            "                                  ",
                    });
                }
                min = (card.getPosition().x - card.getPosition().y) + 1;
                cardsToPrint.add(createCardToPrint(model.getCard(card.getId(), card.getSide())));
            }
        }
        //for the last one print the cards
        printCardArray(cardsToPrint);
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
     * @return The representation of the card as an array of strings.
     */
    public String[] createCardToPrint(PlayedCard card) {
        HashMap<Corner, String> corner = new HashMap<>();
        String p = " ";
        ArrayList<String> midSigns = new ArrayList<>();
        ArrayList<String> requirementsToPrint = new ArrayList<>();
        int requirementsLenght = 0;
        //for special gold card(the emoji for the special type)
        String sp = " ";

        //case starting card up, we got normal corners, and no points
        if (card.isFacingUp() && card.getCard() instanceof StartingCard) {
            for (Corner c : Corner.values()) {
                corner.put(c, Optional.ofNullable((card.getCard()).getCorners().get(c))
                        .map(Object::toString)
                        .orElse(Sign.NULL.toString()));
            }
            //card down, we got bonus resources and backside corners
        }else if(!card.isFacingUp() && card.getCard() instanceof StartingCard){
            for (Corner c : Corner.values()) {
                corner.put(c, Optional.ofNullable(((StartingCard) card.getCard()).getBacksideCorners().get(c))
                        .map(Object::toString)
                        .orElse(Sign.NULL.toString()));
            }
            //add bonus resources to print
            for (Sign s : ((StartingCard) card.getCard()).getBonusResources()) {
                midSigns.add(s.toString());
            }
        }else if (card.isFacingUp()){
            //get normal corners
            for (Corner c : Corner.values()) {
                corner.put(c, Optional.ofNullable((card.getCard()).getCorners().get(c))
                        .map(Object::toString)
                        .orElse(Sign.NULL.toString()));
            }
            //get the points of the resource card and gold card
            if (card.getCard() instanceof ResourceCard) {
                p = Integer.toString(((ResourceCard) card.getCard()).getPoints());
                //we don't want to print 0
                if(p.equals("0")){
                    p = " ";
                }
            }
            //if is up, there isn't the central sign
            midSigns.add(card.getCard().getKingdom().toString());
        } else {
            //facing down, 4 empty corners
            for (Corner c : Corner.values()) {
                corner.put(c, Sign.EMPTY.toString());
            }
            midSigns.add(Optional.ofNullable(card.getCard().getKingdom())
                    .map(Enum::name)
                    .orElse(Sign.NULL.name()));
        }

        switch (midSigns.size()) {
            //we always print 3, if the card doesn't have it we print " "
            case 0:
                midSigns.add(Sign.NULL.toString());
                midSigns.add(Sign.NULL.toString());
                midSigns.add(Sign.NULL.toString());
                break;
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

        if(card.getCard() instanceof GoldCard && card.isFacingUp()){
            HashMap<Sign, Integer> requirements = ((GoldCard) card.getCard()).getRequirements();
            for(Sign s: requirements.keySet()){
                if(s != Sign.NULL && s != Sign.EMPTY && requirements.get(s) > 0) {
                    //integer means how many times
                    for(int i = 0; i < requirements.get(s); i++) {
                        requirementsToPrint.add(s.toString());
                    }
                }
            }
            if(card.getCard() instanceof SpecialGoldCard){
                sp = ((SpecialGoldCard) card.getCard()).getThingToCount().toString();
            }
        }
        switch(requirementsToPrint.size()){
            case 0:
                for(int i = 0; i < 5; i++){
                    requirementsToPrint.add(Sign.NULL.toString());
                }
                break;
            case 3:
                for(int i = 0; i < 2; i++){
                    requirementsToPrint.add(Sign.NULL.toString());
                }
                break;
            case 4:
                requirementsToPrint.add(Sign.NULL.toString());
                break;
            case 5:
                break;
            default:
                System.out.println("The requirementsLenght is not supported. It is: " + requirementsLenght);
                break;
        }

        //transform the sign into Emoji
        String a = signToEmoji(corner.get(Corner.TOP_LEFT));
        String b = signToEmoji(corner.get(Corner.TOP_RIGHT));
        String c = signToEmoji(corner.get(Corner.BOTTOM_LEFT));
        String d = signToEmoji(corner.get(Corner.BOTTOM_RIGHT));
        //different function just since we have a different enumeration
        String e = middleSignToEmoji(midSigns.get(0));
        String g = middleSignToEmoji(midSigns.get(1));
        String h = middleSignToEmoji(midSigns.get(2));
        //for requirements
        String f = middleSignToEmoji(requirementsToPrint.get(0));
        String i = middleSignToEmoji(requirementsToPrint.get(1));
        String j = middleSignToEmoji(requirementsToPrint.get(2));
        String k = middleSignToEmoji(requirementsToPrint.get(3));
        String l = middleSignToEmoji(requirementsToPrint.get(4));

        sp = countableToEmoji(sp);

        return new String[]{
                "┌––––––––┐––––––––––––––┌––––––––┐",
                "    "+a+"          "+p+" "+sp+"        "+b+"   ",
                "└––––––––┘              └––––––––┘",
                "|     "+g+"         " + e + "         "+h+"   |",
                "┌––––––––┐              ┌––––––––┐",
                "    "+c+"                      "+d+ "    ",
                "            "+f+""+i+""+j+""+k+""+l+"           ",
                "└––––––––┘––––––––––––––└––––––––┘",
        };

    }

    /**
     * Converts a given Countable type to its corresponding emoji representation.
     *
     * This method takes a string representation of a Countable type as input and returns the corresponding emoji as a string.
     * The Countable types and their corresponding emojis are as follows:
     * - INKWELL: "\uD83D\uDD0D" (Magnifying Glass Tilted Left emoji)
     * - QUILL: "\u2712" (Black Nib emoji)
     * - SCROLL: "\uD83D\uDCDC" (Scroll emoji)
     * - CORNER: "\uD83C\uDCA1" (Playing Card Ace of Spades emoji)
     *
     * If the input string does not match any of the Countable types, the method returns an empty string.
     *
     * @param sp The string representation of a Countable type.
     * @return The emoji string corresponding to the given Countable type, or an empty string if the input does not match any Countable type.
     */
    private String countableToEmoji(String sp) {
        switch (sp) {
            case "INKWELL":
                return "\uD83D\uDD8B"; //Magnifying Glass Tilted Left emoji
            case "QUILL":
                return "\uD83E\uDEB6"; //Black Nib emoji
            case "SCROLL":
                return "\uD83D\uDCDC"; //Scroll emoji
            case "CORNER":
                return "\uD83C\uDCA1"; //Playing Card Ace of Spades emoji
            default:
                return "  "; // case empty
        }
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
    private String middleSignToEmoji(String sign) {
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
                return "  ";
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

    public void setModel(LittleModel model) {
        this.model = model;
    }
}

