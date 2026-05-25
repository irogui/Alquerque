package model;

import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 5x5 Alquerque game board.
 * Handles movement rules: cells on even-sum coordinates allow 8 directions (orthogonal + diagonal) and for the
 * others only 4 dirs. Provides methods to compute valid simple moves and capture moves for a pawn,.
 */

public class AlquerqueBoard extends ContainerElement {

    /*
     * The valid directions for each cases are here
     * Each case has a list of directions [dRow, dCol]
     * The "diagonals" allows 8 directions,
     * The others only 4
     * So if (row + col) % 2 == 0 : 8 dirs, else 4 dirs
     */

    // The 8 dirs: N, S, E, O, NE, NO, SE, SO
    private static final int[][] ALL_DIRS = {
            {-1, 0}, {1, 0}, {0, 1}, {0, -1},
            {-1, 1}, {-1, -1}, {1, 1}, {1, -1}
    };

    // The 4 others dirs
    private static final int[][] ORTHO_DIRS = {
            {-1, 0}, {1, 0}, {0, 1}, {0, -1}
    };

    public AlquerqueBoard(int x, int y, GameStageModel gameStageModel) {
        // call the super-constructor to create a 5x5 grid, named "alquerqueboard", and in x,y in space
        super("alquerqueboard", x, y, 5, 5, gameStageModel);
    }

    /**
     * Return the allowed dirs from the coordinates (row, col)
     */
    private int[][] getDirs(int row, int col) {
        if ((row + col) % 2 == 0)
            return ALL_DIRS;
        else
            return ORTHO_DIRS;
    }

    /**
     * Calcul all the reachable cells with a simple movement
     */
    public List<Point> getSimpleMoves(int row, int col) {
        List<Point> result = new ArrayList<>();
        int[][] dirs = getDirs(row, col);

        for (int[] d : dirs) {
            int r = row + d[0];
            int c = col + d[1];

            if ((r >= 0) && (r <= 4) && (c >= 0) && (c <= 4) && isEmptyAt(r, c)) {
                result.add(new Point(c, r));
            }
        }

        return result;
    }

    /**
     * Calcul all the possible captures from (row, col) for a pawn
     * Capture = jump over an opponent to take place on an empty cell
     * Return the destinations cells
     */
    public List<Point> getCaptures(int row, int col, int color) {
        List<Point> result = new ArrayList<>();
        int[][] dirs = getDirs(row, col);

        for (int[] d : dirs) {
            int midR = row + d[0];
            int midC = col + d[1];

            int dstR = row + (d[0] * 2);
            int dstC = col + (d[1] * 2);

            // Verify if it's on the board
            if ((dstR >= 0) && (dstR <= 4) && (dstC >= 0) && (dstC <= 4)) {

                // Varify if the middle cell is occuped
                if (!(isEmptyAt(midR, midC))) {
                    Pawn mid = (Pawn) getElement(midR, midC);

                    // Verify if it's an opponent
                    if (!(mid.getColor() == color)) {

                        // Verify if the cell behind the middle cell is empty
                        if (isEmptyAt(dstR, dstC))
                            result.add(new Point(dstC, dstR));
                    }
                }

            }

        }

        return result;
    }


    /**
     * Change the state of the reachableCells
     */
    public void setValidCells(int row, int col, int color, boolean captureOnly) {
        resetReachableCells(false);
        List<Point> valid;

        if (captureOnly) {
            valid = getCaptures(row, col, color);
        } else {
            valid = getSimpleMoves(row, col);
        }

        for (Point p : valid) {
            reachableCells[p.y][p.x] = true;
        }
    }
}