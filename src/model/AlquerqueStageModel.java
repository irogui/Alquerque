package model;

import boardifier.model.*;


/**
 * Holds the complete state of an Alquerque game stage.
 * Stores the board, the two arrays of pawns, pawn counts, and the turn counter.
 * Registers a callback that is called whenever a pawn is removed from the board to update
 * the pawn count and check if the game is over.
 */

public class AlquerqueStageModel extends GameStageModel {

    // Elements
    private AlquerqueBoard board;
    private Pawn[] whitePawns;
    private Pawn[] blackPawns;
    private TextElement playerName;

    private int whitePawnsLeft = 12;
    private int blackPawnsLeft = 12;

    private int count = 1;

    private int turnsWithoutCapture = 0;

    public AlquerqueStageModel(String name, Model model) {
        super(name, model);
        setupCallbacks();
    }

    // -- GETTERS ----------------------------------------------

    public AlquerqueBoard getBoard() { return board; }
    public Pawn[] getWhitePawns() { return whitePawns; }
    public Pawn[] getBlackPawns() { return blackPawns; }
    public TextElement getPlayerName() { return playerName; }
    public int getWhitePawnsLeft() { return whitePawnsLeft; }
    public int getBlackPawnsLeft() { return blackPawnsLeft; }
    public int getCount() { return count; }

    //--- SETTERS ----------------------------------------------

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
        this.count ++;
    }

    public int getTurnsWithoutCapture() { return turnsWithoutCapture; }

    public void registerCaptureMade(boolean captureMade) {
        if (captureMade) {
            turnsWithoutCapture = 0;
        }
        else {
            turnsWithoutCapture ++;
        }
        checkEndOfGame();
    }


    private void setupCallbacks() {
        /*
         * After each player action this callback is called by boardifier
         */
        onRemoveFromContainer((element, container, row, col) -> {
            if (!(element instanceof Pawn))
                return;

            if (container != board)
                return;

            // If the pawn is still visible, it means that it's juste a simple move and not a capture
            if (element.isVisible()) return;

            Pawn p = (Pawn) element;
            if (p.getColor() == Pawn.PAWN_WHITE) {
                whitePawnsLeft--;
            }
            else {
                blackPawnsLeft--;
            }
            checkEndOfGame();
        });
    }

    private void checkEndOfGame() {
        if (whitePawnsLeft == 0) {
            model.setIdWinner(1);
            model.stopStage();
        }
        else if (blackPawnsLeft == 0) {
            model.setIdWinner(0);
            model.stopStage();
        }
        else if (turnsWithoutCapture >= 20) {
            model.setIdWinner(-1);
            model.stopStage();
            System.out.println("20 tours sans prise : égalité !");
        }
    }

    public void checkPlayerBlocked(int currentPlayerId) {
        if (currentPlayerId == 0) {
            model.setIdWinner(1);
        }
        else {
            model.setIdWinner(0);
        }
        model.stopStage();
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new AlquerqueStageFactory(this);
    }
}