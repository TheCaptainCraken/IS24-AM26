package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;
import it.polimi.ingsw.view.Phase;

public class FirstPlayer extends ServerMessage {
    private final String nickname;

    public FirstPlayer(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void callController(Controller controller) {
        Controller.setPhase(Phase.GAMEFLOW);
        controller.showIsFirst(nickname);
    }
}
