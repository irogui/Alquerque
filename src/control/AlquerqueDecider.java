package control;

import boardifier.control.*;
import boardifier.model.*;
import boardifier.model.action.ActionList;
import model.*;

import java.awt.Point;
import java.util.*;

public class AlquerqueDecider extends Decider {

    public static final int MODE_RANDOM    = 0;
    public static final int MODE_HEURISTIC = 1;
    public static final int MODE_MINIMAX   = 2;

    private static final int MINIMAX_DEPTH = 7;

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

    private static final Random rng = new Random();
    private final int aiMode;
    private Point lastCaptureDestination = null;


    public AlquerqueDecider(Model model, Controller control, int aiMode) {
        super(model, control);
        this.aiMode = aiMode;
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
        // get all player's pawns
        Pawn[] myPawns = getPawns(stage, color);
        List<int[]> captureOptions = new ArrayList<>();

        // get all pawns's captures and add them in captureOptions
        for (Pawn p : myPawns) {
            if (p.isVisible()) {
                int[] cell = board.getElementCell(p);
                if (!(cell == null)) {
                    for (Point dst : board.getCaptures(cell[0], cell[1], color))
                        captureOptions.add(new int[]{cell[0], cell[1], dst.y, dst.x});
                }
            }
        }

        // if there are one or many captures chose one randomly
        if (!captureOptions.isEmpty()) {
            int[] choice = captureOptions.get(rng.nextInt(captureOptions.size()));
            return buildCapture(board, choice[0], choice[1], choice[2], choice[3]);
        }

        // Else do the same but with the simple moves
        List<int[]> moveOptions = new ArrayList<>();
        for (Pawn p : myPawns) {
            if (p.isVisible()) {
                int[] cell = board.getElementCell(p);
                if (!(cell == null)) {
                    for (Point dst : board.getSimpleMoves(cell[0], cell[1]))
                        moveOptions.add(new int[]{cell[0], cell[1], dst.y, dst.x});
                }
            }
        }

        
        if (moveOptions.isEmpty())
            return forfeit();

        int[] choice = moveOptions.get(rng.nextInt(moveOptions.size()));
        return buildMove(board, choice[0], choice[1], choice[2], choice[3]);
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

        for (int[] m : moves) {
            int[][] sim = applyMove(snapshot, m[0], m[1], m[2], m[3], color);
            int score = evaluate(sim, color);

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
        int[][] snapshot = boardSnapshot(board);

        int opponent;
        if (color == Pawn.PAWN_WHITE)
            opponent = Pawn.PAWN_BLACK;
        else
            opponent = Pawn.PAWN_WHITE;

        List<int[]> moves = collectAllMovesOnSnapshot(snapshot, color);
        if (moves.isEmpty())
            return forfeit();

        int bestScore = Integer.MIN_VALUE;
        List<int[]> bestMoves = new ArrayList<>();

        for (int[] m : moves) {
            int[][] child = applyMove(snapshot, m[0], m[1], m[2], m[3], color);

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

        // End condition
        if (depth == 0 || isTerminal(board)) {
            return evaluate(board, aiColor);
        }

        List<int[]> moves = collectAllMovesOnSnapshot(board, currentColor);

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


    // Evaluation function for the heuristic method
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

                if (cell == myColor) {
                    myCount++;
                }
                else if (cell == opponent) {
                    oppCount++;
                }
            }
        }
        score += (myCount - oppCount) * W_PAWNS;

        // Criteria 2, 3 and 4: mobility, captures and multiples captures
        int myMoves = 0, oppMoves = 0;
        int myCaptures = 0, oppCaptures = 0;

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                int cell = board[r][c];
                if (cell == myColor) {
                    List<int[]> caps = getCapturesOnSnapshot(board, r, c, myColor);
                    myCaptures += caps.size();
                    myMoves += getSimpleMovesOnSnapshot(board, r, c).size();
                }
                else if (cell == opponent) {
                    List<int[]> caps = getCapturesOnSnapshot(board, r, c, opponent);
                    oppCaptures += caps.size();
                    oppMoves += getSimpleMovesOnSnapshot(board, r, c).size();
                }
            }
        }

        // Criteria 2: mobility
        score += (myMoves - oppMoves) * W_MOBILITY;

        // Critèria 3: available captures
        score += (myCaptures - oppCaptures) * W_CAPTURE;

        return score;
    }


    // -1 = empty, 0 = PAWN_WHITE, 1 = PAWN_BLACK
    private static final int EMPTY = -1;


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

    // Apply a move on a snapshot
    private int[][] applyMove(int[][] src, int srcR, int srcC, int dstR, int dstC, int color) {
        int[][] dst = new int[5][5];
        for (int i = 0; i < 5; i++) dst[i] = src[i].clone();

        dst[dstR][dstC] = color;
        dst[srcR][srcC] = EMPTY;

        // delete the pawn of the middle cell
        int dr = dstR - srcR;
        int dc = dstC - srcC;
        if (Math.abs(dr) == 2 || Math.abs(dc) == 2) {
            dst[srcR + dr / 2][srcC + dc / 2] = EMPTY;
        }

        return dst;
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
    private int[][] getDirsOnSnapshot(int r, int c) {
        if ((r + c) % 2 == 0)
            return ALL_DIRS;
        else
            return ORTHO_DIRS;
    }

    // Test a simple move on a snapshot
    private List<int[]> getSimpleMovesOnSnapshot(int[][] board, int r, int c) {
        List<int[]> list = new ArrayList<>();
        for (int[] d : getDirsOnSnapshot(r, c)) {
            int nr = r + d[0], nc = c + d[1];
            if (inBounds(nr, nc) && board[nr][nc] == EMPTY)
                list.add(new int[]{nr, nc});
        }
        return list;
    }

    // Test captures on a snapshot
    private List<int[]> getCapturesOnSnapshot(int[][] board, int r, int c, int color) {
        List<int[]> list = new ArrayList<>();
        for (int[] d : getDirsOnSnapshot(r, c)) {
            int mr = r + d[0], mc = c + d[1];
            int dr = r + d[0]*2, dc = c + d[1]*2;
            if (inBounds(dr, dc)
                    && board[mr][mc] != EMPTY && board[mr][mc] != color
                    && board[dr][dc] == EMPTY)
                list.add(new int[]{dr, dc});
        }
        return list;
    }

    // Collect all available moves of the current player
    private List<int[]> collectAllMovesOnSnapshot(int[][] board, int color) {
        List<int[]> captures = new ArrayList<>();
        List<int[]> simple   = new ArrayList<>();

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (board[r][c] == color) {
                    for (int[] dst : getCapturesOnSnapshot(board, r, c, color))
                        captures.add(new int[]{r, c, dst[0], dst[1]});
                    if (captures.isEmpty())
                        for (int[] dst : getSimpleMovesOnSnapshot(board, r, c))
                            simple.add(new int[]{r, c, dst[0], dst[1]});
                }
            }
        }

        if (!captures.isEmpty())
            return captures;

        return simple;
    }

    // Collect all the possible moves of a player
    private List<int[]> collectAllMoves(AlquerqueBoard board, AlquerqueStageModel stage, int color) {
        Pawn[] pawns = getPawns(stage, color);

        List<int[]> captures = new ArrayList<>();
        List<int[]> simple   = new ArrayList<>();

        for (Pawn p : pawns) {
            if (p.isVisible()) {
                int[] cell = board.getElementCell(p);

                if (!(cell == null))
                    for (Point dst : board.getCaptures(cell[0], cell[1], color))
                        captures.add(new int[]{cell[0], cell[1], dst.y, dst.x});
            }
        }

        if (!captures.isEmpty())
            return captures;

        for (Pawn p : pawns) {
            if (p.isVisible()) {
                int[] cell = board.getElementCell(p);

                if (!(cell == null))
                    for (Point dst : board.getSimpleMoves(cell[0], cell[1]))
                        simple.add(new int[]{cell[0], cell[1], dst.y, dst.x});
            }
        }
        return simple;
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
        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "alquerqueboard", dstR, dstC);
        actions.setDoEndOfTurn(true);
        return actions;
    }

    private ActionList buildCapture(AlquerqueBoard board, int srcR, int srcC, int dstR, int dstC) {
        Pawn pawn = (Pawn) board.getElement(srcR, srcC);
        int midR = (srcR + dstR) / 2;
        int midC = (srcC + dstC) / 2;

        Pawn captured = (Pawn) board.getElement(midR, midC);
        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "alquerqueboard", dstR, dstC);
        actions.addAll(ActionFactory.generateRemoveFromStage(model, captured));
        actions.setDoEndOfTurn(true);

        lastCaptureDestination = new Point(dstC, dstR);
        return actions;
    }

    private ActionList forfeit() {
        model.setIdWinner(model.getIdPlayer() == 0 ? 1 : 0);
        model.stopStage();
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