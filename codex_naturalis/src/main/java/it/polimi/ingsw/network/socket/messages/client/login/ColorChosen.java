package it.polimi.ingsw.network.socket.messages.client.login;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.network.socket.messages.client.ClientMessage;

public class ColorChosen extends ClientMessage {
    private final String nickname;
    private final Color color;

    public ColorChosen(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
    }

    public String getNickname() {
        return nickname;
    }

    public Color getColor() {
        return color;
    }
}
