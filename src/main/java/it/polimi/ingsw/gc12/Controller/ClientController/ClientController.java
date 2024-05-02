package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.ChooseInitialCardsState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.ChooseObjectiveCardsState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.GameScreenState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.PlayerTurnPlayState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.LobbyScreenState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Utilities.*;

import java.util.*;

public class ClientController implements ClientControllerInterface {

    private static final ClientController SINGLETON_INSTANCE = new ClientController();

    public final Map<Integer, ClientCard> cardsList;
    //FIXME: forse era meglio che rimanessero di competenza della View stessa nel ViewState...
    public View view;
    public ViewState viewState;
    public VirtualServer serverConnection;
    public VirtualClient thisClient;
    public Thread keepAlive;
    /**
     * This player's nickname
     */
    public String ownNickname;
    public Map<UUID, GameLobby> lobbies;
    public UUID currentUUID;
    public GameLobby currentLobbyOrGame;
    public ErrorLogger errorLogger;

    private ClientController() {
        serverConnection = null;
        thisClient = null;
        keepAlive = null;
        ownNickname = "";
        cardsList = loadCards();
        lobbies = new HashMap<>();
        currentUUID = null;
        currentLobbyOrGame = null;
        errorLogger = new ErrorLogger("src/main/java/it/polimi/ingsw/gc12/Utilities/errorLog" + this + ".txt");
    }

    public static ClientController getInstance() {
        return SINGLETON_INSTANCE;
    }

    public ViewState getCurrentState() {
        return viewState;
    }

    public void setCurrentState(ViewState currentState) {
        viewState = currentState;
    }

    public View getView() {
        return view;
    }

    public void setView(View view){
        this.view = view;
    }

    //Helper method to catch RemoteException (and eventually other ones) only one time
    public void requestToServer(ServerCommand command) {
        try {
            serverConnection.requestToServer(thisClient, command);
        } catch (Exception e) {
            errorLogger.log(e);
        }
    }

    public void setCommunicationTechnology(String communicationTechnology) {
        switch (communicationTechnology.trim().toLowerCase()) {
            case "socket" -> SocketClient.getInstance();
            case "rmi" -> RMIClientSkeleton.getInstance();
            default -> System.out.println("Unknown communication technology");
        }
    }

    private Map<Integer, ClientCard> loadCards() {
        Map<Integer, ClientCard> tmp = new HashMap<>();
        Objects.requireNonNull(JSONParser.clientCardsFromJSON("client_cards.json"))
                .forEach((card) -> tmp.put(card.ID, card));
        return Collections.unmodifiableMap(tmp);
    }

    public void throwException(Exception e) {
        errorLogger.log(e);
    }

    public void keepAlive() {
        //TODO: update Timer on VirtualClient Timer Map (add attributes or methods for management)
    }

    public void setNickname(String nickname){
        ownNickname = nickname;
        //FIXME: are we sure it goes here? (View and Controller not separated...?)
        viewState.updateNickname();
    }

    public void restoreGame(ClientGame gameDTO){
        currentLobbyOrGame = gameDTO;
        //TODO: decide which game state
        // viewState = new GameScreenState();
        viewState.executeState();
    }

    public void setLobbies(Map<UUID, GameLobby> lobbies){
        this.lobbies = lobbies;
        viewState = new LobbyScreenState();
        viewState.executeState();
    }

    public void updateLobby(UUID lobbyUUID, GameLobby lobby){
        if (lobby.getPlayers().stream().anyMatch((player) -> player.getNickname().equals(ownNickname))) {
            currentUUID = lobbyUUID;
            currentLobbyOrGame = lobby;
        }
        //Se leaveLobby, cioè se noneMatch e c'ero dentro
        else if (lobbyUUID.equals(currentUUID)) {
            currentUUID = null;
            currentLobbyOrGame = null;
        }

        //The received lobbies with a playersNumber equal to zero or below are removed from the ClientModel
        if(lobby.getPlayersNumber() <= 0)
            lobbies.remove(lobbyUUID);
        else
            lobbies.put(lobbyUUID, lobby);

        //Ristampare tutte le lobby nella schermata in qualsiasi caso
        viewState = new LobbyScreenState();
        viewState.executeState();
    }

    public synchronized void startGame(UUID lobbyUUID, ClientGame gameDTO) {
        updateLobby(lobbyUUID, gameDTO);
        currentLobbyOrGame = gameDTO;
        //FIXME: send clientGame directly?
    }

    public void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID,
                          Side playedSide, EnumMap<Resource, Integer> ownedResources,
                          List<GenericPair<Integer, Integer>> openCorners, int points) {
        ClientPlayer thisPlayer = ((ClientGame) currentLobbyOrGame).getPlayers().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny()
                .orElseThrow();

        thisPlayer.placeCard(coordinates, cardsList.get(cardID), playedSide);
        if (nickname.equals(ownNickname)) ((ClientGame) currentLobbyOrGame).removeCardFromHand(cardsList.get(cardID));
        thisPlayer.setOwnedResources(ownedResources);
        thisPlayer.setOpenCorners(openCorners);
        thisPlayer.setPoints(points);

        if(viewState instanceof ChooseInitialCardsState){
            if(nickname.equals(SINGLETON_INSTANCE.ownNickname)){
                view.gameScreen();
            }
            return;
        }

        //FIXME: sbagliatissimo
        if(viewState instanceof PlayerTurnPlayState) {
            ((GameScreenState) viewState).transition();
            viewState.executeState();
        }
        //new PlayerTurnDrawState() dello stesso giocatore...
    }

    public synchronized void receiveCard(List<Integer> cardIDs) {
        for (var cards : cardIDs)
            ((ClientGame) currentLobbyOrGame).addCardToHand(cardsList.get(cards));

        //FIXME: sbagliatissimo...
        if (viewState instanceof LobbyScreenState) {
            viewState = new ChooseInitialCardsState();
            viewState.executeState();
            return;
        }

        ((GameScreenState) viewState).transition();
        viewState.executeState();
        //new PlayerTurnPlayState() ma degli avversari...
    }

    public void receiveObjectiveChoice(List<Integer> cardIDs) {
        for (var cards : cardIDs)
            ((ClientGame) currentLobbyOrGame).addCardToHand(cardsList.get(cards));

        viewState = new ChooseObjectiveCardsState();
        viewState.executeState();
    }

    public void replaceCard(List<Triplet<Integer, String, Integer>> cardPlacements) {
        for(var cardPlacement : cardPlacements)
            if (cardPlacement.getY().trim().equalsIgnoreCase("RESOURCE"))
                ((ClientGame) currentLobbyOrGame).setPlacedResources(cardsList.get(cardPlacement.getX()), cardPlacement.getZ());
            else if (cardPlacement.getY().trim().equalsIgnoreCase("GOLD"))
                ((ClientGame) currentLobbyOrGame).setPlacedGold(cardsList.get(cardPlacement.getX()), cardPlacement.getZ());
            else if (cardPlacement.getY().trim().equalsIgnoreCase("OBJECTIVE"))
                ((ClientGame) currentLobbyOrGame).setCommonObjectives(cardsList.get(cardPlacement.getX()), cardPlacement.getZ());

        //new PlayerTurnPlayState() di un avversario oppure proprio...in realtà se un avversario pesca da un deck come lo capisco?
    }

    public void toggleActive(String nickname){
        ((ClientGame) currentLobbyOrGame).getPlayers().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny()
                .orElseThrow()
                .toggleActive();
    }

    public void endGame(List<Triplet<String, Integer, Integer>> pointsStats) {
        //TODO: stampare
    }

    public void addChatMessage(String senderNickname, String chatMessage, boolean isPrivate) {
        viewState.addChatMessage(((isPrivate) ? "<Private> " : "") + "[" + senderNickname + "] " + chatMessage);
    }
}
