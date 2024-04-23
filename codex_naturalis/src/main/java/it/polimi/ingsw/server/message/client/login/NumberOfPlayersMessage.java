package it.polimi.ingsw.server.message.client.login;

import it.polimi.ingsw.server.message.Message;

public class NumberOfPlayersMessage extends Message {//This is not a ClientMessage, watch out!
    private final int numberOfPlayers;

    public NumberOfPlayersMessage(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
