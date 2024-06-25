package it.polimi.ingsw.view.model;

/**
 * The Phase enum represents the different phases that a player can have in the game.
 */
public enum Phase {
    /**
     * The LOGIN phase is used to authenticate a player in the game. It is the first state of the game.
     * We return in LOGIN Phase if these errors occur:
     * - RMI: SameNameException
     * - Socket: NAME_ALREADY_USED
     */
    LOGIN,
    /**
     * The CHOOSE_COLOR phase is used to set the color of the player in the game.
     * It is the second state of the game. It is set when:
     * - RMI: after stopWaitingMethod
     * - Socket: after NAME_ACCEPTED
     */
    COLOR,
    /**
     * The CHOOSE_SIDE_STARTING_CARD phase is used to set the side and the starting card of the player in the game.
     * It is the third state of the game. It is set when:
     * - RMI: after ShowStaringCard
     * - Socket: after ShowStartingCard message
     */
    CHOOSE_SIDE_STARTING_CARD,
    /**
     * The CHOOSE_SECRET_OBJECTIVE_CARD phase is used to set the secret objective card of the player in the game.
     * It is the fourth state of the game. It is set when:
     * - RMI: after sendSecretObjectiveCardsToChoose
     * - Socket: after GiveSecretObjectiveCards
     */
    CHOOSE_SECRET_OBJECTIVE_CARD,
    /**
     * The GAME FLOW phase is used to set the action of the player in the game.
     * It is the fifth state of the game. It is set when:
     * - RMI: after getIsFirstAndStartGame
     * - Socket: after FirstPlayer
     */
    GAME_FLOW,
    /**
     * The NUMBER OF PLAYERS phase is used to set the number of players in the game.
     * It is the second state of the game(just for the first player). It is set when:
     * - RMI: after login method with iSFirst = true
     * - Socket: after StatusLogin with isFirst = true
     */
    NUMBER_OF_PLAYERS,

    //All these phases are set by the controller client.
    /**
     * The WAIT phase is used to wait for the other players to choose their color, and in all phases for RMI
     */
    WAIT,
    /**
     * The WAIT_NUMBER_OF_PLAYERS phase is used to wait for the first players to choose
     * the number of players in the game or that enough players are connected.
     */
    WAIT_NUMBER_OF_PLAYERS,
    /**
     * The WAIT_ALL_CHOSEN_COLOR phase is used to wait for all players to choose their color.
     */
    WAIT_ALL_CHOSEN_COLOR,
    /**
     * The WAIT_ALL_CHOSEN_STARTING_CARD phase is used to wait for all players to choose their starting card.
     */
    WAIT_ALL_CHOSEN_STARTING_CARD,
    /**
     * The WAIT_ALL_CHOSEN_SECRET_CARD phase is used to wait for all players to choose their secret objective card.
     */
    WAIT_ALL_CHOSEN_SECRET_CARD;
}
