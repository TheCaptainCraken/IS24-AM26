package it.polimi.ingsw.controller.client;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.*;

import it.polimi.ingsw.network.rmi.ClientRMI;
import it.polimi.ingsw.network.client.ClientSocket;
import it.polimi.ingsw.network.client.NetworkClient;
import it.polimi.ingsw.network.rmi.RMIClientInterface;
import it.polimi.ingsw.view.LittleModel;
import it.polimi.ingsw.view.Phase;
import it.polimi.ingsw.view.Tui;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
    private static String nickname;
    public static Phase phase;
    private static RMIClientInterface clientRMI;
    private NetworkClient connection;
    private Tui view;
    private LittleModel model;

    public Controller(){
        model = new LittleModel();
        phase = Phase.LOGIN;
    }

    public static synchronized Phase getPhase() {
        return Controller.phase;
    }

    public static synchronized void setPhase(Phase phase) {
        Controller.phase = phase;
    }

    public void setView(String typeOfView) throws InterruptedException {
        model = new LittleModel();
        if(typeOfView.equals("TUI")){
            this.view = new Tui(model, this);
            view.start();
        }else if(typeOfView.equals("GUI")){
            //TODO
            //view = new GUI();
        }

        //for socket
//        new Thread({
//                this.view = new Tui(model, this);
//                view.start();
//        });
    }

    public void createInstanceOfConnection(String typeOfConnection){
        if(typeOfConnection.equals("RMI")){
            try {
                clientRMI = new ClientRMI(this);
            } catch (RemoteException e) {
                //TODO
            } catch (SameNameException e) {
                throw new RuntimeException(e);
            } catch (LobbyCompleteException e) {
                throw new RuntimeException(e);
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            }
            connection = (NetworkClient) clientRMI;
        }else if(typeOfConnection.equals("Socket")){
            ClientSocket socket = new ClientSocket(this, "placeholder", 0);
            connection = (NetworkClient) socket;
            socket.run();
            //TODO vanno aggiunti address e port ,
            // il primo sarà una costante salvata nel codice l'altro sarà generato automaticamente
        }
    }

    public static RMIClientInterface getClientRMI() {
        return clientRMI;
    }

    /**
     * Sets the nickname of the player.
     * @param nickname
     */
    public static void setNickname(String nickname) {
        Controller.nickname = nickname;
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
        view.showInsertNumberOfPlayers();
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
        model.updateUsers(playersAndPins);
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
        view.showCommonTable();
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
        view.showSecretObjectiveCard(model.getSecretObjectiveCardsToChoose()[indexCard]);
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
        model.updateSecretObjectiveCardsToChoose(objectiveCardIds);
        view.showSecretObjectiveCardsToChoose(objectiveCardIds);
        Controller.setPhase(Phase.CHOOSE_SECRET_OBJECTIVE_CARD); //TODO prova a vedere se con socket è correto
    }

    public void updatePlaceCard(String nickname, int id, Point position, boolean side) {
        model.updatePlaceCard(nickname, id, position, side);
    }

    public void updateResources(String nickname, HashMap<Sign, Integer> resources) {
        model.updateResources(nickname, resources);
    }

    public void updateDrawCard(String nickname, Integer[] newHand) {
        model.updatePrivateHand(newHand);
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
        view.showHand();
        model.updatePrivateHand(hand);
    }

    public void updateHiddenHand(String nickname, Pair<Kingdom, Boolean>[] hand) {
        model.updateHiddenHand(nickname, hand);
    }
    public void showIsFirst(String firstPlayer) {
        Controller.phase = Phase.GAMEFLOW;
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
        Controller.phase = Phase.WAIT;
        connection.login(nickname);
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
        Controller.phase = Phase.WAIT;
        connection.insertNumberOfPlayers(numberOfPlayers);
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
        Controller.phase = Phase.WAIT;
        connection.chooseColor(color);
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
        connection.chooseSideStartingCard(side);
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
        connection.chooseSecretObjectiveCard(indexCard);
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
        connection.playCard(indexHand, position, side);
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
        connection.drawCard(nickname, gold, onTableOrDeck);
    }


    public void correctNumberOfPlayers(int numberOfPlayers) {
        view.correctNumberOfPlayers(numberOfPlayers);
    }

    public void updateAndShowStartingCard(int startingCardId) {
        Controller.setPhase(Phase.CHOOSE_SIDE_STARTING_CARD);
        System.out.println("Starting card id: " + startingCardId);
        view.showStartingCard(startingCardId);
    }

    public void noConnection() {
        view.noConnection();
    }

    public void wrongPhase() {
        view.wrongGamePhase();
    }

    public void noTurn() {
        view.noTurn();
    }

    public void noName() {
        view.noPlayer();
    }

    public void notEnoughResources() {
        view.notEnoughResources();
    }

    public void NoName() {
        //TODO
    }

    public void cardPosition() {
        view.cardPositionError();
    }

    public void sameName(String name) {
        view.sameName(name);
    }

    public void colorAlreadyTaken() {
        view.colorAlreadyTaken();
    }

    public void updateAndShowSecretObjectiveCard(int indexCard) {
        model.updateSecretObjectiveCard(indexCard);
        view.showSecretObjectiveCard(model.getSecretObjectiveCard());
    }

    //TODO capire se serve veramente questa cosa qui.
    public void updateSecretObjectiveCard(int indexCard) {
        model.updateSecretObjectiveCard(indexCard);
    }

    public void updateCommonObjectiveCards(Integer[] commonObjectiveCards) {
        model.updateCommonObjectiveCards(commonObjectiveCards);
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
        view.showHiddenHand();
    }
}
