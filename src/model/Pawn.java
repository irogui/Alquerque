package model;


import boardifier.model.ElementTypes;
import boardifier.model.GameElement;
import boardifier.model.GameStageModel;
import boardifier.model.animation.AnimationStep;

/**
 * A basic pawn element, with only one fixed parameter : color
 * There are no setters because the state of a pawn is fixed.
 */

public class Pawn extends GameElement {

    // Two colors of pawns
    public static final int PAWN_WHITE = 0;
    public static final int PAWN_BLACK = 1;

    private int color;

    @Override
    public void update() {
        // if must be animated, move the pawn
        if (animation != null) {
            AnimationStep step = animation.next();
            if (step != null) {
                setLocation(step.getInt(0), step.getInt(1));
            }
            else {
                animation = null;
            }
        }
    }

    public Pawn(int color, GameStageModel gameStageModel) {
        super(gameStageModel);

        // register a new type of element for the pawns
        ElementTypes.register("pion", 50);
        type = ElementTypes.getType("pion");

        // initialize attribute color
        this.color = color;
    }

    public int getColor() {
        return color;
    }

}