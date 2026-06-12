package control;

import boardifier.control.*;
import boardifier.model.*;
import boardifier.model.action.ActionList;
import boardifier.model.animation.AnimationTypes;
import boardifier.view.GridLook;
import boardifier.view.View;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import model.AlquerqueBoard;
import model.AlquerqueStageModel;
import model.Pawn;

import java.util.List;

public class AlquerqueControllerMouse extends ControllerMouse implements EventHandler<MouseEvent> {

    public AlquerqueControllerMouse(Model model, View view, Controller control) {
        super(model, view, control);
    }

    public void handle(MouseEvent event) {
        if (!model.isCaptureMouseEvent()) return;

        Coord2D clic = new Coord2D(event.getSceneX(), event.getSceneY());
        List<GameElement> list = control.elementsAt(clic);

        AlquerqueStageModel stageModel = (AlquerqueStageModel) model.getGameStage();
        AlquerqueBoard board = stageModel.getBoard();

        if (stageModel.getState() == AlquerqueStageModel.STATE_SELECTPAWN) {
            for (GameElement element : list) {
                if (element.getType() == ElementTypes.getType("pawn")) {
                    Pawn pawn = (Pawn) element;
                    if (pawn.getColor() == model.getIdPlayer()) {
                        if (board.getElementCell(pawn) != null) {
                            element.toggleSelected();
                            stageModel.setState(AlquerqueStageModel.STATE_SELECTDEST);
                            return;
                        }
                    }
                }
            }
        }

        else if (stageModel.getState() == AlquerqueStageModel.STATE_SELECTDEST) {
            for (GameElement element : list) {
                if (element.isSelected()) {
                    element.toggleSelected();
                    stageModel.setState(AlquerqueStageModel.STATE_SELECTPAWN);
                    return;
                }
            }

            boolean boardClicked = false;
            for (GameElement element : list) {
                if (element == board) { boardClicked = true; break; }
            }
            if (!boardClicked) return;

            GameElement pawnElement = model.getSelected().get(0);
            Pawn pawn = (Pawn) pawnElement;

            GridLook lookBoard = (GridLook) control.getElementLook(board);
            int[] dest = lookBoard.getCellFromSceneLocation(clic);
            if (dest == null) return;

            if (!board.canReachOrCaptureCell(dest[0], dest[1])) return;

            int[] src = board.getElementCell(pawn);
            boolean isCapture = (Math.abs(dest[0] - src[0]) == 2 || Math.abs(dest[1] - src[1]) == 2);

            ActionList actions = ActionFactory.generatePutInContainer(control, model, pawnElement,
                    "alquerqueboard", dest[0], dest[1], AnimationTypes.MOVE_LINEARPROP, 10);

            if (isCapture) {
                int midRow = (src[0] + dest[0]) / 2;
                int midCol = (src[1] + dest[1]) / 2;
                Pawn captured = (Pawn) board.getElement(midRow, midCol);
                if (captured != null)
                    actions.addAll(ActionFactory.generateRemoveFromStage(model, captured));
            }

            boolean canChain = isCapture && hasChainCapture(board, dest[0], dest[1], pawn.getColor(), (src[0] + dest[0]) / 2, (src[1] + dest[1]) / 2);

            if (canChain) {
                stageModel.setMovesSinceLastCapture(0);

                actions.setDoEndOfTurn(false);
                stageModel.setChainPawn(pawn);
                stageModel.unselectAll();
                stageModel.setState(AlquerqueStageModel.STATE_CHAINCAPTURE);

                final int finalRow = dest[0], finalCol = dest[1];
                new AlquerqueChainAction(model, control, actions, () -> {
                    board.resetReachableCells(false);
                    board.resetCaptureCells();
                    for (java.awt.Point p : board.getCaptures(finalRow, finalCol, pawn.getColor()))
                        board.getCaptureCells()[p.y][p.x] = true;
                    board.addChangeFaceEvent();
                }).start();
            }
            else if (isCapture) {
                actions.setDoEndOfTurn(true);
                stageModel.setChainPawn(null);
                stageModel.unselectAll();
                stageModel.setState(AlquerqueStageModel.STATE_SELECTPAWN);
                stageModel.setMovesSinceLastCapture(0);
                new ActionPlayer(model, control, actions).start();
            }
            else {
                actions.setDoEndOfTurn(true);
                stageModel.setChainPawn(null);
                stageModel.unselectAll();
                stageModel.setState(AlquerqueStageModel.STATE_SELECTPAWN);
                stageModel.setMovesSinceLastCapture(stageModel.getMovesSinceLastCapture() + 1);
                new ActionPlayer(model, control, actions).start();
            }
        }

        else if (stageModel.getState() == AlquerqueStageModel.STATE_CHAINCAPTURE) {
            boolean boardClicked = false;
            for (GameElement element : list) {
                if (element == board) { boardClicked = true; break; }
            }
            if (!boardClicked) return;

            Pawn pawn = stageModel.getChainPawn();
            int[] src = board.getElementCell(pawn);
            if (src == null) return;

            GridLook lookBoard = (GridLook) control.getElementLook(board);
            int[] clicked = lookBoard.getCellFromSceneLocation(clic);
            if (clicked == null) return;

            // Click on the pawn's own cell → player chooses to end the chain
            if (clicked[0] == src[0] && clicked[1] == src[1]) {
                stageModel.setChainPawn(null);
                stageModel.setState(AlquerqueStageModel.STATE_SELECTPAWN);
                board.resetCaptureCells();
                board.resetReachableCells(false);
                board.addChangeFaceEvent();
                Platform.runLater(() -> control.endOfTurn());
                return;
            }

            // Only capture cells are valid during a chain
            if (!board.getCaptureCells()[clicked[0]][clicked[1]]) return;

            int[] dest = clicked;
            int midRow = (src[0] + dest[0]) / 2;
            int midCol = (src[1] + dest[1]) / 2;
            Pawn captured = (Pawn) board.getElement(midRow, midCol);

            ActionList actions = ActionFactory.generatePutInContainer(control, model, pawn,
                    "alquerqueboard", dest[0], dest[1], AnimationTypes.MOVE_LINEARPROP, 10);
            if (captured != null)
                actions.addAll(ActionFactory.generateRemoveFromStage(model, captured));

            boolean canChain = hasChainCapture(board, dest[0], dest[1],
                    pawn.getColor(), midRow, midCol);

            if (canChain) {
                actions.setDoEndOfTurn(false);
                stageModel.setState(AlquerqueStageModel.STATE_CHAINCAPTURE);
                stageModel.setMovesSinceLastCapture(0);

                final int finalRow = dest[0], finalCol = dest[1];
                new AlquerqueChainAction(model, control, actions, () -> {
                    board.resetReachableCells(false);
                    board.resetCaptureCells();
                    for (java.awt.Point p : board.getCaptures(finalRow, finalCol, pawn.getColor()))
                        board.getCaptureCells()[p.y][p.x] = true;
                    board.addChangeFaceEvent();
                }).start();
            }
            else {
                actions.setDoEndOfTurn(true);
                stageModel.setChainPawn(null);
                stageModel.setState(AlquerqueStageModel.STATE_SELECTPAWN);
                stageModel.setMovesSinceLastCapture(0);
                board.resetCaptureCells();
                board.resetReachableCells(false);
                new ActionPlayer(model, control, actions).start();
            }
        }
    }

    private boolean hasChainCapture(AlquerqueBoard board, int row, int col,
                                    int color, int capturedRow, int capturedCol) {
        int[][] dirs = ((row + col) % 2 == 0)
                ? new int[][]{{-1,0},{1,0},{0,1},{0,-1},{-1,1},{-1,-1},{1,1},{1,-1}}
                : new int[][]{{-1,0},{1,0},{0,1},{0,-1}};

        for (int[] d : dirs) {
            int mr = row + d[0], mc = col + d[1];
            int dr = row + d[0]*2, dc = col + d[1]*2;
            if (dr < 0 || dr > 4 || dc < 0 || dc > 4) continue;
            if (mr == capturedRow && mc == capturedCol) continue;
            if (!board.isEmptyAt(mr, mc)) {
                Pawn mid = (Pawn) board.getElement(mr, mc);
                if (mid.getColor() != color && board.isEmptyAt(dr, dc))
                    return true;
            }
        }
        return false;
    }
}