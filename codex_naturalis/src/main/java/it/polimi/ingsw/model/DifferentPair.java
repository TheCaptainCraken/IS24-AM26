package it.polimi.ingsw.model;

/**
 * class created for ease of implementation of methods in PlayedCard class
 * T and U are generic which means they can represent whatever data type we like
 * @author Arturo */
public class DifferentPair <T,U> {

    private final T first;
    private final U second;

    public DifferentPair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
}
