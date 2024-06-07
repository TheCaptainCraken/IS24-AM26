package it.polimi.ingsw.network.socket.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

public class ShowHand extends ServerMessage {
    private final String nickname;
    private final Integer[] cardsInHand;

    public ShowHand(String nickname, Integer[] cardsInHand) {
        this.nickname = nickname;
        this.cardsInHand = cardsInHand;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer[] getCardsInHand() {
        return cardsInHand;
    }

    @Override
    public void callController(Controller controller) {
        controller.updateHand(cardsInHand);
    }
}
