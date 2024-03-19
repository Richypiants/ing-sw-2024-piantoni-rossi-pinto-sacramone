package it.polimi.ingsw.gc12.Utilities;

/*
A pair of generic elements
 */
public class GenericPair<T1, T2> {

    /*
    The first attribute of this pair
     */
    private T1 x;

    /*
    The second attribute of this pair
     */
    private T2 y;

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

    public boolean equals(GenericPair<T1, T2> x, GenericPair<T1, T2> y) {
        return (x.getX().equals(y.getX())) && (x.getY().equals(y.getY()));
    }
}

// No test needed
