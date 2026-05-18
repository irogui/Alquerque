package control;

import boardifier.control.*;
import boardifier.model.*;
import boardifier.model.action.ActionList;
import boardifier.view.View;
import model.*;

import java.awt.Point;
import java.io.*;
import java.util.List;

public class AlquerqueController extends Controller {

    private BufferedReader consoleIn;

    public AlquerqueController(Model model, View view) {
        super(model, view);
    }

    @Override
    public void stageLoop() {
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        update();
        while (!model.isEndStage()) {
            playTurn();
            endOfTurn();
            update();
        }
        endGame();
    }

    private void playTurn() {
        Player p = model.getCurrentPlayer();

        if (p.getType() == Player.COMPUTER) {
            System.out.println("L'ordinateur réfléchit...");
            AlquerqueDecider decider = new AlquerqueDecider(model, this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
        }

        else {
            boolean cap = false;

            while (!cap) {
                System.out.print(p.getName() + " (ex: A1-B2) : ");
                try {
                    String line = consoleIn.readLine();
                    cap = analyseAndPlay(line);

                    if (!cap)
                        System.out.println("Coup invalide, va arracher tes morts !.");

                }
                catch (IOException e) {}
            }
        }
    }

    public void endOfTurn() {
        model.setNextPlayer();
        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
        stage.getPlayerName().setText(model.getCurrentPlayerName());
    }

    private boolean analyseAndPlay(String line) {
        // Format attendu : "A1-B2" (5 caractères)
        if (line == null || line.length() != 5)
            return false;

        if (line.charAt(2) != '-')
            return false;

        int srcCol = line.charAt(0) - 'A';
        int srcRow = line.charAt(1) - '1';
        int dstCol = line.charAt(3) - 'A';
        int dstRow = line.charAt(4) - '1';

        // Vérif coo valides (cad ne dépasse pas du plateau)
        if (srcRow<0 || srcRow>4 || srcCol<0 || srcCol>4)
            return false;
        if (dstRow<0 || dstRow>4 || dstCol<0 || dstCol>4)
            return false;

        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
        AlquerqueBoard board = stage.getBoard();

        // Y a-t-il un pion du joueur courant à la source ?
        if (board.isEmptyAt(srcRow, srcCol))
            return false;

        Pion pion = (Pion) board.getElement(srcRow, srcCol);

        int currentColor;
        if (model.getIdPlayer() == 0)
            currentColor = Pion.PAWN_WHITE;
        else
            currentColor = Pion.PAWN_BLACK;

        if (pion.getColor() != currentColor) return false;

        // Vérif si des captures sont disponibles
        boolean captureAvailable = hasAnyCapture(stage, currentColor);

        // Vérifier si le coup joué est une capture
        List<Point> captures = board.getCaptures(srcRow, srcCol, currentColor);
        Point dst = new Point(dstCol, dstRow);
        boolean isCapture = captures.contains(dst);

        // Si une capture est possible mais le joueur ne capture pas → invalide
        if (captureAvailable && !isCapture) return false;

        if (isCapture) {
            // Trouver le pion adverse à supprimer (case intermédiaire)
            int midRow = (srcRow + dstRow) / 2;
            int midCol = (srcCol + dstCol) / 2;
            Pion captured = (Pion) board.getElement(midRow, midCol);

            // ActionList : déplacer le pion joueur + supprimer le pion capturé
            ActionList actions = ActionFactory.generatePutInContainer(model, pion, "alquerqueboard", dstRow, dstCol);
            ActionList remove = ActionFactory.generateRemoveFromStage(model, captured);
            actions.addAll(remove);
            actions.setDoEndOfTurn(true);
            new ActionPlayer(model, this, actions).start();
        }
        else {
            // Déplacement simple : vérifier que la case dest. est dans les coups valides
            List<Point> moves = board.getSimpleMoves(srcRow, srcCol);
            if (!moves.contains(dst)) return false;

            ActionList actions = ActionFactory.generatePutInContainer(
                    model, pion, "alquerqueboard", dstRow, dstCol);
            actions.setDoEndOfTurn(true);
            new ActionPlayer(model, this, actions).start();
        }
        return true;
    }

    /**
     * Vérifie si le joueur de la couleur donnée a au moins une capture possible
     * sur tout le plateau. Si oui, il est obligé de capturer.
     */
    private boolean hasAnyCapture(AlquerqueStageModel stage, int color) {
        AlquerqueBoard board = stage.getBoard();

        Pion[] pawns;

        if (color == Pion.PAWN_WHITE)
            pawns = stage.getWhitePawns();
        else
            pawns = stage.getBlackPawns();

        for (Pion p : pawns) {
            if (!p.isVisible()) continue; // pion capturé = invisible
            int[] coords = board.getElementCell(p);
            if (coords == null) continue;
            if (!board.getCaptures(coords[0], coords[1], color).isEmpty())
                return true;
        }
        return false;
    }
}