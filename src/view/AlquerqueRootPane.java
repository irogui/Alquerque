package view;

import boardifier.view.RootPane;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.*;


public class AlquerqueRootPane extends RootPane {

    public AlquerqueRootPane() {
        super();
    }

    @Override
    public void createDefaultGroup() {
        StackPane stack = new StackPane();
        stack.setPrefSize(700, 800);
        stack.setStyle("-fx-background-color: #6b624d;");

        Text title = new Text("ALQUERQUE");
        title.setFont(Font.font("bebas neue", FontWeight.BOLD, 50));
        title.setFill(Color.web("#ffffff"));

        Text sub = new Text("Press Game to begin...");
        sub.setFont(Font.font("bebas neue", 18));
        sub.setFill(Color.web("#ffffff"));

        VBox content = new VBox(20, title, sub);
        content.setAlignment(Pos.CENTER);

        stack.getChildren().add(content);
        group.getChildren().clear();
        group.getChildren().add(stack);
    }
}