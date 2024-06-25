package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * This interface defines the methods that a View in the MVC pattern should implement.
 * The View in this context is responsible for presenting data to the user and interpreting the user's actions.
 *
 * Each method should be implemented according to the specific needs of the View (e.g., GUI or TUI).
 */
public interface ViewInterface {
    /**
     * This method is responsible for prompting the user to input the number of players for the game.
     * It is typically called at the beginning of the game setup process.
     */
    void askNumberOfPlayers();
    /**
     * This method is responsible for making the user wait in the lobby until the first player have chosen the number of players and enough players have joined the lobby.
     * It is typically called during the game setup process, after the number of players has been determined and before the game starts.
     */
    void waitLobby();
    /**
     * This method is responsible for notifying the user that the waiting period in the lobby is over.
     * It is called during the game setup process, after all players have joined the lobby and the game is about to start.
     */
    void stopWaiting();
    /**
     * This method is responsible for confirming the number of players in the game.
     * It is called after the first player has chosen the number of players.
     * @param numberOfPlayers The number of players in the game.
     */
    void correctNumberOfPlayers(int numberOfPlayers);
    /**
     * This method is responsible for updating the list of players and their associated colors. color could be null if the player has not chosen the color yet.
     * @param playersAndPins A map of player names to their associated colors.
     */
    void refreshUsers(HashMap<String, Color> playersAndPins);
    /**
     * This method is responsible for showing which player is the first to play.
     * @param firstPlayer The name of the first player.
     */
    void showIsFirst(String firstPlayer);
    /**
     * This method is responsible for showing the starting card to the players.
     * @param startingCardId The ID of the starting card.
     */
    void showStartingCard(int startingCardId);
    /**
     * This method is responsible for showing the starting card chosen by the player.
     */
    void showStartingCardChosen();
    /**
     * This method is responsible for showing the common table to the players.
     */
    void showCommonTable();
    /**
     * This method is responsible for showing the common objective cards to the players.
     * @param objectiveCardIds The IDs of the common objective cards.
     */
    void showCommonObjectives(Integer[] objectiveCardIds);
    /**
     * This method is responsible for showing the secret objective cards for the player to choose from.
     * @param objectiveCardIds The IDs of the secret objective cards.
     */
    void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds);
    /**
     * This method is responsible for showing the secret objective card chosen by the player.
     * @param indexCard The index of the chosen card. (0,1) it is located in little model.
     */
    void showSecretObjectiveCard(int indexCard);
    /**
     * This method is responsible for showing the current player and the game state.
     * @param currentPlayer The name of the current player.
     * @param gameState The current game state.
     */
    void showTurnInfo(String currentPlayer, GameState gameState);
    /**
     * This method is responsible for showing the resources of the client player.
     */
    void showResourcesPlayer();
    /**
     * This method is responsible for showing the points of all player.
     */
    void showPoints();
    /**
     * This method is responsible for showing the extra points of all player. They are the objective cards points.
     * @param extraPoints A map of player names to their extra points.
     */
    void showExtraPoints(HashMap<String, Integer> extraPoints);
    /**
     * This method is responsible for showing the ranking of the players. It should visualize the players sorted by their points, and their full points(normal + objective).
     * @param ranking A list of players sorted by their points.
     */
    void showRanking(ArrayList<Player> ranking);
    /**
     * This method is responsible for showing the cards placed of the player on the table.
     * @param nickname The name of the player.
     */
    void showTableOfPlayer(String nickname);
    /**
     * This method is responsible for showing the cards placed of all players on the table. One cart at time could be NULL
     *
     * @param nickname The name of the player.
     */
    void showHiddenHand(String nickname);
    /**
     * This method is responsible for showing the cards placed of all players on the table. One cart at time could be NULL
     */
    void showHand();

    //Exceptions
    /**
     *  This method is responsible for showing an error message when the player tries to choose a nickname that is already taken.
     * @param nickname The nickname that the player tried to choose.
     */
    void sameName(String nickname);
    /**
     * This method is responsible for showing an error message when the player tries to choose a color that is already taken.
     */
    void colorAlreadyTaken();
    /**
     * This method is responsible for showing an error message when the player tries to do an action when it is not his/her turn.
     */
    void noTurn();
    /**
     * This method is responsible for showing an error message when the player has not enough resources to play a card.
     */
    void notEnoughResources();
    /**
     * This method is responsible for showing an error message when the player tries to play a card in a wrong position.
     */
    void cardPositionError();
    /**
     * This method is responsible for showing an error message when the player tries to enter in  already started lobby.
     */
    void lobbyComplete();
    /**
     * This method is responsible for showing an error message when the player tries to play/draw/set a card in a wrong phase.
     */
    void wrongGamePhase();
    /**
     * This method is responsible for showing an error message when the server doesn't know the name of a client.
     */
    void noPlayer();
    /**
     * This method is responsible for showing an error message when the player tries to set the number of players, but the client can't perform this action.
     */
    void closingLobbyError();
    /**
     * This method is responsible for showing a message when someone has disconnected, and the game will end for all players.
     */
    void stopGaming();
    /**
     * This method is responsible for showing that the client loses the connection with the server.
     */
    void noConnection();
    /**
     *  The server disconnect you from the game, since enough players have joined the lobby.
     */
    void disconnect();
    /**
     * This method is responsible for showing a message for chatting.
     *
     * @param sender  The sender of the message.
     * @param message The message.
     * @param broadcast A boolean indicating whether the message is a broadcast message.
     */
    void receiveChatMessage(String sender, String message, boolean broadcast);
}
