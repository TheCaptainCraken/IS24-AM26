package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.HashMap;

public interface ViewInterface {
    //create the GUI/UI. It is launched by controller
    //non credo debba essere implementato da gui, esiste già un metodo start() ma è della Classe Astratta Application
    void start();
    //Server to Client
    //It is the first player please: Print something to say choose number of Players.
    void askNumberOfPlayers();
    //Wait that all have performed the action. Remember we wait at color, choice of starting card and objective card. Also at the login. You can control the phase in the controller.
    //controller.getPhase()...
    void waitLobby();
    //message of stopWaiting. The next phase is chooseColor.
    void stopWaiting();
    //after the choice of number of players, we just print the choice of the first player and in case generate the right number of profiles for GUI.
    void correctNumberOfPlayers(int numberOfPlayers);
    //mi cade la connessione a me( perdo io connessione)
    void disconnect();

    // void disconnetOther(); indica che qualcun altro si è disconesso.

    //void too many eople in lobby--> indica che ci sono troppi giocatori nella lobby.

    //refresh users. NOTE: color can be null if the player has not chosen a color yet.
    void refreshUsers(HashMap<String, Color> playersAndPins);
    //Show who has black pin and has to start.
    void showIsFirst(String firstPlayer);
    //show the starting card to choose side. You should print both the side of the same starting card.
    void showStartingCard(int startingCardId);
    //prints the card on the table or the update
    void showCommonTable();
    //print the common objectives card. They are fixed for all the game.
    void showCommonObjectives(Integer[] objectiveCardIds);
    //The to card to choose from. You should allow the player to choose one of them.
    //for the selection call the method: controller.chooseSecretObjectiveCard(indexCard); IndexCard can be 0(first card) or 1(second card).
    //NOTE: control the input, This method does not control the integer type
    void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds);
    //The selection given by the player. When this method is called, you can print the secret objective card chosen by the player. It is fixed.
    void showSecretObjectiveCard(int indexCard);
    //print the current player and the game state.
    void showTurnInfo(String currentPlayer, GameState gameState);
    void showResourcesPlayer();
    void showResourcesAllPlayers();
    void showPoints();
    //Show points earned at the end
    void showExtraPoints(HashMap<String, Integer> extraPoints);
    //you should print just the order of their nickname. The order given is from winner to loser. It is set by model in the server.
    void showRanking(ArrayList<Player> ranking);
    //They are triggered by menus and listeners or they automatically appear on the screen if you are watching to them.
    //TODO Should be done also for the TUI
    //Show the cards placed of the player on the table.
    void showTableOfPlayer(String nickname);
    //show the new hand of another player. NULL
    void showHiddenHand(String nickname);
    //this is your private Hand. You can take this information from the model. NULL
    void showHand();

    //Report Exceptions
    void sameName(String nickname);
    void colorAlreadyTaken();
    void noTurn();
    void notEnoughResources();
    void noConnection();
    void cardPositionError();
    void lobbyComplete();
    void wrongGamePhase();
    void noPlayer();
    //error in insert number of players
    void closingLobbyError();

    void showStartingCardChosen();
    void stopGaming();
}
