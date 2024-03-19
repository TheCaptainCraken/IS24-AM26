package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private ArrayList<Player> players;

    public Lobby(){
        players = new ArrayList<>();
    }
    public void addPlayer(String nickname/*, ColorPin colorPin*/){//TODO excpetion
        if(players.size()<4){
            Player newPlayer = new Player(nickname, ColorPin.values()[players.size()], players.size()==0);
            players.add(newPlayer);
            //return newPlayer;
        }
        //return null;
    }

    public Player[] getPlayers() {
        return players.toArray( new Player[players.size()]);
    }

    public Player getPlayerFromName(String nickname){//TODO clone
        for(Player player : getPlayers()){
            if(player.getNickname().equals(nickname)){
                return player;
            }
        }
        return null;//TODO add a NoSuchFieldException
    }
}
