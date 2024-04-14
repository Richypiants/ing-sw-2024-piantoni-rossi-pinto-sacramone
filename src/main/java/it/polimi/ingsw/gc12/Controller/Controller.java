package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Controller {

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup(); //FIXME: why does publicLookup() not work?
    public static final Map<String, MethodHandle> commandHandles = createHandles();

    //TODO: keep lambda or not?
    protected static Map<String, MethodHandle> createHandles() {
        return Arrays.stream(ClientController.class.getDeclaredMethods())
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
}
