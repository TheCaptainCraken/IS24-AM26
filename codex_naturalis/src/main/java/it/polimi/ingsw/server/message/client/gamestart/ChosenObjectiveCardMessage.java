package it.polimi.ingsw.server.message.client.gamestart;

import it.polimi.ingsw.server.message.ClientMessage;

public class ChosenObjectiveCardMessage extends ClientMessage {
    public final int index;

    public ChosenObjectiveCardMessage(String nickname, int index) {
        super(nickname);
        this.index = index;
    }
}
