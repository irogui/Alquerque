package view;

import boardifier.model.GameStageModel;
import boardifier.view.GameStageView;
import boardifier.view.TextLook;
import model.AlquerqueStageModel;


public class AlquerqueStageView extends GameStageView {

    public AlquerqueStageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);
    }

    @Override
    public void createLooks() {
        AlquerqueStageModel model = (AlquerqueStageModel) gameStageModel;

        addLook(new AlquerqueBoardLook(700, model.getBoard()));

        for (int i = 0; i < 12; i++) {
            addLook(new PawnLook(40, model.getWhitePawns()[i]));
            addLook(new PawnLook(40, model.getBlackPawns()[i]));
        }

        addLook(new TextLook(20, "0x000000", model.getPlayerName()));
        addLook(new TextLook(20, "0x000000", model.getTurnCount()));
        addLook(new TextLook(20, "0x000000", model.getWhitePawnsText()));
        addLook(new TextLook(20, "0x000000", model.getBlackPawnsText()));
    }
}