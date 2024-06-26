package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * The Sign enum represents the different types of signs that can be present on a card in the game.
 * Each sign corresponds to a different element or item in nature.
 *
 * LEAF: Represents the leaf sign.
 * WOLF: Represents the wolf sign.
 * MUSHROOM: Represents the mushroom sign.
 * BUTTERFLY: Represents the butterfly sign.
 * QUILL: Represents the quill sign.
 * SCROLL: Represents the scroll sign.
 * INKWELL: Represents the inkwell sign.
 * EMPTY: Represents the absence of a sign.
 * NULL: Represents a null or uninitialized sign.
 */
public enum Sign implements Serializable {
    /**
     * The LEAF sign.
     */
    LEAF,
    /**
     * The WOLF sign.
     */
    WOLF,
    /**
     * The MUSHROOM sign.
     */
    MUSHROOM,
    /**
     * The BUTTERFLY sign.
     */
    BUTTERFLY,
    /**
     * The QUILL sign.
     */
    QUILL,
    /**
     * The SCROLL sign.
     */
    SCROLL,
    /**
     * The INKWELL sign.
     */
    INKWELL,
    /**
     * The EMPTY sign.
     */
    EMPTY,
    /**
     * The NULL sign.
     */
    NULL,
}
