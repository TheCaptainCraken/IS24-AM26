package it.polimi.ingsw.network.message;

public abstract class ClientMessage extends Message{
    private String player;//TODO check if it is necessary or we can check based on instance of the the connection, otherwise it is possible to fake it

    public ClientMessage(String player) {
        this.player = player;
    }
}
