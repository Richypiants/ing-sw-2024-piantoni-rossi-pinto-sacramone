package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.Server.Game;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ViewModelTest {

    static ViewModel viewModel = new ViewModel();

    @Test
    void operationsOnNickname(){
        String newNickname = "testedPlayer";

        assertEquals("", viewModel.getOwnNickname());
        viewModel.setOwnNickname(newNickname);
        assertEquals(newNickname, viewModel.getOwnNickname());
    }

    @Test
    void operationsOnLobbies(){
        Map<UUID, Lobby> newLobbies = new HashMap<>();
        Player testPlayerOne = new Player("testerOne");
        Player testPlayerTwo = new Player("testerTwo");
        Player testPlayerThree = new Player("testerThree");
        Player testPlayerFour = new Player("testerFour");

        UUID firstLobbyId = UUID.randomUUID();
        UUID secondLobbyId = UUID.randomUUID();
        newLobbies.put(firstLobbyId, new Lobby(firstLobbyId, testPlayerOne, 2));
        Lobby generatedLobby = new Lobby(secondLobbyId, testPlayerTwo, 2);
        assertDoesNotThrow( () -> generatedLobby.addPlayer(testPlayerThree));
        newLobbies.put(secondLobbyId, generatedLobby);

        viewModel.setLobbies(newLobbies);
        assertEquals(newLobbies, viewModel.getLobbies());

        UUID thirdLobbyId = UUID.randomUUID();
        Lobby anotherLobby = new Lobby(thirdLobbyId, testPlayerFour, 2);
        viewModel.putLobby(thirdLobbyId, anotherLobby);

        int numOfCurrentLobbies = viewModel.getLobbies().size();

        viewModel.removeLobby(thirdLobbyId);

        assertEquals(numOfCurrentLobbies-1, viewModel.getLobbies().size());
    }

    public Lobby generateLobbyForTesting(UUID id){
        Player creatorPlayer = new Player("creatorPlayer");

        return new Lobby(id, creatorPlayer, 1);
    }

    @Test
    void operationsOnThisClientRoom(){
        UUID targetLobbyId = UUID.randomUUID();
        Lobby targetLobby = generateLobbyForTesting(targetLobbyId);
        viewModel.putLobby(targetLobbyId, targetLobby);

        assertNull(viewModel.getCurrentRoomUUID());

        assertFalse(viewModel.inRoom());
        viewModel.joinRoom(targetLobby);
        assertTrue(viewModel.inRoom());

        assertEquals(targetLobby, viewModel.getCurrentLobby());
        assertEquals(targetLobbyId, viewModel.getCurrentRoomUUID());

        viewModel.leaveRoom();
        assertFalse(viewModel.inRoom());
    }

    @Test
    void assertingIsAClientGame(){
        Game targetGame = new Game(generateLobbyForTesting(UUID.randomUUID()));
        ClientGame targetClientGame = targetGame.generateDTO(targetGame.getPlayers().getFirst());

        viewModel.joinRoom(targetClientGame);

        assertInstanceOf(ClientGame.class, viewModel.getCurrentGame());
        assertEquals(targetClientGame, viewModel.getCurrentGame());
    }

    @Test
    void viewModelReset() {
        Lobby lobby = new Lobby(UUID.randomUUID(), new Player("nickname"), 2);
        viewModel.putLobby(lobby.getRoomUUID(), lobby);
        viewModel.setOwnNickname("nickname");
        viewModel.joinRoom(lobby);

        viewModel.clearModel();
        assertEquals(viewModel.getOwnNickname(), "");
        assertEquals(viewModel.getLobbies().size(), 0);
        assertNull(viewModel.getCurrentRoomUUID());
        assertNull(viewModel.getCurrentLobby());
        assertNull(viewModel.getCurrentGame());
    }
}