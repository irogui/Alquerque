import boardifier.control.StageFactory;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.view.View;
import control.AlquerqueController;

public class AlquerqueConsole {

    public static void main(String[] args) {

        // Game Mod : 0=human vs human, 1=human/AI, 2=AI/AI
        int mode = 0;
        if (args.length == 1) {
            try {
                mode = Integer.parseInt(args[0]);
                if (mode < 0 || mode > 2) mode = 0;
            }
            // utilisez try/catch en permanence pour ne pas que ça plante
            catch (NumberFormatException e) { mode = 0; }
        }

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
        AlquerqueController controller = new AlquerqueController(model, view);


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