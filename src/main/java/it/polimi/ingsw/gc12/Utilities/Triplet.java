package it.polimi.ingsw.gc12.Utilities;

import java.io.Serializable;
import java.util.Objects;

/**
A triplet of generic elements
 */
public class Triplet<T1, T2, T3> implements Serializable {

    /**
    The first attribute of this triplet
     */
    private final T1 X;

    /**
    The second attribute of this triplet
     */
    private final T2 Y;

    /**
    The third attribute of this triplet
     */
    private final T3 Z;

    /**
    Generates a triplet from the given parameters
     */
    //FIXME: possible reference escape (in GenericPair too?)
    public Triplet(T1 X, T2 Y, T3 Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    /**
    Returns the first attribute of this triplet
     */
    public T1 getX() {
        return X;
    }

    /**
    Returns the second attribute of this triplet
     */
    public T2 getY() {
        return Y;
    }

    /**
    Returns the third attribute of this triplet
     */
    public T3 getZ() {
        return Z;
    }

    //FIXME: delete these methods below?

    /**
    Returns a pair composed of the first and second attributes of this triplet
     */
    public GenericPair<T1, T2> getXY() {
        return new GenericPair<>(X, Y);
    }

    /**
    Returns a pair composed of the first and third attributes of this triplet
     */
    public GenericPair<T1, T3> getXZ() {
        return new GenericPair<>(X, Z);
    }

    /**
    Returns a pair composed of the second and third attributes of this triplet
     */
    public GenericPair<T2, T3> getYZ() {
        return new GenericPair<>(Y, Z);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Triplet<?, ?, ?> otherTriple)) return false;
        return (this.getX().equals(otherTriple.getX())) && (this.getY().equals(otherTriple.getY())) && (this.getZ().equals(otherTriple.getZ()));
    }

    //FIXME: is this hashCode function good? maybe we should use preexistent pair classes?
    @Override
    public int hashCode() {
        return Objects.hash(this.getX(), this.getY(), this.getZ());
    }
}

// No test needed

