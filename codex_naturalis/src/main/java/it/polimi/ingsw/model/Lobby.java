package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exception.ClosingLobbyException;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.SameNameException;
import it.polimi.ingsw.model.exception.NoNameException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The Lobby class represents a lobby where players can join before the game
 * starts.
 * It manages the players, their nicknames, and the maximum size of the lobby.
 * It also handles the locking of the lobby, which prevents more players from
 * joining.
 * The Lobby interacts with the Player class to manage the players.
 * The Lobby class throws various exceptions to handle invalid actions such as
 * adding a player to a full lobby, adding a player with a duplicate nickname,
 * and setting an invalid lobby size.
 */
public class Lobby implements Serializable {
    /**
     * The list of players in the lobby.
     */
    private final ArrayList<Player> players;
    /**
     * The maximum size of the lobby.
     */
    private int maxSize;
    /**
     * The lock status of the lobby.
     */
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
     * @throws LobbyCompleteException if the lobby is full
     * @throws SameNameException      if the nickname is already taken
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
     * @throws NoNameException if the nickname is not found
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

    /**
     * It sets the maximum size of the lobby scraping the players that exceed the
     * limit
     * @throws ClosingLobbyException if the lobby is locked or the size is invalid
     * @param maxSize the maximum size of the lobby
     */
    public void setMaxSize(int maxSize) throws ClosingLobbyException {
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

    /**
     * It returns the lock status of the lobby
     *
     * @return true if the lobby is locked
     */
    public boolean getLock() {
        return complete;
    }

    /**
     * It returns nicknames of the players and relative color pins
     *
     * @return a map with the nicknames of the players and their color pins
     */
    public HashMap<String, Color> getPlayersAndPins() {
        HashMap<String, Color> PlayerAndPin = new HashMap<>();

        for (Player player : players) {
            PlayerAndPin.put(player.getName(), player.getColor());
        }

        return PlayerAndPin;
    }

    /**
     * It checks if a player is already in the lobby
     *
     * @param nickname nickname of the player
     * @return true if the player is in the lobby
     */
    public boolean isAdmitted(String nickname) {
        for (Player player : players) {
            if (player.getName().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * It checks if the lobby is ready to start
     *
     * @return true if the lobby is ready to start
     */
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

            ArrayList<String> playerNames = new ArrayList<>();
            for (Player player : players) {
                playerNames.add(player.getName());
            }

            for (int i = 0; i < players.size(); i++) {
                if (!playerNames.contains(lobby.players.get(i).getName()))
                {
                    return false;
                }
            }

            return true;
        }
        return false;
    }
}