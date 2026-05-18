package view;

import boardifier.model.GameElement;
import boardifier.view.ConsoleColor;
import boardifier.view.ElementLook;
import model.Pion;

public class PionLook extends ElementLook {

    public PionLook(GameElement element) {
        // Place du look je suppose
        super(element, 1, 1);
    }

    @Override
    protected void render() {
        Pion pion = (Pion) element;

        if (pion.getColor() == Pion.PAWN_WHITE) {
            // Fond blanc, texte noir
            shape[0][0] = ConsoleColor.WHITE_BACKGROUND
                    + ConsoleColor.BLACK
                    + "●"
                    + ConsoleColor.RESET;
        }
        else {
            // Fond noir, texte blanc
            shape[0][0] = ConsoleColor.BLACK_BACKGROUND
                    + ConsoleColor.WHITE
                    + "●"
                    + ConsoleColor.RESET;
        }
    }
}