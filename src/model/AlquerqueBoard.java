package model;

import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlquerqueBoard extends ContainerElement {

    /*
     * Les directions valides pour chaque case.
     * Chaque case a une liste de directions [dRow, dCol] autorisées.
     * Les cases "diagonales" (paires) autorisent 8 directions,
     * les autres seulement 4 (haut, bas, gauche, droite).
     * On code ça simplement : si (row + col) est pair → 8 dirs, sinon 4 dirs.
     */

    // Les 8 directions : N, S, E, O, NE, NO, SE, SO
    private static final int[][] ALL_DIRS = {
            {-1, 0}, {1, 0}, {0, 1}, {0, -1},
            {-1, 1}, {-1, -1}, {1, 1}, {1, -1}
    };

    // Les 4 directions orthogonales seulement
    private static final int[][] ORTHO_DIRS = {
            {-1, 0}, {1, 0}, {0, 1}, {0, -1}
    };

    public AlquerqueBoard(int x, int y, GameStageModel gameStageModel) {
        // Grille 5 lignes × 5 colonnes, nommée "alquerqueboard"
        super("alquerqueboard", x, y, 5, 5, gameStageModel);
    }

    /**
     * Retourne les directions autorisées depuis une case (row, col).
     * Si row+col est paire alors case diagonale qui a 8 directions.
     * Sinon c'est une case orthogonale qui a 4 directions.
     */
    private int[][] getDirs(int row, int col) {
        if ((row + col) % 2 == 0)
            return ALL_DIRS;
        else
            return ORTHO_DIRS;
    }

    /**
     * Calcule toutes les cases vides atteignables par un déplacement SIMPLE
     * (pas de capture) pour le pion en (row, col).
     */
    public List<Point> getSimpleMoves(int row, int col) {
        List<Point> result = new ArrayList<>();
        int[][] dirs = getDirs(row, col);

        for (int[] d : dirs) {
            int r = row + d[0];
            int c = col + d[1];

            // Vérifier que c'est dans la grille et que c'est vide
            if ((r >= 0) && (r <= 4) && (c >= 0) && (c <= 4) && isEmptyAt(r, c)) {
                result.add(new Point(c, r)); // Point(x=col, y=row)
            }
        }

        return result;
    }

    /**
     * Calcule toutes les captures possibles depuis (row, col) pour un pion
     * de la couleur donnée.
     * Une capture = sauter par-dessus un adversaire vers une case vide.
     * Retourne des Points de destination (pas les cases intermédiaires).
     */
    public List<Point> getCaptures(int row, int col, int color) {
        List<Point> result = new ArrayList<>();
        int[][] dirs = getDirs(row, col);

        for (int[] d : dirs) {
            int midR = row + d[0];
            int midC = col + d[1];

            int dstR = row + (d[0] * 2);
            int dstC = col + (d[1] * 2);

            // Destination bien dans la grille ?
            if ((dstR >= 0) && (dstR <= 4) && (dstC >= 0) && (dstC <= 4)) {

                // Case middle occupée par un adversaire ?
                if (!(isEmptyAt(midR, midC))) {
                    Pion mid = (Pion) getElement(midR, midC);

                    // allié ou adversaire ?
                    if (!(mid.getColor() == color)) {

                        // Case d'atterrissage vide ?
                        if (isEmptyAt(dstR, dstC))
                            result.add(new Point(dstC, dstR));
                    }
                }

            }

        }

        return result;
    }


    /**
     * Met à jour reachableCells pour surligner visuellement
     * les cases valides pour le pion sélectionné.
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