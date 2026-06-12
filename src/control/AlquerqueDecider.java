package control;

import boardifier.control.ActionFactory;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.Coord2D;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import boardifier.model.action.GameAction;
import boardifier.model.action.PutInContainerAction;
import boardifier.model.action.RemoveFromContainerAction;
import boardifier.model.animation.AnimationTypes;
import boardifier.view.ElementLook;
import boardifier.view.GridLook;
import javafx.geometry.Point2D;
import model.AlquerqueBoard;
import model.AlquerqueStageModel;
import model.Pawn;

import java.awt.*;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import java.util.*;

/**
 * AI decision-maker for the game, supporting three difficulty levels.
 * In Random mode, it picks any legal move at random. In Heuristic mode, it simulates all moves
 * and scores them based on pawn count, mobility, and available captures. In Minimax mode, it uses
 * alpha-beta pruning to search several moves ahead and choose the best one. All three modes enforce
 * the mandatory-capture rule.
 */

public class AlquerqueDecider extends Decider {

    public static final int MODE_RANDOM    = 0;
    public static final int MODE_HEURISTIC = 1;
    public static final int MODE_MINIMAX   = 2;

    private final int MINIMAX_DEPTH;

    // Values of the following options
    private static final int W_PAWNS    = 100;
    private static final int W_MOBILITY =  10;
    private static final int W_CAPTURE  =  25;

    // The 2 types of directions's possibilities for a pawn
    private static final int[][] ALL_DIRS = {
            {-1,0},{1,0},{0,1},{0,-1},{-1,1},{-1,-1},{1,1},{1,-1}
    };
    private static final int[][] ORTHO_DIRS = {
            {-1,0},{1,0},{0,1},{0,-1}
    };

    // We use it to do make the matrix of the board: -1 = empty, 0 = PAWN_WHITE, 1 = PAWN_BLACK
    private static final int EMPTY = -1;

    private static final Random rng = new Random();
    private final int aiMode;
    private Point lastCaptureDestination = null;


    public AlquerqueDecider(Model model, Controller control, int aiMode) {
        super(model, control);
        this.aiMode = aiMode;
        this.MINIMAX_DEPTH = view.AlquerqueSettingsPane.getMinimaxDepth();
    }


    @Override
    public ActionList decide() {
        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
        AlquerqueBoard board = stage.getBoard();

        int color;
        if (model.getIdPlayer() == 0)
            color = Pawn.PAWN_WHITE;
        else
            color = Pawn.PAWN_BLACK;


        switch (aiMode) {
            case MODE_HEURISTIC: return decideHeuristic(stage, board, color);
            case MODE_MINIMAX:   return decideMinimax(stage, board, color);
            default:             return decideRandom(stage, board, color);
        }
    }


    // Random mode: we still have it because it's defined as a noob level
    private ActionList decideRandom(AlquerqueStageModel stage, AlquerqueBoard board, int color) {
        Pawn[] myPawns = getPawns(stage, color);
        List<int[]> allOptions = new ArrayList<>();

        for (Pawn p : myPawns) {
            if (p.isVisible()) {
                int[] cell = board.getElementCell(p);
                if (!(cell == null)) {
                    for (Point dst : board.getCaptures(cell[0], cell[1], color))
                        allOptions.add(new int[]{cell[0], cell[1], dst.y, dst.x});
                    for (Point dst : board.getSimpleMoves(cell[0], cell[1]))
                        allOptions.add(new int[]{cell[0], cell[1], dst.y, dst.x});
                }
            }
        }

        if (allOptions.isEmpty())
            return forfeit();

        int[] choice = allOptions.get(rng.nextInt(allOptions.size()));
        return buildActionFromMove(board, choice);
    }


    // HEURISTIC mode
    private ActionList decideHeuristic(AlquerqueStageModel stage, AlquerqueBoard board, int color) {
        List<int[]> moves = collectAllMoves(board, stage, color);
        if (moves.isEmpty())
            return forfeit();

        // We create a matrix to copy the current board and apply the moves on it
        int[][] snapshot = boardSnapshot(board);
        int bestScore = Integer.MIN_VALUE;;
        List<int[]> bestMoves = new ArrayList<>();

        // For each moves we apply a move and evalute the new board
        for (int[] m : moves) {
            int[][] simulation = applyMove(snapshot, m[0], m[1], m[2], m[3], color);
            int score = evaluate(simulation, color);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(m);
            }
            else if (score == bestScore) {
                bestMoves.add(m);
            }
        }

        int[] chosen = bestMoves.get(rng.nextInt(bestMoves.size()));
        return buildActionFromMove(board, chosen);
    }

    // MINIMAX mod
    private ActionList decideMinimax(AlquerqueStageModel stage, AlquerqueBoard board, int color) {
        int[][] simulation = boardSnapshot(board);

        int opponent;
        if (color == Pawn.PAWN_WHITE)
            opponent = Pawn.PAWN_BLACK;
        else
            opponent = Pawn.PAWN_WHITE;

        List<int[]> moves = collectAllMoves(simulation, color);

        // If there are no moves, the IA forfeit()
        if (moves.isEmpty())
            return forfeit();

        int bestScore = Integer.MIN_VALUE;
        List<int[]> bestMoves = new ArrayList<>();

        for (int[] m : moves) {
            int[][] child = applyMove(simulation, m[0], m[1], m[2], m[3], color);

            int score = minimaxAB(child, MINIMAX_DEPTH - 1, false, color, opponent, 0, Integer.MAX_VALUE); // Integer.MAX_VALUE sert à prendre la valeur max possible d'un int

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(m);
            }
            else if (score == bestScore) {
                bestMoves.add(m);
            }
        }

        int[] chosen = bestMoves.get(rng.nextInt(bestMoves.size()));
        return buildActionFromMove(board, chosen);
    }


    private int minimaxAB(int[][] board, int depth, boolean maximizing, int aiColor, int currentColor, int alpha, int beta) {
        int opponent;
        if (currentColor == Pawn.PAWN_WHITE)
            opponent = Pawn.PAWN_BLACK;
        else
            opponent = Pawn.PAWN_WHITE;

        // end the recursion if the max depath has been reached or if the game is finished
        if (depth == 0 || isTerminal(board)) {
            return evaluate(board, aiColor);
        }

        List<int[]> moves = collectAllMoves(board, currentColor);

        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;;
            for (int[] m : moves) {
                int[][] child = applyMove(board, m[0], m[1], m[2], m[3], currentColor);
                int eval = minimaxAB(child, depth - 1, false, aiColor, opponent, alpha, beta);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha) break;
            }
            return maxEval;
        }
        else {
            int minEval = Integer.MAX_VALUE;
            for (int[] m : moves) {
                int[][] child = applyMove(board, m[0], m[1], m[2], m[3], currentColor);
                int eval = minimaxAB(child, depth - 1, true, aiColor, opponent, alpha, beta);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if (beta <= alpha) break;
            }
            return minEval;
        }
    }


    // Evaluation function: Receive a board and calcul the score according to all the options(pawn advantage, mobility, captures,...) of the IA
    private int evaluate(int[][] board, int myColor) {
        int opponent;
        if (myColor == Pawn.PAWN_WHITE)
            opponent = Pawn.PAWN_BLACK;
        else
            opponent = Pawn.PAWN_WHITE;

        int score = 0;

        // criteria 1: pawns count
        int myCount = 0, oppCount = 0;

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                int cell = board[r][c];

                if (cell == myColor)
                    myCount++;
                else if (cell == opponent)
                    oppCount++;
            }
        }

        // add to the score the difference of the pawns number between the 2 players and multiplied by the num pawns advantage score
        score += (myCount - oppCount) * W_PAWNS;

        // Criteria 2, 3 and 4: mobility, captures and multiples captures
        int myMoves = 0, oppMoves = 0;
        int myCaptures = 0, oppCaptures = 0;

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                int cell = board[r][c];

                if (cell == myColor) {
                    List<int[]> captures = getCaptures(board, r, c, myColor);
                    myCaptures += captures.size();
                    myMoves += getSimpleMoves(board, r, c).size();
                }
                else if (cell == opponent) {
                    List<int[]> captures = getCaptures(board, r, c, opponent);
                    oppCaptures += captures.size();
                    oppMoves += getSimpleMoves(board, r, c).size();
                }
            }
        }

        // Criteria 2: mobility
        score += (myMoves - oppMoves) * W_MOBILITY;

        // Critèria 3: available captures
        score += (myCaptures - oppCaptures) * W_CAPTURE;

        return score;
    }


    // Make a matrix from the current board
    private int[][] boardSnapshot(AlquerqueBoard board) {
        int[][] snap = new int[5][5];

        for (int[] row : snap) Arrays.fill(row, EMPTY);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (!board.isEmptyAt(r, c)) {
                    Pawn p = (Pawn) board.getElement(r, c);
                    snap[r][c] = p.getColor();
                }
            }
        }
        return snap;
    }

    // Apply a move on a board and the return the new one
    private int[][] applyMove(int[][] board, int srcR, int srcC, int dstR, int dstC, int color) {
        int[][] simulation = new int[5][5];

        for (int i = 0; i < 5; i++)
            simulation[i] = board[i].clone();

        simulation[dstR][dstC] = color;
        simulation[srcR][srcC] = EMPTY;

        // delete the pawn of the middle cell
        int dr = dstR - srcR;
        int dc = dstC - srcC;

        if (Math.abs(dr) == 2 || Math.abs(dc) == 2)
            simulation[srcR + dr / 2][srcC + dc / 2] = EMPTY;

        return simulation;
    }

    // Verify the game's end
    private boolean isTerminal(int[][] board) {
        boolean white = false;
        boolean black = false;

        for (int[] row : board)
            for (int c : row) {
                if (c == Pawn.PAWN_WHITE)
                    white = true;

                if (c == Pawn.PAWN_BLACK)
                    black = true;
            }

        return !white || !black;
    }

    // Return the available directions depending on the cell's coordinates
    private int[][] getDirs(int r, int c) {
        if ((r + c) % 2 == 0)
            return ALL_DIRS;
        else
            return ORTHO_DIRS;
    }

    // Test a simple move on a noard
    private List<int[]> getSimpleMoves(int[][] board, int r, int c) {
        List<int[]> list = new ArrayList<>();

        for (int[] d : getDirs(r, c)) {
            int nr = r + d[0], nc = c + d[1];

            if (inBounds(nr, nc) && board[nr][nc] == EMPTY)
                list.add(new int[]{nr, nc});
        }

        return list;
    }

    // Test captures on a board
    private List<int[]> getCaptures(int[][] board, int r, int c, int color) {
        List<int[]> list = new ArrayList<>();

        for (int[] d : getDirs(r, c)) {
            int mr = r + d[0], mc = c + d[1];
            int dr = r + d[0]*2, dc = c + d[1]*2;

            if ((inBounds(dr, dc)) && (board[mr][mc] != EMPTY) && (board[mr][mc] != color && board[dr][dc] == EMPTY))
                list.add(new int[]{dr, dc});
        }
        return list;
    }

    // Collect all available moves of the current player (FOR THE SNAPSHOTS)
    private List<int[]> collectAllMoves(int[][] board, int color) {
        List<int[]> all = new ArrayList<>();

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (board[r][c] == color) {
                    for (int[] dst : getCaptures(board, r, c, color))
                        all.add(new int[]{r, c, dst[0], dst[1]});
                    for (int[] dst : getSimpleMoves(board, r, c))
                        all.add(new int[]{r, c, dst[0], dst[1]});
                }
            }
        }
        return all;
    }

    // Collect all the possible moves of a player
    private List<int[]> collectAllMoves(AlquerqueBoard board, AlquerqueStageModel stage, int color) {
        Pawn[] pawns = getPawns(stage, color);
        List<int[]> all = new ArrayList<>();

        for (Pawn p : pawns) {
            if (p.isVisible()) {
                int[] cell = board.getElementCell(p);
                if (!(cell == null)) {
                    for (Point dst : board.getCaptures(cell[0], cell[1], color))
                        all.add(new int[]{cell[0], cell[1], dst.y, dst.x});
                    for (Point dst : board.getSimpleMoves(cell[0], cell[1]))
                        all.add(new int[]{cell[0], cell[1], dst.y, dst.x});
                }
            }
        }
        return all;
    }


    // This function make the ActionList for boardifier
    private ActionList buildActionFromMove(AlquerqueBoard board, int[] m) {
        int srcR = m[0], srcC = m[1], dstR = m[2], dstC = m[3];
        boolean isCapture = (Math.abs(dstR - srcR) == 2 || Math.abs(dstC - srcC) == 2);

        if (isCapture)
            return buildCapture(board, srcR, srcC, dstR, dstC);

        return buildMove(board, srcR, srcC, dstR, dstC);
    }

    private ActionList buildMove(AlquerqueBoard board, int srcR, int srcC, int dstR, int dstC) {
        Pawn pawn = (Pawn) board.getElement(srcR, srcC);
        ActionList actions = ActionFactory.generatePutInContainer(control, model, pawn, "alquerqueboard", dstR, dstC, AnimationTypes.MOVE_LINEARPROP, 10);
        actions.setDoEndOfTurn(true);
        ((AlquerqueStageModel) model.getGameStage()).setMovesSinceLastCapture(((AlquerqueStageModel) model.getGameStage()).getMovesSinceLastCapture() + 1);
        return actions;
    }

    private ActionList buildCapture(AlquerqueBoard board, int srcR, int srcC, int dstR, int dstC) {
        Pawn pawn = (Pawn) board.getElement(srcR, srcC);
        int midR = (srcR + dstR) / 2;
        int midC = (srcC + dstC) / 2;
        Pawn captured = (Pawn) board.getElement(midR, midC);
        int color = pawn.getColor();
        ((AlquerqueStageModel) model.getGameStage()).setMovesSinceLastCapture(0);

        ActionList actions = ActionFactory.generatePutInContainer(
                control, model, pawn, "alquerqueboard", dstR, dstC,
                AnimationTypes.MOVE_LINEARPROP, 10
        );
        if (captured != null)
            actions.addAll(ActionFactory.generateRemoveFromStage(model, captured));

        int[][] snap = boardSnapshot(board);
        snap[dstR][dstC] = color;
        snap[srcR][srcC] = EMPTY;
        snap[midR][midC] = EMPTY;
        List<int[]> nextCaptures = getCaptures(snap, dstR, dstC, color);

        if (!nextCaptures.isEmpty()) {
            actions.setDoEndOfTurn(false);

            AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
            stage.setChainPawn(pawn);
            stage.setState(AlquerqueStageModel.STATE_CHAINCAPTURE);

            final int nextSrcR = dstR, nextSrcC = dstC;
            final int nextDstR = nextCaptures.get(0)[0];
            final int nextDstC = nextCaptures.get(0)[1];

            new AlquerqueChainAction(model, control, actions, () -> {
                AlquerqueBoard currentBoard = stage.getBoard();
                int midNextR = (nextSrcR + nextDstR) / 2;
                int midNextC = (nextSrcC + nextDstC) / 2;
                if (currentBoard.getElement(midNextR, midNextC) == null) {
                    stage.setChainPawn(null);
                    stage.setState(AlquerqueStageModel.STATE_SELECTPAWN);
                    javafx.application.Platform.runLater(() -> control.endOfTurn());
                    return;
                }
                ActionList chain = buildCapture(currentBoard, nextSrcR, nextSrcC, nextDstR, nextDstC);
                new AlquerqueChainAction(model, control, chain, null).start();
            }).start();

            // Return a no-op list: AlquerqueChainAction above handles everything
            ActionList empty = new ActionList(false);
            return empty;
        }
        else {
            actions.setDoEndOfTurn(true);
            AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
            stage.setChainPawn(null);
            stage.setState(AlquerqueStageModel.STATE_SELECTPAWN);
            return actions;
        }
    }

    private ActionList forfeit() {
        model.setIdWinner(model.getIdPlayer() == 0 ? 1 : 0);
        model.stopGame();
        return new ActionList();
    }


    private Pawn[] getPawns(AlquerqueStageModel stage, int color) {
        if (color == Pawn.PAWN_WHITE)
            return stage.getWhitePawns();
        else
            return stage.getBlackPawns();
    }

    private static boolean inBounds(int r, int c) {
        return r >= 0 && r < 5 && c >= 0 && c < 5;
    }

    public Point getLastCaptureDestination() { return lastCaptureDestination; }
}