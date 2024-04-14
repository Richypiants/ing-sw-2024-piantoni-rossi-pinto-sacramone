package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.Controller;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Utilities.*;

import java.util.*;

public abstract class ClientController extends Controller {

    private static final Map<Integer, ClientCard> cardsList = loadCards();
    /**
     * This player's nickname
     */
    public static VirtualServer serverConnection;
    private static String ownNickname;
    private static Map<UUID, GameLobby> lobbies = new HashMap<>();
    private static GameLobby currentLobbyOrGame = null;

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

    public void receiveCard(List<Integer> cardIDs) {
        for (var cards : cardIDs)
            ((ClientGame) currentLobbyOrGame).addCardToHand(cardsList.get(cards));
    }

    public void replaceCard(List<Triplet<Integer, String, Integer>> cardPlacements) {
        for(var cardPlacement : cardPlacements)
            if (cardPlacement.getY().trim().equalsIgnoreCase("RESOURCE"))
                ((ClientGame) currentLobbyOrGame).setPlacedResources(cardsList.get(cardPlacement.getX()), cardPlacement.getZ());
            else if (cardPlacement.getY().trim().equalsIgnoreCase("GOLD"))
                ((ClientGame) currentLobbyOrGame).setPlacedGold(cardsList.get(cardPlacement.getX()), cardPlacement.getZ());
            else if (cardPlacement.getY().trim().equalsIgnoreCase("OBJECTIVE"))
                ((ClientGame) currentLobbyOrGame).setCommonObjectives(cardsList.get(cardPlacement.getX()), cardPlacement.getZ());
    }

    public void toggleActive(String nickname){
        ((ClientGame) currentLobbyOrGame).getPlayers().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny()
                .orElseThrow()
                .toggleActive();
    }

    public void endGame(ArrayList<Triplet<String, Integer, Integer>> pointsStats) {
        //TODO: stampare
    }

    public void addChatMessage(String chatMessage){

    }
}
