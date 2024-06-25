package it.polimi.ingsw.controller.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents the main controller of the game.
 * It manages the game flow and interactions between server and the game model.
 * It follows the Singleton design pattern to ensure only one instance of the
 * controller exists.
 *
 * The controller is responsible for initializing the lobby, adding players,
 * managing game flow,
 * and handling interactions between the server and the game model.
 *
 */
public class Controller {
    /**
     * The base path of the decks of cards.
     */
    static String basePath = "codex_naturalis/src/main/java/it/polimi/ingsw/model/decks/";
    /**
     * The instance of the controller.
     */
    private static Controller INSTANCE = null;
    /**
     * The lobby of the game.
     */
    Lobby lobby;
    /**
     * The GameMaster object which manages the game flow.
     */
    GameMaster game = null;

    /**
     * The path to save the game state.
     */
    private final String savePath = "SavedGame.data";

    /**
     * The base path of the decks of cards.
     */
    String resourcePath = "/decksJSON/resourceCardsDeck.json";
    String goldPath = "/decksJSON/goldCardsDeck.json";
    String startingPath = "/decksJSON/startingCardsDeck.json";
    String objectivePath = "/decksJSON/objectiveCardsDeck.json";

    /**
     * Gets the instance of the controller.
     *
     * @return The instance of the controller.
     */
    public static Controller getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Controller();
        }
        return INSTANCE;
    }

    /**
     * Resets the instance of the controller.
     * This method is used to reset the singleton instance of the controller.
     * It can be used when you want to start a new game and need a fresh instance of
     * the controller.
     */
    public void reset() {
        INSTANCE = null;
    }

    /**
     * Private constructor for the singleton pattern.
     * Initializes the lobby.
     */
    private Controller() {
        lobby = new Lobby();
    }

    /**
     * Initializes the lobby with a maximum number of players.
     *
     * @param nPlayers The maximum number of players.
     * @throws ClosingLobbyException If the lobby is already closed.
     */
    public void initializeLobby(int nPlayers) throws ClosingLobbyException {
        lobby.setMaxSize(nPlayers);
    }

    /**
     * Initializes the GameMaster object which manages the game flow.
     *
     * This method is used to create a new GameMaster object with the lobby and the
     * paths to the decks of cards.
     * The actual initialization of the GameMaster is handled by the GameMaster's
     * constructor.
     *
     */
    public void start() {
        try {
            game = new GameMaster(lobby,
                    getClass().getResourceAsStream(resourcePath),
                    getClass().getResourceAsStream(goldPath),
                    getClass().getResourceAsStream(objectivePath),
                    getClass().getResourceAsStream(startingPath));
        } catch (IOException e) {
            System.out.println("the file is not found");
        } catch (ParseException e) {
            System.out.println("the file is not valid");
        }
    }

    /**
     * Tries to load a game from a file.
     * 
     * @throws IOException            If the file is not found.
     * @throws ClassNotFoundException If the file is not valid.
     *
     * @return The GameMaster object loaded from the file.
     */
    public GameMaster tryLoadingGame() throws IOException, ClassNotFoundException {
        return GameMaster.tryLoadingGameMaster(savePath);
    }

    /**
     * This method is used to delete a saved game file from the file system.
     * It calls the static method `cancelFile` from the `GameMaster` class with the path of the saved game file.
     * The actual deletion of the file is handled by the `GameMaster` class.
     */
    public void cancelFile() {
        GameMaster.cancelFile(savePath);
    }

    /**
     * Adds a player to the lobby.
     *
     * @param nickname The nickname of the player.
     * @throws SameNameException      If a player with the same nickname already
     *                                exists.
     * @throws LobbyCompleteException If the lobby is already full.
     */
    public void addPlayer(String nickname) throws SameNameException, LobbyCompleteException {
        lobby.addPlayer(nickname);
    }

    /**
     * Places the root card for a player.
     *
     * @param player The player's name.
     * @param side   The side of the card.
     * @throws WrongGamePhaseException If the game is not in the correct phase.
     * @throws NoTurnException         If it's not the player's turn.
     * @throws NoNameException         If the field does not exist.
     *
     * @return The id of the placed card.
     */
    public int placeRootCard(String player, boolean side)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        return game.placeRootCard(player, side);
    }

    /**
     * Allows a player to choose an objective card.
     *
     * @param player    The player's name.
     * @param whichCard The index of the card to choose.
     * @throws WrongGamePhaseException If the game is not in the correct phase.
     * @throws NoTurnException         If it's not the player's turn.
     * @throws NoNameException         If the field does not exist.
     */
    public void chooseObjectiveCard(String player, int whichCard)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        game.chooseObjectiveCard(player, whichCard);

        if (areAllSecretObjectiveCardChosen()) {
            saveGame();
        }
    }

    /**
     * Allows a player to place a card.
     *
     * @param player    The player's name.
     * @param indexHand The index of the card in the player's hand.
     * @param position  The position to place the card.
     * @param side      The side of the card.
     * @throws WrongGamePhaseException     If the game is not in the correct phase.
     * @throws NoTurnException             If it's not the player's turn.
     * @throws NotEnoughResourcesException If the player does not have enough
     *                                     resources.
     * @throws NoNameException             If the field does not exist.
     * @throws CardPositionException       If the card position is invalid.
     * @return The id of the placed card.
     */
    public int placeCard(String player, int indexHand, Point position, boolean side) throws WrongGamePhaseException,
            NoTurnException, NotEnoughResourcesException, NoNameException, CardPositionException {
        int id = game.placeCard(player, indexHand, position, side);
        saveGame();
        return id;
    }

    /**
     * Allows a player to draw a card.
     *
     * @param player        The player's name.
     * @param gold          Whether the card is gold or not.
     * @param onTableOrDeck The index of the card on the table or deck.
     * @throws WrongGamePhaseException If the game is not in the correct phase.
     * @throws NoTurnException         If it's not the player's turn.
     * @throws NoNameException         If the field does not exist.
     * @throws CardPositionException   If the card position is invalid.
     * @return The id of the drawn card.
     */
    public int drawCard(String player, boolean gold, int onTableOrDeck)
            throws WrongGamePhaseException, NoTurnException, NoNameException, CardPositionException {
        int id = game.drawCard(player, gold, onTableOrDeck);
        saveGame();
        return id;
    }

    /**
     * Gets the points of a player.
     *
     * @param player The player's name.
     * @throws NoNameException If the field does not exist.
     * @return The player's points.
     */
    public int getPlayerPoints(String player) throws NoNameException {
        return lobby.getPlayerFromName(player).getPoints();
    }

    /**
     * Gets the resources of a player.
     *
     * @param player The player's name.
     * @throws NoNameException If the field does not exist.
     * @return A map of the player's resources.
     */
    public HashMap<Sign, Integer> getPlayerResources(String player) throws NoNameException {
        return lobby.getPlayerFromName(player).getResources();
    }

    /**
     * Gets the name of the current player.
     *
     * @return The name of the current player.
     */
    public String getCurrentPlayer() {
        return game.getCurrentPlayer().getName();
    }

    /**
     * Gets the id of the starting card of a player.
     *
     * @param nickname The player's nickname.
     * @throws NoNameException If the field does not exist.
     * @return The id of the starting card.
     */
    public int getStartingCard(String nickname) throws NoNameException {
        return game.getStartingCardToPosition(nickname).getId();
    }

    /**
     * Gets a player.
     *
     * @param nickname The player's nickname.
     * @throws NoNameException If the field does not exist.
     * @return The player.
     */
    public Player getPlayer(String nickname) throws NoNameException {
        return lobby.getPlayerFromName(nickname);
    }

    /**
     * Sets the color for a player.
     *
     * @param name   The player's name.
     * @param colour The color to set.
     * @throws NoNameException            If the field does not exist.
     * @throws ColorAlreadyTakenException If the color is already taken.
     * @return Whether all players have chosen a color.
     */
    public boolean setColourAndGameIsReadyToStart(String name, Color colour)
            throws ColorAlreadyTakenException, NoNameException {
        for (Player player : lobby.getPlayers()) {
            if (player.getColor() == colour) {
                throw new ColorAlreadyTakenException();
            }
        }
        // set the color
        lobby.getPlayerFromName(name).setColour(colour);

        // check if all players have chosen a color
        for (Player player : lobby.getPlayers()) {
            if (player.getColor() == null) {
                return false;
            }
        }

        // start the game
        start();

        // return true if all players have chosen a color
        return true;
    }

    /**
     * Gets the players and their pins.
     *
     * @return A map of players and their pins.
     */
    public HashMap<String, Color> getPlayersAndPins() {
        return lobby.getPlayersAndPins();
    }

    /**
     * Checks if a player is the first player.
     *
     * @param nickname The player's nickname.
     * @return Whether the player is the first player.
     */
    public boolean isFirst(String nickname) {
        return lobby.getPlayers()[0].getName().equals(nickname);
    }

    /**
     * Places a new card on the table.
     *
     * @param gold          Whether the card is gold or not.
     * @param onTableOrDeck The index of the card on the table or deck.
     * @return The id of the new card on the table, or -1 if is the deck
     */
    public Integer newCardOnTable(boolean gold, int onTableOrDeck) {
        // it turns -1 since no card on table has updated, the little model client know
        // how to manage it
        if (onTableOrDeck == -1) {
            return -1;
        } else {
            // can be null, correct to manage it
            return game.getCard(gold, onTableOrDeck);
        }
    }

    /**
     * Checks if all root cards are placed.
     *
     * @return Whether all root cards are placed.
     */
    public boolean areAllRootCardPlaced() {
        for (Player player : lobby.getPlayers()) {
            if (player.getRootCard() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Getter of the common objective cards for the game.
     *
     * This method is used to get the IDs of the common objective cards that are
     * currently in play.
     *
     * @return An array of Integer representing the IDs of the common objective
     *         cards.
     */
    public Integer[] getCommonObjectiveCards() {
        Integer[] objectiveCards = new Integer[2];
        objectiveCards[0] = game.getObjectiveCard(0).getId();
        objectiveCards[1] = game.getObjectiveCard(1).getId();

        return objectiveCards;
    }

    /**
     * Retrieves the secret objective cards for a specific player to choose from.
     *
     * This method is used to get the IDs of the secret objective cards that the
     * specified player can choose from.
     * These cards are individual objectives that only the specific player can aim
     * to achieve.
     *
     * @param name The name of the player.
     * @throws NoNameException If the player's name does not exist.
     * @return An array of Integer representing the IDs of the secret objective
     *         cards.
     */
    public Integer[] getSecretObjectiveCardsToChoose(String name) throws NoNameException {
        Integer[] secretObjectiveCards = new Integer[2];
        int position = game.getOrderPlayer(name);
        secretObjectiveCards[0] = game.getObjectiveCardToChoose(position, 0).getId();
        secretObjectiveCards[1] = game.getObjectiveCardToChoose(position, 1).getId();

        return secretObjectiveCards;
    }

    /**
     * Gets the head of the deck.
     *
     * @param gold Whether the card is gold or not.
     * @return The head of the deck.
     */
    public Kingdom getHeadDeck(boolean gold) {
        return game.getHeadDeck(gold);
    }

    /**
     * Checks if all players have chosen the secret objective card.
     *
     * @return Whether all secret objective cards are chosen.
     */
    public boolean areAllSecretObjectiveCardChosen() {
        for (Player player : lobby.getPlayers()) {
            if (player.getSecretObjective() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the hand of a player.
     *
     * @param nickname The player's nickname.
     * @throws NoNameException If the field does not exist.
     * @return The hand of the player.
     */
    public Integer[] getHand(String nickname) throws NoNameException {
        Player player = lobby.getPlayerFromName(nickname);
        int i;
        Integer[] hand = new Integer[3];

        for (i = 0; i < 3; i++) {
            hand[i] = player.getHand()[i].getId();
        }
        return hand;
    }

    /**
     * Gets the back card of player's hand.
     *
     * @param nickname The player's nickname.
     * @throws NoNameException If the field does not exist.
     * @return The hidden hand of the player.
     */
    public Pair<Kingdom, Boolean>[] getHiddenHand(String nickname) throws NoNameException {
        Player player = lobby.getPlayerFromName(nickname);
        Pair<Kingdom, Boolean>[] hiddenHand = new Pair[3];
        int i;

        for (i = 0; i < 3; i++) {
            // hiddenHand[i] can be null since one player has placed a card, but not yet
            // drawn a new one.
            try {
                hiddenHand[i] = new Pair<>(player.getHand()[i].getKingdom(), (player.getHand()[i] instanceof GoldCard));
            } catch (NullPointerException e) {
                hiddenHand[i] = null;
            }
        }
        return hiddenHand;
    }

    /**
     * Gets the game state.
     *
     * @return The game state.
     */
    public GameState getGameState() {
        return game.getGameState();
    }

    /**
     * Gets the extra points of the players.
     *
     * @return A map of the players and their extra points.
     */
    public HashMap<String, Integer> getExtraPoints() {
        HashMap<String, Integer> extraPoints = new HashMap<>();
        for (Player player : lobby.getPlayers()) {
            extraPoints.put(player.getName(), player.getObjectivePoints());
        }
        return extraPoints;
    }

    /**
     * Returns the ranking of players.
     *
     * @return An ArrayList of players sorted by their ranking.
     */
    public ArrayList<Player> getRanking() {
        return game.getRanking();
    }

    /**
     * Checks if the game has ended.
     *
     * @return true if the game state is END, false otherwise.
     */
    public boolean isEndGame() {
        GameState state = game.getGameState();
        return state == GameState.END;
    }

    /**
     * Returns the name of the first player in the lobby.
     *
     * @return The name of the first player.
     */
    public String getFirstPlayer() {
        return lobby.getPlayers()[0].getName();
    }

    /**
     * Checks if the lobby is locked.
     *
     * @return true if the lobby is locked, false otherwise.
     */
    public boolean isLobbyLocked() {
        return lobby.getLock();
    }

    /**
     * Retrieves the resource card at the specified position.
     *
     * @param position The position of the resource card.
     * @return The ID of the resource card at the specified position.
     */
    public Integer getResourceCards(int position) {
        return game.getResourceCard(position).getId();
    }

    /**
     * Retrieves the gold card at the specified position.
     *
     * @param position The position of the gold card.
     * @return The ID of the gold card at the specified position.
     */
    public Integer getGoldCard(int position) {
        return game.getGoldCard(position).getId();
    }

    /**
     * Checks if the player with the given nickname is admitted.
     *
     * @param nickname The nickname of the player.
     * @return true if the player is admitted, false otherwise.
     */
    public boolean isAdmitted(String nickname) {
        return lobby.isAdmitted(nickname);
    }

    /**
     * Checks if the lobby is ready.
     *
     * @return true if the lobby is ready, false otherwise.
     */
    public boolean lobbyIsReady() {
        return lobby.isReady();
    }

    /**
     * Retrieves the current turn of the game.
     *
     * @return The current turn of the game.
     */
    public int getTurn() {
        return game.getTurn();
    }

    /**
     * Retrieves the lobby of the game.
     *
     * @return The lobby of the game.
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     * Saves the game state to a file. This implements the game saving advanced
     * functionality.
     */
    public void saveGame() {
        try {
            FileOutputStream saveFile = new FileOutputStream(savePath);
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(game);

            save.close();
            saveFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the GameMaster for the current game.
     * This method is used when loading a saved game state. It sets the GameMaster
     * to the saved game state and updates the lobby accordingly.
     *
     * @param savedGame The saved GameMaster object loaded from a file.
     */
    public void setGameMaster(GameMaster savedGame) {
        lobby = savedGame.getLobby();
        game = savedGame;
    }
}