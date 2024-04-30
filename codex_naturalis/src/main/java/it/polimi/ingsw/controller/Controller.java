package it.polimi.ingsw.controller;


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
        lobby = new Lobby();
        //TODO
    }

    public void initializeLobby(int nPlayers) {
        lobby = new Lobby(nPlayers);
    }

    public void addPlayer(String nickname) throws SameNameException, LobbyCompleteException {
        lobby.addPlayer(nickname);
    }

//    public void chooseFirstPlayer(int i) {
//        lobby.chooseFirstPlayer(i);
//    }

    public void placeRootCard(String player, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        game.placeRootCard(player, side);
    }

    public void chooseObjectiveCard(String player, int whichCard) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        game.chooseObjectiveCard(player, whichCard);
    }

    public void placeCard(String player, ResourceCard cardToPlace, Point position, boolean side) throws WrongGamePhaseException,
            NoTurnException, NoSuchFieldException, NotEnoughResourcesException {
        game.placeCard(player, cardToPlace, position, side);
    }

    public int drawCard(String player, boolean gold, int position) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        return game.drawCard(player, gold, position);
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

    public int getStartingCard(Player player) {
        return player.getRootCard().getCard().getId();
    }

    public int getSecretObjective(Player player) {
        return player.getSecretObjective().getId();
    }

    public Player getPlayer(String name) throws NoSuchFieldException {
        return lobby.getPlayerFromName(name);
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
    }
}

