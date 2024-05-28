package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exception.ClosingLobbyException;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.model.exception.NoNameException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Lobby {
    private final ArrayList<Player> players;
    private int maxSize;
    boolean complete;

    /**
     * It's a holder for the players that permits to check and limit access
     */
    public Lobby() {
        this.players = new ArrayList<>();
        this.complete = false;
    }

    /**
     * If possible it adds a new player to the lobby with a unique nickname
     * 
     * @param nickname nickname of the new Player
     */
    public void addPlayer(String nickname) throws LobbyCompleteException, SameNameException {
        if (complete) {
            throw new LobbyCompleteException();
        }
        for (Player player : players) {
            if (player.getName().equals(nickname)) {
                throw new SameNameException();
            }
        }
        Player newPlayer = new Player(nickname);
        players.add(newPlayer);

        // max size is always more than zero when we check here.
        // The first player can choose the size of the lobby(2, 3, 4), so we set lock
        // always when the lobby is full
        // and the first player has chosen the number of players.
        if (players.size() == maxSize) {
            setLock();
        }
    }

    /**
     * Get a fixed array of players
     * 
     * @return an array of players
     */
    public Player[] getPlayers() {
        return players.toArray(new Player[players.size()]);
    }

    /**
     * Given a nickname it returns the player with that unique nickname
     * 
     * @param nickname nickname of the Player we want to get
     * @return the Player with the given nickname
     */
    public Player getPlayerFromName(String nickname) throws NoNameException {
        for (Player player : getPlayers()) {
            if (player.getName().equals(nickname)) {
                return player;
            }
        }
        throw new NoNameException();
    }

    public void setMaxSize(int maxSize) throws ClosingLobbyException {
        // TODO questa eccezione è inutile, input gestito da client. complete okay ma
        // già bloccante in AddPlayer.
        if (complete || maxSize > 4 || maxSize < 2) {
            throw new ClosingLobbyException();
        }
        this.maxSize = maxSize;

        if (maxSize < players.size()) {
            for (int i = players.size() - 1; i >= maxSize; i--) {
                players.remove(i);
            }
            // after that the lobby is locked, no one can join anymore.
            setLock();
        } else if (maxSize == players.size()) {
            setLock();
        }
    }

    /**
     * It locks the lobby so nobody can join anymore, the lobby cannot be unlocked
     */
    public void setLock() {
        complete = true;
    }

    public boolean getLock() {
        return complete;
    }

    public HashMap<String, Color> getPlayersAndPins() {
        HashMap<String, Color> PlayerAndPin = new HashMap<>();

        for (Player player : players) {
            PlayerAndPin.put(player.getName(), player.getColor());
        }

        return PlayerAndPin;
    }

    public boolean isAdmitted(String nickname) {
        for (Player player : players) {
            if (player.getName().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    public boolean isReady() {
        // if ready to start, I shuffle the players.
        if (maxSize == players.size()) {
            Collections.shuffle(players);
        }
        return maxSize == players.size();
    }

    /**
     * Two lobbies are equal if they have the same number of players with the same
     * name.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Lobby) {
            Lobby lobby = (Lobby) obj;

            if (lobby.players.size() != players.size()) {
                return false;
            }

            for (int i = 0; i < players.size(); i++) {
                if (!players.contains(lobby.players.get(i))) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }
}