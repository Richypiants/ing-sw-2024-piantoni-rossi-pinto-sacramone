package it.polimi.ingsw.gc12.Client.ClientModel;

import it.polimi.ingsw.gc12.Utilities.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientGame extends ClientLobby{

    private final ClientPlayer MYSELF;
    private final List<ClientPlayer> OTHER_PLAYERS; //FIXME: conflict with ClientLobby
    /**
     * The cards in this player's hand
     */
    private final ArrayList<ClientCard> OWN_HAND;
    /**
     * The secret Objective Card chosen by this player
     */
    private ClientCard ownObjective;
    private int currentRound;
    private final ClientCard[] PLACED_RESOURCE_CARDS;
    private final ClientCard[] PLACED_GOLD_CARDS;
    private final ClientCard[] COMMON_OBJECTIVES;
    //TODO: costruire scoreboarde

    public ClientGame(Color color, ClientLobby lobby){
        super(lobby);
        this.MYSELF = new ClientPlayer(color);
        this.OTHER_PLAYERS = Collections.unmodifiableList(lobby.otherPlayers);
        this.OWN_HAND = new ArrayList<>();
        this.ownObjective = null;
        this.currentRound = 0;
        this.PLACED_RESOURCE_CARDS = new ClientCard[2];
        this.PLACED_GOLD_CARDS = new ClientCard[2];
        this.COMMON_OBJECTIVES = new ClientCard[2];
    }
}
