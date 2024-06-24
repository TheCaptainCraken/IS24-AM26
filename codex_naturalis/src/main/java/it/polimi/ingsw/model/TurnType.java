package it.polimi.ingsw.model;

/**
 * The TurnType enum represents the different stages of a turn in the game.
 *
 * PLAYING: Represents the main phase of the turn where players can perform
 * actions.
 * SECOND_LAST_TURN: Represents the second last turn of the game.
 * LAST_TURN: Represents the final turn of the game.
 */
public enum TurnType {
    PLAYING,
    SECOND_LAST_TURN,
    LAST_TURN
}
