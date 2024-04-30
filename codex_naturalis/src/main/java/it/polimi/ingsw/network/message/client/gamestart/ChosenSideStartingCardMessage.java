package it.polimi.ingsw.network.message.client.gamestart;

import it.polimi.ingsw.network.message.ClientMessage;

public class ChosenSideStartingCardMessage extends ClientMessage {
    public final boolean side;

    public ChosenSideStartingCardMessage(String nickname, boolean side) {
        super(nickname);
        this.side = side;
    }
}
