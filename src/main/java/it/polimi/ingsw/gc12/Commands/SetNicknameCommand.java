package it.polimi.ingsw.gc12.Commands;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a command to set a nickname for a client session, implemented as both a {@link ServerCommand} and a {@link ClientCommand}.
 * <p>
 * This command allows the server to set the nickname associated with a {@link NetworkSession} on the server side,
 * and allows the client to set its own nickname on the client side if the request was successful.
 * </p>
 */
public class SetNicknameCommand implements ServerCommand, ClientCommand {

    private final String NICKNAME;

    /**
     * Constructs a {@code SetNicknameCommand} with the specified nickname.
     *
     * @param nickname The nickname to be set.
     */
    public SetNicknameCommand(String nickname){
        this.NICKNAME = nickname;
    }

    /**
     * Executes the command on the server side to set the nickname for the client identified by {@code caller}.
     *
     * @param caller           The network session representing the client for which the nickname is being set.
     * @param serverController The server controller interface responsible for managing server operations.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.setNickname(caller, NICKNAME);
    }

    /**
     * Executes the command on the client side to set the nickname within the client's controller interface.
     *
     * @param controller The client controller interface responsible for managing client operations.
     */
    @Override
    public void execute(ClientControllerInterface controller) {
        controller.setNickname(NICKNAME);
    }
}
