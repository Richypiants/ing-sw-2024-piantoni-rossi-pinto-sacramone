package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.GameTransitionCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.AlreadySetCardException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.util.ArrayList;
import java.util.Map;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class ChooseObjectiveCardsState extends GameState {

    private final Map<InGamePlayer, ArrayList<ObjectiveCard>> objectivesMap;

    public ChooseObjectiveCardsState(Game thisGame, Map<InGamePlayer, ArrayList<ObjectiveCard>> map) {
        super(thisGame, 0, -1);
        this.objectivesMap = map;
    }

    @Override
    public void pickObjective(InGamePlayer target, ObjectiveCard objective)
            throws CardNotInHandException, AlreadySetCardException {
        if(!objectivesMap.get(target).contains(objective))
            throw new CardNotInHandException();

        if (target.getSecretObjective() == null)
            target.setSecretObjective(objective);
        else
            throw new AlreadySetCardException();

        //FIXME: dopo timeout e disconnessione: eseguo un'azione random per i player disconnessi
        if(GAME.getPlayers().stream()
                .map((player) -> player.getSecretObjective() != null)
                .reduce(true, (a, b) -> a && b))
            transition();
    }

    @Override
    public void transition() {
        super.transition();

        System.out.println("[SERVER]: Sending GameTransitionCommand to clients in "+ GAME.toString());
        for (var targetPlayer : GAME.getPlayers()) {
            //TODO: manage exceptions
            try {
                VirtualClient target = keyReverseLookup(ServerController.getInstance().players, targetPlayer::equals);

                target.requestToClient(new GameTransitionCommand());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //TODO: ci starebbe segnalare che tutti sono pronti e il gioco inizia? Tipo far comparire "Gioco iniziato"
        // oppure "Turno 1"

        GAME.setState(new PlayerTurnPlayState(GAME, 0, -1));
    }
}
