package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private ArrayList<Player> players;
    boolean complete=false;

    /**It's an holder for the players that permits to check and limit access
     */
    public Lobby(){
        players = new ArrayList<>();
    }

    /**If possible it adds a new player to the lobby with an unique nickname
     * @param nickname nickname of the new Player
     * @param color color of the pin that the player has chosen
     */
    public void addPlayer(String nickname, Color color) throws IndexOutOfBoundsException, IllegalArgumentException{//TODO excpetion
        if(complete){
            throw new IndexOutOfBoundsException("The lobby has already 4 players");
        }
        for(Player player : players){
            if(player.getName().equals(nickname)){
                throw new IllegalArgumentException("There's already a player with this nickname");
            }
        }
        Player newPlayer = new Player(nickname, color);
        players.add(newPlayer);
        if(players.size()==4){
            setLock();
        }
    }

    /**Get a fixed array of players
     * @return an array of players
     */
    public Player[] getPlayers() {
        return players.toArray( new Player[players.size()]);
    }

    /**Givena  nickname it returns the player with that unique nickname
     * @param nickname nickname of the Player we want to get
     * @return the Player with the given nickname
     */
    public Player getPlayerFromName(String nickname) throws NoSuchFieldException {//TODO clone
        for(Player player : getPlayers()){
            if(player.getName().equals(nickname)){
                return player;
            }
        }
        throw new NoSuchFieldException("There's no Player with the given nickname");
    }

    /** It locks the lobby so nobody can join anymore, the lobby cannot be unlocked
     */
    public void setLock(){
        complete=true;
    }
}
