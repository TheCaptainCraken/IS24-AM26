package it.polimi.ingsw.controller.server;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;

import java.awt.*;
import java.util.HashMap;

public class Controller {
    private static final Controller INSTANCE = new Controller();

    Lobby lobby;
    GameMaster game = null;

    private Controller() {
        //TODO
    }

    public void initializeLobby(int nPlayers) {
        lobby = new Lobby();
    }

    public void addPlayer(String nickname) throws SameNameException, LobbyCompleteException {
        lobby.addPlayer(nickname);
    }

//    public void chooseFirstPlayer(int i) {
//        lobby.chooseFirstPlayer(i);
//    }

    public int placeRootCard(String player, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        return game.placeRootCard(player, side);
    }

    public void chooseObjectiveCard(String player, int whichCard) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        game.chooseObjectiveCard(player, whichCard);
    }

    public int placeCard(String player, int indexHand, Point position, boolean side) throws WrongGamePhaseException,
            NoTurnException, NoSuchFieldException, NotEnoughResourcesException {
        int cardId = game.placeCard(player, indexHand, position, side);
        return cardId;
    }

    public int drawCard(String player, boolean gold, int onTableOrDeck) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        return game.drawCard(player, gold, onTableOrDeck);
    }

    public int getPlayerPoints(String player) throws NoSuchFieldException {
        return lobby.getPlayerFromName(player).getPoints();
    }

    public HashMap<Sign, Integer> getPlayerResources(String player) throws NoSuchFieldException {
        return lobby.getPlayerFromName(player).getResources();
    }

    public String getCurrentPlayer() {
        return game.getCurrentPlayer().getName();
    }

    public int getStartingCard(String nickname) throws NoSuchFieldException {//TODO from string
        return lobby.getPlayerFromName(nickname).getRootCard().getCard().getId();
    }

    public int getSecretObjective(String nickname) throws NoSuchFieldException {
        return lobby.getPlayerFromName(nickname).getSecretObjective().getId();
    }

    public Player getPlayer(String nickname) throws NoSuchFieldException {
        return lobby.getPlayerFromName(nickname);
    }

//    public ... getTable() {
//        return //TODO
//    }

    public int getNumberOfPlayers(){
        return lobby.getSize();
    }

    public boolean lobbyLocked(){
        return lobby.getLock();
    }

    public static Controller getInstance(){
        return INSTANCE;
    }

    public boolean setColour(String name, Color colour) throws NoSuchFieldException {
        //TODO color exception
        lobby.getPlayerFromName(name).setColour(colour);
        //TODO check if all player have choose a color -> start() return true else return false
        return false;
    }

    public Object getPlayersInLobby() {
        //TODO
        return null;
    }

    public boolean start() {
        //TODO new GameMaster
        return true;
    }

    public HashMap<String, Color> getPlayersAndPins() {
        //TODO
        return null;
    }

    public boolean getIsFirst() {
        //throw la lobby è già chiusa o è piena
        //TODO
        return false;
    }

    public int newCardOnTable(boolean gold, int onTableOrDeck) {
        //TODO
        //if it's -1 return -1
        return -1;
    }

    public boolean areAllRootCardPlaced() {
        //TODO
        return false;
    }

    public Integer[] getObjectiveCards() {
        //TODO
        return null;
    }

    public Integer[] getSecretObjectiveCards(String nicknameRefresh) {
        //TODO
        return null;
    }

    public boolean areAllSecretObjectiveCardChosen() {
        //TODO
        return false;
    }

    public Integer[] getHand(String nickname) {
        //TODO
        return null;
    }

    public Kingdom[] getHiddenHand(String nickname) {
        return null;
    }

    public GameState getGameState() {
        //TODO
        return null;
    }

    public HashMap<String, Integer> getExtraPoints() {
        //TODO
        return null;
    }

    public HashMap<String, Integer> getRanking() {
        //TODO
        return null;
    }

    public Kingdom getHeadDeck(boolean gold) {
        //TODO
        return null;
    }

    public boolean isEndGame() {
        //TODO Check conditions where this happens
        //also after placing card? data diagram says so but code doesn't work in this way
        return false;
    }
}

