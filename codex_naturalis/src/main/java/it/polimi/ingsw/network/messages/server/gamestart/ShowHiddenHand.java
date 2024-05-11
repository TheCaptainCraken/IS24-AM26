package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.network.messages.server.ServerMessage;

import java.util.ArrayList;

public class ShowHiddenHand extends ServerMessage {
    private final String nickname;
    private final ArrayList<Kingdom> hand;

    public ShowHiddenHand(String nickname, ArrayList<Kingdom> hand) {
        this.nickname = nickname;
        this.hand = hand;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<Kingdom> getHand() {
        return hand;
    }

    @Override
    public void callController(Controller controller) {
        Kingdom[] array = hand.toArray(new Kingdom[0]);
        controller.updateHiddenHand(nickname,array);
    }
}
