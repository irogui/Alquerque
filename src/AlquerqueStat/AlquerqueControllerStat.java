package AlquerqueStat;

import boardifier.model.*;
import boardifier.control.*;
import boardifier.view.*;
import control.*;
import model.*;
import boardifier.model.action.ActionList;

import java.awt.Point;
import java.util.List;

/**
 * Contrôleur silencieux pour simulations statistiques.
 * Corrigé pour :
 * - limiter les boucles infinies
 * - enchaîner correctement les captures multiples
 * - ne pas afficher l'UI pendant les tests
 */
public class AlquerqueControllerStat extends Controller {

    private final int modeWhite;
    private final int modeBlack;
    private static final int MAX_TURNS = 500;

    public AlquerqueControllerStat(Model model, View view, int modeWhite, int modeBlack) {
        super(model, view);
        this.modeWhite = modeWhite;
        this.modeBlack = modeBlack;
    }

    @Override
    public void stageLoop() {
        update();
        int turns = 0;

        while (!model.isEndStage() && turns < MAX_TURNS) {
            playTurn();

            // Si une action a déjà terminé la partie, on arrête immédiatement
            if (model.isEndStage()) break;

            endOfTurn();
            turns++;
        }

        // Match nul forcé si trop de tours
        if (!model.isEndStage() && turns >= MAX_TURNS) {
            model.setIdWinner(-1);
        }

        endGame();
    }

    private void playTurn() {
        int currentMode = (model.getIdPlayer() == 0) ? modeWhite : modeBlack;

        // Coup principal choisi par l'IA
        AlquerqueDecider decider = new AlquerqueDecider(model, this, currentMode);
        ActionPlayer play = new ActionPlayer(model, this, decider, null);
        play.start();

        // Recherche d'une éventuelle capture multiple obligatoire
        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
        AlquerqueBoard board = stage.getBoard();
        int color = (model.getIdPlayer() == 0) ? Pawn.PAWN_WHITE : Pawn.PAWN_BLACK;
        Pawn[] pawns = (color == Pawn.PAWN_WHITE)
                ? stage.getWhitePawns()
                : stage.getBlackPawns();

        for (Pawn pw : pawns) {
            if (!pw.isVisible()) continue;

            int[] cell = board.getElementCell(pw);
            if (cell == null) continue;

            if (!board.getCaptures(cell[0], cell[1], color).isEmpty()) {
                multipleCaptures(new Point(cell[1], cell[0]), board, color);
                break;
            }
        }
    }

    public void endOfTurn() {
        model.setNextPlayer();
        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();

        if (model.getIdPlayer() == 0) {
            stage.incrementCount();
        }

        stage.getPlayerName().setText(model.getCurrentPlayerName());
    }

    private void multipleCaptures(Point point, AlquerqueBoard board, int color) {
        int row = point.y;
        int col = point.x;

        List<Point> captures = board.getCaptures(row, col, color);
        if (captures.isEmpty()) return;

        // Choix déterministe : première capture disponible
        Point nextDst = captures.get(0);

        Pawn pawn = (Pawn) board.getElement(row, col);
        int midRow = (row + nextDst.y) / 2;
        int midCol = (col + nextDst.x) / 2;
        Pawn captured = (Pawn) board.getElement(midRow, midCol);

        ActionList actions = ActionFactory.generatePutInContainer(
            model, pawn, "alquerqueboard", nextDst.y, nextDst.x
        );
        actions.addAll(ActionFactory.generateRemoveFromStage(model, captured));
        actions.setDoEndOfTurn(false);

        new ActionPlayer(model, this, actions).start();

        // Continue tant qu'une capture est encore possible avec ce même pion
        multipleCaptures(nextDst, board, color);
    }

    @Override
    public void update() {
        // Aucun affichage pendant les simulations
    }

    @Override
    public void endGame() {
        // Aucun affichage
    }
}
