package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ClientGame extends GameLobby implements Serializable {

    private final ClientPlayer MYSELF;
    /**
     * The cards in this player's hand
     */
    private final ArrayList<ClientCard> OWN_HAND;
    //TODO: valutare private final Map<String, ClientCard[]> PLACED_CARDS;
    private final ClientCard[] PLACED_RESOURCE_CARDS;
    private final ClientCard[] PLACED_GOLD_CARDS;
    private final ClientCard[] COMMON_OBJECTIVES;

    private ClientCard topDeckResourceCard;
    private ClientCard topDeckGoldCard;
    /**
     * The secret Objective Card chosen by this player
     */
    private ClientCard ownObjective;
    private int currentRound;
    private int currentPlayerIndex;
    private final List<String> chatLog;

    private LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>> temporaryReceiverField;

    //TODO: costruire scoreboard
    public ClientGame(Game game, InGamePlayer myself) {
        super(game.getMaxPlayers(), game.getPlayers().stream()
                //.filter(Predicate.not(myself::equals))
                .map(ClientPlayer::new)
                .toList());

        Map<Integer, ClientCard> clientCards = ServerController.getInstance().clientCardsList;

        this.MYSELF = getPlayers().stream().filter((player) -> player.getNickname().equals(myself.getNickname())).findAny().orElseThrow();

        LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>> receiverField = new LinkedHashMap<>();
        for(var thisPlayerFieldEntry : myself.getPlacedCards().sequencedEntrySet())
            receiverField.put(thisPlayerFieldEntry.getKey(), new GenericPair<>(thisPlayerFieldEntry.getValue().getX().ID, thisPlayerFieldEntry.getValue().getY()));

        this.temporaryReceiverField = receiverField;
        this.OWN_HAND = new ArrayList<>();
        this.PLACED_RESOURCE_CARDS = Arrays.stream(game.getPlacedResources())
                .map((card) -> clientCards.get(card.ID))
                .toArray(ClientCard[]::new);
        this.PLACED_GOLD_CARDS = Arrays.stream(game.getPlacedGolds())
                .map((card) -> clientCards.get(card.ID))
                .toArray(ClientCard[]::new);
        this.COMMON_OBJECTIVES = Arrays.stream(game.getCommonObjectives())
                .map((card) -> card == null ? null : clientCards.get(card.ID))
                .toArray(ClientCard[]::new);
        this.topDeckResourceCard = clientCards.get(game.peekFrom(game.getResourceCardsDeck()) == null ? -1 : game.peekFrom(game.getResourceCardsDeck()).ID);
        this.topDeckGoldCard = clientCards.get(game.peekFrom(game.getGoldCardsDeck()) == null ? -1 : game.peekFrom(game.getGoldCardsDeck()).ID);
        this.ownObjective = myself.getSecretObjective() == null ? null : clientCards.get(myself.getSecretObjective().ID);
        this.currentRound = 0;
        this.currentPlayerIndex = -1;
        this.chatLog = new ArrayList<>();
    }

    /**
     * Returns the player who is currently playing
     */
    @Override
    public ArrayList<ClientPlayer> getPlayers() {
        return super.getPlayers()
                .stream()
                .map((player) -> (ClientPlayer) player)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ClientPlayer getThisPlayer(){
        return this.MYSELF;
    }

    //TODO: make a method that returns all players except thisPlayer?

    public ArrayList<ClientCard> getCardsInHand(){
        return OWN_HAND; //TODO: copia?
    }

    public void addCardToHand(ClientCard card){
        OWN_HAND.add(card);
    }

    public void removeCardFromHand(ClientCard card){
        OWN_HAND.remove(card);
    }

    //TODO: Maybe move as an attribute of RestoreGameCommand
    public LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>> getTemporaryReceiverField(){
        return this.temporaryReceiverField;
    }
    /**
     * Returns the ResourceCards placed on the table
     */
    public ClientCard[] getPlacedResources() {
        return PLACED_RESOURCE_CARDS;
    }

    /**
     * Replaces one of the ResourceCards placed on the table
     */
    public void setPlacedResources(ClientCard card, int position) {
        PLACED_RESOURCE_CARDS[position] = card;
    }

    /**
     * Returns the GoldCards placed on the table
     */
    public ClientCard[] getPlacedGold() {
        return PLACED_GOLD_CARDS;
    }

    /**
     * Replaces one of the GoldCards placed on the table
     */
    public void setPlacedGold(ClientCard card, int position) {
        PLACED_GOLD_CARDS[position] = card;
    }

    /**
     * Returns the ObjectiveCards placed on the table
     */
    public ClientCard[] getCommonObjectives() {
        return COMMON_OBJECTIVES;
    }

    public void setCommonObjectives(ClientCard objective, int position) {
        COMMON_OBJECTIVES[position] = objective;
    }

    public ClientCard getTopDeckResourceCard() {
        return topDeckResourceCard;
    }

    public void setTopDeckResourceCard(ClientCard card) {
        topDeckResourceCard = card;
    }

    public ClientCard getTopDeckGoldCard() {
        return topDeckGoldCard;
    }

    public void setTopDeckGoldCard(ClientCard card) {
        topDeckGoldCard = card;
    }

    public ClientCard getOwnObjective(){
        return ownObjective;
    }

    public void setOwnObjective(ClientCard card){
        ownObjective = card;
    }

    public int getCurrentRound(){
        return currentRound;
    }

    public void setCurrentRound(int currentRound){ this.currentRound = currentRound; }

    public int getCurrentPlayerIndex() {return currentPlayerIndex; }

    public void setCurrentPlayerIndex(int currentPlayerIndex) { this. currentPlayerIndex = currentPlayerIndex; }

    public List<String> getChatLog() {
        return Collections.unmodifiableList(chatLog);
    }

    public void addMessageToChatLog(String message) {
        chatLog.add(message);
    }

}
