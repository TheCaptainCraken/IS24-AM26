package it.polimi.ingsw.model.exception;
import java.io.IOException;

/**
 * This class represents an exception that is thrown when a user
 * tries to join a lobby that is already closed, or the input fot number of players is incorrect.
 *
 * It extends the IOException class, indicating that this exception is related to I/O operations.
 */
public class ClosingLobbyException extends IOException {
}
