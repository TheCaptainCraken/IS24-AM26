package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.server.ServerMessage;

import java.util.ArrayList;

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
        controller.updateHand(nickname, cardsInHand);
    }
}
