package model;

import boardifier.model.ElementTypes;
import boardifier.model.GameElement;
import boardifier.model.GameStageModel;

public class Pion extends GameElement {

    // Constantes pour les deux couleurs
    public static final int PAWN_WHITE = 0;
    public static final int PAWN_BLACK = 1;

    private int color;

    public Pion(int color, GameStageModel gameStageModel) {
        super(gameStageModel);                    // obligatoire
        ElementTypes.register("pion", 50);        // enregistre le type une fois
        type = ElementTypes.getType("pion");      // this.type = identifiant numérique
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}