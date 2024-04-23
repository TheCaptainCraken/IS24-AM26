package it.polimi.ingsw.controller.message.server;

import it.polimi.ingsw.controller.ServerMessage;

public class DrawnCardMessage extends ServerMessage {
    private final int idCard;

    public DrawnCardMessage(int idCard) {
        super(true);
        this.idCard = idCard;
    }

    public int getIdCard() {
        return idCard;
    }
}
