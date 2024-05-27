package it.polimi.ingsw.network.messages;

public enum ErrorType {
    CARD_POSITION, // CardPositionException
    LOBBY_IS_CLOSED, //ClosingLobbyException
    COLOR_UNAVAILABLE, // ColorAlreadyTakenException
    LOBBY_ALREADY_FULL, // LobbyCompleteException
    NAME_UNKNOWN, // NoNameException
    NOT_ENOUGH_RESOURCES, // NotEnoughResourcesException
    NO_TURN, //NoTurnException
    NAME_ALREADY_USED, // SameNameException
    WRONG_PHASE, // WrongPhaseException
    INVALID_MESSAGE //Server doesn't know the exception type
}
