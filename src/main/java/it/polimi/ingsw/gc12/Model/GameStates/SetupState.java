package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;
import static it.polimi.ingsw.gc12.Utilities.Commons.varargsToArrayList;

public class SetupState extends GameState {

    public SetupState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    @Override
    public void transition() {
        super.transition();

        //Sending common cards here
        ArrayList<Triplet<Integer, String, Integer>> cardPlacements = new ArrayList<>();
        for (int i = 0; i < GAME.getPlacedResources().length; i++)
            cardPlacements.add(new Triplet<>(GAME.getPlacedResources()[i].ID, "Resource", i));
        for (int i = 0; i < GAME.getPlacedGolds().length; i++)
            cardPlacements.add(new Triplet<>(GAME.getPlacedGolds()[i].ID, "Gold", i));

        //TODO: manage exception
        for (var target : GAME.getPlayers()) {
            try {
                keyReverseLookup(ServerController.players, target::equals)
                        .requestToServer(varargsToArrayList("replaceCard", cardPlacements));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        CardDeck<InitialCard> initialCardsDeck = new CardDeck<>(
                ServerController.cardsList.values().stream()
                        .filter((card -> card instanceof InitialCard))
                        .map((card) -> (InitialCard) card)
                        .toList()
        );
        for (var target : GAME.getPlayers()) {
            InitialCard tmp = initialCardsDeck.draw();
            target.addCardToHand(tmp);

            //TODO: manage exceptions
            try {
                keyReverseLookup(ServerController.players, target::equals)
                        .requestToServer(varargsToArrayList("receiveCard", List.of(tmp.ID)));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        GAME.setState(new ChooseInitialCardsState(GAME));
    }
}
