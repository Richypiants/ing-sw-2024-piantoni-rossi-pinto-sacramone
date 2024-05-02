package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;

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
    /**
     * The secret Objective Card chosen by this player
     */
    private ClientCard ownObjective;
    private int currentRound;
    private final List<String> chatLog;
    /**
     *
     */

    //TODO: costruire scoreboard
    public ClientGame(Game game, InGamePlayer myself, Map<Integer, ClientCard> clientCards) {
        super(game.getMaxPlayers(), game.getPlayers().stream()
                //.filter(Predicate.not(myself::equals))
                .map(ClientPlayer::new)
                .toList());
        this.MYSELF = new ClientPlayer(myself);
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
        this.ownObjective = myself.getSecretObjective() == null ? null : clientCards.get(myself.getSecretObjective().ID);
        this.currentRound = 0;
        this.chatLog = new ArrayList<>();
    }

    //FIXME: useless??? remove...
    /*public ClientGame(int maxPlayers, List<ClientPlayer> players, ArrayList<ClientCard> ownHand,
                      ClientCard[] placedResourceCards, ClientCard[] placedGoldCards,
                      ClientCard[] commonObjectives, ClientCard ownObjective, int currentRound){
        super(maxPlayers, players);
        this.OWN_HAND = ownHand;
        this.PLACED_RESOURCE_CARDS = placedResourceCards;
        this.PLACED_GOLD_CARDS = placedGoldCards;
        this.COMMON_OBJECTIVES = commonObjectives;
        this.ownObjective = ownObjective;
        this.currentRound = currentRound;
        this.chatLog = new ArrayList<>();
    }
    */

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
    public ArrayList<ClientCard> getCardsInHand(){
        return OWN_HAND; //TODO: copia?
    }

    public void addCardToHand(ClientCard card){
        OWN_HAND.add(card);
    }

    public void removeCardFromHand(ClientCard card){
        OWN_HAND.remove(card);
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

    public ClientCard getOwnObjective(){
        return ownObjective;
    }

    public void setOwnObjective(ClientCard card){
        ownObjective = card;
    }

    public int getCurrentRound(){
        return currentRound;
    }

    public void increaseRound(){
        currentRound++;
    }

    public List<String> getChatLog() {
        return Collections.unmodifiableList(chatLog);
    }

    public void addMessageToChatLog(String message) {
        chatLog.add(message);
    }
}
