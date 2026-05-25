package control;

import boardifier.control.Controller;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import model.AlquerqueBoard;
import model.AlquerqueStageModel;
import model.Pawn;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlquerqueDeciderUnitTest {

    @Test
    public void loseWhenNoMoveAvailableTest() {
        Model model = mock(Model.class);
        Controller controller = mock(Controller.class);
        AlquerqueStageModel stage = mock(AlquerqueStageModel.class);
        AlquerqueBoard board = mock(AlquerqueBoard.class);

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
        verify(model).stopStage();
    }

    @Test
    public void forceCaptureTest() {
        Model model = mock(Model.class);
        Controller controller = mock(Controller.class);
        AlquerqueStageModel stage = mock(AlquerqueStageModel.class);
        AlquerqueBoard board = mock(AlquerqueBoard.class);

        Pawn pawn = mock(Pawn.class);
        Pawn capturedPawn = mock(Pawn.class);

        when(model.getGameStage()).thenReturn(stage);
        when(model.getIdPlayer()).thenReturn(0);

        when(stage.getBoard()).thenReturn(board);
        when(stage.getWhitePawns()).thenReturn(new Pawn[]{pawn});

        when(pawn.isVisible()).thenReturn(true);
        when(board.getElementCell(pawn)).thenReturn(new int[]{2, 0});

        when(board.getCaptures(2, 0, Pawn.PAWN_WHITE))
                .thenReturn(List.of(new Point(0, 4)));

        when(board.getElement(2, 0)).thenReturn(pawn);
        when(board.getElement(3, 0)).thenReturn(capturedPawn);

        AlquerqueDecider decider = new AlquerqueDecider(model, controller, AlquerqueDecider.MODE_RANDOM);

        ActionList result = decider.decide();

        assertNotNull(result);
        assertEquals(new Point(0, 4), decider.getLastCaptureDestination());

        verify(board).getCaptures(2, 0, Pawn.PAWN_WHITE);
        verify(board).getElement(2, 0);
        verify(board).getElement(3, 0);
    }

    @Test
    public void onlySimpleMovesTest() {
        Model model = mock(Model.class);
        Controller controller = mock(Controller.class);
        AlquerqueStageModel stage = mock(AlquerqueStageModel.class);
        AlquerqueBoard board = mock(AlquerqueBoard.class);

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

        AlquerqueDecider decider = new AlquerqueDecider(model, controller, AlquerqueDecider.MODE_RANDOM
        );

        ActionList result = decider.decide();

        assertNotNull(result);
        assertNull(decider.getLastCaptureDestination());

        verify(board).getSimpleMoves(2, 2);
        verify(board).getElement(2, 2);
        verify(model, never()).stopStage();
    }
}
