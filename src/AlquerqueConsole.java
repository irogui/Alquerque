import boardifier.control.StageFactory;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.view.View;
import control.AlquerqueController;
import control.AlquerqueDecider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Entry point of the game.
 * Reads command-line arguments to choose the game mode:
 * 0: Human vs Human
 * 1: Human vs AI
 * 2: AI vs AI
 * 3: file input mode
 *
 * Set up the AI difficulty (Random, Heuristic, or Minimax) for each player, then creates the Model, View
 * and Controller to starts the game loop.

 *
 * Examples:
 *
 * java -cp out AlquerqueConsole 0
 * java -cp out AlquerqueConsole 2 20
 * java -cp out AlquerqueConsole 1 1
 * java -cp out AlquerqueConsole 3 moves.txt
 */

public class AlquerqueConsole {

    public static void main(String[] args) {

        int mode = 0;
        // Default mods for White and Black IA
        int whiteIaMode = AlquerqueDecider.MODE_RANDOM;
        int blackIaMode = AlquerqueDecider.MODE_RANDOM;

        if (args.length >= 1) {
            try {
                mode = Integer.parseInt(args[0]);
                if (mode < 0 || mode > 3)
                    mode = 0;
            }
            catch (NumberFormatException e) { mode = 0; }
        }

        if (args.length >= 2 && mode != 3) {
            try {
                // Mode IA vs IA
                if (mode == 2) {
                    String iaModes = args[1];
                    if (iaModes.length() == 2) {
                        whiteIaMode = Character.getNumericValue(iaModes.charAt(0));
                        blackIaMode = Character.getNumericValue(iaModes.charAt(1));

                        if (whiteIaMode < 0 || whiteIaMode > 2)
                            whiteIaMode = AlquerqueDecider.MODE_RANDOM;

                        if (blackIaMode < 0 || blackIaMode > 2)
                            blackIaMode = AlquerqueDecider.MODE_RANDOM;
                    }
                }
                // Mode Human vs IA
                else {
                    int iaMode = Integer.parseInt(args[1]);
                    if (iaMode < 0 || iaMode > 2)
                        iaMode = AlquerqueDecider.MODE_RANDOM;
                    blackIaMode = iaMode;
                }
            }
            catch (NumberFormatException e) {
                whiteIaMode = AlquerqueDecider.MODE_RANDOM;
                blackIaMode = AlquerqueDecider.MODE_RANDOM;
            }
        }

        Scanner inputScanner = null;
        if (mode == 3) {
            if (args.length >= 2) {
                try {
                    inputScanner = new Scanner(new File(args[1]));
                    System.out.println("File mode: " + args[1]);
                }
                catch (FileNotFoundException e) {
                    System.err.println("File not found");
                    return;
                }
            }
        }

        // Show's AI's selected mods
        if (mode != 0) {
            String whiteLabel;
            switch (whiteIaMode) {
                case AlquerqueDecider.MODE_HEURISTIC: whiteLabel = "Heuristic"; break;
                case AlquerqueDecider.MODE_MINIMAX:   whiteLabel = "Minimax"; break;
                default:                              whiteLabel = "Random";
            }

            String blackLabel;
            switch (blackIaMode) {
                case AlquerqueDecider.MODE_HEURISTIC: blackLabel = "Heuristic"; break;
                case AlquerqueDecider.MODE_MINIMAX:   blackLabel = "Minimax"; break;
                default:                              blackLabel = "Random";
            }

            if (mode == 1)
                System.out.println("Mode IA Black : " + blackLabel);

            if (mode == 2) {
                System.out.println("Mode IA White : " + whiteLabel);
                System.out.println("Mode IA Black  : " + blackLabel);
            }
        }

        // Create the modek
        Model model = new Model();

        // Add the players
        if (mode == 0 || mode == 3) {
            model.addHumanPlayer("WHITE");
            model.addHumanPlayer("BLACK");
        }
        else if (mode == 1) {
            model.addHumanPlayer("WHITE");
            model.addComputerPlayer("BLACK (IA)");
        }
        else {
            model.addComputerPlayer("WHITE (IA)");
            model.addComputerPlayer("BLACK (IA)");
        }

        // Register model and view for the stage "alquerque"
        StageFactory.registerModelAndView(
                "alquerque",
                "model.AlquerqueStageModel",
                "view.AlquerqueStageView"
        );

        // Create the View and the Controller
        View view = new View(model);
        AlquerqueController controller = new AlquerqueController(model, view, whiteIaMode, blackIaMode, inputScanner);


        controller.setFirstStageName("alquerque");
        try {
            controller.startGame();
            controller.stageLoop();
        }
        catch (GameException e) {
            System.err.println("Problem detected... " + e.getMessage());
        }
    }
}