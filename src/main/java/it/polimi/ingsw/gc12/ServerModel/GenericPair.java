package it.polimi.ingsw.gc12.ServerModel;

public class GenericPair<T1, T2> {
    // first attribute
    private T1 x;
    // second attribute
    private T2 y;

    public GenericPair(T1 x, T2 y) {
        this.x = x;
        this.y = y;
    }

    //Getter method for first attribute
    public T1 getX() {
        return x;
    }

    //Getter method for second attribute
    public T2 getY() {
        return y;
    }
}
