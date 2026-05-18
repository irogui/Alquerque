package control;

import boardifier.control.*;
import boardifier.model.*;
import boardifier.model.action.ActionList;
import model.*;

import java.awt.Point;
import java.util.*;

public class AlquerqueDecider extends Decider {

    private static final Random rng = new Random();

    public AlquerqueDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
        AlquerqueBoard board = stage.getBoard();

        int color = (model.getIdPlayer() == 0) ? Pion.PAWN_WHITE : Pion.PAWN_BLACK;

        Pion[] myPawns = (color == Pion.PAWN_WHITE) ? stage.getWhitePawns() : stage.getBlackPawns();

        // Collecter toutes les captures possibles
        List<int[]> captureOptions = new ArrayList<>();
        for (Pion p : myPawns) {
            if (!p.isVisible()) continue; // Ca fonctionne un break en python mon reuf
            int[] cell = board.getElementCell(p);
            if (cell == null) continue;

            for (Point dst : board.getCaptures(cell[0], cell[1], color)) {
                captureOptions.add(new int[]{cell[0], cell[1], dst.y, dst.x});
            }
        }

        if (!captureOptions.isEmpty()) {
            // Si une ou plusieurs captures possible on en choisit une au pif
            int[] choice = captureOptions.get(rng.nextInt(captureOptions.size()));
            return buildCapture(board, choice[0], choice[1], choice[2], choice[3], color);
        }

        // Sinon, collecter tous les déplacements simples
        List<int[]> moveOptions = new ArrayList<>();
        for (Pion p : myPawns) {
            if (!p.isVisible()) continue;
            int[] cell = board.getElementCell(p);
            if (cell == null) continue;
            for (Point dst : board.getSimpleMoves(cell[0], cell[1])) {
                moveOptions.add(new int[]{cell[0], cell[1], dst.y, dst.x});
            }
        }

        if (moveOptions.isEmpty()) {
            // Aucun coup possible: le joueur perd (bloqué)
            model.setIdWinner(model.getIdPlayer() == 0 ? 1 : 0);
            model.stopStage();
            return new ActionList();
        }

        int[] choice = moveOptions.get(rng.nextInt(moveOptions.size()));
        Pion pion = (Pion) board.getElement(choice[0], choice[1]);
        ActionList actions = ActionFactory.generatePutInContainer(
                model, pion, "alquerqueboard", choice[2], choice[3]);
        actions.setDoEndOfTurn(true);
        return actions;
    }

    private ActionList buildCapture(AlquerqueBoard board,
                                    int srcR, int srcC, int dstR, int dstC, int color) {
        Pion pion = (Pion) board.getElement(srcR, srcC);
        int midR = (srcR + dstR) / 2;
        int midC = (srcC + dstC) / 2;
        Pion captured = (Pion) board.getElement(midR, midC);
        ActionList actions = ActionFactory.generatePutInContainer(
                model, pion, "alquerqueboard", dstR, dstC);
        actions.addAll(ActionFactory.generateRemoveFromStage(model, captured));
        actions.setDoEndOfTurn(true);
        return actions;
    }
}