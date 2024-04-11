package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.Controller;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientController extends Controller {

    public void throwException(Exception e) throws Exception{
        throw e;
    }

    public void setLobbies(Map<UUID, GameLobby> lobbies){

    }

    public void updateLobby(UUID lobbyUUID, GameLobby lobby){
        //se la lobby ricevuta ha 0 giocatori la rimuoviamo dalla mappa
    }

    public void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide,
                          List<GenericPair<Integer, Integer>> openCorners, Map<Resource, Integer> ownedResources){
    }

    public void receiveCard(int ID){

    }

    public void replaceCard(int ID, String deck, int position){

    }

    public void toggleActive(String playerNickname){

    }

    public void addChatMessage(String chatMessage){

    }
}
