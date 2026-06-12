package view;

import boardifier.model.Model;
import boardifier.view.RootPane;
import boardifier.view.View;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class AlquerqueView extends View {

    private MenuItem menuStart;
    private MenuItem menuIntro;
    private MenuItem menuQuit;
    private MenuItem menuSettings;

    public AlquerqueView(Model model, Stage stage, RootPane rootPane) {
        super(model, stage, rootPane);
    }

    @Override
    protected void createMenuBar() {
        menuBar = new MenuBar();
        Menu menu1 = new Menu("Game");
        menuStart = new MenuItem("New game");
        menuIntro = new MenuItem("Intro");
        menuSettings = new MenuItem("Settings");
        menuQuit  = new MenuItem("Quit");

        menu1.getItems().add(menuStart);
        menu1.getItems().add(menuIntro);
        menu1.getItems().add(menuSettings);
        menu1.getItems().add(menuQuit);
        menuBar.getMenus().add(menu1);
    }

    public MenuItem getMenuStart() { return menuStart; }
    public MenuItem getMenuIntro() { return menuIntro; }
    public MenuItem getMenuSettings() { return menuSettings; }
    public MenuItem getMenuQuit()  { return menuQuit;  }
}