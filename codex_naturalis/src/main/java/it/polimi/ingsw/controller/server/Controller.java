package it.polimi.ingsw.controller.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.Serializable;

/**
 * This class represents the main controller of the game.
 * It manages the game flow and interactions between server and the game model.
 */
public class Controller {
    static String basePath = "codex_naturalis/src/main/java/it/polimi/ingsw/model/decks/";
    private static final Controller INSTANCE = new Controller();
    private final String savePath = "savedGame.ser";

    Lobby lobby;
    GameMaster game = null;

    /**
     * Gets the instance of the controller.
     * 
     * @return The instance of the controller.
     */
    public synchronized static Controller getInstance() {
        return INSTANCE;
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
                    basePath + "resourceCardsDeck.json",
                    basePath + "goldCardsDeck.json",
                    basePath + "objectiveCardsDeck.json",
                    basePath + "startingCardsDeck.json");
            saveGame();
        } catch (IOException | ParseException ex) {
            System.out.println("the file is not found");
        }
    }

    /**
     * Tries to load a game from a file.
     * 
     * @throws IOException            If the file is not found.
     * @throws ClassNotFoundException If the file is not valid.
     */
    public GameMaster tryLoadingGame() throws IOException, ClassNotFoundException {
        return GameMaster.tryLoadingGameMaster(savePath);
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
     * @return The id of the placed card.
     */
    public int placeRootCard(String player, boolean side)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        int id = game.placeRootCard(player, side);
        saveGame();
        return id;
    }

    /**
     * Gets the nicknames of the players in the lobby.
     * 
     * @return An ArrayList of the nicknames of the players.
     */
    public ArrayList<String> getNicknames() {
        ArrayList<String> nicknames = new ArrayList<>();

        for (Player player : lobby.getPlayers()) {
            nicknames.add(player.getName());
        }
        return nicknames;
    }

    /**
     * Allows a player to choose an objective card.
     * 
     * @param player    The player's name.
     * @param whichCard The index of the card to choose.
     * @throws WrongGamePhaseException If the game is not in the correct phase.
     * @throws NoTurnException         If it's not the player's turn.
     */
    public void chooseObjectiveCard(String player, int whichCard)
            throws WrongGamePhaseException, NoTurnException, NoNameException {
        game.chooseObjectiveCard(player, whichCard);
        saveGame();
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
    public boolean setColourAndLobbyIsReadyToStart(String name, Color colour)
            throws ColorAlreadyTakenException, NoNameException {
        for (Player player : lobby.getPlayers()) {
            if (player.getColor() == colour) {
                throw new ColorAlreadyTakenException();
            }
        }

        lobby.getPlayerFromName(name).setColour(colour);

        for (Player player : lobby.getPlayers()) {
            if (player.getColor() == null) {
                return false;
            }
        }

        start();
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
    public boolean getIsFirst(String nickname) {
        return lobby.getPlayers()[0].getName().equals(nickname);
    }

    /**
     * Places a new card on the table.
     * 
     * @param gold          Whether the card is gold or not.
     * @param onTableOrDeck The index of the card on the table or deck.
     * @return The id of the new card on the table, or -1 if is the deck
     */
    public int newCardOnTable(boolean gold, int onTableOrDeck) {
        if (onTableOrDeck == -1) {
            return -1;
        } else {
            int id = game.getCard(gold, onTableOrDeck);
            saveGame();
            return id;
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

    public Integer[] getCommonObjectiveCards() {
        Integer[] objectiveCards = new Integer[2];
        objectiveCards[0] = game.getObjectiveCard(0).getId();
        objectiveCards[1] = game.getObjectiveCard(1).getId();

        return objectiveCards;
    }

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
            hiddenHand[i] = new Pair<>(player.getHand()[i].getKingdom(), (player.getHand()[i] instanceof GoldCard));
        }
        return hiddenHand;
    }

    /**
     * Gets the game state.
     * 
     * @return The game state.
     */
    public GameState getGameState() {
        return game.getState();
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
        ArrayList<Player> ranking = game.getRanking();
        saveGame();
        return ranking;
    }

    /**
     * Checks if the game has ended.
     * 
     * @return true if the game state is END, false otherwise.
     */
    public boolean isEndGame() {
        GameState state = game.getState();
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

    public Integer getResourceCards(int position) {
        return game.getResourceCard(position).getId();
    }

    public Integer getGoldCard(int position) {
        return game.getGoldCard(position).getId();
    }

    public boolean isAdmitted(String nickname) {
        return lobby.isAdmitted(nickname);
    }

    public boolean lobbyIsReady() {
        return lobby.isReady();
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

    public int getTurn() {
        return game.getTurn();
    }

    public int getSecretObjectiveCard(String nickname) throws NoNameException {
        return lobby.getPlayerFromName(nickname).getSecretObjective().getId();
    }

    public Lobby getLobby() {
        return lobby;
    }
}
