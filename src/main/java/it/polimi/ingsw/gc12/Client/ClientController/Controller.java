package it.polimi.ingsw.gc12.Client.ClientController;

import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller { //TODO: make a generic Utility class "Controller" and make client/server extend it?

    public static final MethodHandles.Lookup lookup = MethodHandles.lookup(); //FIXME: why does publicLookup() not work?
    public static final Map<String, MethodHandle> commandHandles = createHandles();

    //TODO: keep lambda or not?
    private static Map<String, MethodHandle> createHandles() {
        return Arrays.stream(Controller.class.getDeclaredMethods())
                .filter((method) -> Modifier.isPublic(method.getModifiers()))
                .map((method) -> {
                            try {
                                return new GenericPair<>(method.getName(), lookup.unreflect(method));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        /*MethodType type = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
                        MethodHandle thisMethod = lookup.findVirtual(GameCommand.class, method.getName(), type);
                        */
                )
                .collect(Collectors.toUnmodifiableMap(GenericPair::getX, GenericPair::getY)
                );
    }

    public void throwException(Exception e) throws Exception{
        throw e;
        //TODO: not good to pass the exception from server to client (sensitive info leak)
    }

    public void setLobbies(ArrayList<LobbyDTO> lobbies){

    }

    public void updateLobby(UUID lobbyUUID, LobbyDTO lobby){
        //se la lobby ricevuta ha 0 giocatori la rimuoviamo dalla mappa
    }
}
