package it.polimi.ingsw.network.socket.messages;

/**
 * This enumeration represents the types of errors that can occur in the application.
 *
 * Each constant corresponds to a specific type of exception that can be thrown.
 */
public enum ErrorType {
    /**
     * Represents an error related to card positioning.
     * Corresponds to the CardPositionException.
     */
    CARD_POSITION, // CardPositionException

    /**
     * Represents an error when trying to join a closed lobby.
     * Corresponds to the ClosingLobbyException.
     */
    LOBBY_IS_CLOSED, //ClosingLobbyException

    /**
     * Represents an error when a chosen color is already taken.
     * Corresponds to the ColorAlreadyTakenException.
     */
    COLOR_UNAVAILABLE, // ColorAlreadyTakenException

    /**
     * Represents an error when trying to join a full lobby.
     * Corresponds to the LobbyCompleteException.
     */
    LOBBY_ALREADY_FULL, // LobbyCompleteException

    /**
     * Represents an error when a name is unknown.
     * Corresponds to the NoNameException.
     */
    NAME_UNKNOWN, // NoNameException

    /**
     * Represents an error when there are not enough resources.
     * Corresponds to the NotEnoughResourcesException.
     */
    NOT_ENOUGH_RESOURCES, // NotEnoughResourcesException

    /**
     * Represents an error when a player tries to play out of turn.
     * Corresponds to the NoTurnException.
     */
    NO_TURN, //NoTurnException

    /**
     * Represents an error when a name is already used.
     * Corresponds to the SameNameException.
     */
    NAME_ALREADY_USED, // SameNameException

    /**
     * Represents an error when an action is performed in the wrong phase.
     * Corresponds to the WrongPhaseException.
     */
    WRONG_PHASE, // WrongPhaseException

    /**
     * Represents an error when the server doesn't recognize the exception type.
     */
    INVALID_MESSAGE //Server doesn't know the exception type
}
