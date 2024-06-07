package it.polimi.ingsw.network.socket.messages.server.login;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;
import it.polimi.ingsw.view.Phase;

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
            Controller.setPhase(Phase.NUMBER_OF_PLAYERS);
            controller.askNumberOfPlayer();
        } else {
            Controller.setPhase(Phase.WAIT);
        }
    }
}
