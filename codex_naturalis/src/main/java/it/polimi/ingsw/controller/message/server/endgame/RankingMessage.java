package it.polimi.ingsw.controller.message.server.endgame;

import it.polimi.ingsw.controller.ReferredServerMessage;

import java.util.HashMap;

public class RankingMessage extends ReferredServerMessage {
    private final HashMap<String, Integer> ranking;
    
    public RankingMessage(String player, HashMap<String, Integer> ranking) {
        super(true, player);
        this.ranking = ranking;
    }

    public HashMap<String, Integer> getRanking() {
        return ranking;
    }
}
