package model;

import boardifier.model.*;


public class AlquerqueStageModel extends GameStageModel {

    private AlquerqueBoard board;
    private Pawn[] whitePawns;
    private Pawn[] blackPawns;
    private TextElement playerName;

    private int whitePawnsLeft = 12;
    private int blackPawnsLeft = 12;
    private int count = 1;

    public AlquerqueStageModel(String name, Model model) {
        super(name, model);
        setupCallbacks();
    }

    // ------------------------------------------------------------------ getters

    public AlquerqueBoard getBoard()          { return board; }
    public Pawn[]         getWhitePawns()     { return whitePawns; }
    public Pawn[]         getBlackPawns()     { return blackPawns; }
    public TextElement    getPlayerName()     { return playerName; }
    public int            getWhitePawnsLeft() { return whitePawnsLeft; }
    public int            getBlackPawnsLeft() { return blackPawnsLeft; }
    public int            getCount()          { return count; }

    // ------------------------------------------------------------------ setters

    public void setBoard(AlquerqueBoard board) {
        this.board = board;
        addContainer(board);
    }

    public void setWhitePawns(Pawn[] pawns) {
        this.whitePawns = pawns;
        for (Pawn p : pawns) addElement(p);
    }

    public void setBlackPawns(Pawn[] pawns) {
        this.blackPawns = pawns;
        for (Pawn p : pawns) addElement(p);
    }

    public void setPlayerName(TextElement t) {
        this.playerName = t;
        addElement(t);
    }

    public void incrementCount() { count++; }

    // ------------------------------------------------------------------ callbacks

    private void setupCallbacks() {

        /**
         *
         * This is called by boardifier every time an element is selected or deselected by the player.
         *
         * - If nothing is selected: clear all reachable cell highlights.
         * - If a pawn is selected: compute its reachable cells and highlight them.
         *
         * Captures are not mandatory in this version, so both simple moves
         * and captures are always shown (captureOnly = false).
         */
        onSelectionChange(() -> {
            if (selected.isEmpty()) {
                board.resetReachableCells(false);
                return;
            }

            Pawn pawn = (Pawn) selected.get(0);
            int[] cell = board.getElementCell(pawn);

            if (cell == null) {
                board.resetReachableCells(false);
                return;
            }

            board.setValidCells(cell[0], cell[1], pawn.getColor(), false);
        });


        onRemoveFromContainer((element, container, row, col) -> {
            if (!(element instanceof Pawn)) return;
            if (container != board) return;
            if (element.isVisible()) return; // simple move, not a capture

            Pawn p = (Pawn) element;
            if (p.getColor() == Pawn.PAWN_WHITE) {
                whitePawnsLeft--;
            } else {
                blackPawnsLeft--;
            }
            checkEndOfGame();
        });
    }

    // ------------------------------------------------------------------ end of game

    private void checkEndOfGame() {
        if (whitePawnsLeft == 0) {
            model.setIdWinner(1);
            model.stopStage();
        } else if (blackPawnsLeft == 0) {
            model.setIdWinner(0);
            model.stopStage();
        }
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new AlquerqueStageFactory(this);
    }
}
