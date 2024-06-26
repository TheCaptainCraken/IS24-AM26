package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.ErrorType;
import it.polimi.ingsw.view.model.Phase;
/**
 * This class represents an error message that can be sent from the server to a client.
 * It is send when the client sends an invalid message or performs an invalid action.
 * It extends the `ServerMessage` class and overrides the `callController` method to handle the error message.
 * The `callController` method is used to pass the error message to the controller for further processing.
 *
 * @see ServerMessage
 */
public class ErrorMessage extends ServerMessage {
    /**
     * The type of the error message.
     */
    private final ErrorType type;
    /**
     * Class constructor that initializes the type of the error message.
     * @param type The type of the error message.
     */
    public ErrorMessage(ErrorType type) {
        this.type = type;
    }
    /**
     * This method is used to get the type of the error message.
     * @return The type of the error message.
     */
    public ErrorType getType() {
        return type;
    }
    /**
     * This method is used to invoke the appropriate method on the provided controller based on the type of the error message.
     * @param controller The controller(singleton) on which the appropriate method will be invoked.
     */
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
                    Controller.setPhase(Phase.GAME_FLOW);
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
                    Controller.setPhase(Phase.GAME_FLOW);
                }
                controller.wrongPhase();
                break;
            //NoNameException
            case NAME_UNKNOWN:
                System.out.println("Name not known. This error should never occur.");
                controller.noName();
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
                Controller.setPhase(Phase.LOGIN);
                controller.sameName(controller.getNickname());
                Controller.setNickname(null);
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
