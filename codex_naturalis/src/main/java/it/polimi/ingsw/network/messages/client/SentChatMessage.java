package it.polimi.ingsw.network.messages.client;

public class SentChatMessage extends ClientMessage {
    private final String sender;
    private final String message;

    public SentChatMessage(String sender, String message) {
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
}
