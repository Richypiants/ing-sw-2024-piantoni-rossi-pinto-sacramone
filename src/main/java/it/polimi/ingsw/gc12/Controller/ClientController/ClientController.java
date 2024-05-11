package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.ChooseInitialCardsState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.ChooseObjectiveCardsState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.GameScreenState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.LeaderboardScreenState;
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
    public String serverIPAddress = "localhost";
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

    //FIXME: da qui in poi i synchronized servono davvero oppure risolvendo la writePending e facendo una coda di comandi si risolve?
    public synchronized void startGame(UUID lobbyUUID, ClientGame gameDTO) {
        updateLobby(lobbyUUID, gameDTO);
        currentLobbyOrGame = gameDTO;
        //FIXME: send clientGame directly?

        viewState = new ChooseInitialCardsState();
    }

    public void confirmObjectiveChoice(int cardID){
        ((ClientGame) currentLobbyOrGame).setOwnObjective(cardsList.get(cardID));
        view.gameScreen();
    }

    public synchronized void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID,
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
        }
    }

    public synchronized void receiveCard(List<Integer> cardIDs) {
        for (var cardID : cardIDs)
            ((ClientGame) currentLobbyOrGame).addCardToHand(cardsList.get(cardID));

        viewState.executeState();
    }

    public synchronized void receiveObjectiveChoice(List<Integer> cardIDs) {
        for (var cardID : cardIDs)
            ((ChooseObjectiveCardsState) viewState).objectivesSelection.add(cardsList.get(cardID));

        viewState.executeState();
    }

    public synchronized void replaceCard(List<Triplet<Integer, String, Integer>> cardPlacements) {
        for(var cardPlacement : cardPlacements) {
            ClientCard card = cardsList.get(cardPlacement.getX());
            switch (cardPlacement.getY().trim().toLowerCase()) {
                case "resource_deck" -> ((ClientGame) currentLobbyOrGame).setTopDeckResourceCard(card);
                case "gold_deck" -> ((ClientGame) currentLobbyOrGame).setTopDeckGoldCard(card);
                case "resource_visible" ->
                        ((ClientGame) currentLobbyOrGame).setPlacedResources(card, cardPlacement.getZ());
                case "gold_visible" -> ((ClientGame) currentLobbyOrGame).setPlacedGold(card, cardPlacement.getZ());
                case "objective_visible" ->
                        ((ClientGame) currentLobbyOrGame).setCommonObjectives(card, cardPlacement.getZ());
            }
        }
    }

    public synchronized void transition(int round, int currentPlayerIndex) {
        if(round != 0 )
            ((ClientGame) currentLobbyOrGame).setCurrentRound(round);
        if(currentPlayerIndex != -1 )
            ((ClientGame) currentLobbyOrGame).setCurrentPlayerIndex(currentPlayerIndex);

        ((GameScreenState) viewState).transition();
        viewState.executeState();
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
        viewState = new LeaderboardScreenState(pointsStats);
        viewState.executeState();
    }

    public void addChatMessage(String senderNickname, String chatMessage, boolean isPrivate) {
        viewState.addChatMessage(((isPrivate) ? "<Private> " : "") + "[" + senderNickname + "] " + chatMessage);
    }

    public boolean isThisClientTurn(){
        ClientGame game = (ClientGame) currentLobbyOrGame;
        return game.getPlayers().get(game.getCurrentPlayerIndex()).getNickname().equals(game.getThisPlayer().getNickname());
    }
}
