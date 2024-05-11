package it.polimi.ingsw.network.messages.server.login;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;

public class StatusLogin extends ServerMessage {
    private final boolean  isFirst;

    public StatusLogin(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isFirst() {
        return isFirst;
    }

    @Override
    public void callController(Controller controller){
        if(isFirst){
            controller.askNumberOfPlayer();
        } else {
            controller.waitLobby();
        }
    }
}
