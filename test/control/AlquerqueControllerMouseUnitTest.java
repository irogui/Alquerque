package control;

import boardifier.control.Controller;
import boardifier.model.Coord2D;
import boardifier.model.Model;
import boardifier.view.View;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.AlquerqueBoard;
import model.AlquerqueStageModel;
import model.Pawn;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class AlquerqueControllerMouseUnitTest {

    @Test
    void clickOnOwnPawnSelectsItTest() {
        Model model = mock(Model.class);
        View view = mock(View.class);
        Controller control = mock(Controller.class);
        Pane rootPane = new Pane();

        AlquerqueStageModel stage = mock(AlquerqueStageModel.class);
        AlquerqueBoard board = mock(AlquerqueBoard.class);
        Pawn pawn = new Pawn(Pawn.PAWN_WHITE, stage);

        MouseEvent event = new MouseEvent(
                MouseEvent.MOUSE_CLICKED,
                100.0,
                100.0,
                100.0,
                100.0,
                MouseButton.PRIMARY,
                1,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                null
        );

        when(view.getRootPane()).thenReturn(rootPane);

        when(model.isCaptureMouseEvent()).thenReturn(true);
        when(model.getGameStage()).thenReturn(stage);
        when(model.getIdPlayer()).thenReturn(Pawn.PAWN_WHITE);

        when(stage.getBoard()).thenReturn(board);
        when(stage.getState()).thenReturn(AlquerqueStageModel.STATE_SELECTPAWN);

        when(control.elementsAt(any(Coord2D.class))).thenReturn(List.of(pawn));

        when(board.getElementCell(pawn)).thenReturn(new int[]{2, 2});

        AlquerqueControllerMouse mouseController = new AlquerqueControllerMouse(model, view, control);

        mouseController.handle(event);

        verify(stage).setState(AlquerqueStageModel.STATE_SELECTDEST);
        verify(board).getElementCell(pawn);
    }

    @Test
    void doNothingWhenMouseCaptureDisabled() {
        Model model = mock(Model.class);
        View view = mock(View.class);
        Controller control = mock(Controller.class);
        MouseEvent event = mock(MouseEvent.class);
        Pane rootPane = new Pane();

        when(view.getRootPane()).thenReturn(rootPane);
        when(model.isCaptureMouseEvent()).thenReturn(false);

        AlquerqueControllerMouse mouseController = new AlquerqueControllerMouse(model, view, control);

        mouseController.handle(event);
    }

    @Test
    void clickOnOpponentPawnDoesNotSelectItTest() {
        Model model = mock(Model.class);
        View view = mock(View.class);
        Controller control = mock(Controller.class);

        AlquerqueStageModel stage = mock(AlquerqueStageModel.class);
        AlquerqueBoard board = mock(AlquerqueBoard.class);
        Pane rootPane = new Pane();
        Pawn opponentPawn = new Pawn(Pawn.PAWN_BLACK, stage);

        MouseEvent event = new MouseEvent(
                MouseEvent.MOUSE_CLICKED,
                100.0,
                100.0,
                100.0,
                100.0,
                MouseButton.PRIMARY,
                1,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                null
        );

        when(view.getRootPane()).thenReturn(rootPane);

        when(model.isCaptureMouseEvent()).thenReturn(true);
        when(model.getGameStage()).thenReturn(stage);
        when(model.getIdPlayer()).thenReturn(Pawn.PAWN_WHITE);

        when(stage.getBoard()).thenReturn(board);
        when(stage.getState()).thenReturn(AlquerqueStageModel.STATE_SELECTPAWN);

        when(control.elementsAt(any(Coord2D.class))).thenReturn(List.of(opponentPawn));

        AlquerqueControllerMouse mouseController = new AlquerqueControllerMouse(model, view, control);

        mouseController.handle(event);

        verify(stage, never()).setState(AlquerqueStageModel.STATE_SELECTDEST);
        verify(board, never()).getElementCell(opponentPawn);
    }
}