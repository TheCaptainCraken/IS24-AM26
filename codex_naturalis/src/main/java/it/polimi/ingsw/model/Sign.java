package it.polimi.ingsw.model;

public enum Sign {
    LEAF,
    WOLF,
    MUSHROOM,
    BUTTERFLY,
    QUILL,
    INKWELL,
    SCROLL,
    EMPTY;

    @Override
    public String toString() {
        switch(this) {
            case MUSHROOM:
                return "M";
            case LEAF:
                return "L";
            case BUTTERFLY:
                return "B";
            case WOLF:
                return "W";
            case QUILL:
                return "Q";
            case INKWELL:
                return "I";
            case SCROLL:
                return "S";
            case EMPTY:
                return " ";
            default:
                return "X";
        }
    }
}
