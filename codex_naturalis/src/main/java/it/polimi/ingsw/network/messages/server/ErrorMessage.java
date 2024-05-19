package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.ErrorType;
import it.polimi.ingsw.network.messages.server.ServerMessage;
import it.polimi.ingsw.view.Phase;

public class ErrorMessage extends ServerMessage {
    private final ErrorType type;

    public ErrorMessage(ErrorType type) {
        this.type = type;
    }

    public ErrorType getType() {
        return type;
    }

    @Override
    public void callController(Controller controller) {
        switch(type){
            case NO_TURN:
                if(Controller.getPhase() == Phase.WAIT_ALL_CHOSEN_STARTING_CARD){
                    Controller.setPhase(Phase.CHOOSE_SIDE_STARTING_CARD);
                }else if(Controller.getPhase() == Phase.WAIT_ALL_CHOSEN_SECRET_CARD){
                    Controller.setPhase(Phase.CHOOSE_SECRET_OBJECTIVE_CARD);
                }else{
                    Controller.setPhase(Phase.GAMEFLOW);
                }
                controller.noTurn();
            case FULL_LOBBY:
                //TODO
            case WRONG_PHASE:
                if(Controller.getPhase() == Phase.WAIT_ALL_CHOSEN_STARTING_CARD){
                    Controller.setPhase(Phase.CHOOSE_SIDE_STARTING_CARD);
                }else if(Controller.getPhase() == Phase.WAIT_ALL_CHOSEN_SECRET_CARD){
                    Controller.setPhase(Phase.CHOOSE_SECRET_OBJECTIVE_CARD);
                }else{
                    Controller.setPhase(Phase.GAMEFLOW);
                }
                //TODO messaggio errore
            case NAME_UNKNOWN:
                //TODO no succede mai
            case CARD_POSITION:
               //TODO
            case LOBBY_IS_CLOSED:
                //TODO VIEW
            case COLOR_UNAVAILABLE:
                Controller.setPhase(Phase.COLOR);
                controller.colorAlreadyTaken();
            case NAME_ALREADY_USED:
                Controller.setPhase(Phase.LOGIN);
                //TODO stampare nome non disponibile, senti Pietro
            case LOBBY_ALREADY_FULL:
                //TODO VIEW
            case NOT_ENOUGH_RESOURCES:
                controller.notEnoughResources();
            case PLAYER_DOES_NOT_EXIST:
                //TODO VIEW, non accade mai mandare questo messaggio
                //TODO aggiungere due fasi di wait una per sidestartingCard, l'altra per objectiveCard
        }
    }
}
