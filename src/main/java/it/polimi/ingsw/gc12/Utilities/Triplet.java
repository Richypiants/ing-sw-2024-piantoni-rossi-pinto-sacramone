package it.polimi.ingsw.gc12.Utilities;

import java.io.Serializable;
import java.util.Objects;

/**
 * A triplet of generic elements.
 *
 * @param <T1> The type of the first element.
 * @param <T2> The type of the second element.
 * @param <T3> The type of the third element.
 */
public class Triplet<T1, T2, T3> implements Serializable {

    /**
     * The first attribute of this triplet.
     */
    private final T1 X;

    /**
     * The second attribute of this triplet.
     */
    private final T2 Y;

    /**
     * The third attribute of this triplet.
     */
    private final T3 Z;

    /**
     * Generates a triplet from the given parameters.
     *
     * @param X The first element of the triplet.
     * @param Y The second element of the triplet.
     * @param Z The third element of the triplet.
     */
    public Triplet(T1 X, T2 Y, T3 Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    /**
     * Returns the first attribute of this triplet.
     *
     * @return The first element of the triplet.
     */
    public T1 getX() {
        return X;
    }

    /**
     * Returns the second attribute of this triplet.
     *
     * @return The second element of the triplet.
     */
    public T2 getY() {
        return Y;
    }

    /**
     * Returns the third attribute of this triplet.
     *
     * @return The third element of the triplet.
     */
    public T3 getZ() {
        return Z;
    }

    /**
     * Checks if this triplet is equal to another object.
     * Two triplets are considered equal if their corresponding elements are equal.
     *
     * @param other The object to compare with.
     * @return True if the other object is a Triplet with the same elements, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Triplet<?, ?, ?> otherTriple)) return false;
        return (this.getX().equals(otherTriple.getX())) && (this.getY().equals(otherTriple.getY())) && (this.getZ().equals(otherTriple.getZ()));
    }

    /**
     * Returns a hash code for this triplet.
     * The hash code is computed based on the hash codes of the elements of the triplet.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getX(), this.getY(), this.getZ());
    }
}


