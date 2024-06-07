package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;

import java.util.HashMap;

public class ReceivedChatMessage extends ServerMessage {
    private final String sender;
    private final String message;

    public ReceivedChatMessage(String sender, String message) {
        super();
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    /**
     * @param controller
     */
    @Override
    public void callController(Controller controller) {
        controller.receiveMessage(sender, message);
    }
}
