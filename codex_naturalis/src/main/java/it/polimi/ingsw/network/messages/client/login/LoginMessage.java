package it.polimi.ingsw.network.messages.client.login;

import it.polimi.ingsw.network.messages.client.ClientMessage;

public class LoginMessage extends ClientMessage {
    private final String nickname;

    public LoginMessage(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
