package it.polimi.ingsw.network.messages.server.login;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;

public class LobbyIsReady extends ServerMessage {
    private final boolean isReady;

    public LobbyIsReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isReady() {
        return isReady;
    }

    @Override
    public void callController(Controller controller) {
        if(isReady) {
            //TODO è sempre true quando mandi questo messaggio in realtà
            controller.stopWaiting();
        } else{
            controller.waitLobby();
        }
    }
}
