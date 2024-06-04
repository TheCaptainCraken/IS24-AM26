package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.controller.client.Controller;

public class StopGaming extends ServerMessage {
    @Override
    public void callController(Controller controller){
        controller.stopGaming();
    }
}
