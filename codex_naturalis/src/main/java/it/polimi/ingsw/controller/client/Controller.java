package it.polimi.ingsw.controller.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.*;

import it.polimi.ingsw.network.client.ClientRMI;
import it.polimi.ingsw.network.client.ClientSocket;
import it.polimi.ingsw.network.client.NetworkClient;
import it.polimi.ingsw.view.LittleModel;
import it.polimi.ingsw.view.Tui;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Controller {
    private String nickname;
    private NetworkClient connection;
    private Tui view;
    private LittleModel model;

    public Controller(){
        model = new LittleModel();
    }

    public void setView(String typeOfView) {
        model = new LittleModel();
        if(typeOfView.equals("TUI")){
            this.view = new Tui(model, this);
            view.start();
        }else if(typeOfView.equals("GUI")){
            //TODO
            //view = new GUI();
        }
    }

    public void createInstanceOfConnection(String typeOfConnection){
        if(typeOfConnection.equals("RMI")){
            connection = new ClientRMI(this);
        }else if(typeOfConnection.equals("Socket")){
            connection = new ClientSocket("placeholder",69);
            //TODO vanno aggiunti address e port al costruttore,
            // il primo sarà una costante salvata nel codice l'altro sarà generato automaticamente
        }
    }

    /**
     * Sets the nickname of the player.
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns the nickname of the player.
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }


    /**
     * Triggers the view to ask the user for the number of players.
     *
     * This method is used to prompt the user to input the number of players that will be participating in the game.
     * The actual input is handled by the view (TUI or GUI).
     */
    public void askNumberOfPlayer() {
        view.showInsertNumberOfPlayer();
    }
    /**
     * Triggers the view to display a waiting message to the user.
     *
     * This method is used to inform the user that they are waiting for other players to join the game lobby.
     * The actual display of the waiting message is handled by the view (TUI or GUI).
     */
    public void waitLobby() {
        view.waitLobby();
    }

    /**
     * Triggers the view to stop displaying the waiting message.
     *
     * This method is used to inform the user that the waiting phase is over and the game is starting.
     * The actual removal of the waiting message is handled by the view (TUI or GUI).
     */
    public void stopWaiting() {
        view.stopWaiting();
    }


    /**
     * Triggers the view to refresh the display of users.
     *
     * This method is used to update the display of users and their associated colors in the game.
     * The actual display update is handled by the view (TUI or GUI).
     *
     * @param playersAndPins A HashMap where the keys are the nicknames of the players and the values are their associated colors.
     */
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        view.refreshUsers(playersAndPins);
    }

    /**
     * Triggers the view to handle the disconnection of the user.
     *
     * This method is used to inform the user that they have been disconnected from the game.
     */
    public void disconnect() {
        view.disconnect();
    }

    public void cardsOnTable(Integer[] resourceCards, Integer[] goldCard, Kingdom resourceCardOnDeck, Kingdom goldCardOnDeck) {
        model.updateCommonTable(resourceCards, goldCard, resourceCardOnDeck, goldCardOnDeck);
    }
    /**
     * Triggers the view to display the starting card to the user.
     *
     * This method is used to inform the user about their starting card in the game.
     * The actual display of the starting card is handled by the view (TUI or GUI).
     *
     * @param startingCardId The ID of the starting card.
     */
    public void showStartingCard(int startingCardId) {
        view.showStartingCard(startingCardId);
    }

    /**
     * Triggers the view to display the objective cards to the user.
     *
     * This method is used to inform the user about their objective cards in the game.
     * The actual display of the objective cards is handled by the view (TUI or GUI).
     *
     * @param objectiveCardIds The IDs of the objective cards.
     */
    public void showObjectiveCards(Integer[] objectiveCardIds) {
        view.showCommonObjectives(objectiveCardIds);
    }

    /**
     * Triggers the view to display the secret objective card to the user.
     * This method is used to inform the user about their secret objective card in the game.
     * The actual display of the secret objective card is handled by the view (TUI or GUI).
     *
     * @param indexCard The index of the secret objective card.
     */
    public void showSecretObjectiveCard(int indexCard) {
        view.showSecretObjectiveCard(indexCard);
    }


    /**
     * Triggers the view to refresh the turn information.
     *
     * This method is used to update the display of the current player and the game state.
     * The actual display update is handled by the view (TUI or GUI).
     *
     * @param currentPlayer The nickname of the current player.
     * @param gameState The current state of the game.
     */
    public void turnInfo(String currentPlayer, GameState gameState) {
        view.showTurnInfo(currentPlayer, gameState);
        //TODO capire chiamate con Daniel
    }

    /**
     * Triggers the view to display the extra points to the user.
     *
     * This method is used to inform the user about their extra points in the game.
     * The actual display of the extra points is handled by the view (TUI or GUI).
     *
     * @param extraPoints A HashMap where the keys are the nicknames of the players and the values are their associated extra points.
     */
    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
        view.showExtraPoints(extraPoints);
    }

    /**
     * Triggers the view to display the ranking to the user.
     *
     * This method is used to inform the user about the ranking in the game.
     * The actual display of the ranking is handled by the view (TUI or GUI).
     *
     * @param ranking A HashMap where the keys are the nicknames of the players and the values are their associated scores.
     */
    public void showRanking(ArrayList ranking) {
        view.showRanking(ranking);
    }

    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {
        view.showSecretObjectiveCardsToChoose(objectiveCardIds);
    }

    public void updatePlaceCard(String nickname, int id, Point position, boolean side) {
        model.updatePlaceCard(nickname, id, position, side);
    }

    public void updateResources(String nickname, HashMap<Sign, Integer> resources) {
        model.updateResources(nickname, resources);
    }

    public void updateDrawCard(String nickname, int cardId) {
        model.updateDrawCard(nickname, cardId);
    }

    public void updateScore(String nickname, int points) {
        model.updateScore(nickname, points);
    }

    public void updateHeadDeck(Kingdom headDeck, boolean gold) {
        model.updateHeadDeck(headDeck, gold);
    }

    public void updateCardOnTable(int newCardId, boolean gold, int onTableOrDeck) {
        model.updateCardOnTable(newCardId, gold, onTableOrDeck);
    }
    public void updateHand(String nickname, Integer[] hand) {
        model.updateHand(nickname, hand);
    }

    public void updateHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand) {
        model.updateHiddenHand(nickname, hand);
    }
    public void showIsFirst(String firstPlayer) {
        view.showIsFirst(firstPlayer);
    }

    /**
     * Logs in the player to the game.
     *
     * This method is used to log in the player to the game by using the connection object.
     * The actual login is handled by the connection object.
     *
     * @param nickname The nickname of the player.
     */
    public void login(String nickname) {
        try{
            connection.login(nickname);
        } catch (SameNameException e) {
            view.sameName(nickname);
        } catch (LobbyCompleteException e){
            view.lobbyComplete();
        } catch (RemoteException e) {
            view.noConnection();
        }
    }

    /**
     * Sets the number of players in the game.
     *
     * This method is used to set the number of players that will be participating in the game.
     * The actual setting of the number of players is handled by the connection object.
     *
     * @param numberOfPlayers The number of players.
     */
    public void insertNumberOfPlayers(int numberOfPlayers) {
        try{
            connection.insertNumberOfPlayers(numberOfPlayers);
        } catch (RemoteException e) {
            view.noConnection();
        } catch (ClosingLobbyException e) {
            view.closingLobbyError();
        } catch (SameNameException e) {
            view.sameName(nickname);
        } catch (LobbyCompleteException e) {
            view.lobbyComplete();
        } catch (NoNameException e) {
            view.noPlayer();
        }
    }

    /**
     * Sets the color of the player in the game.
     *
     * This method is used to set the color of the player in the game.
     * The actual setting of the color is handled by the connection object.
     *
     * @param color The color chosen by the player.
     */
    public void chooseColor(Color color) {
        try{
            connection.chooseColor(color);
        } catch (RemoteException e) {
            view.noConnection();
        } catch (ColorAlreadyTakenException e) {
            view.colorAlreadyTaken();
        } catch (NoNameException e) {
            view.noPlayer();
        }
    }
    /**
     * Sets the side of the starting card for the player in the game.
     *
     * This method is used to set the side of the starting card for the player in the game.
     * The actual setting of the side is handled by the connection object.
     *
     * @param side The side chosen by the player.
     */
    public void chooseSideStartingCard(boolean side) {
        try {
            connection.chooseSideStartingCard(side);
        }
        catch (RemoteException e) {
            view.noConnection();
        }
        catch (WrongGamePhaseException e) {
            view.wrongGamePhase();
        } catch (NoTurnException e) {
            view.noTurn();
        } catch (NoNameException e) {
            view.noPlayer();
        }
    }

    /**
     * Sets the secret objective card for the player in the game.
     *
     * This method is used to set the secret objective card for the player in the game.
     * The actual setting of the card is handled by the connection object.
     *
     * @param indexCard The index of the secret objective card chosen by the player.
     */
    public void chooseSecretObjectiveCard(int indexCard) {
        try{
            connection.chooseSecretObjectiveCard(indexCard);
        } catch (WrongGamePhaseException e) {
            view.wrongGamePhase();
        } catch (NoTurnException e) {
            view.noTurn();
        }
        catch (NoNameException e) {
            view.noPlayer();
        } catch (RemoteException e) {
           view.noConnection();
        }
    }

    /**
     * Plays a card in the game.
     *
     * This method is used to play a card in the game.
     * The actual playing of the card is handled by the connection object.
     *
     * @param indexHand The index of the card in the player's hand.
     * @param position The position where the card will be placed.
     * @param side The side of the card.
     */
    public void playCard(int indexHand, Point position, boolean side) {
        try{
            connection.playCard(indexHand, position, side);
        } catch (WrongGamePhaseException e) {
            view.wrongGamePhase();
        } catch (NoTurnException e) {
            view.noTurn();
        } catch (NotEnoughResourcesException e) {
            view.notEnoughResources(nickname);
        } catch (NoNameException e) {
            view.noPlayer();
        } catch (CardPositionException e) {
            view.cardPositionError();
        } catch (RemoteException e) {
            view.noConnection();
        }
    }

    /**
     * Draws a card in the game.
     *
     * This method is used to draw a card in the game.
     * The actual drawing of the card is handled by the connection object.
     *
     * @param gold The gold status of the card.
     * @param onTableOrDeck The location from where the card is drawn.
     */
    public void drawCard(boolean gold, int onTableOrDeck) {
        try{
            connection.drawCard(nickname, gold, onTableOrDeck);
        } catch (WrongGamePhaseException e) {
            view.wrongGamePhase();
        } catch (NoTurnException e) {
            view.noTurn();
        } catch (NoNameException e) {
            view.noPlayer();
        } catch (RemoteException e) {
            view.noConnection();
        }
    }

    public void showTableOfPlayer(String name){
        view.showTableOfPlayer(model.getTableOfPlayer(name));
    }

    public void showPoints(){
        view.showPoints(model.getPoints());
    }

    public void showResources(){
        view.showResourcesPlayer(getNickname());
    }

    public void showResourcesAllPlayers(){
        view.showResourcesAllPlayers();
    }

    public void showHand(){
        view.showHand();
    }

    public void showHiddenHand(String name){
        view.showHiddenHand(name);
    }

}
