package it.polimi.ingsw.model.exception;
import java.io.IOException;

/**
 * This class represents an exception that is thrown when a user tries to join a lobby that is already closed.
 *
 * It extends the IOException class, indicating that this exception is related to I/O operations.
 */
public class LobbyCompleteException extends IOException{
}
