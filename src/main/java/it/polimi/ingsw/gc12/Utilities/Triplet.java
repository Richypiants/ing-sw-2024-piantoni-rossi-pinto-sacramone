package it.polimi.ingsw.gc12.Utilities;

// Class that model the cartesian plan with coordinates x, y, z

public class Triplet<T1, T2, T3> {

    // First attribute
    private T1 x;

    // Second attribute
    private T2 y;

    // Third coordinate
    private T3 z;

    //FIXME: possible reference escape (in GenericPair too?)
    public Triplet(T1 x, T2 y, T3 z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Getter method for first attribute
    public T1 getX() {
        return x;
    }

    // Getter method for second attribute
    public T2 getY() {
        return y;
    }

    // Getter method for third attributee
    public T3 getZ() {
        return z;
    }

    // Getter method for attributes 1,2
    public GenericPair<T1, T2> getXY() {
        return new GenericPair<T1, T2>(x, y);
    }

    // Getter method for attributes 1,3
    public GenericPair<T1, T3> getXZ() {
        return new GenericPair<T1, T3>(x, z);
    }

    // Getter method for attributes 2,3
    public GenericPair<T2, T3> getYZ() {
        return new GenericPair<T2, T3>(y, z);
    }


}
