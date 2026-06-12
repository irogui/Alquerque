package control;

import boardifier.control.Controller;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import boardifier.model.action.GameAction;
import boardifier.model.animation.Animation;
import javafx.application.Platform;
import java.util.List;


// This class is a different ActionPlayer made to execute a callback after the animations.
// We use it to manage the chained captures

public class AlquerqueChainAction extends Thread {

    private Model model;
    private Controller control;
    private ActionList actions;
    private Runnable onDone;

    public AlquerqueChainAction(Model model, Controller control, ActionList actions, Runnable onDone) {
        this.model = model;
        this.control = control;
        this.actions = actions;
        this.onDone = onDone;
    }

    @Override
    public void run() {
        model.setCaptureEvents(false);

        for (List<GameAction> pack : actions.getActions()) {

            for (GameAction action : pack) {
                if (!action.isAnimateBeforeExecute()) {
                    action.execute();
                }
            }

            Animation[] animations = new Animation[pack.size()];
            for (int i = 0; i < pack.size(); i++) {
                animations[i] = pack.get(i).setupAnimation();
                if (animations[i] != null) {
                    animations[i].start();
                }
            }

            for (int i = 0; i < pack.size(); i++) {
                if (animations[i] != null) {
                    animations[i].getAnimationState().waitStop();
                }
            }

            for (GameAction action : pack) {
                if (action.isAnimateBeforeExecute()) {
                    action.execute();
                }
            }
        }

        if (onDone != null) {
            onDone.run();
        }

        model.setCaptureEvents(true);

        if (!model.isEndStage() && !model.isEndGame() && actions.mustDoEndOfTurn()) {
            Platform.runLater(() -> control.endOfTurn());
        }
    }
}