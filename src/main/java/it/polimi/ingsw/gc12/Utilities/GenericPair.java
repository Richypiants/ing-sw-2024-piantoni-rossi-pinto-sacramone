package it.polimi.ingsw.gc12.Utilities;

import java.io.Serializable;
import java.util.Objects;

/**
 * A pair of generic elements.
 *
 * @param <T1> The type of the first element.
 * @param <T2> The type of the second element.
 */
public class GenericPair<T1, T2> implements Serializable {

    /**
     * The first attribute of this pair.
     */
    private final T1 X;

    /**
     * The second attribute of this pair.
     */
    private final T2 Y;

    /**
     * Constructs a pair from the given parameters.
     *
     * @param x The first element of the pair.
     * @param y The second element of the pair.
     */
    public GenericPair(T1 x, T2 y) {
        this.X = x;
        this.Y = y;
    }

    /**
     * Returns the first attribute of this pair.
     *
     * @return The first element of the pair.
     */
    public T1 getX() {
        return X;
    }

    /**
     * Returns the second attribute of this pair.
     *
     * @return The second element of the pair.
     */
    public T2 getY() {
        return Y;
    }

    /**
     * Checks if this pair is equal to another object.
     * Two pairs are considered equal if their corresponding elements are equal.
     *
     * @param other The object to compare with.
     * @return True if the other object is a GenericPair with the same elements, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof GenericPair<?, ?> otherGenericPair)) return false;
        return (this.getX().equals(otherGenericPair.getX())) && (this.getY().equals(otherGenericPair.getY()));
    }

    /**
     * Returns a hash code for this pair.
     * The hash code is computed based on the hash codes of the elements of the pair.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getX(), this.getY());
    }

    /**
     * Returns a string representation of this pair.
     * The string representation is of the form "{X, Y}".
     *
     * @return A string representation of this object.
     */
    public String toString() {
        return "{" + X + ", " + Y + "}";
    }
}


