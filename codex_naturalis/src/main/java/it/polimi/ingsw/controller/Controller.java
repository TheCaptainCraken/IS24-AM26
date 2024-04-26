package it.polimi.ingsw.controller;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.NoTurnException;
import it.polimi.ingsw.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.model.exception.NotExistsPlayerException;
import it.polimi.ingsw.model.exception.WrongGamePhaseException;

import java.awt.*;
import java.util.HashMap;

public class Controller {
    private static final Controller INSTANCE = new Controller();

    Lobby lobby;
    GameMaster game = null;

    private Controller() {
    }

    public void initializeLobby(int nPlayers){
        lobby = new Lobby(nPlayers);
    }

    public void addPlayer(String nickname, Color color) {
        lobby.addPlayer(nickname, color);
    }


//    public void chooseFirstPlayer(int i) {
//        lobby.chooseFirstPlayer(i);
//    }

    public void start() {
        lobby.setLock();
        game = new GameMaster(lobby, "f1", "f2", "f3", "f4");
    }

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

//    public ... getTable() {
//        return //TODO
//    }

    public static Controller getInstance(){
        return INSTANCE;
    }
}

