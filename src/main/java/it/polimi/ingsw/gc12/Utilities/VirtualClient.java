package it.polimi.ingsw.gc12.Utilities;

import java.util.ArrayList;

public interface VirtualClient {

    void serverMessage(ArrayList<Object> objects) throws Throwable;
}
