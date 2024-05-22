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
public class Tui implements InterfaceForView{
    private final Controller controller;
    private final LittleModel model;

    /**
     * Constructor for the TUI.
     * Initializes the TUI with default values.
     */
    public Tui(LittleModel model, Controller controller) {
        this.model = model;
        this.controller = controller;
    }

    /**
     * Displays to show the first player to enter the number of players.
     */
    @Override
    public void showInsertNumberOfPlayers() {
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
            controller.insertNumberOfPlayers(numberOfPlayers);
        //TODO dividere fasi
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

    private void askColor() {
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
                        controller.chooseColor(Color.BLUE);
                        validInput = true;
                        break;
                    case 2:
                        controller.chooseColor(Color.YELLOW);
                        validInput = true;
                        break;
                    case 3:
                        controller.chooseColor(Color.GREEN);
                        validInput = true;
                        break;
                    case 4:
                        controller.chooseColor(Color.RED);
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
     * Displays the starting card to the player.
     *
     * @param startingCardId The ID of the starting card.
     */
    @Override
    public synchronized void showStartingCard(int startingCardId) {
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
    private String[] createCardToPrint(PlayedCard card) {
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
            midSigns.add("");
        } else {
            for (Corner c : Corner.values()) {
                corner.put(c, Sign.NULL.toString());
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
            case 3:
                break;
            default:
                break;
        }

        String a = signToEmoji(corner.get(Corner.TOP_LEFT));
        String b = signToEmoji(corner.get(Corner.TOP_RIGHT));
        String c = signToEmoji(corner.get(Corner.BOTTOM_LEFT));
        String d = signToEmoji(corner.get(Corner.BOTTOM_RIGHT));
        String e = signToEmojiForStartingCard(midSigns.get(0));
        String g = signToEmojiForStartingCard(midSigns.get(1));
        String h = signToEmojiForStartingCard(midSigns.get(2));

        return new String[]{
                "┌--------┐--------------┌--------┐",
                "    "+a+"           "+p+"          "+b+"    ",
                "└--------┘              └--------┘",
                "|     "+g+"         " + e + "         "+h+"   |",
                "┌--------┐              ┌--------┐",
                "    "+c+"                      "+d+ "    ",
                "└--------┘--------------└--------┘",
        };

    }

    private String signToEmoji(String sign) {
        switch (sign) {
            case "MUSHROOM":
                return "\uD83C\uDF44"; // Fungus emoji
            case "WOLF":
                return "\uD83D\uDC36"; // Dog emoji
            case "BUTTERFLY":
                return "\uD83D\uDC1B"; // Bug emoji
            case "LEAF":
                return "\uD83C\uDF31"; // Herb emoji
            case "SCROLL":
                return "\uD83D\uDCC2"; // Scroll emoji
            case "INKWELL":
                return "\u2712"; // Black Nib emoji
            case "QUILL":
                return "\uD83D\uDCD1"; // Notebook With Decorative Cover emoji
            case "NULL":
                return "\uD83D\uDFE5"; // Question mark emoji
            default:
                return "  "; // Restituisci una stringa vuota o un'altra emoji di default se il Sign non è gestito
        }
    }

    private String signToEmojiForStartingCard(String sign) {
        switch (sign) {
            case "MUSHROOM":
            case "FUNGI":
                return "\uD83C\uDF44"; // Fungus emoji
            case "WOLF":
            case "ANIMAL":
                return "\uD83D\uDC36"; // Dog emoji
            case "BUTTERFLY":
            case "INSECT":
                return "\uD83D\uDC1B"; // Bug emoji
            case "LEAF":
            case "PLANT":
                return "\uD83C\uDF31"; // Herb emoji
            case "SCROLL":
                return "\uD83D\uDCC2"; // Scroll emoji
            case "INKWELL":
                return "\u2712"; // Black Nib emoji
            case "QUILL":
                return "\uD83D\uDCD1"; // Notebook With Decorative Cover emoji
            case "NULL":
                return "  "; // Question mark emoji
            default:
                return "  "; // Restituisci una stringa vuota o un'altra emoji di default se il Sign non è gestito
        }
    }

    private String centerStringMidSign(String word) {
        int length = 37;
        int wordLength = word.length();
        int startSpaces = (length - wordLength) / 2;
        int endSpaces = length - startSpaces - wordLength;
        return "|" + " ".repeat(startSpaces) + word + " ".repeat(endSpaces) + "|";
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

    @Override
    public void showHiddenHand(String nickname) {
        //TODO
    }

//    public void printTableAreaOfPlayer(String nickname) {
//        printTableArea(getTable(nickname));
//    }

    private void printTableArea(PlayedCard card) {
        if (card == null) {
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
        switch (option) {
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
        for (Corner c : attachments.keySet()) {
            attachmentsStrings.put(c, createCardToPrint(attachments.get(c)));
        }
        HashMap<Corner, String[]> arrows = createArrows(attachments, focusedCard);

        for (int i = 0; i < 7; i++) {
            toPrint += attachmentsStrings.get(Corner.TOP_LEFT)[i] + "                             " + attachmentsStrings.get(Corner.TOP_RIGHT)[i] + "\n";
        }
        toPrint += "                             " + arrows.get(Corner.TOP_LEFT)[0] + "                             " + arrows.get(Corner.TOP_RIGHT)[0] + "                             " + "\n";
        toPrint += "                             " + arrows.get(Corner.TOP_LEFT)[1] + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + arrows.get(Corner.TOP_RIGHT)[1] + "                             " + "\n";
        toPrint += "                             " + arrows.get(Corner.TOP_LEFT)[2] + "                             " + arrows.get(Corner.TOP_RIGHT)[2] + "                             " + "\n";

        for (int i = 0; i < 7; i++) {
            toPrint += "                             " + " ~ " + attachmentsStrings.get(Corner.TOP_LEFT)[i] + " ~ " + "                             " + "\n";
        }
        toPrint += "                             " + arrows.get(Corner.BOTTOM_LEFT)[0] + "                             " + arrows.get(Corner.BOTTOM_RIGHT)[0] + "                             " + "\n";
        toPrint += "                             " + arrows.get(Corner.BOTTOM_LEFT)[1] + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + arrows.get(Corner.BOTTOM_RIGHT)[1] + "                             " + "\n";
        toPrint += "                             " + arrows.get(Corner.BOTTOM_LEFT)[2] + "                             " + arrows.get(Corner.BOTTOM_RIGHT)[2] + "                             " + "\n";
        for (int i = 0; i < 7; i++) {
            toPrint += attachmentsStrings.get(Corner.BOTTOM_LEFT)[i] + "                             " + attachmentsStrings.get(Corner.BOTTOM_RIGHT)[i] + "\n";
        }
        return toPrint;
    }


    public HashMap<Corner, String[]> createArrows(HashMap<Corner, PlayedCard> attachments, PlayedCard focusedCard) {
        HashMap<Corner, String[]> arrows = new HashMap<>();
        for (Corner corner : Corner.values()) {
            switch (corner) {
                case TOP_LEFT: {
                    if (attachments.get(Corner.TOP_RIGHT).getTurnOfPositioning() > focusedCard.getTurnOfPositioning()) {
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                " ",
                                " \\ ",
                                " -┘"
                        });
                    } else {
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                "┌- ",
                                "\\ ",
                                "   "
                        });
                    }
                }
                case TOP_RIGHT: {
                    if (attachments.get(Corner.TOP_RIGHT).getTurnOfPositioning() > focusedCard.getTurnOfPositioning()) {
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                "   ",
                                " / ",
                                "└- "
                        });
                    } else {
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                " -┐",
                                " / ",
                                "   "
                        });
                    }
                }
                case BOTTOM_LEFT: {
                    if (attachments.get(Corner.BOTTOM_LEFT).getTurnOfPositioning() > focusedCard.getTurnOfPositioning()) {
                        arrows.put(Corner.BOTTOM_LEFT, new String[]{
                                " -┐",
                                " / ",
                                "   "
                        });
                    } else {
                        arrows.put(Corner.BOTTOM_LEFT, new String[]{
                                "   ",
                                " / ",
                                "└- "
                        });
                    }
                }
                case BOTTOM_RIGHT: {
                    if (attachments.get(Corner.TOP_RIGHT).getTurnOfPositioning() > focusedCard.getTurnOfPositioning()) {
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                "┌- ",
                                "\\ ",
                                "   "
                        });
                    } else {
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
                scanner.next(); // Consuma l'input non valido
            }
        }
        switch (choice) {
            case 1:
                printTableAreaOfPlayer(controller.getNickname());
                placeCard();
                break;
            case 2:
                drawCard();
                break;
            case 3:
                showTableOfPlayerChecked();
                break;
            case 4:
                showResourcesPlayer(controller.getNickname());
                break;
            case 5:
                showResourcesAllPlayers();
                break;
            case 6:
                showPoints(model.getPoints());
                break;
            case 7:
                showHand();
                break;
            case 8:
                showHiddenHand();
                break;
            default:
                System.out.println("Invalid input. Please retry");
                defaultMenu();
        }

    }

    private void showTableOfPlayerChecked() {
        Scanner scanner = new Scanner(System.in);
        String nickname;
        System.out.println("Please insert the nickname of the player you want to see the table of");
        nickname = scanner.nextLine();
        //TODO check if the nickname is valid and call proper function
    }

    private void drawCard() {
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
                    controller.drawCard(gold, onTableOrDeck);
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter the correct values.");
                    scanner.next(); // Consumes the invalid input
                }
            }
    }

    private void placeCard() {
        Scanner scanner = new Scanner(System.in);
        Integer indexHand;
        Point position = new Point();
        boolean isFacingUp;
        boolean validInput = false;

        showHand();
        while (!validInput) {
            try {
                System.out.println("Enter index of the card in hand (0-2):");
                indexHand = scanner.nextInt();
                //TODO mettere da 1 a 3, facendo più uno
                if (indexHand < 0 || indexHand > 2) {
                    System.out.println("Invalid input. Index must be between 0 and 2.");
                    continue;
                }
                System.out.println("Enter position x:");
                position.x = scanner.nextInt();
                System.out.println("Enter position y:");
                position.y = scanner.nextInt();
                System.out.println("Enter true if the card is facing up, false otherwise:");
                isFacingUp = scanner.nextBoolean();
                controller.playCard(indexHand, position, isFacingUp);
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter the correct values.");
                scanner.next(); // Consumes the invalid input
            }
        }
    }

    private void startPhase() {
        Scanner scanner = new Scanner(System.in);
        String name;

        do {
            System.out.println("Please enter your nickname (without spaces):");
            name = scanner.nextLine();
        } while (name.contains(" "));

        controller.login(name);
    }

    /**
     * Shows the hidden hand of a player.
     *
     */
    //TODO modifica uguale ad interfaccia
    public synchronized void showHiddenHand() {
        Set<String> players = model.getOtherPlayersCards().keySet();

        // Iterate over all players
        for (String player : players) {
            Pair<Kingdom, Boolean>[] hand = model.getHiddenHand(player);
            ArrayList<String[]> cards = new ArrayList<>();
            int i;

            // Create a printable representation for each card in the hand
            for (i = 0; i < hand.length; i++) {
                cards.add(createCardToPrint(fromKingdomToId(hand[i].getKey())));
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
     * Shows the resources of the client.
     *
     * @param name The name of client.
     */
    @Override
    public synchronized void showResourcesPlayer(String name) {
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
    @Override
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

    @Override
    public synchronized void showPoints(HashMap<String, Integer> points) {
        System.out.println("The points of the players are:");
        for (String player : points.keySet()) {
            System.out.println(player + " has " + points.get(player));
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
        System.out.println("You are not connected to the server. Please retry\n");
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
        System.out.println("The input is not correct. Please retry");
    }

    /**
     * Converts Kingdom enum to Sign enum
     *
     * @param kingdom Kingdom to convert
     * @return Sign in which the Kingdom has been converted
     */
    //TODO PERCHE NON è USATA?
    private Sign fromKingdomToSign(Kingdom kingdom) throws IllegalArgumentException {
        switch (kingdom) {
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

    public synchronized void printCard(int id, boolean side) {
        String[] card = createCardToPrint(model.getCard(id, side));
        for (String row : card) {
            System.out.println(row);
        }

    }

    @Override
    public void start() {
        while (true) {
            if(Controller.getPhase() != Phase.WAIT)
                System.out.println("Current phase: " + Controller.getPhase());
            switch (Controller.getPhase()) {
                case LOGIN:
                    startPhase();
                    break;
                case COLOR:
                    askColor();
                    break;
                case CHOOSE_SIDE_STARTING_CARD:
                    //la rete invia le carte e il giocatore sceglie
                    chooseStartingCard();
                    break;
                case CHOOSE_SECRET_OBJECTIVE_CARD:
                    //la rete invia le carte obiettivo e il giocatore sceglie
                    chooseSecretObjectiveCard();
                    break;
                case WAIT:
                    //la rete invia il giocatore che inizia
                    //TODO
                    break;
                case WAIT_NUMBER_OF_PLAYERS:
                    //la rete invia il numero di giocatori
                    //TODO
                    break;
                case GAMEFLOW:
                    defaultMenu();
                    break;
            }
        }

    }

    private void chooseSecretObjectiveCard() {
        Scanner scanner = new Scanner(System.in);
        int indexCard = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.println("Enter 0 or 1:");
                indexCard = scanner.nextInt();
                if (indexCard == 0 || indexCard == 1) {
                    controller.chooseSecretObjectiveCard(indexCard);
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
    @Override
    public synchronized void showIsFirst(String firstPlayer) {
        System.out.println("The first player is " + firstPlayer + ". The game is about to start");
    }

    @Override
    public synchronized void correctNumberOfPlayers(int numberOfPlayers) {
        System.out.println("You have correctly set the number of players");
        System.out.println("The number of players are " + numberOfPlayers);
    }

    private void chooseStartingCard() {
        Scanner scanner = new Scanner(System.in);
        boolean isFacingUp = false;

        String side;
        do {
            side = scanner.nextLine();
            if (side.equals("true"))
                isFacingUp = true;
            else if (side.equals("false"))
                isFacingUp = false;
            else
                System.out.println("Invalid input");
        } while (!side.equals("true") && !side.equals("false"));

        controller.chooseSideStartingCard(isFacingUp);

    }

    public synchronized void showCommonTable() {
        Integer[] resourceCards = model.getResourceCards();
        Integer[] goldCard = model.getGoldCards();

        Kingdom resourceCardOnDeck = model.getHeadDeckResource();
        Kingdom goldCardOnDeck = model.getHeadDeckGold();

        ArrayList<String[]> cardsToPrint = new ArrayList<>();
        for (Integer cardId : resourceCards) {
            cardsToPrint.add(createCardToPrint(model.getCard(cardId, true)));
        }
        cardsToPrint.add(createCardToPrint(fromKingdomToId(resourceCardOnDeck)));

        ArrayList<String[]> goldCardsToPrint = new ArrayList<>();
        for (Integer cardId : goldCard) {
            goldCardsToPrint.add(createCardToPrint(model.getCard(cardId, true)));
        }
        goldCardsToPrint.add(createCardToPrint(fromKingdomToId(goldCardOnDeck)));

        System.out.println("This is the resource card deck:\n");
        printCardArray(cardsToPrint);
        System.out.println("This is the gold card deck:\n");
        printCardArray(goldCardsToPrint);

    }

    private void printCardArray(ArrayList<String[]> cardsToPrint) {
        int size = cardsToPrint.get(0).length;
        int i;
        for (i = 0; i < size; i++) {
            for (String[] card : cardsToPrint) {
                System.out.print(card[i]);
                System.out.print("   ");
            }
            System.out.println();
        }
    }

    public PlayedCard fromKingdomToId(Kingdom kingdom) {
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

    private synchronized void printTableAreaOfPlayer(String nickname) {
        ArrayList<CardClient> cards = model.getListOfCardForTui(nickname);
        ArrayList<String[]> cardsToPrint = new ArrayList<>();

        Collections.sort(cards, Comparator.comparing((CardClient card) -> card.getPosition().x + card.getPosition().y, Comparator.reverseOrder())
                .thenComparing(card -> card.getPosition().x));

        int level = cards.get(0).getPosition().x + cards.get(0).getPosition().y;
        int min = cards.stream().mapToInt(card -> card.getPosition().x).min().orElse(0);
        int inizialier = min;

        for(CardClient card: cards){
            //TODO sistemare fai shift delle carte
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
        //solo per ultima lista di carte
        printCardArray(cardsToPrint);
    }
}




