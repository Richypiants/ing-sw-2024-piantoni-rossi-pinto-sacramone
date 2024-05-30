package it.polimi.ingsw.gc12.Listeners;

//TODO: maybe should make abstract class?
public interface Listenable {

    void addListener(Listener listener);

    void removeListener(Listener listener);

    void notifyListeners();
}
