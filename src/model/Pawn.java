package model;

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
        ElementTypes.register("pion", 50);
        type = ElementTypes.getType("pion");
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    /**
     * If an animation is attached to this pawn advance it by one step and move the pawn to that position.
     * When all steps are consumed, set animation to null to signal completion.
     *
     * Without this method, pawns would teleport instantly instead of sliding smoothly across the board.
     */
    @Override
    public void update() {
        if (animation != null) {
            AnimationStep step = animation.next();
            if (step != null) {
                // Move the pawn to the coordinates of the current step
                setLocation(step.getInt(0), step.getInt(1));
            } else {
                // No more steps: animation is complete
                animation = null;
            }
        }
    }
}
