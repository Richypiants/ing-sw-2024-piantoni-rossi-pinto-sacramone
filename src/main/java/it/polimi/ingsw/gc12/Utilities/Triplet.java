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
    private final T1 x;

    /**
    The second attribute of this triplet
     */
    private final T2 y;

    /**
    The third attribute of this triplet
     */
    private final T3 z;

    /**
    Generates a triplet from the given parameters
     */
    //FIXME: possible reference escape (in GenericPair too?)
    public Triplet(T1 x, T2 y, T3 z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
    Returns the first attribute of this triplet
     */
    public T1 getX() {
        return x;
    }

    /**
    Returns the second attribute of this triplet
     */
    public T2 getY() {
        return y;
    }

    /**
    Returns the third attribute of this triplet
     */
    public T3 getZ() {
        return z;
    }

    //FIXME: delete these methods below?

    /**
    Returns a pair composed of the first and second attributes of this triplet
     */
    public GenericPair<T1, T2> getXY() {
        return new GenericPair<T1, T2>(x, y);
    }

    /**
    Returns a pair composed of the first and third attributes of this triplet
     */
    public GenericPair<T1, T3> getXZ() {
        return new GenericPair<T1, T3>(x, z);
    }

    /**
    Returns a pair composed of the second and third attributes of this triplet
     */
    public GenericPair<T2, T3> getYZ() {
        return new GenericPair<T2, T3>(y, z);
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

