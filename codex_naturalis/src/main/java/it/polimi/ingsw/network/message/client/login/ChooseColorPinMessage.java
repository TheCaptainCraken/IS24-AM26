package it.polimi.ingsw.network.message.client.login;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.network.message.ClientMessage;

public class ChooseColorPinMessage extends ClientMessage {
    public final Color color;

    public ChooseColorPinMessage(String nickname, Color color) {
        super(nickname);
        this.color = color;
    }
}
