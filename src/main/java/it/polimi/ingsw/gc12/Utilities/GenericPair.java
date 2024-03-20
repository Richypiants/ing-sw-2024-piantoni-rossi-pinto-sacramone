package it.polimi.ingsw.gc12.Utilities;

import java.util.Objects;

/*
A pair of generic elements
 */
public class GenericPair<T1, T2> {

    /*
    The first attribute of this pair
     */
    private final T1 x;

    /*
    The second attribute of this pair
     */
    private final T2 y;

    /*
    Generates a pair from the given parameters
     */
    public GenericPair(T1 x, T2 y) {
        this.x = x;
        this.y = y;
    }

    /*
    Returns the first attribute of this pair
     */
    public T1 getX() {
        return x;
    }

    /*
    Returns the second attribute of this pair
     */
    public T2 getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof GenericPair<?, ?> otherGenericPair)) return false;
        return (this.getX().equals(otherGenericPair.getX())) && (this.getY().equals(otherGenericPair.getY()));
    }

    //FIXME: is this hashCode function good? maybe we should use preexistent pair classes?
    @Override
    public int hashCode() {
        return Objects.hash(this.getX(), this.getY());
    }
}

// No test needed
