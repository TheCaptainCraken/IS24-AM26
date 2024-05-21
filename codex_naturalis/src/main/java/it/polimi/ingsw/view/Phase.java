package it.polimi.ingsw.view;

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
     * - Socket: after NAME_ACCEPTED //TODO
     */
    COLOR,
    CHOOSE_SIDE_STARTING_CARD,
    GAMEFLOW,
    CHOOSE_SECRET_OBJECTIVE_CARD,
    WAIT,
    NUMBER_OF_PLAYERS,
    WAIT_NUMBER_OF_PLAYERS,
    WAIT_ALL_CHOOSEN_COLOR,
    WAIT_ALL_CHOSEN_STARTING_CARD,
    WAIT_ALL_CHOSEN_SECRET_CARD;
}
