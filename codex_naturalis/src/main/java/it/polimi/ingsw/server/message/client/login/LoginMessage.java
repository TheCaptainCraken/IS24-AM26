package it.polimi.ingsw.server.message.client.login;

import it.polimi.ingsw.server.message.ClientMessage;

public class LoginMessage extends ClientMessage {
    private final String nickname;

    public LoginMessage(String nickname) {
        super(nickname);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
