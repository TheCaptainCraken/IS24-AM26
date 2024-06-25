package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * This enum represents the colors that can be used in the application.
 * It implements Serializable, which means it can be serialized and sent over a network or written to persistent storage.
 * The colors available are RED, YELLOW, GREEN, and BLUE.
 */
public enum Color implements Serializable {
    /**
     * The RED color.
     */
    RED,
    /**
     * The YELLOW color.
     */
    YELLOW,
    /**
     * The GREEN color.
     */
    GREEN,
    /**
     * The BLUE color.
     */
    BLUE,
}
