package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exception.ClosingLobbyException;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.model.exception.NoNameException;

import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Lobby {
    private final ArrayList<Player> players;
    private int index;//to letchooseFirstPlayer being reusable
    private int maxSize;
    boolean complete;

    /**It's a holder for the players that permits to check and limit access
     */
    public Lobby(){
        this.players = new ArrayList<>();
        this.complete = false;
    }

    /**If possible it adds a new player to the lobby with a unique nickname
     * @param nickname nickname of the new Player
     */
    public void addPlayer(String nickname) throws LobbyCompleteException, SameNameException {
        if(complete){
            throw new LobbyCompleteException();
        }
        for(Player player : players){
            if(player.getName().equals(nickname)){
                throw new SameNameException();
            }
        }
        Player newPlayer = new Player(nickname);
        players.add(newPlayer);
        if(players.size() == maxSize){
            setLock();
        }

        //TODO bisognerebbe settare casualmente il giocatore iniziale.
        // Non deve essere sempre il primo aggiunto, basta anche uno shuffle.
    }

    /**Get a fixed array of players
     * @return an array of players
     */
    public Player[] getPlayers() {
        return players.toArray(new Player[players.size()]);
    }

    public int getSize(){
        return players.size();
    }

    /**Given a  nickname it returns the player with that unique nickname
     * @param nickname nickname of the Player we want to get
     * @return the Player with the given nickname
     */
    public Player getPlayerFromName(String nickname) throws NoNameException {
        for(Player player : getPlayers()){
            if(player.getName().equals(nickname)){
                return player;
            }
        }
        throw new NoNameException();
    }

    /**Set the first player
     *
     * @param i the index starting from 0 of the player that will be the first one to play
     */
    public void chooseFirstPlayer(int i){
        Collections.rotate(players, index);
        Collections.rotate(players, -i);
        index = i;
    }

    public void setMaxSize(int maxSize) throws ClosingLobbyException {
        if(complete || maxSize > 4 || maxSize < 2){
            throw new ClosingLobbyException();
        }
        this.maxSize = maxSize;
    }

    /** It locks the lobby so nobody can join anymore, the lobby cannot be unlocked
     */
    public void setLock(){
        complete = true;
    }

    public boolean getLock() {
        return complete;
    }

    public HashMap<String, Color> getPlayersAndPins() {
        HashMap<String, Color> PlayerAndPin = new HashMap<>();

        for(Player player : players) {
            PlayerAndPin.put(player.getName(), player.getColor());
        }

        return PlayerAndPin;
    }
}