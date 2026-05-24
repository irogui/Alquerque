import boardifier.control.StageFactory;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.view.View;
import control.AlquerqueController;
import control.AlquerqueDecider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AlquerqueConsole {

    public static void main(String[] args) {

        int mode   = 0;
        int iaMode = AlquerqueDecider.MODE_RANDOM;

        if (args.length >= 1) {
            try {
                mode = Integer.parseInt(args[0]);
                if (mode < 0 || mode > 2) mode = 0;
            } catch (NumberFormatException e) { mode = 0; }
        }

        if (args.length >= 2) {
            try {
                iaMode = Integer.parseInt(args[1]);
                if (iaMode < 0 || iaMode > 2) iaMode = AlquerqueDecider.MODE_RANDOM;
            } catch (NumberFormatException e) { iaMode = AlquerqueDecider.MODE_RANDOM; }
        }

        Scanner inputScanner = null;

        if (args.length >= 3) {
            try {
                inputScanner = new Scanner(new File(args[2]));
                System.out.println("File mod: " + args[2]);
            } catch (FileNotFoundException e) {
                System.err.println("File not found");
            }
        }

        String modeLabel;
        switch (iaMode) {
            case AlquerqueDecider.MODE_HEURISTIC: modeLabel = "Heuristic"; break;
            case AlquerqueDecider.MODE_MINIMAX:   modeLabel = "Minimax"; break;
            default:                              modeLabel = "Random";
        }
        if (mode != 0)
            System.out.println("Mode IA : " + modeLabel);

        // Create the modek
        Model model = new Model();

        // Add the players
        if (mode == 0) {
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
        AlquerqueController controller = new AlquerqueController(model, view, iaMode, inputScanner);


        controller.setFirstStageName("alquerque");
        try {
            controller.startGame();
            controller.stageLoop();
        }
        catch (GameException e) {
            System.err.println("Un problème est survenue... " + e.getMessage());
        }
    }
}