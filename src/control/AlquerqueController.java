package control;

import boardifier.control.*;
import boardifier.model.*;
import boardifier.model.action.ActionList;
import boardifier.view.View;
import model.*;

import java.awt.Point;
import java.util.List;
import java.util.Scanner;

public class AlquerqueController extends Controller {

    private Scanner scanner;
    private Scanner fileScanner;
    private final int whiteIaMode;
    private final int blackIaMode;


    public AlquerqueController(Model model, View view, int whiteIaMode, int blackIaMode, Scanner fileScanner) {
        super(model, view);

        this.whiteIaMode = whiteIaMode;
        this.blackIaMode = blackIaMode;
        this.fileScanner = fileScanner;
    }

    @Override
    public void stageLoop() {
        // The BufferedReader didn't work so we moved to the classic scanner
        scanner = new Scanner(System.in);
        update();

        while (!model.isEndStage()) {
            playTurn();
            endOfTurn();
            System.out.println("");
            update();
        }
        endGame();
    }

    private void playTurn() {
        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
        Player p = model.getCurrentPlayer();

        System.out.println("Turn: " + stage.getCount());
        System.out.println("Pawns left (" + stage.getWhitePawnsLeft() + "/" + stage.getBlackPawnsLeft() + ") \n");

        if (p.getType() == Player.COMPUTER) {
            System.out.println("COMPUTER THINKING...");

            // A bot last at least 3 seconds for the user to clearly see the game
            try {
                Thread.sleep(1000); // c'est en millisecondes en java
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            int currentIaMode;

            if (model.getIdPlayer() == 0) {
                currentIaMode = whiteIaMode;
            }
            else {
                currentIaMode = blackIaMode;
            }

            AlquerqueDecider decider = new AlquerqueDecider(model, this, currentIaMode);

            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();

            // If the AI made a capture, allow it to chain further captures
            Point lastCapture = decider.getLastCaptureDestination();
            if (lastCapture != null) {
                multipleCaptures(lastCapture);
            }
        }

        else {
            boolean ok = false;

            while (!ok) {

                System.out.print(">");

                // File scanner
                String line;
                if (fileScanner != null) {
                    if (fileScanner.hasNextLine()) {
                        line = fileScanner.nextLine().trim();
                        System.out.println(line);
                    }
                    else {
                        System.out.println("End of file.");
                        stopStage();
                        return;
                    }
                }
                else {
                    line = scanner.nextLine().trim();
                }

                // End the game if a user enter 'stop'
                if (line.equals("stop")) {
                    System.out.println("");
                    stopStage(); // sets isEndStage() = true, stops the main loop
                    endGame();
                    ok = true; // exit the input loop
                }
                else {
                    ok = analyseAndPlay(line);

                    if (!ok) {
                        System.out.println("incorrect instruction. retry !");
                    }
                }
            }
        }
    }

    public void endOfTurn() {
        model.setNextPlayer();
        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();

        // increment of the turns when the white player plays
        if (model.getIdPlayer() == 0)
            stage.incrementCount();

        stage.getPlayerName().setText(model.getCurrentPlayerName());
    }

    private boolean analyseAndPlay(String line) {
        // Allowed form: "A1-B2"

        if (line == null || line.length() != 5)
            return false;

        if (line.charAt(2) != '-')
            return false;

        int srcCol = line.charAt(0) - 'A';
        int srcRow = line.charAt(1) - '1';
        int dstCol = line.charAt(3) - 'A';
        int dstRow = line.charAt(4) - '1';

        // Verify if the coordinates doesn't cross the board
        if (srcRow<0 || srcRow>4 || srcCol<0 || srcCol>4)
            return false;
        if (dstRow<0 || dstRow>4 || dstCol<0 || dstCol>4)
            return false;

        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
        AlquerqueBoard board = stage.getBoard();


        if (board.isEmptyAt(srcRow, srcCol)) {
            System.out.println("No pawns here...");
            return false;
        }

        Pawn pawn = (Pawn) board.getElement(srcRow, srcCol);

        int currentColor;
        if (model.getIdPlayer() == 0)
            currentColor = Pawn.PAWN_WHITE;
        else
            currentColor = Pawn.PAWN_BLACK;

        if (pawn.getColor() != currentColor) {
            System.out.println("That's not your pawn...");
            return false;
        }

        // return true if there are any captures for the player
        boolean captureAvailable = hasAnyCapture(stage, currentColor);

        // Verify if the player wants to capture a pawn or make a simple move
        List<Point> captures = board.getCaptures(srcRow, srcCol, currentColor);
        Point dst = new Point(dstCol, dstRow);
        boolean isCapture = captures.contains(dst);

        // If a capture is available but the player didn't noticed it
        if (captureAvailable && !isCapture) {
            System.out.println("First you have to captur a pawn...");
            return false;
        }

        if (isCapture) {
            // Find the opponent's pawn to deltete it
            int midRow = (srcRow + dstRow) / 2;
            int midCol = (srcCol + dstCol) / 2;
            Pawn captured = (Pawn) board.getElement(midRow, midCol);

            // ActionList : move the pawn of the current player and delete the captured pawn from the board
            ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "alquerqueboard", dstRow, dstCol);
            ActionList remove = ActionFactory.generateRemoveFromStage(model, captured);
            actions.addAll(remove);
            actions.setDoEndOfTurn(true);
            new ActionPlayer(model, this, actions).start();

            // Call a method to automaticalt capture again and again opponent's pawns if it is possible from the destination point
            multipleCaptures(dst);
        }
        else {
            // Simple move: just verify if the current pawn can move to the wished direction
            List<Point> moves = board.getSimpleMoves(srcRow, srcCol);
            if (!moves.contains(dst)) {
                System.out.println("You cannot go this way...");
                return false;
            }

            ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "alquerqueboard", dstRow, dstCol);
            actions.setDoEndOfTurn(true);
            new ActionPlayer(model, this, actions).start();
        }

        return true;
    }

    /**
     * Return true if the player as at least a captur to make
     */
    private boolean hasAnyCapture(AlquerqueStageModel stage, int color) {
        AlquerqueBoard board = stage.getBoard();

        Pawn[] pawns;

        if (color == Pawn.PAWN_WHITE)
            pawns = stage.getWhitePawns();
        else
            pawns = stage.getBlackPawns();

        for (Pawn p : pawns) {
            if (p.isVisible()) {
                int[] coords = board.getElementCell(p);
                if (!(coords == null))
                    if (!board.getCaptures(coords[0], coords[1], color).isEmpty())
                        return true;
            }
        }
        return false;
    }

    /**
     * Recapture automaticly if is it possible from the registered position on param for the current player
     */
    public void multipleCaptures(Point point) {
        AlquerqueStageModel stage = (AlquerqueStageModel) model.getGameStage();
        AlquerqueBoard board = stage.getBoard();

        // Current player's color
        int currentColor;
        if (model.getIdPlayer() == 0)
            currentColor = Pawn.PAWN_WHITE;
        else
            currentColor = Pawn.PAWN_BLACK;

        int row = point.y;
        int col = point.x;

        List<Point> captures = board.getCaptures(row, col, currentColor);

        // Recursion's end if there are no capturable pawns
        if (captures.isEmpty()) {
            return;
        }

        // We delete the first element of the the list
        Point nextDst = captures.get(0);

        Pawn pawn = (Pawn) board.getElement(row, col);

        int midRow = (row + nextDst.y) / 2;
        int midCol = (col + nextDst.x) / 2;
        Pawn captured = (Pawn) board.getElement(midRow, midCol);

        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "alquerqueboard", nextDst.y, nextDst.x);

        ActionList remove = ActionFactory.generateRemoveFromStage(model, captured);

        actions.addAll(remove);

        actions.setDoEndOfTurn(false);

        new ActionPlayer(model, this, actions).start();

        // call the same function again
        multipleCaptures(nextDst);
    }
}