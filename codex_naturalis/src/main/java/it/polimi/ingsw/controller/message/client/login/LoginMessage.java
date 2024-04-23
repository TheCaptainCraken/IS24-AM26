package it.polimi.ingsw.controller.message.client.login;

public class LoginMessage {
    private String nickname;

    public LoginMessage(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
