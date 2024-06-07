package it.polimi.ingsw.network.socket.messages.client;

public class SendMessage extends ClientMessage {
    private final String message;
    private final String sender;

    public SendMessage(String message, String sender) {
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
