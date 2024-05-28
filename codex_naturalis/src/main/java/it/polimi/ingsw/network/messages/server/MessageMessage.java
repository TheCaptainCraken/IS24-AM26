package it.polimi.ingsw.network.messages.server;

public class MessageMessage extends ServerMessage {
    private final String message;
    private final String sender;

    public MessageMessage(String message, String sender) {
        super();
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
