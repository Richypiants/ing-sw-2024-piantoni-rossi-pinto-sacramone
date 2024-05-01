package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public class AddChatMessageCommand implements ClientCommand {

    private final String SENDER_NICKNAME;
    private final String CHAT_MESSAGE;
    private final boolean IS_PRIVATE;

    public AddChatMessageCommand(String senderNickname, String chatMessage, boolean isPrivate) {
        this.SENDER_NICKNAME = senderNickname;
        this.CHAT_MESSAGE = chatMessage;
        this.IS_PRIVATE = isPrivate;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.addChatMessage(SENDER_NICKNAME, CHAT_MESSAGE, IS_PRIVATE);
    }
}
