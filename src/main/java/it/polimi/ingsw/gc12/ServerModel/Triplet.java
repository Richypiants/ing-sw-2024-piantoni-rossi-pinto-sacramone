package it.polimi.ingsw.gc12.ServerModel;

// Class that model the cartesian plan with coordinates x, y, z

public class Triplet<T1, T2, T3> {

    // Coordinate x
    protected T1 x;

    // Coordinate y
    protected T2 y;

    // Coordinate z
    protected T3 z;

    // Getter method for x coordinate
    public T1 getX() {
        return x;
    }

    // Getter method for y coordinate
    public T2 getY() {
        return y;
    }

    // Getter method for z coordinate
    public T3 getZ() {
        return z;
    }

    // Getter method for x, y coordinates
    public GenericPair<T1, T2> getXY() {
        return new GenericPair<T1, T2>();
    }

    // Getter method for x, z coordinates
    public GenericPair<T1, T3> getXZ() {
        return new GenericPair<T1, T3>();
    }

    // Getter method for y, z coordinates
    public GenericPair<T2, T3> getYZ() {
        return new GenericPair<T2, T3>();
    }


}
