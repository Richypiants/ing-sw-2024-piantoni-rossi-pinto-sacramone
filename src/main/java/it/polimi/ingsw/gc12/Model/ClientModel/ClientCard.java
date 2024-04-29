package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.Map;

public class ClientCard {
    //TODO: dovrà mergearsi con Card

    /**
     * A unique card identifier to facilitate the card's retrieval
     */
    public final int ID;
    /**
     * The front image for this card
     */
    public final String FRONT_SPRITE;
    /**
     * The back image for this card
     */
    public final String BACK_SPRITE;

    public final Map<Side, ArrayList<ArrayList<Triplet<String, Integer[], Integer>>>> TUI_SPRITES;

    public ClientCard(int id, String front, String back,
                      Map<Side, ArrayList< ArrayList<Triplet<String, Integer[], Integer>>>> tuiSprites) {
        this.ID = id;
        this.FRONT_SPRITE = front;
        this.BACK_SPRITE = back;
        this.TUI_SPRITES = tuiSprites;
    }

    public Ansi standardAnsi(Side side) {
        Ansi sprite = Ansi.ansi();
        for (var line : TUI_SPRITES.get(side)) {
            for (var triplet : line) {
                if (triplet.getY()[0] != -1)
                    sprite = sprite.fg(triplet.getY()[0]);
                if (triplet.getY()[1] != 1)
                    sprite = sprite.bg(triplet.getY()[1]);

                for (int i = 0; i < triplet.getZ(); i++)
                    sprite.a(triplet.getX());

                sprite = sprite.reset();
            }
            sprite.cursorMove(-13, 1);
        }

        return sprite;
    }

    public Ansi upscaledAnsi(Side side) {
        Ansi sprite = Ansi.ansi();
        Ansi[] tmp = new Ansi[3];
        for (var line : TUI_SPRITES.get(side)) {
            for (int i = 0; i < 2; i++)
                tmp[i] = Ansi.ansi();

            for (var triplet : line) {
                for (int i = 0; i < 2; i++) {
                    if (triplet.getY()[0] != -1)
                        tmp[i] = tmp[i].fg(triplet.getY()[0]);
                    if (triplet.getY()[1] != 1)
                        tmp[i] = tmp[i].bg(triplet.getY()[1]);
                }

                for (int i = 0; i < triplet.getZ(); i++)
                    if (triplet.getX().charAt(0) != ' ') {
                        tmp[0].a("   ");
                        tmp[1].a(" " + triplet.getX().charAt(0) + " ");
                        tmp[2].a("   ");
                    } else
                        for (int j = 0; j < 2; j++)
                            tmp[j].a("   ");

                for (int i = 0; i < 2; i++) {
                    tmp[i].reset();
                }
            }
            sprite.a(tmp[0]).cursorMove(-13, 1).a(tmp[1]).cursorMove(-13, 1).a(tmp[2]).cursorMove(-13, 1);
        }

        return sprite;
    }
}
