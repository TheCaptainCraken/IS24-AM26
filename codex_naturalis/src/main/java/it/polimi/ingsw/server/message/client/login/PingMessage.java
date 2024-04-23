package it.polimi.ingsw.server.message.client.login;

import it.polimi.ingsw.controller.message.ClientMessage;

public class PingMessage extends ClientMessage {
    public PingMessage(String player) {
        super(player);
    }
}
