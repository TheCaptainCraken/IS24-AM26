package it.polimi.ingsw.network.socket.messages.client.login;

import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

public class NumberOfPlayersMessage extends ClientMessage {
    private final int number;

    public NumberOfPlayersMessage(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
