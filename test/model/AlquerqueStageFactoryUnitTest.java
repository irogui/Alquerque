package model;

import boardifier.model.Model;
import boardifier.model.TextElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlquerqueStageFactoryUnitTest {

    @Test
    void createBoardTest() {
        Model model = mock(Model.class);
        when(model.getCurrentPlayerName()).thenReturn("Player 1");

        AlquerqueStageModel stageModel = new AlquerqueStageModel("test-stage", model);
        AlquerqueStageFactory factory = new AlquerqueStageFactory(stageModel);

        factory.setup();

        assertNotNull(stageModel.getBoard());
        assertInstanceOf(AlquerqueBoard.class, stageModel.getBoard());
    }

    @Test
    void createWhitePawnsAndPlaceTest() {
        /*Creating the white pawns.*/
        Model model = mock(Model.class);
        when(model.getCurrentPlayerName()).thenReturn("Player 1");

        AlquerqueStageModel stageModel = new AlquerqueStageModel("test-stage", model);
        AlquerqueStageFactory factory = new AlquerqueStageFactory(stageModel);

        factory.setup();

        Pawn[] whitePawns = stageModel.getWhitePawns();

        assertNotNull(whitePawns);
        assertEquals(12, whitePawns.length);

        for (Pawn pawn : whitePawns) {
            assertNotNull(pawn);
            assertEquals(Pawn.PAWN_WHITE, pawn.getColor());
        }

        /*Checking if the white pawns are placed on the right space on the board.*/
        AlquerqueBoard board = stageModel.getBoard();

        int[][] whitePos = {{2, 3}, {2, 4}, {3, 0}, {3, 1}, {3, 2}, {3, 3}, {3, 4}, {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4}};

        for (int i = 0; i < whitePos.length; i++) {
            assertSame(whitePawns[i], board.getElement(whitePos[i][0], whitePos[i][1]));
        }
    }

    @Test
    void createBlackPawnsAndPlaceTest() {
        /*Creating the black pawns.*/
        Model model = mock(Model.class);
        when(model.getCurrentPlayerName()).thenReturn("Player 1");

        AlquerqueStageModel stageModel = new AlquerqueStageModel("test-stage", model);
        AlquerqueStageFactory factory = new AlquerqueStageFactory(stageModel);

        factory.setup();

        Pawn[] blackPawns = stageModel.getBlackPawns();

        assertNotNull(blackPawns);
        assertEquals(12, blackPawns.length);

        for (Pawn pawn : blackPawns) {
            assertNotNull(pawn);
            assertEquals(Pawn.PAWN_BLACK, pawn.getColor());
        }

        /*Checking if the black pawns are placed on the right space on the board.*/
        AlquerqueBoard board = stageModel.getBoard();

        int[][] blackPos = {{0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}, {1, 0}, {1, 1}, {1, 2}, {1, 3}, {1, 4}, {2, 0}, {2, 1}};

        for (int i = 0; i < blackPos.length; i++) {
            assertSame(
                    blackPawns[i],
                    board.getElement(blackPos[i][0], blackPos[i][1])
            );
        }
    }

    @Test
    void playerNameTest() {
        Model model = mock(Model.class);
        when(model.getCurrentPlayerName()).thenReturn("Player 1");

        AlquerqueStageModel stageModel = new AlquerqueStageModel("test-stage", model);
        AlquerqueStageFactory factory = new AlquerqueStageFactory(stageModel);

        factory.setup();

        TextElement playerName = stageModel.getPlayerName();

        assertEquals("Player 1 to play", playerName.getText());
    }
}
