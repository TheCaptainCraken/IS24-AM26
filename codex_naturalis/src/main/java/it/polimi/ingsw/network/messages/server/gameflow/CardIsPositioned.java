package it.polimi.ingsw.network.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;

import java.awt.*;

public class CardIsPositioned extends ServerMessage {
    private final String nickname;
    private final int cardId;
    private final Point position;
    private final boolean side;

    public CardIsPositioned(String nickname, int cardId, Point position, boolean side) {
        this.nickname = nickname;
        this.cardId = cardId;
        this.position = position;
        this.side = side;
    }

    public String getNickname() {
        return nickname;
    }

    public int getCardId() {
        return cardId;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isSide() {
        return side;
    }

    @Override
    public void callController(Controller controller) {
        controller.updatePlaceCard(nickname,cardId,position,side);
    }
}
