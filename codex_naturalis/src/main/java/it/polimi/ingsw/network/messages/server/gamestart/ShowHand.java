package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.network.messages.server.ServerMessage;

import java.util.ArrayList;

public class ShowHand extends ServerMessage {
    private final String nickname;
    private final ArrayList<Integer> cardsInHand;

    public ShowHand(String nickname, ArrayList<Integer> cardsInHand) {
        this.nickname = nickname;
        this.cardsInHand = cardsInHand;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<Integer> getCardsInHand() {
        return cardsInHand;
    }
}
