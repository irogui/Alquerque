package model;

import boardifier.model.GameStageModel;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;

/**
 * This file is responsible for initialising the state of an Alquerque game stage.
 * Creates the 5x5 board, instantiates the 12 white and 12 black pawns, and places them
 * in their standard starting positions. Also adds a text element to display the current player's name.
 *
 *
 * The coordinates are now in pixels instead of characters like in the console version.
 */
public class AlquerqueStageFactory extends StageElementsFactory {

    private AlquerqueStageModel stageModel;

    public AlquerqueStageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (AlquerqueStageModel) gameStageModel;
    }

    @Override
    public void setup() {

        // Coordinates are now in pixels (x=30, y=60) instead of characters.
        // The board is placed 30px from the left edge and 60px from the top, leaving room for the menu bar and the current player text.
        AlquerqueBoard board = new AlquerqueBoard(30, 60, stageModel);
        stageModel.setBoard(board);

        // Create the 12 white pawns
        Pawn[] whitePawns = new Pawn[12];
        for (int i = 0; i < 12; i++) {
            whitePawns[i] = new Pawn(Pawn.PAWN_WHITE, stageModel);
        }
        stageModel.setWhitePawns(whitePawns);

        // Create the 12 black pawns
        Pawn[] blackPawns = new Pawn[12];
        for (int i = 0; i < 12; i++) {
            blackPawns[i] = new Pawn(Pawn.PAWN_BLACK, stageModel);
        }
        stageModel.setBlackPawns(blackPawns);

        // Place pawns on the board
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

        // Coordinates in pixels for the current player text element.
        // Placed at the top left, 30px from the left edge and 40px from the top.
        TextElement playerName = new TextElement(stageModel.getCurrentPlayerName() + " to play", stageModel);
        playerName.setLocation(30, 40);
        stageModel.setPlayerName(playerName);
    }
}
