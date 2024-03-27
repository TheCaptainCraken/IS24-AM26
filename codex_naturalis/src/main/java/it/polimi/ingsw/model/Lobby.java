package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private ArrayList<Player> players;
    boolean complete=true;

    public Lobby(){
        players = new ArrayList<>();
    }
    public void addPlayer(String nickname, Color color){//TODO excpetion
        if(complete){
            throw new IndexOutOfBoundsException("The lobby has already 4 players");
        }
        Player newPlayer = new Player(nickname, color);
        players.add(newPlayer);
        if(players.size()==4){
            setLock();
        }
    }

    public Player[] getPlayers() {
        return players.toArray( new Player[players.size()]);
    }

    public Player getPlayerFromName(String nickname){//TODO clone
        for(Player player : getPlayers()){
            if(player.getName().equals(nickname)){
                return player;
            }
        }
        return null;//TODO add a NoSuchFieldException
    }

    public void setLock(){
        complete=false;
    }
}
