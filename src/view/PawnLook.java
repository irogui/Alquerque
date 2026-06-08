package view;

import boardifier.model.GameElement;
import boardifier.view.ConsoleColor;
import boardifier.view.ElementLook;
import model.Pawn;

public class PawnLook extends ElementLook {

    public PawnLook(GameElement element) {
        // Place du pion je suppose
        super(element, 1, 1);
    }

    @Override
    protected void render() {
        Pawn pawn = (Pawn) element;

        if (pawn.getColor() == Pawn.PAWN_WHITE) {
            shape[0][0] = ConsoleColor.WHITE_BACKGROUND + ConsoleColor.WHITE + "@" + ConsoleColor.RESET;
        }
        else {
            shape[0][0] = ConsoleColor.BLACK_BACKGROUND + ConsoleColor.BLACK + "@" + ConsoleColor.RESET;
        }
    }


}