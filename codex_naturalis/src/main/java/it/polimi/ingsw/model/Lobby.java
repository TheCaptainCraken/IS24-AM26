package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private List<Player> community;

    public Lobby(){
        community = new ArrayList<Player>();
    }
    public Player addPlayer(String nickname, ColorPin colorPin){//TODO check not more then 4
        community.add(new Player(nickname, community.size()==0, community.size()+1));
    }

    public List<Player> getCommunity() {
        return community;
    }
}
