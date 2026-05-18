package model;

import boardifier.model.*;

public class AlquerqueStageModel extends GameStageModel {

    // Les éléments du jeu
    private AlquerqueBoard board;
    private Pion[] whitePawns;  // 12 pions blancs
    private Pion[] blackPawns;  // 12 pions noirs
    private TextElement playerName;

    // État de la partie
    private int whitePawnsLeft = 12;
    private int blackPawnsLeft = 12;

    public AlquerqueStageModel(String name, Model model) {
        super(name, model);
        setupCallbacks();
    }

    // ── GETTERS ─────────────────────────────────────────────

    public AlquerqueBoard getBoard() { return board; }
    public Pion[] getWhitePawns() { return whitePawns; }
    public Pion[] getBlackPawns() { return blackPawns; }
    public TextElement getPlayerName() { return playerName; }
    public int getWhitePawnsLeft() { return whitePawnsLeft; }
    public int getBlackPawnsLeft() { return blackPawnsLeft; }


    // ── SETTERS ─────────────────────────────────────────────

    public void setBoard(AlquerqueBoard board) {
        this.board = board;
        addContainer(board); // boardifier sait maintenant que ce conteneur existe
    }

    public void setWhitePawns(Pion[] pawns) {
        this.whitePawns = pawns;
        for (Pion p : pawns) addElement(p);
    }

    public void setBlackPawns(Pion[] pawns) {
        this.blackPawns = pawns;
        for (Pion p : pawns) addElement(p);
    }

    public void setPlayerName(TextElement t) {
        this.playerName = t;
        addElement(t);
    }

    // ── CALLBACKS ───────────────────────────────────────────

    private void setupCallbacks() {
        /*
         * Ce callback est appelé par boardifier APRÈS chaque action
         * de type "RemoveFromContainer" — c'est-à-dire quand un pion
         * est retiré du plateau suite à une capture.
         * On décrémente le compteur et on vérifie la fin de partie.
         */
        onRemoveFromContainer((element, container, row, col) -> {
            if (!(element instanceof Pion)) return;

            if (container != board) return; // seulement depuis le plateau

            Pion p = (Pion) element;
            if (p.getColor() == Pion.PAWN_WHITE) {
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
            model.setIdWinner(1); // joueur 1 (noir) gagne
            model.stopStage();
        }
        else if (blackPawnsLeft == 0) {
            model.setIdWinner(0); // joueur 0 (blanc) gagne
            model.stopStage();
        }
    }

    // Boardifier appelle cette méthode pour créer les éléments
    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new AlquerqueStageFactory(this);
    }
}