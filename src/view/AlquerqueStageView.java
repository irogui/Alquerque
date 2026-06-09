package view;

import boardifier.model.GameStageModel;
import boardifier.view.GameStageView;
import boardifier.view.GridLook;
import boardifier.view.TextLook;
import model.AlquerqueStageModel;
import model.Pawn;
import javafx.scene.paint.Color;

/**
 * Defines the visual layout of the Alquerque stage in JavaFX.
 */
public class AlquerqueStageView extends GameStageView {

    public AlquerqueStageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);

        // Game window size in pixels
        width  = 700;
        height = 600;
    }

    @Override
    public void createLooks() {
        AlquerqueStageModel model = (AlquerqueStageModel) gameStageModel;


        // The parameter 80 is the size of each cell in pixels (80x80 px).
        addLook(new GridLook(80, 80, model.getBoard(), 1, 1, Color.BLACK));

        addLook(new TextLook(20, "Arial", model.getPlayerName()));

        for (Pawn p : model.getWhitePawns()) {
            addLook(new PawnLook(p));
        }
        for (Pawn p : model.getBlackPawns()) {
            addLook(new PawnLook(p));
        }
    }
}
