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

        // Creation of the board
        AlquerqueBoard board = new AlquerqueBoard(0, 0, model);
        stageModel.setBoard(board);

        // Create the 12 white pawns
        Pawn[] whitePawns = new Pawn[12];
        for (int i = 0; i < 12; i++) {
            whitePawns[i] = new Pawn(Pawn.PAWN_WHITE, model);
        }
        stageModel.setWhitePawns(whitePawns);

        // Create the 12 black pawns
        Pawn[] blackPawns = new Pawn[12];
        for (int i = 0; i < 12; i++) {
            blackPawns[i] = new Pawn(Pawn.PAWN_BLACK, model);
        }
        stageModel.setBlackPawns(blackPawns);

        // placements on the board
        int[][] whitePos = {
                                     {2,3}, {2,4},
                {3,0}, {3,1}, {3,2}, {3,3}, {3,4},
                {4,0}, {4,1}, {4,2}, {4,3}, {4,4}
        };
        for (int i = 0; i < 12; i++) {
            board.addElement(whitePawns[i], whitePos[i][0], whitePos[i][1]);
        }

        int[][] blackPos = {
                {0,0}, {0,1}, {0,2}, {0,3}, {0,4},
                {1,0}, {1,1}, {1,2}, {1,3}, {1,4},
                {2,0}, {2,1}
        };
        for (int i = 0; i < 12; i++) {
            board.addElement(blackPawns[i], blackPos[i][0], blackPos[i][1]);
        }


        TextElement playerName = new TextElement(model.getCurrentPlayerName() + " to play", model);
        playerName.setLocation(0, 12);
        stageModel.setPlayerName(playerName);
    }
}