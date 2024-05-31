package it.polimi.ingsw.gc12.Listeners;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;

//TODO: maybe should make abstract class?
public interface Listenable {

    void addListener(Listener listener);

    void removeListener(Listener listener);

    void notifyListeners(ClientCommand command);
}
