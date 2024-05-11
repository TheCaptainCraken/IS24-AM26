package it.polimi.ingsw.network.messages;

public enum ErrorType {
    CARD_POSITION,
    LOBBY_IS_CLOSED,
    COLOR_UNAVAILABLE,
    FULL_LOBBY, // FullLobbyException
    LOBBY_ALREADY_FULL, // LobbyCompleteException
    NAME_UNKNOWN,
    NOT_ENOUGH_RESOURCES,
    PLAYER_DOES_NOT_EXIST,
    NO_TURN,
    NAME_ALREADY_USED,
    WRONG_PHASE,
    INVALID_MESSAGE
}
