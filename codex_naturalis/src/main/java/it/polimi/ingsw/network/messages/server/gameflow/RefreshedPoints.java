package it.polimi.ingsw.network.messages.server.gameflow;

import it.polimi.ingsw.network.messages.server.ServerMessage;

public class RefreshedPoints extends ServerMessage {
    private final String nickname;

    private final int points;

    public RefreshedPoints(String nickname, int points) {
        this.nickname = nickname;
        this.points = points;
    }

    public String getNickname() {
        return nickname;
    }

    public int getPoints() {
        return points;
    }
}
