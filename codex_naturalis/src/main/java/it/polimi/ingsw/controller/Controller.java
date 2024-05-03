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

    public void initializeLobby(int numPlayers) throws ClosingLobbyException {
        lobby = new Lobby();
        lobby.setMaxSize(numPlayers);
    }

    public void addPlayer(String nickname) throws SameNameException, LobbyCompleteException {
        lobby.addPlayer(nickname);
    }

    public void placeRootCard(String player, boolean side) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        game.placeRootCard(player, side);
    }

    public void chooseObjectiveCard(String player, int whichCard) throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        game.chooseObjectiveCard(player, whichCard);
    }

    public void placeCard(String player, int cardId, Point position, boolean side) throws WrongGamePhaseException,
            NoTurnException, NoSuchFieldException, NotEnoughResourcesException {
        game.placeCard(player, cardId, position, side);
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

    public int getRootCard(String name) throws NoSuchFieldException {
        Player player = lobby.getPlayerFromName(name);
        return player.getRootCard().getCard().getId();
    }

    public int getSecretObjective(Player player) {
        return player.getSecretObjective().getId();
    }

    public Player getPlayer(String name) throws NoSuchFieldException {
        return lobby.getPlayerFromName(name);
    }

    public int getNumberOfPlayers(){
        return lobby.getSize();
    }

    public boolean lobbyLocked(){
        return lobby.getLock();
    }

    public static Controller getInstance(){
        return INSTANCE;
    }

    public boolean setColour(String name, Color colour) throws NoSuchFieldException, ColorAlreadyTakenException {
        //TODO controll input
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
        return true;
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
        HashMap<String, Color> PlayerAndPin= new HashMap<>();
        for(Player player : lobby.getPlayers()) {
            PlayerAndPin.put(player.getName(), player.getColor());
        }
        return PlayerAndPin;
    }

    public boolean getIsFirst(String nickname) {
        return lobby.getPlayers()[0].getName().equals(nickname);
    }

    public boolean endStartingPhase() {
        int i;
        for (i = 0; i < lobby.getSize(); i++) {
            if (lobby.getPlayers()[i].getRootCard() == null) {
                return false;
            }
        }
        return true;
    }
}

