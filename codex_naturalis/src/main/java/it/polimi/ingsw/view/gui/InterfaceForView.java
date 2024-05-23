package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.HashMap;

public interface InterfaceForView {
    //create the GUI/UI. It is launched by controller
    //void start();
    //message of stopWaiting. The next phase is chooseColor.
    void stopWaiting();
    //you have been disconnected since you have been inactive for too long, or the lobby is already full.
    void disconnect();
    //refresh users. NOTE: color can be null if the player has not chosen a color yet.
    void refreshUsers(HashMap<String, Color> playersAndPins);
    //show the starting card to choose side. You should print both the side of the same starting card.
    void showStartingCard(int startingCardId);
    //print the common objectives card. They are fixed for all the game.
    void showCommonObjectives(Integer[] objectiveCardIds);
    //The selection given by the player. When this method is called, you can print the secret objective card chosen by the player. It is fixed.
    void showSecretObjectiveCard(int indexCard);
    //The to card to choose from. You should allow the player to choose one of them.
    //for the selection call the method: controller.chooseSecretObjectiveCard(indexCard); IndexCard can be 0(first card) or 1(second card).
    //NOTE: control the input, This method does not control the integer type
    void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds);
    //print the current player and the game state.
    void showTurnInfo(String currentPlayer, GameState gameState);
    void showExtraPoints(HashMap<String, Integer> extraPoints);
    //you should print just the order of their nickname. The order given is from winner to loser. It is set by model in the server.
    void showRanking(ArrayList<Player> ranking);
    //TODO capire come fare differenziazione su controller.
    void showHiddenHand(String nickname);
    //this is your private Hand. YOu can take this information from the model.
    void showHand();
    //TODO capire come fare differenziazione su controller.
    void showResourcesPlayer(String name);
    //TODO capire come fare differenziazione su controller.
    void showResourcesAllPlayers();
    //TODO capire come fare differenziazione su controller.
    void showPoints(HashMap<String, Integer> points);
    void colorAlreadyTaken();
    void sameName(String nickname);
    void noTurn();
    void notEnoughResources();
    void noConnection();
    void cardPositionError();
    void lobbyComplete();
    void wrongGamePhase();
    void noPlayer();
    void closingLobbyError();
    void showIsFirst(String firstPlayer);
    //It is the first player please: Print something to say choose number of Players.
    void showInsertNumberOfPlayers();
    //Wait that all have performed the action. Remember we wait at color, choice of starting card and objective card. Also at the login. You can control the phase in the controller.
    //controller.getPhase()...
    void waitLobby();
    //after the choice of number of players, we just print the choice of the first player.
    void correctNumberOfPlayers(int numberOfPlayers);

}
