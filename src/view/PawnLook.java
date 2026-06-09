package view;

import boardifier.model.GameElement;
import boardifier.view.ElementLook;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import model.Pawn;

/**
 * Visual look for an Alquerque pawn in JavaFX.
 *
 * COMPLETE REWRITE FROM CONSOLE VERSION:
 *
 * Console: the pawn was represented by a colored "@" character in a 2D
 *          String array (shape[0][0] = "...@...").
 *
 * JavaFX:  the pawn is represented by a JavaFX Circle object.
 *          - White pawn: white circle with a dark gray border
 *          - Black pawn: dark circle with a black border
 *
 * NEW: the onSelectionChange() method changes the circle's appearance
 * when the pawn is selected (thick yellow border) or deselected (normal border).
 * There was no visual selection feedback in console mode.
 */


public class PawnLook extends ElementLook {

    @Override
    protected void render() {
        // In JavaFX, rendering is handled automatically by JavaFX through the shapes added with addShape() in the constructor.
        // This method is required by ElementLook but has nothing to do here.
    }

    // Circle radius in pixels
    private static final int RADIUS = 18;

    // Keep a reference to the circle so it can be modified in onSelectionChange
    private final Circle circle;

    public PawnLook(GameElement element) {
        super(element);

        Pawn pawn = (Pawn) element;

        circle = new Circle();
        circle.setRadius(RADIUS);

        // The top-left corner of the bounding box is at (0,0),
        // so the circle center is at (RADIUS, RADIUS)
        circle.setCenterX(RADIUS);
        circle.setCenterY(RADIUS);

        // Fill color based on pawn color
        if (pawn.getColor() == Pawn.PAWN_WHITE) {
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.DARKGRAY);
        } else {
            circle.setFill(Color.web("#2c2c2c"));
            circle.setStroke(Color.BLACK);
        }

        circle.setStrokeWidth(2);
        circle.setStrokeType(StrokeType.INSIDE);

        // IMPORTANT: without addShape(), the circle will never be displayed
        addShape(circle);
    }

    /**
     * Called automatically by boardifier whenever the player selects or deselects this pawn.
     *
     * Changes the circle's border to visually indicate the state:
     * - Selected:   thick yellow border
     * - Deselected: normal border (gray or black)
     */
    @Override
    public void onSelectionChange() {
        Pawn pawn = (Pawn)getElement();

        if (pawn.isSelected()) {
            circle.setStroke(Color.YELLOW);
            circle.setStrokeWidth(4);
        } else {
            if (pawn.getColor() == Pawn.PAWN_WHITE) {
                circle.setStroke(Color.DARKGRAY);
            } else {
                circle.setStroke(Color.BLACK);
            }
            circle.setStrokeWidth(2);
        }
    }
}
