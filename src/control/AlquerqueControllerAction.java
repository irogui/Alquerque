package control;

import boardifier.control.Controller;
import boardifier.control.ControllerAction;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.view.View;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import view.AlquerqueView;
import view.AlquerqueSettingsPane;
import boardifier.model.*;


/**
 * A basic action controller that only manages menu actions
 * Action events are mostly generated when there are user interactions with widgets like
 * buttons, checkboxes, menus, ...
 */
public class AlquerqueControllerAction extends ControllerAction implements EventHandler<ActionEvent> {

    // to avoid lots of casts, create an attribute that matches the instance type.
    private AlquerqueView alquerqueView;

    public AlquerqueControllerAction(Model model, View view, Controller control) {
        super(model, view, control);
        alquerqueView = (AlquerqueView) view;
        setMenuHandlers();
    }

    private void setMenuHandlers() {

        // set event handler on the MenuIntro item
        alquerqueView.getMenuIntro().setOnAction(e -> {
            control.stopGame();
            alquerqueView.resetView();
        });

        // set event handler on the MenuSettings item
        alquerqueView.getMenuSettings().setOnAction(e -> {
            AlquerqueSettingsPane.show();
        });

        // set event handler on the MenuStart item
        alquerqueView.getMenuStart().setOnAction(e -> {
            int mode        = AlquerqueSettingsPane.getMode();
            int whiteIaMode = AlquerqueSettingsPane.getWhiteIaMode();
            int blackIaMode = AlquerqueSettingsPane.getBlackIaMode();

            model.getPlayers().clear();

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

            ((AlquerqueController) control).setIaModes(whiteIaMode, blackIaMode);

            try {
                control.startGame();
            }
            catch (GameException err) {
                System.err.println(err.getMessage());
                System.exit(1);
            }

            // Execute the first move if the first player to play is an AI
            Player p = model.getCurrentPlayer();
            if (p != null && p.getType() == Player.COMPUTER) {
                ((AlquerqueController) control).playComputerTurn();
            }
        });

        // set event handler on the MenuQuit item
        alquerqueView.getMenuQuit().setOnAction(e -> System.exit(0));
    }

    public void handle(ActionEvent event) {
        if (!model.isCaptureActionEvent()) return;
    }
}