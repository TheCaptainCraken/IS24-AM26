package it.polimi.ingsw.model.exception;
import java.io.IOException;

/**
 * This exception is thrown when the player tries to perform an action that is not allowed in the current game phase.
 *
 * It extends the IOException class.
 */
public class WrongGamePhaseException extends IOException{
}
