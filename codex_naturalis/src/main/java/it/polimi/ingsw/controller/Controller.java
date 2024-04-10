package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;

import java.awt.*;
import java.util.HashMap;


//In the controller of the client the view will be updated basing on currentPlayer
//Example: Controller of the client gets an answer of the new update and it updates its points or the points of the player who is currently playing
//Another possible implementation is send an object with property currentPlayer and the view will be updated basing on that property, in this way client controller will not need to knwo who is the current player and update it
public class Controller {
    Lobby lobby;
    GameMaster game;
    public Controller(){
        lobby=new Lobby();
    }

    public ... addPlayer(String nickname, Color color){
        lobby.addPlayer(nickname, color);
    }

    //TODO Return objects with cards on table and to each player root card and objective cards
    public ... start(){
        lobby.setLock();
        game=new GameMaster(lobby, "f1", "f2", "f3", "f4");
        for(Player player : lobby.getPlayers()){
            game.getStartingCard(player);
            game.getObjectiveCard(player);
            //prepare object to send to that player
        }
    }

    placeRootCard(){
        game.placeRootCard();
    }

    chooseObjectiveCard(int index){
        game.chooseObjectiveCard();
        return index
    }

    public ... placeCard(int indexHand, int  x, int y){//TODO understand because we pass an object in GameMaster.placeCard()
        game.placeCard(getCurrentPlayer(), id, new Point(x, y));
    }

    public ... drawCard(boolean goldOrNot, int onTableOrDeck){
        int id=game.drawCard(getCurrentPlayer(), goldOrNot, );
        //
        if(game.getGameStatus()==GameStatus.END){
            endGame();
        }
    }

    private void endGame(){
        for(Player player : lobby.getPlayers()){
            game.endGame();
        }
    }

    public int getPlayerPoints(){
        return game.getPlayerPoints();
    }

    public HashMap<Sign, Integer> getPlayerResources(){
        return game.getPlayerResources();
    }

    private/public ... getCurrentPlayer(){
        return game.getCurrentPlayer();
    }
}
