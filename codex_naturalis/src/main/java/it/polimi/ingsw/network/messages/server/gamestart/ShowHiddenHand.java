package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.network.messages.server.ServerMessage;
import javafx.util.Pair;


public class ShowHiddenHand extends ServerMessage {
    private final String nickname;
    private final Pair<Kingdom,Boolean>[] hand;

    public ShowHiddenHand(String nickname, Pair<Kingdom,Boolean>[] hand) {
        this.nickname = nickname;
        this.hand = hand;
    }

    public String getNickname() {
        return nickname;
    }

    public Pair<Kingdom,Boolean>[] getHand() {
        return hand;
    }

    @Override
    public void callController(Controller controller) {
        controller.updateHiddenHand(nickname, hand);
    }
}
