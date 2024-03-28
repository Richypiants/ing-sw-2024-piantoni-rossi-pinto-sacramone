package it.polimi.ingsw.gc12.ServerController.Command;

import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.GameLobby;
import it.polimi.ingsw.gc12.ServerModel.GameStates.ChooseInitialCardsState;
import it.polimi.ingsw.gc12.ServerModel.GameStates.GameState;
import it.polimi.ingsw.gc12.ServerModel.Player;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Command {
    private final static Command THIS_SINGLETON_COMMAND = null;

    protected Command() {
    }

    public static Command getInstance() {
        return THIS_SINGLETON_COMMAND;
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Player thisPlayer = new Player("Piants");
        Game thisGame = new Game(new GameLobby(1, thisPlayer));
        GameState currentState = new ChooseInitialCardsState(thisGame);
        ArrayList<Object> parameters = new ArrayList<>();

        parameters.add(thisGame.getPlayers().getFirst());
        parameters.add(Side.FRONT);

        System.out.println(Arrays.toString(parameters.stream()
                .map(Object::getClass)
                .toArray()));

        final int[] pos = {0};

        System.out.println(currentState.getClass().getMethod(
                                "placeInitialCard",
                                parameters.stream()
                                        .map(Object::getClass)
                                        .collect(() -> new Class<?>[parameters.size()],
                                                (c1, c2) -> {
                                                    c1[pos[0]] = c2;
                                                    pos[0]++;
                                                },
                                                (c1, c2) -> System.out.println()
                                        )
                        )
                        .invoke(currentState, parameters.toArray())
        );
    }

    protected void validateParameters(Class<? extends GameState> executionState, String methodName) throws NoSuchMethodException {

    }

    public void execute(Player commandCaller, ArrayList<Object> args) {
        //throw new NotImplementedCommandException();
    }

}
