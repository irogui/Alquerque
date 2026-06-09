package view;

import boardifier.view.RootPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Introduction panel is displayed when the application launches, before any game starts.
 *
 * The player must use the "Game > New Game" menu to start.
 *
 * Inherits from RootPane and overrides createDefaultGroup() to customize
 * this panel. Without this override, boardifier would display a plain
 * gray background with basic text.
 *
 * Visual elements are JavaFX objects (Rectangle, Text) added to group
 * (inherited from RootPane).
 */
public class AlquerqueRootPane extends RootPane {

    public AlquerqueRootPane() {
        super();
    }

    @Override
    public void createDefaultGroup() {
        // Dark background
        Rectangle background = new Rectangle(700, 600, Color.web("#2c3e50"));

        // Game title
        Text title = new Text("ALQUERQUE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 52));
        title.setFill(Color.web("#ecf0f1"));
        title.setX(155);
        title.setY(220);

        // Subtitle
        Text subtitle = new Text("Ancient Board Game");
        subtitle.setFont(Font.font("Arial", 22));
        subtitle.setFill(Color.web("#bdc3c7"));
        subtitle.setX(220);
        subtitle.setY(280);

        // Start instructions
        Text instructions = new Text("Game > New Game to start");
        instructions.setFont(Font.font("Arial", 16));
        instructions.setFill(Color.web("#95a5a6"));
        instructions.setX(240);
        instructions.setY(350);

        // Clears the group first before adding our elements, because boardifier already puts some default content in it.
        group.getChildren().clear();
        group.getChildren().addAll(background, title, subtitle, instructions);
    }
}
