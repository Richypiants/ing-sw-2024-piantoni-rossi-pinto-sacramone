package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.Controller;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.*;

public class ClientController extends Controller {

    /**
     * This player's nickname
     */
    private String ownNickname;
    private final Map<Integer, ClientCard> cardsList = loadCards();
    private Map<UUID, GameLobby> lobbies = new HashMap<>();
    private GameLobby currentLobbyOrGame = null;

    private static Map<Integer, ClientCard> loadCards() {
        //TODO: map of maps?
        Map<Integer, ClientCard> tmp = new HashMap<>();
        /*Objects.requireNonNull(JSONParser.deckFromJSONConstructor("client_cards.json", new TypeToken<>(){}))
                .forEach((card) -> tmp.put(card.ID, card));*/
        return Collections.unmodifiableMap(tmp);
    }

    public void throwException(Exception e) throws Exception{
        throw e;
    }

    public void restoreGame(ClientGame gameDTO){
        currentLobbyOrGame = gameDTO;
    }

    public void setLobbies(Map<UUID, GameLobby> lobbies){
        this.lobbies = lobbies;
    }

    public void updateLobby(UUID lobbyUUID, GameLobby lobby){
        //se la lobby ricevuta ha 0 giocatori la rimuoviamo dalla mappa
        if(lobby.getPlayersNumber() <= 0)
            lobbies.remove(lobbyUUID);
        lobbies.put(lobbyUUID, lobby);
        if(lobby.getPlayers().stream().anyMatch((player) -> player.getNickname().equals(ownNickname)))
            currentLobbyOrGame = lobby;
    }

    public void startGame(UUID lobbyUUID, GameLobby lobby){
        updateLobby(lobbyUUID, lobby);
        currentLobbyOrGame = new ClientGame(currentLobbyOrGame);
    }

    public void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID,
                          Side playedSide, EnumMap<Resource, Integer> ownedResources,
                          ArrayList<GenericPair<Integer, Integer>> openCorners, int points){
        ClientPlayer thisPlayer = ((ClientGame) currentLobbyOrGame).getPlayers().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny()
                .orElseThrow();

        thisPlayer.placeCard(coordinates, cardsList.get(cardID), playedSide);
        thisPlayer.setOwnedResources(ownedResources);
        thisPlayer.setOpenCorners(openCorners);
        thisPlayer.setPoints(points);
    }

    public void receiveCard(int cardID){
        ((ClientGame) currentLobbyOrGame).addCardToHand(cardsList.get(cardID));
    }

    public void replaceCard(int cardID, String deck, int position){
        if (deck.trim().equalsIgnoreCase("RESOURCE"))
            ((ClientGame) currentLobbyOrGame).setPlacedResources(cardsList.get(cardID), position);
        else ((ClientGame) currentLobbyOrGame).setPlacedGold(cardsList.get(cardID), position);
    }

    public void toggleActive(String nickname){
        ((ClientGame) currentLobbyOrGame).getPlayers().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny()
                .orElseThrow()
                .toggleActive();
    }

    public void addChatMessage(String chatMessage){

    }
}
