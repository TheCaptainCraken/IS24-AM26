package it.polimi.ingsw.controller.message.server.endgame;

import it.polimi.ingsw.controller.ReferredServerMessage;

public class ExtraPointMessage extends ReferredServerMessage {
    private final int extraPoints;

    public ExtraPointMessage(String player, int extraPoints) {
        super(true, player);
        this.extraPoints = extraPoints;
    }

    public int getExtraPoints() {
        return extraPoints;
    }
}
