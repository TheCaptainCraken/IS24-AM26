package it.polimi.ingsw.controller.server;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;

import java.awt.*;
import java.util.HashMap;


/**
 * This class represents the main controller of the game.
 * It manages the game flow and interactions between server and the game model.
 */
public class Controller {
    private static final Controller INSTANCE = new Controller();

    Lobby lobby;
    GameMaster game = null;

    /**
     * Gets the instance of the controller.
     * @return The instance of the controller.
     */
    public static Controller getInstance(){
        return INSTANCE;
    }

    /**
     * Private constructor for the singleton pattern.
     * Initializes the lobby.
     */
    private Controller() {
        lobby = new Lobby();
        //TODO max size Lobby
    }
    /**
     * Initializes the lobby with a maximum number of players.
     * @param nPlayers The maximum number of players.
     * @throws ClosingLobbyException If the lobby is already closed.
     */
    public void initializeLobby(int nPlayers) throws ClosingLobbyException {
        lobby.setMaxSize(nPlayers);
    }
    /**
     * Adds a player to the lobby.
     * @param nickname The nickname of the player.
     * @throws SameNameException If a player with the same nickname already exists.
     * @throws LobbyCompleteException If the lobby is already full.
     */
    public void addPlayer(String nickname) throws SameNameException, LobbyCompleteException {
        lobby.addPlayer(nickname);
    }

    /**
     * Places the root card for a player.
     * @param player The player's name.
     * @param side The side of the card.
     * @throws WrongGamePhaseException If the game is not in the correct phase.
     * @throws NoTurnException If it's not the player's turn.
     * @throws NotExistsPlayerException If the player does not exist.
     * @return The id of the placed card.
     */
    public int placeRootCard(String player, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        return game.placeRootCard(player, side);
    }

    /**
     * Allows a player to choose an objective card.
     * @param player The player's name.
     * @param whichCard The index of the card to choose.
     * @throws WrongGamePhaseException If the game is not in the correct phase.
     * @throws NoTurnException If it's not the player's turn.
     * @throws NotExistsPlayerException If the player does not exist.
     */
    public void chooseObjectiveCard(String player, int whichCard) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        game.chooseObjectiveCard(player, whichCard);
    }

    /**
     * Allows a player to place a card.
     * @param player The player's name.
     * @param indexHand The index of the card in the player's hand.
     * @param position The position to place the card.
     * @param side The side of the card.
     * @throws WrongGamePhaseException If the game is not in the correct phase.
     * @throws NoTurnException If it's not the player's turn.
     * @throws NoSuchFieldException If the field does not exist.
     * @throws NotEnoughResourcesException If the player does not have enough resources.
     * @return The id of the placed card.
     */
    public int placeCard(String player, int indexHand, Point position, boolean side) throws WrongGamePhaseException,
            NoTurnException, NoSuchFieldException, NotEnoughResourcesException {
        int cardId = game.placeCard(player, indexHand, position, side);
        return cardId;
    }

    /**
     * Allows a player to draw a card.
     * @param player The player's name.
     * @param gold Whether the card is gold or not.
     * @param onTableOrDeck The index of the card on the table or deck.
     * @throws WrongGamePhaseException If the game is not in the correct phase.
     * @throws NoTurnException If it's not the player's turn.
     * @throws NotExistsPlayerException If the player does not exist.
     * @return The id of the drawn card.
     */
    public int drawCard(String player, boolean gold, int onTableOrDeck) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        return game.drawCard(player, gold, onTableOrDeck);
    }

    /**
     * Gets the points of a player.
     * @param player The player's name.
     * @throws NoSuchFieldException If the field does not exist.
     * @return The player's points.
     */
    public int getPlayerPoints(String player) throws NoSuchFieldException {
        return lobby.getPlayerFromName(player).getPoints();
    }

    /**
     * Gets the resources of a player.
     * @param player The player's name.
     * @throws NoSuchFieldException If the field does not exist.
     * @return A map of the player's resources.
     */
    public HashMap<Sign, Integer> getPlayerResources(String player) throws NoSuchFieldException {
        return lobby.getPlayerFromName(player).getResources();
    }

    /**
     * Gets the name of the current player.
     * @return The name of the current player.
     */
    public String getCurrentPlayer() {
        return game.getCurrentPlayer().getName();
    }

    /**
     * Gets the id of the starting card of a player.
     * @param nickname The player's nickname.
     * @throws NoSuchFieldException If the field does not exist.
     * @return The id of the starting card.
     */
    public int getStartingCard(String nickname) throws NoSuchFieldException {
        return lobby.getPlayerFromName(nickname).getRootCard().getCard().getId();
    }
    /**
     * Gets a player.
     * @param nickname The player's nickname.
     * @throws NoSuchFieldException If the field does not exist.
     * @return The player.
     */
    public Player getPlayer(String nickname) throws NoSuchFieldException {
        return lobby.getPlayerFromName(nickname);
    }

    /**
     * Sets the color for a player.
     * @param name The player's name.
     * @param colour The color to set.
     * @throws NoSuchFieldException If the field does not exist.
     * @throws ColorAlreadyTakenException If the color is already taken.
     * @return Whether all players have chosen a color.
     */
    public boolean setColour(String name, Color colour) throws NoSuchFieldException, ColorAlreadyTakenException {
        //TODO input errato
        for(Player player : lobby.getPlayers()){
            if(player.getColor() == colour){
                throw new ColorAlreadyTakenException();
            }
        }

        lobby.getPlayerFromName(name).setColour(colour);
        //Check if all players have chosen a color
        for (Player player : lobby.getPlayers()) {
            if (player.getColor() == null) {
                return false;
            }
        }

        start();
        //TODO better way to check if all players have chosen a color
        return true;
    }


    public void start() {
  //      game = new GameMaster(lobby,);
    }

    /**
     * Gets the players and their pins.
     * @return A map of players and their pins.
     */
    public HashMap<String, Color> getPlayersAndPins() {
        HashMap<String, Color> PlayerAndPin = new HashMap<>();

        for(Player player : lobby.getPlayers()) {
            PlayerAndPin.put(player.getName(), player.getColor());
        }

        return PlayerAndPin;
    }

    /**
     * Checks if a player is the first player.
     * @param nickname The player's nickname.
     * @return Whether the player is the first player.
     */
    public boolean getIsFirst(String nickname) {
        return lobby.getPlayers()[0].getName().equals(nickname);
    }

    /**
     * Places a new card on the table.
     * @param gold Whether the card is gold or not.
     * @param onTableOrDeck The index of the card on the table or deck.
     * @return The id of the new card on the table, or -1 if the index is -1.
     */
    public int newCardOnTable(boolean gold, int onTableOrDeck) {
        if(onTableOrDeck == -1) {
            return -1;
        }else{
            return game.getCard(gold, onTableOrDeck);
        }
    }
    /**
     * Checks if all root cards are placed.
     * @return Whether all root cards are placed.
     */
    public boolean areAllRootCardPlaced() {
        for(Player player : lobby.getPlayers()) {
            if(player.getRootCard() == null) {
                return false;
            }
        }
        return true;
    }

    public Integer[] getObjectiveCards() {
        //TODO, pubbliche??
        return null;
    }

    public Integer[] getSecretObjectiveCards(String nicknameRefresh) {
        //TODO percheè array di Integer?
        return null;
    }
    /**
     * Gets the head of the deck.
     * @param gold Whether the card is gold or not.
     * @return The head of the deck.
     */
    public Kingdom getHeadDeck(boolean gold) {
        //TODO
        return null;
    }

    /**
     * Checks if all players have chosen the secret objective card.
     * @return Whether all secret objective cards are chosen.
     */
    public boolean areAllSecretObjectiveCardChosen() {
        for(Player player : lobby.getPlayers()) {
            if(player.getSecretObjective() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the hand of a player.
     * @param nickname The player's nickname.
     * @throws NoSuchFieldException If the field does not exist.
     * @return The hand of the player.
     */
    public Integer[] getHand(String nickname) throws NoSuchFieldException {
        Player player = lobby.getPlayerFromName(nickname);
        int i;
        Integer[] hand = new Integer[3];

        for(i = 0; i < 3; i++) {
            hand[i] = player.getHand()[i].getId();
        }
        return hand;
    }

    /**
     * Gets the back card of player's hand.
     * @param nickname The player's nickname.
     * @throws NoSuchFieldException If the field does not exist.
     * @return The hidden hand of the player.
     */
    public Kingdom[] getHiddenHand(String nickname) throws NoSuchFieldException {
        Player player = lobby.getPlayerFromName(nickname);
        Kingdom[] hiddenHand = new Kingdom[3];
        int i;

        for(i = 0; i < 3; i++){
            hiddenHand[i] = player.getHand()[i].getKingdom();
        }
        return hiddenHand;
    }
    /**
     * Gets the game state.
     * @return The game state.
     */
    public GameState getGameState() {
        return game.getState();
    }

    /**
     * Gets the extra points of the players.
     * @return A map of the players and their extra points.
     */
    public HashMap<String, Integer> getExtraPoints() {
        //TODO sostituire con extra points
        HashMap<String, Integer> extraPoints = new HashMap<>();
        for(Player player : lobby.getPlayers()) {
           extraPoints.put(player.getName(), player.getPoints());
        }
        return null;
    }

    public HashMap<String, Integer> getRanking() {
        //TODO
        return null;
    }

    public boolean isEndGame() {
        //TODO Check conditions where this happens
        //also after placing card? data diagram says so but code doesn't work in this way
        //TODO yes
        return false;
    }
}

