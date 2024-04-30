package it.polimi.ingsw.network.message.client.gamestart;

import it.polimi.ingsw.network.message.ClientMessage;

public class ChosenObjectiveCardMessage extends ClientMessage {
    public final int index;

    public ChosenObjectiveCardMessage(String nickname, int index) {
        super(nickname);
        this.index = index;
    }
}
