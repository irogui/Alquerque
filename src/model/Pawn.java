package model;

import boardifier.control.Logger;
import boardifier.model.animation.Animation;
import boardifier.model.animation.AnimationStep;
import boardifier.model.ElementTypes;
import boardifier.model.GameElement;
import boardifier.model.GameStageModel;

/**
 * A basic pawn element with a single fixed attribute: its color.
 *
 * CHANGES FROM CONSOLE VERSION:
 * - Added update() to handle movement animations frame by frame.
 */
public class Pawn extends GameElement {

    public static final int PAWN_WHITE = 0;
    public static final int PAWN_BLACK = 1;

    private int color;

    public Pawn(int color, GameStageModel gameStageModel) {
        super(gameStageModel);
        ElementTypes.register("pawn", 50);
        type = ElementTypes.getType("pawn");
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void update() {
        // if must be animated, move the pawn
        if (animation != null) {
            AnimationStep step = animation.next();
            if (step == null) {
                animation = null;
            }
            else if (step == Animation.NOPStep) {
                Logger.debug("nothing to do", this);
            }
            else {
                Logger.debug("move animation", this);
                setLocation(step.getInt(0), step.getInt(1));
            }
        }
    }
}
