package view;

import boardifier.model.Model;
import boardifier.view.RootPane;
import boardifier.view.View;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * Global view for the game, with a menu bar.
 *
 * In console mode, the view was a plain empty shell (new View(model)).
 * In JavaFX, we create a subclass of View to add a menu bar.
 *
 * The menu bar contains a single "Game" menu with 3 items:
 *  - New Game    : starts or restarts a game
 *  - Introduction: returns to the welcome panel
 *  - Quit        : exits the application
 *
 * The getters allow AlquerqueControllerAction to access the menu items
 * in order to attach click handlers (setOnAction).
 */
public class AlquerqueView extends View {

    // Keep references to the items so handlers can be attached to them
    private MenuItem menuStart;
    private MenuItem menuIntro;
    private MenuItem menuQuit;

    public AlquerqueView(Model model, Stage stage, RootPane rootPane) {
        super(model, stage, rootPane);
    }

    /**
     * Overrides menu bar creation.
     * Boardifier calls this method automatically during initialization.
     */
    @Override
    protected void createMenuBar() {
        menuBar = new MenuBar();

        Menu gameMenu = new Menu("Game");

        menuStart = new MenuItem("New Game");
        menuIntro = new MenuItem("Introduction");
        menuQuit  = new MenuItem("Quit");

        gameMenu.getItems().addAll(menuStart, menuIntro, menuQuit);
        menuBar.getMenus().add(gameMenu);
    }

    // Getters for AlquerqueControllerAction
    public MenuItem getMenuStart() { return menuStart; }
    public MenuItem getMenuIntro() { return menuIntro; }
    public MenuItem getMenuQuit()  { return menuQuit; }
}
