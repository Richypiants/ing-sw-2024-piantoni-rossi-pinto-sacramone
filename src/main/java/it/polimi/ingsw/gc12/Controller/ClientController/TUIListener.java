package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.BroadcastMessageCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.CreateLobbyCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.DirectMessageCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;

import java.util.*;
import java.util.function.Function;

import static org.fusesource.jansi.Ansi.ansi;

public class TUIListener {

    private static TUIListener SINGLETON_TUI_LISTENER;
    private Map<String, Function<ArrayList<Object>, ServerCommand>> knownCommands = new HashMap<>();

    private TUIListener() {
        //knownCommands = PARSING THE COMMANDS FROM A JSON FILE
        //FIXME: move into controller for GUI use too?
        knownCommands.put("createLobby", (arguments) -> createCommand(
                (args) -> new CreateLobbyCommand((Integer) args.getFirst()),
                arguments,
                1)
        );
        knownCommands.put("broadcastMessage", (arguments) -> createCommand(
                (args) -> new BroadcastMessageCommand((String) args.getFirst()),
                arguments,
                1)
        ); //Second parameter is command args.
        knownCommands.put("directMessage", (arguments) -> createCommand(
                (args) -> new DirectMessageCommand((String) args.getFirst(), (String) args.get(1)),
                arguments,
                2)
        ); //target, message
        //TODO: complete list...
    }

    public static TUIListener getInstance() {
        if (SINGLETON_TUI_LISTENER == null)
            SINGLETON_TUI_LISTENER = new TUIListener();
        return SINGLETON_TUI_LISTENER;
    }

    public void startReading() {
        Scanner scanner = new Scanner(System.in);
        new Thread(() -> {
            while (true) {
                //FIXME: devo salvare la precedente posizione del cursore?
                System.out.println(ansi().cursor(20, 0));
                scanner.nextLine();
            }
        }
        ).start();
    }

    private ServerCommand createCommand(Function<ArrayList<Object>, ServerCommand> commandConstructor,
                                        ArrayList<Object> arguments, int numArgs) {
        if (arguments.size() != numArgs)
            throw new IllegalArgumentException(); //TODO: remember to try/catch this!

        return commandConstructor.apply(arguments);
    }

    public String parseCommand(String input) {
        String delimiters = " ";
        ArrayList<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(input, delimiters);

        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        //check that the tokens match with the issued command
        Function<ArrayList<Object>, ServerCommand> commandConstructor = knownCommands.get(tokens.getFirst());
        if (commandConstructor != null) {
            //commandConstructor.apply(tokens);
            //send command to a queue or controller.
        } else {
            //printError("Unknown Command || Unmatching arguments ...");
            //More verbose error logging is possible, like printing the correct templated usage ...
        }

        String filtered_input = input.trim().toLowerCase(); //No lower for nick?
        if (filtered_input.isEmpty()) { //Plus other unknown checks
            return "You must issue a command";
        }

        //tokens = new List<String>(input.split(delimiters, StringSplitOption))

        return null;
    }
}
