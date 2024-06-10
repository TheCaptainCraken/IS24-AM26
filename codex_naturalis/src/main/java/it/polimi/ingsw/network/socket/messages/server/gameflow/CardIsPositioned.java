package it.polimi.ingsw.network.socket.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.awt.*;

public class CardIsPositioned extends ServerMessage {
    private final String nickname;
    private final int cardId;
    private final Point position;
    private final boolean side;
    private final int turn;

    public CardIsPositioned(String nickname, int cardId, Point position, boolean side, int turn) {
        this.nickname = nickname;
        this.cardId = cardId;
        this.position = position;
        this.side = side;
        this.turn = turn;
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
        //resources and points of players are send in a different messages. This messages contains only the information of a placed card.
        controller.updatePlaceCard(nickname, cardId, position, side, turn);
    }
}
