package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.Message;

public abstract class ServerMessage extends Message {

    public void callController(Controller controller){};
}
