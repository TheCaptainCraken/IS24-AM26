package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.messages.ErrorType;
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
            //NoTurnException
            case NO_TURN:
                if(Controller.getPhase() == Phase.WAIT_ALL_CHOSEN_STARTING_CARD){
                    Controller.setPhase(Phase.CHOOSE_SIDE_STARTING_CARD);
                }else if(Controller.getPhase() == Phase.WAIT_ALL_CHOSEN_SECRET_CARD){
                    Controller.setPhase(Phase.CHOOSE_SECRET_OBJECTIVE_CARD);
                }else{
                    Controller.setPhase(Phase.GAMEFLOW);
                }
                controller.noTurn();
                break;
            //WrongPhaseException
            case WRONG_PHASE:
                if(Controller.getPhase() == Phase.WAIT_ALL_CHOSEN_STARTING_CARD){
                    Controller.setPhase(Phase.CHOOSE_SIDE_STARTING_CARD);
                }else if(Controller.getPhase() == Phase.WAIT_ALL_CHOSEN_SECRET_CARD){
                    Controller.setPhase(Phase.CHOOSE_SECRET_OBJECTIVE_CARD);
                }else{
                    Controller.setPhase(Phase.GAMEFLOW);
                }
                controller.wrongPhase();
                break;
            //NoNameException
            case NAME_UNKNOWN:
                System.out.println("Name not known. This error should never occur.");
                break;
            //CardPositionException
            case CARD_POSITION:
                controller.cardPositionError();
                break;
            //ColorAlreadyTakenException
            case COLOR_UNAVAILABLE:
                Controller.setPhase(Phase.COLOR);
                controller.colorAlreadyTaken();
                break;
            //SameNameException
            case NAME_ALREADY_USED:
                Controller.setNickname(null);
                Controller.setPhase(Phase.LOGIN);
                break;
            //LobbyCompleteException
            case LOBBY_ALREADY_FULL:
                controller.lobbyComplete();
                break;
            //ClosingLobbyException
            case LOBBY_IS_CLOSED:
                controller.closingLobbyError();
                break;
            //NotEnoughResourcesException
            case NOT_ENOUGH_RESOURCES:
                controller.notEnoughResources();
                break;
        }
    }
}
