package control;

import boardifier.control.Controller;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import model.AlquerqueBoard;
import model.AlquerqueStageModel;
import model.Pawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlquerqueDeciderUnitTest {

    private Model model;
    private Controller controller;
    private AlquerqueStageModel stage;
    private AlquerqueBoard board;

    @BeforeEach
    public void setUp() {
        model = mock(Model.class);
        controller = mock(Controller.class);
        stage = mock(AlquerqueStageModel.class);
        board = mock(AlquerqueBoard.class);

        when(model.getGameStage()).thenReturn(stage);
        when(stage.getBoard()).thenReturn(board);
    }

    @Test
    public void blackLoseWhenNoMoveAvailableTest() {
        Pawn pawn = mock(Pawn.class);

        when(model.getGameStage()).thenReturn(stage);
        when(model.getIdPlayer()).thenReturn(0);

        when(stage.getBoard()).thenReturn(board);
        when(stage.getWhitePawns()).thenReturn(new Pawn[]{pawn});

        when(pawn.isVisible()).thenReturn(true);
        when(board.getElementCell(pawn)).thenReturn(new int[]{2, 2});

        when(board.getCaptures(2, 2, Pawn.PAWN_WHITE)).thenReturn(Collections.emptyList());
        when(board.getSimpleMoves(2, 2)).thenReturn(Collections.emptyList());

        AlquerqueDecider decider = new AlquerqueDecider(model, controller, AlquerqueDecider.MODE_RANDOM);

        ActionList result = decider.decide();

        assertNotNull(result);
        verify(model).setIdWinner(1);
        verify(model).stopGame();
    }

    @Test
    public void whiteLoseWhenNoMoveAvailableTest() {
        Pawn pawn = mock(Pawn.class);

        when(model.getIdPlayer()).thenReturn(1);
        when(stage.getBlackPawns()).thenReturn(new Pawn[]{pawn});

        when(pawn.isVisible()).thenReturn(true);
        when(board.getElementCell(pawn)).thenReturn(new int[]{2, 2});

        when(board.getCaptures(2, 2, Pawn.PAWN_BLACK)).thenReturn(Collections.emptyList());
        when(board.getSimpleMoves(2, 2)).thenReturn(Collections.emptyList());

        AlquerqueDecider decider = new AlquerqueDecider(model, controller, AlquerqueDecider.MODE_RANDOM);

        ActionList result = decider.decide();

        assertNotNull(result);

        verify(model).setIdWinner(0);
        verify(model).stopGame();
        verify(board, never()).getElement(anyInt(), anyInt());
    }

    @Test
    public void forceCaptureTest() {
        Pawn pawn = mock(Pawn.class);
        Pawn capturedPawn = mock(Pawn.class);

        when(board.isEmptyAt(anyInt(), anyInt())).thenReturn(true);

        when(board.isEmptyAt(2, 0)).thenReturn(false);
        when(board.isEmptyAt(3, 0)).thenReturn(false);

        when(pawn.getColor()).thenReturn(Pawn.PAWN_WHITE);
        when(capturedPawn.getColor()).thenReturn(Pawn.PAWN_BLACK);

        when(model.getIdPlayer()).thenReturn(0);
        when(stage.getWhitePawns()).thenReturn(new Pawn[]{pawn});

        when(pawn.isVisible()).thenReturn(true);
        when(board.getElementCell(pawn)).thenReturn(new int[]{2, 0});

        when(board.getCaptures(2, 0, Pawn.PAWN_WHITE)).thenReturn(List.of(new Point(0, 4)));

        when(board.getElement(2, 0)).thenReturn(pawn);
        when(board.getElement(3, 0)).thenReturn(capturedPawn); // midR=3, midC=0 ✓

        AlquerqueDecider decider = new AlquerqueDecider(model, controller, AlquerqueDecider.MODE_RANDOM);

        ActionList result = decider.decide();

        assertNotNull(result);
        verify(board).getCaptures(2, 0, Pawn.PAWN_WHITE);
    }

    @Test
    public void whiteOnlySimpleMovesTest() {
        Pawn pawn = mock(Pawn.class);

        when(model.getGameStage()).thenReturn(stage);
        when(model.getIdPlayer()).thenReturn(0);

        when(stage.getBoard()).thenReturn(board);
        when(stage.getWhitePawns()).thenReturn(new Pawn[]{pawn});

        when(pawn.isVisible()).thenReturn(true);
        when(board.getElementCell(pawn)).thenReturn(new int[]{2, 2});

        when(board.getCaptures(2, 2, Pawn.PAWN_WHITE)).thenReturn(Collections.emptyList());
        when(board.getSimpleMoves(2, 2)).thenReturn(List.of(new Point(2, 1)));

        when(board.getElement(2, 2)).thenReturn(pawn);

        AlquerqueDecider decider = new AlquerqueDecider(model, controller, AlquerqueDecider.MODE_RANDOM);

        ActionList result = decider.decide();

        assertNotNull(result);

        verify(board).getSimpleMoves(2, 2);
        verify(board).getElement(2, 2);
        verify(model, never()).stopStage();
    }

    @Test
    public void blackOnlySimpleMovesTest() {
        Pawn blackPawn = mock(Pawn.class);

        when(model.getIdPlayer()).thenReturn(1);
        when(stage.getBlackPawns()).thenReturn(new Pawn[]{blackPawn});

        when(blackPawn.isVisible()).thenReturn(true);
        when(board.getElementCell(blackPawn)).thenReturn(new int[]{2, 2});

        when(board.getCaptures(2, 2, Pawn.PAWN_BLACK)).thenReturn(Collections.emptyList());
        when(board.getSimpleMoves(2, 2)).thenReturn(List.of(new Point(2, 1)));
        when(board.getElement(2, 2)).thenReturn(blackPawn);

        AlquerqueDecider decider = new AlquerqueDecider(model, controller, AlquerqueDecider.MODE_RANDOM);

        ActionList result = decider.decide();

        assertNotNull(result);

        verify(stage).getBlackPawns();
        verify(stage, never()).getWhitePawns();
        verify(board).getCaptures(2, 2, Pawn.PAWN_BLACK);
        verify(board).getSimpleMoves(2, 2);
    }

    @Test
    public void checkInvisibleIsIgnoredTest() {
        Pawn invisiblePawn = mock(Pawn.class);
        Pawn visiblePawn = mock(Pawn.class);

        when(model.getIdPlayer()).thenReturn(0);
        when(stage.getWhitePawns()).thenReturn(new Pawn[]{invisiblePawn, visiblePawn});

        when(invisiblePawn.isVisible()).thenReturn(false);
        when(visiblePawn.isVisible()).thenReturn(true);

        when(board.getElementCell(visiblePawn)).thenReturn(new int[]{2, 2});
        when(board.getCaptures(2, 2, Pawn.PAWN_WHITE)).thenReturn(Collections.emptyList());
        when(board.getSimpleMoves(2, 2)).thenReturn(List.of(new Point(2, 1)));
        when(board.getElement(2, 2)).thenReturn(visiblePawn);

        AlquerqueDecider decider = new AlquerqueDecider(model, controller, AlquerqueDecider.MODE_RANDOM);

        ActionList result = decider.decide();

        assertNotNull(result);

        verify(board, never()).getElementCell(invisiblePawn);
        verify(board).getElementCell(visiblePawn);
    }
}
