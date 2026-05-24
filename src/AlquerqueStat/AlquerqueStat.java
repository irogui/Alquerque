package AlquerqueStat;

import boardifier.model.*;
import boardifier.control.*;
import boardifier.view.*;
import control.*;
import model.*;

/**
 * Lance des séries de parties pour produire des statistiques plus fiables :
 * - alterne les couleurs (pour éviter le biais du premier joueur)
 * - compte les matchs nuls
 * - affiche des pourcentages décimaux
 */
public class AlquerqueStat {

    private static final int N = 10; // parties par "aller" (donc 2N au total)

    public static void main(String[] args) throws GameException {
        runFairBatch("Random vs Random",     N, AlquerqueDecider.MODE_RANDOM,    AlquerqueDecider.MODE_RANDOM);
        runFairBatch("Heuristic vs Random",  N, AlquerqueDecider.MODE_HEURISTIC, AlquerqueDecider.MODE_RANDOM);
        runFairBatch("Minimax vs Random",    N, AlquerqueDecider.MODE_MINIMAX,   AlquerqueDecider.MODE_RANDOM);
        runFairBatch("Minimax vs Heuristic", N, AlquerqueDecider.MODE_MINIMAX,   AlquerqueDecider.MODE_HEURISTIC);
    }

    static void runFairBatch(String label, int n, int modeA, int modeB) throws GameException {
        int winsA = 0, winsB = 0, draws = 0;

        // Aller : A joue blanc, B joue noir
        for (int i = 0; i < n; i++) {
            int winner = playOneGame(modeA, modeB);
            if (winner == 0) winsA++;
            else if (winner == 1) winsB++;
            else draws++;
        }

        // Retour : B joue blanc, A joue noir (compensation du biais couleur/premier joueur)
        for (int i = 0; i < n; i++) {
            int winner = playOneGame(modeB, modeA);
            if (winner == 0) winsB++;
            else if (winner == 1) winsA++;
            else draws++;
        }

        int total = 2 * n;
        double pctA = winsA * 100.0 / total;
        double pctB = winsB * 100.0 / total;
        double pctD = draws * 100.0 / total;

        System.out.printf(
            "%-25s | A %6.2f%% | B %6.2f%% | Draws %6.2f%% (%d/%d)%n",
            label, pctA, pctB, pctD, draws, total
        );
    }

    static int playOneGame(int modeWhite, int modeBlack) throws GameException {
        Model model = new Model();
        model.addComputerPlayer("WHITE");
        model.addComputerPlayer("BLACK");

        StageFactory.registerModelAndView(
            "alquerque",
            "model.AlquerqueStageModel",
            "view.AlquerqueStageView"
        );

        View view = new View(model);
        AlquerqueControllerStat controller =
            new AlquerqueControllerStat(model, view, modeWhite, modeBlack);

        controller.setFirstStageName("alquerque");
        controller.startGame();
        controller.stageLoop();

        return model.getIdWinner();
    }
}