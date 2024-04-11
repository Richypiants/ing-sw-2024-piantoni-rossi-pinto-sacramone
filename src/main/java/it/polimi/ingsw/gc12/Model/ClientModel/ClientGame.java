package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.GameLobby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientGame extends GameLobby{

    //private final ClientPlayer MYSELF;
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
    /**
     *
     */
    //TODO: vedere se sarà necessario
    // private GameState currentState;

    //TODO: costruire scoreboard

    public ClientGame(GameLobby lobby/*, Player myself*/) {
        super(lobby.getMaxPlayers(), lobby.getPlayers().stream()
                //.filter(Predicate.not(myself::equals))
                .map(ClientPlayer::new)
                .toList());
        //this.MYSELF = new ClientPlayer(myself);
        this.OWN_HAND = new ArrayList<>();
        this.PLACED_RESOURCE_CARDS = new ClientCard[2];
        this.PLACED_GOLD_CARDS = new ClientCard[2];
        this.COMMON_OBJECTIVES = new ClientCard[2];
        this.ownObjective = null;
        this.currentRound = 0;
        //this.currentState = ???;
    }

    public ClientGame(int maxPlayers, List<ClientPlayer> players, ArrayList<ClientCard> ownHand,
                      ClientCard[] placedResourceCards, ClientCard[] placedGoldCards,
                      ClientCard[] commonObjectives, ClientCard ownObjective, int currentRound){
        super(maxPlayers, players);
        this.OWN_HAND = ownHand;
        this.PLACED_RESOURCE_CARDS = placedResourceCards;
        this.PLACED_GOLD_CARDS = placedGoldCards;
        this.COMMON_OBJECTIVES = commonObjectives;
        this.ownObjective = ownObjective;
        this.currentRound = currentRound;
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

    public void setCommonObjectives(ClientCard[] objectives) {
        COMMON_OBJECTIVES[0] = objectives[0];
        COMMON_OBJECTIVES[1] = objectives[1];
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
}
