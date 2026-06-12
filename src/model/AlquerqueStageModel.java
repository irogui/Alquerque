package model;

import boardifier.model.*;


public class AlquerqueStageModel extends GameStageModel {

    public final static int STATE_SELECTPAWN = 1;
    public final static int STATE_SELECTDEST = 2;
    public final static int STATE_CHAINCAPTURE = 3;

    private Pawn chainPawn = null;

    private AlquerqueBoard board;

    private Pawn[] whitePawns;
    private Pawn[] blackPawns;
    private TextElement playerName;

    private int whitePawnsLeft = 12;
    private int blackPawnsLeft = 12;
    private int count = 1;
    private int movesSinceLastCapture = 0;

    private TextElement turnCount;
    private TextElement whitePawnsText;
    private TextElement blackPawnsText;


    public AlquerqueStageModel(String name, Model model) {
        super(name, model);
        state = STATE_SELECTPAWN;
        setupCallbacks();
    }

    // ------------------------------------------------------------------ getters

    public AlquerqueBoard getBoard()          { return board; }
    public Pawn[]         getWhitePawns()     { return whitePawns; }
    public Pawn[]         getBlackPawns()     { return blackPawns; }
    public TextElement    getPlayerName()     { return playerName; }

    public int getMovesSinceLastCapture() { return movesSinceLastCapture; }
    public void setMovesSinceLastCapture(int num) { movesSinceLastCapture = num; }

    public TextElement getTurnCount()      { return turnCount; }
    public TextElement getWhitePawnsText() { return whitePawnsText; }
    public TextElement getBlackPawnsText() { return blackPawnsText; }

    public Pawn getChainPawn()       { return chainPawn; }


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

    public void incrementCount() {
        count++;
        turnCount.setText("Turn: " + count);
    }


    public void setTurnCount(TextElement t)      {
        this.turnCount = t;
        addElement(t);
    }
    public void setWhitePawnsText(TextElement t) {
        this.whitePawnsText = t;
        addElement(t);
    }
    public void setBlackPawnsText(TextElement t) {
        this.blackPawnsText = t;
        addElement(t);
    }

    public void setChainPawn(Pawn p) { chainPawn = p; }

    // ------------------------------------------------------------------ callbacks

    private void setupCallbacks() {

        onSelectionChange(() -> {
            if (selected.isEmpty()) {
                board.resetReachableCells(false);
                board.resetCaptureCells();
                board.addChangeFaceEvent();
                return;
            }

            Pawn pawn = (Pawn) selected.get(0);
            int[] cell = board.getElementCell(pawn);

            if (cell == null) {
                board.resetReachableCells(false);
                board.resetCaptureCells();
                board.addChangeFaceEvent();
                return;
            }

            board.setValidCells(cell[0], cell[1], pawn.getColor(), false);
        });


        onRemoveFromContainer((element, container, row, col) -> {
            if (!(element instanceof Pawn)) return;
            if (container != board) return;
            if (element.isVisible()) return;

            Pawn p = (Pawn) element;
            if (p.getColor() == Pawn.PAWN_WHITE) {
                whitePawnsLeft --;
                whitePawnsText.setText("White left: " + whitePawnsLeft);
            }
            else {
                blackPawnsLeft --;
                blackPawnsText.setText("Black left: " + blackPawnsLeft);
            }
            checkEndOfGame();
        });
    }

    // ------------------------------------------------------------------ end of game

    private void checkEndOfGame() {
        if (whitePawnsLeft == 0) {
            model.setIdWinner(1);
            model.stopGame();
        }
        else if (blackPawnsLeft == 0) {
            model.setIdWinner(0);
            model.stopGame();
        }
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new AlquerqueStageFactory(this);
    }
}
