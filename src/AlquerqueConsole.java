import boardifier.control.StageFactory;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.view.View;
import control.AlquerqueController;

public class AlquerqueConsole {

    public static void main(String[] args) {

        // Mode de jeu : 0=humain/humain, 1=humain/IA, 2=IA/IA
        int mode = 0;
        if (args.length == 1) {
            try {
                mode = Integer.parseInt(args[0]);
                if (mode < 0 || mode > 2) mode = 0;
            }
            // J'utilise catch en permanence pour ne pas que ça plante
            catch (NumberFormatException e) { mode = 0; }
        }

        // Créer le model
        Model model = new Model();

        // Ajouter les joueurs
        if (mode == 0) {
            model.addHumanPlayer("Blanc");
            model.addHumanPlayer("Noir");
        }
        else if (mode == 1) {
            model.addHumanPlayer("Blanc");
            model.addComputerPlayer("Noir (IA)");
        }
        else {
            model.addComputerPlayer("Blanc (IA)");
            model.addComputerPlayer("Noir (IA)");
        }

        // Enregistrer les classes Model et View pour le stage "alquerque"
        // Le nom doit correspondre exactement dans les deux sens
        StageFactory.registerModelAndView(
                "alquerque",
                "model.AlquerqueStageModel",
                "view.AlquerqueStageView"
        );

        // Créer la View et le Controller
        View view = new View(model);
        AlquerqueController controller = new AlquerqueController(model, view);

        // Démarrer
        controller.setFirstStageName("alquerque");
        try {
            controller.startGame();
            controller.stageLoop();
        }
        catch (GameException e) {
            System.err.println("Ca recommence... : " + e.getMessage());
        }
    }
}