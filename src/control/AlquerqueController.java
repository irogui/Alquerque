package control;

import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.control.Logger;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.view.View;
import model.AlquerqueStageModel;
import view.*;

public class AlquerqueController extends Controller {

    private int whiteIaMode;
    private int blackIaMode;


    public AlquerqueController(Model model, View view, int whiteIaMode, int blackIaMode) {
        super(model, view);
        this.whiteIaMode = whiteIaMode;
        this.blackIaMode = blackIaMode;

        setControlKey(new AlquerqueControllerKey(model, view, this));
        setControlMouse(new AlquerqueControllerMouse(model, view, this));
        setControlAction(new AlquerqueControllerAction(model, view, this));
    }

    public void endOfTurn() {
        // use the default method to compute next player
        model.setNextPlayer();
        Player p = model.getCurrentPlayer();

        AlquerqueStageModel stageModel = (AlquerqueStageModel) model.getGameStage();
        stageModel.getPlayerName().setText(p.getName());

        if (AlquerqueSettingsPane.isDrawRuleOk() && stageModel.getMovesSinceLastCapture() >= 20) {
            model.setIdWinner(-1);
            model.stopGame();
            return;
        }

        // Increment count of the turns
        if (model.getIdPlayer() == 0)
            stageModel.incrementCount();


        if (p.getType() == Player.COMPUTER) {
            Logger.debug("COMPUTER PLAYS");

            int iaMode;
            if (model.getIdPlayer() == 0)
                iaMode = whiteIaMode;
            else
                iaMode = blackIaMode;

            AlquerqueDecider decider = new AlquerqueDecider(model, this, iaMode);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);

            play.start();
        }
        else {
            Logger.debug("PLAYER PLAYS");
        }
    }

    public void setIaModes(int whiteIaMode, int blackIaMode) {
        this.whiteIaMode = whiteIaMode;
        this.blackIaMode = blackIaMode;
    }

    public void playComputerTurn() {
        Player p = model.getCurrentPlayer();
        if (p.getType() == Player.COMPUTER) {
            Logger.debug("COMPUTER PLAYS FIRST");

            int iaMode;
            if (model.getIdPlayer() == 0)
                iaMode = whiteIaMode;
            else
                iaMode = blackIaMode;

            AlquerqueDecider decider = new AlquerqueDecider(model, this, iaMode);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
        }
    }
}