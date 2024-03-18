package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private List<Player> players;

    public Lobby(){
        players = new ArrayList<Player>();
    }
    public Player addPlayer(String nickname/*, ColorPin colorPin*/){
        if(players.size()<4){
            Player newPlayer = new Player(nickname, ColorPin.values()[players.size()], players.size()==0);
            players.add(newPlayer);
            return newPlayer;
        }
        return null;
    }

    public Player[] getPlayers() {
        return players.toArray( new Player[players.size()]);
    }
}
