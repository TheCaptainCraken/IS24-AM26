package it.polimi.ingsw.network.messages.server.endgame;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.messages.server.ServerMessage;

import java.util.ArrayList;

public class ShowRanking extends ServerMessage {
    private final ArrayList<Player> ranking;

    public ShowRanking(ArrayList<Player> ranking) {
        this.ranking = ranking;
    }

    public ArrayList<Player> getRanking() {
        return ranking;
    }
}
