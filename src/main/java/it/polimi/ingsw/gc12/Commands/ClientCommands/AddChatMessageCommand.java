package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

/**
 * Represents a command to add a chat message received from the server to the client's chat interface.
 * This command is executed by calling {@link #execute(ClientControllerInterface)} on a client controller.
 */
public class AddChatMessageCommand implements ClientCommand {

    private final String SENDER_NICKNAME;
    private final String CHAT_MESSAGE;
    private final boolean IS_PRIVATE;

    /**
     * Constructs an AddChatMessageCommand with the given sender's nickname, chat message content,
     * and indication of whether the message is private.
     *
     * @param senderNickname The nickname of the sender of the chat message.
     * @param chatMessage    The content of the chat message.
     * @param isPrivate      True if the message is private, false otherwise.
     */
    public AddChatMessageCommand(String senderNickname, String chatMessage, boolean isPrivate) {
        this.SENDER_NICKNAME = senderNickname;
        this.CHAT_MESSAGE = chatMessage;
        this.IS_PRIVATE = isPrivate;
    }

    /**
     * Executes the command on the provided client controller, adding the message to the log.
     *
     * @param clientController The client controller instance on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.addChatMessage(SENDER_NICKNAME, CHAT_MESSAGE, IS_PRIVATE);
    }
}
