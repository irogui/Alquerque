package control;

import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import javafx.application.Platform;

/**
 * An ActionPlayer that executes a post-action callback once all animations are
 * done, before re-enabling event capture and before triggering endOfTurn.
 * This avoids Platform.runLater for board-state updates, keeping them
 * synchronised with the action sequence without modifying boardifier.
 */
public class AlquerqueChainAction extends ActionPlayer {

    private final Runnable onDone;

    public AlquerqueChainAction(Model model, Controller control, ActionList actions, Runnable onDone) {
        super(model, control, actions);
        this.onDone = onDone;
    }

    @Override
    public void run() {
        model.setCaptureEvents(false);

        playActionsReflect();

        if (onDone != null) {
            onDone.run();
        }

        model.setCaptureEvents(true);

        if (!model.isEndStage() && !model.isEndGame() && actions.mustDoEndOfTurn()) {
            Platform.runLater(() -> control.endOfTurn());
        }
    }

    /**
     * Calls the protected playActions method via reflection since boardifier
     * declares it as private. Falls back to super.run() if reflection fails.
     */
    private void playActionsReflect() {
        try {
            java.lang.reflect.Method m =
                    ActionPlayer.class.getDeclaredMethod("playActions", ActionList.class);
            m.setAccessible(true);
            m.invoke(this, actions);
        } catch (Exception e) {
            // Fallback: let the parent handle everything (no callback in that case)
            super.run();
        }
    }
}