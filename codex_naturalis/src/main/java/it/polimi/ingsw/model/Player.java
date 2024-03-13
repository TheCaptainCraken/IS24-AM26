package it.polimi.ingsw.model;

import java.util.Dictionary;

public class Player {
    private String nickname;

    private int points;
    private ColorPin pin;
    private boolean isFirst;
    private PlayedCard rootCard;
    private Dictionary<Sign, Integer>  simbolCounter;
    private ObjectiveCard hiddenObjectiveCard;
    private ResourceCard[] hand=new ResourceCard[3];

    private int stateTurn;

    public Player(String nickname, ColorPin pin, boolean isFirst) {
        this.nickname=nickname;
        this.pin = pin;
        this.isFirst = isFirst;
        for(Sign sign : Sign.values()){
            this.simbolCounter.put(sign, 0);
        }
        this.hiddenObjectiveCard = hiddenObjectiveCard;
        this.hand = hand;
    }

    public void setRootCard(PlayedCard rootCard) {//TODO che rimanga const, final?
        this.rootCard = rootCard;
    }

    public void addToSimbolCounter(Sign sign){//TODO capire questa cosa

    }

    public void addToSimbolCounter(){// 7 numeri

    }
}
