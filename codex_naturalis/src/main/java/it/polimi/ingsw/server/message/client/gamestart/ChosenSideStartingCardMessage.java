package it.polimi.ingsw.server.message.client.gamestart;

import it.polimi.ingsw.server.message.ClientMessage;

public class ChosenSideStartingCardMessage extends ClientMessage {
    public final boolean side;

    public ChosenSideStartingCardMessage(String nickname, boolean side) {
        super(nickname);
        this.side = side;
    }
}
