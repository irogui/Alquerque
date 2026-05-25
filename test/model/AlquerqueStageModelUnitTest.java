package model;

import boardifier.model.Model;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlquerqueStageModelUnitTest {

    @Test
    void allGettersTest() {
        Model model = mock(Model.class);

        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        assertNull(stage.getBoard());
        assertNull(stage.getWhitePawns());
        assertNull(stage.getBlackPawns());
        assertNull(stage.getPlayerName());

        assertEquals(12, stage.getWhitePawnsLeft());
        assertEquals(12, stage.getBlackPawnsLeft());
        assertEquals(1, stage.getCount());
    }

    @Test
    void allSettersTest() {
        /*Setting up the board.*/
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        AlquerqueBoard board = mock(AlquerqueBoard.class);

        stage.setBoard(board);

        assertSame(board, stage.getBoard());

        /*Setting up some of the white pawns.*/
        Pawn Wpawn1 = mock(Pawn.class);
        Pawn Wpawn2 = mock(Pawn.class);
        Pawn[] Wpawns = { Wpawn1, Wpawn2};

        stage.setWhitePawns(Wpawns);

        assertSame(Wpawns, stage.getWhitePawns());

        /*Setting up some of the black pawns.*/
        Pawn Bpawn1 = mock(Pawn.class);
        Pawn Bpawn2 = mock(Pawn.class);
        Pawn[] Bpawns = {Bpawn1, Bpawn2};

        stage.setBlackPawns(Bpawns);

        assertSame(Bpawns, stage.getBlackPawns());

        /*Setting up the player's name.*/
        TextElement textElement = mock(TextElement.class);

        stage.setPlayerName(textElement);

        assertSame(textElement, stage.getPlayerName());
    }

    @Test
    void countTest() {
        /*Testing count incrementation.*/
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        assertEquals(1, stage.getCount());

        stage.incrementCount();

        assertEquals(2, stage.getCount());

        stage.incrementCount();

        assertEquals(3, stage.getCount());
    }

    @Test
    void getDefaultElementFactoryTest() {
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        StageElementsFactory factory = stage.getDefaultElementFactory();
        
        assertInstanceOf(AlquerqueStageFactory.class, factory);
    }

    @Test
    void removeInvisibleWhitePawnTest() {
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        AlquerqueBoard board = mock(AlquerqueBoard.class);
        stage.setBoard(board);

        Pawn whitePawn = mock(Pawn.class);
        when(whitePawn.isVisible()).thenReturn(false);
        when(whitePawn.getColor()).thenReturn(Pawn.PAWN_WHITE);

        stage.removedFromContainer(whitePawn, board, 0, 0);

        assertEquals(11, stage.getWhitePawnsLeft());
        assertEquals(12, stage.getBlackPawnsLeft());
        verify(model, never()).stopStage();
    }

    @Test
    void removeInvisibleBlackPawnTest() {
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        AlquerqueBoard board = mock(AlquerqueBoard.class);
        stage.setBoard(board);

        Pawn blackPawn = mock(Pawn.class);
        when(blackPawn.isVisible()).thenReturn(false);
        when(blackPawn.getColor()).thenReturn(Pawn.PAWN_BLACK);

        stage.removedFromContainer(blackPawn, board, 0, 0);

        assertEquals(12, stage.getWhitePawnsLeft());
        assertEquals(11, stage.getBlackPawnsLeft());
        verify(model, never()).stopStage();
    }

    @Test
    void removeVisiblePawnFailTest() {
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        AlquerqueBoard board = mock(AlquerqueBoard.class);
        stage.setBoard(board);

        Pawn pawn = mock(Pawn.class);
        when(pawn.isVisible()).thenReturn(true);
        when(pawn.getColor()).thenReturn(Pawn.PAWN_WHITE);

        stage.removedFromContainer(pawn, board, 0, 0);

        assertEquals(12, stage.getWhitePawnsLeft());
        assertEquals(12, stage.getBlackPawnsLeft());
        verify(pawn, never()).getColor();
        verify(model, never()).stopStage();
    }

    @Test
    void captureAllWhitePawnsBlackWinsTest() {
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        AlquerqueBoard board = mock(AlquerqueBoard.class);
        stage.setBoard(board);

        Pawn whitePawn = mock(Pawn.class);
        when(whitePawn.isVisible()).thenReturn(false);
        when(whitePawn.getColor()).thenReturn(Pawn.PAWN_WHITE);

        for (int i = 0; i < 12; i++) {
            stage.removedFromContainer(whitePawn, board, 0, 0);
        }

        assertEquals(0, stage.getWhitePawnsLeft());
        verify(model).setIdWinner(1);
        verify(model).stopStage();
    }

    @Test
    void captureAllBlackPawnsWhiteWinsTest() {
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        AlquerqueBoard board = mock(AlquerqueBoard.class);
        stage.setBoard(board);

        Pawn blackPawn = mock(Pawn.class);
        when(blackPawn.isVisible()).thenReturn(false);
        when(blackPawn.getColor()).thenReturn(Pawn.PAWN_BLACK);

        for (int i = 0; i < 12; i++) {
            stage.removedFromContainer(blackPawn, board, 0, 0);
        }

        assertEquals(0, stage.getBlackPawnsLeft());
        verify(model).setIdWinner(0);
        verify(model).stopStage();
    }
}
