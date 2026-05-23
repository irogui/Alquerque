package view;

import boardifier.model.GameStageModel;
import boardifier.view.ClassicBoardLook;
import boardifier.view.GameStageView;
import boardifier.view.TextLook;
import model.AlquerqueStageModel;
import model.Pawn;

public class AlquerqueStageView extends GameStageView {

    public AlquerqueStageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);
    }

    @Override
    public void createLooks() {
        AlquerqueStageModel stageModel = (AlquerqueStageModel) gameStageModel;

        // Parameters : height, width, élément, border
        addLook(new ClassicBoardLook(2, 4, stageModel.getBoard(), 1, 1, true));

        // Pawn white's look
        for (Pawn p : stageModel.getWhitePawns()) {
            addLook(new PawnLook(p));
        }

        // Pawn black's look
        for (Pawn p : stageModel.getBlackPawns()) {
            addLook(new PawnLook(p));
        }

        // Text's look
        addLook(new TextLook(stageModel.getPlayerName()));
    }
}