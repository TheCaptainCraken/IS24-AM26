package it.polimi.ingsw.model;

/**
 * The GameState enum represents the different states that the game can be in.
 * Each state corresponds to a different phase of the game.
 *
 * CHOOSING_ROOT_CARD: The phase where players choose their root card.
 * CHOOSING_OBJECTIVE_CARD: The phase where players choose their objective card.
 * DRAWING_PHASE: The phase where players draw cards.
 * PLACING_PHASE: The phase where players place cards on the board.
 * END: The final state of the game, indicating that the game has ended.
 */
public enum GameState {
    /**
     * The CHOOSING_ROOT_CARD state.
     */
    CHOOSING_ROOT_CARD,
    /**
     * The CHOOSING_OBJECTIVE_CARD state.
     */
    CHOOSING_OBJECTIVE_CARD,
    /**
     * The DRAWING_PHASE state.
     */
    DRAWING_PHASE,
    /**
     * The PLACING_PHASE state.
     */
    PLACING_PHASE,
    /**
     * The END state.
     */
    END
}
