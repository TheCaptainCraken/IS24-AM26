package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;
import it.polimi.ingsw.view.Phase;

public class StopWaitingOrDisconnect extends ServerMessage {
    boolean StopWaitingOrDisconnect;

    public StopWaitingOrDisconnect(boolean StopWaitingOrDisconnect) {
         this.StopWaitingOrDisconnect = StopWaitingOrDisconnect;
    }

    public void callController(Controller controller) {
        if(StopWaitingOrDisconnect) {
            Controller.setPhase(Phase.COLOR);
            controller.stopWaiting();
        } else {
            controller.disconnect();
        }
    }

}
