import boardifier.control.Logger;
import boardifier.control.StageFactory;
import boardifier.model.Model;
import control.AlquerqueDecider;
import control.AlquerqueController;
import javafx.application.Application;
import javafx.stage.Stage;
import view.AlquerqueRootPane;
import view.AlquerqueView;

public class Alquerque extends Application {

    private static int mode = 1;
    private static int whiteIaMode = AlquerqueDecider.MODE_RANDOM;
    private static int blackIaMode = AlquerqueDecider.MODE_RANDOM;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Logger.setLevel(Logger.LOGGER_DEBUG);

        // create the global model
        Model model = new Model();

        // add some players taking mode into account
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

        StageFactory.registerModelAndView("alquerque", "model.AlquerqueStageModel", "view.AlquerqueStageView");
        AlquerqueRootPane rootPane = new AlquerqueRootPane();
        AlquerqueView view = new AlquerqueView(model, stage, rootPane);
        AlquerqueController control = new AlquerqueController(model, view, whiteIaMode, blackIaMode);
        control.setFirstStageName("alquerque");
        stage.setTitle("The Alquerque");
        stage.show();
    }
}