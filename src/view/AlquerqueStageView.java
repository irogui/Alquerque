package view;

import boardifier.model.GameStageModel;
import boardifier.view.ClassicBoardLook;
import boardifier.view.GameStageView;
import boardifier.view.TextLook;
import model.AlquerqueStageModel;
import model.Pion;

public class AlquerqueStageView extends GameStageView {

    public AlquerqueStageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);
    }

    @Override
    public void createLooks() {
        AlquerqueStageModel stageModel = (AlquerqueStageModel) gameStageModel;

        // Paramètres : hauteur cellule, largeur cellule, élément, bordure
        addLook(new ClassicBoardLook(1, 3, stageModel.getBoard(), -1, 1, true));

        // Look de chaque pion blanc
        for (Pion p : stageModel.getWhitePawns()) {
            addLook(new PionLook(p));
        }

        // Look de chaque pion noir
        for (Pion p : stageModel.getBlackPawns()) {
            addLook(new PionLook(p));
        }

        // Look du texte joueur courant
        addLook(new TextLook(stageModel.getPlayerName()));
    }
}