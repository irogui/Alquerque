package model;

import boardifier.model.GameStageModel;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;

public class AlquerqueStageFactory extends StageElementsFactory {

    private AlquerqueStageModel stageModel;

    public AlquerqueStageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (AlquerqueStageModel) gameStageModel;
    }

    @Override
    public void setup() {

        // Création du plateau en position (0,0)
        AlquerqueBoard board = new AlquerqueBoard(0, 0, model);
        stageModel.setBoard(board);

        // Créer les 12 pions blancs
        Pion[] whitePawns = new Pion[12];
        for (int i = 0; i < 12; i++) {
            whitePawns[i] = new Pion(Pion.PAWN_WHITE, model);
        }
        stageModel.setWhitePawns(whitePawns);

        // Créer les 12 pions noirs
        Pion[] blackPawns = new Pion[12];
        for (int i = 0; i < 12; i++) {
            blackPawns[i] = new Pion(Pion.PAWN_BLACK, model);
        }
        stageModel.setBlackPawns(blackPawns);

        // Placement des pions sur le plateau
        int[][] whitePos = {
                {0,0}, {0,1}, {0,2}, {0,3}, {0,4},
                {1,0}, {1,1}, {1,2}, {1,3}, {1,4},
                {2,0}, {2,1}
        };
        for (int i = 0; i < 12; i++) {
            board.addElement(whitePawns[i], whitePos[i][0], whitePos[i][1]);
        }

        int[][] blackPos = {
                {2,3}, {2,4},
                {3,0}, {3,1}, {3,2}, {3,3}, {3,4},
                {4,0}, {4,1}, {4,2}, {4,3}, {4,4}
        };
        for (int i = 0; i < 12; i++) {
            board.addElement(blackPawns[i], blackPos[i][0], blackPos[i][1]);
        }

        // Créer le texte affichant le joueur courant
        TextElement playerName = new TextElement(
                model.getCurrentPlayerName(), model
        );
        playerName.setLocation(0, 12); // en dessous du plateau
        stageModel.setPlayerName(playerName);
    }
}