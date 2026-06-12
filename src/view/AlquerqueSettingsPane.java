package view;

import control.AlquerqueDecider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AlquerqueSettingsPane {

    // Default mode: (Humain VS IA)
    // IA mode: Random
    private static int mode        = 1;
    private static int whiteIaMode = AlquerqueDecider.MODE_RANDOM;
    private static int blackIaMode = AlquerqueDecider.MODE_RANDOM;
    private static int minimaxDepth = 6;
    private static boolean drawRule = false;

    public static int getMode()        { return mode; }
    public static int getWhiteIaMode() { return whiteIaMode; }
    public static int getBlackIaMode() { return blackIaMode; }
    public static int getMinimaxDepth() { return minimaxDepth; }
    public static boolean isDrawRuleOk() { return drawRule; }

    public static void show() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("Game configuration");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        // Mode selector
        ToggleGroup modeGroup = new ToggleGroup();

        RadioButton rbHH   = new RadioButton("Human vs Human");
        RadioButton rbHAI  = new RadioButton("Human vs AI");
        RadioButton rbAIAI = new RadioButton("AI vs AI");

        rbHH.setToggleGroup(modeGroup);
        rbHAI.setToggleGroup(modeGroup);
        rbAIAI.setToggleGroup(modeGroup);

        if      (mode == 0) rbHH.setSelected(true);
        else if (mode == 1) rbHAI.setSelected(true);
        else                rbAIAI.setSelected(true);

        // AI difficulty selectors
        String[] levels = {"Random", "Heuristic", "Minimax"};

        Label lblBlackAI = new Label("Black (AI):");
        ComboBox<String> cbBlack = new ComboBox<>();
        cbBlack.getItems().addAll(levels);
        cbBlack.getSelectionModel().select(blackIaMode);

        Label lblWhiteAI = new Label("White (AI):");
        ComboBox<String> cbWhite = new ComboBox<>();
        cbWhite.getItems().addAll(levels);
        cbWhite.getSelectionModel().select(whiteIaMode);

        Label lblDepth = new Label("Minimax depth:");
        Spinner<Integer> spDepth = new Spinner<>(1, 10, minimaxDepth);
        spDepth.setEditable(true);
        spDepth.setPrefWidth(70);

        CheckBox cbDraw = new CheckBox("Draw after 20 moves without capture");
        cbDraw.setSelected(drawRule);

        Runnable updateControls = () -> {
            cbWhite.setDisable(!rbAIAI.isSelected());
            cbBlack.setDisable(rbHH.isSelected());
            boolean whiteUsesMinmax = rbAIAI.isSelected()
                    && cbWhite.getSelectionModel().getSelectedIndex() == AlquerqueDecider.MODE_MINIMAX;
            boolean blackUsesMinmax = !rbHH.isSelected()
                    && cbBlack.getSelectionModel().getSelectedIndex() == AlquerqueDecider.MODE_MINIMAX;
            spDepth.setDisable(!whiteUsesMinmax && !blackUsesMinmax);
        };
        updateControls.run();
        modeGroup.selectedToggleProperty().addListener((obs, old, nw) -> updateControls.run());
        cbWhite.getSelectionModel().selectedIndexProperty().addListener((obs, old, nw) -> updateControls.run());
        cbBlack.getSelectionModel().selectedIndexProperty().addListener((obs, old, nw) -> updateControls.run());


        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        VBox modes = new VBox(6, rbHH, rbHAI, rbAIAI);
        grid.add(new Label("Game mode:"), 0, 0);
        grid.add(modes, 1, 0);
        grid.add(new Separator(), 0, 1, 2, 1);
        grid.add(lblWhiteAI, 0, 2);
        grid.add(cbWhite,    1, 2);
        grid.add(lblBlackAI, 0, 3);
        grid.add(cbBlack,    1, 3);

        grid.add(new Separator(), 0, 4, 2, 1);
        grid.add(lblDepth, 0, 5);
        grid.add(spDepth,  1, 5);

        grid.add(new Separator(), 0, 6, 2, 1);
        grid.add(cbDraw, 0, 7, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                mode        = rbHH.isSelected() ? 0 : rbHAI.isSelected() ? 1 : 2;
                whiteIaMode = cbWhite.getSelectionModel().getSelectedIndex();
                blackIaMode = cbBlack.getSelectionModel().getSelectedIndex();
                drawRule = cbDraw.isSelected();

                try {
                    spDepth.getValueFactory().setValue(Integer.parseInt(spDepth.getEditor().getText()));
                }
                catch (NumberFormatException ignored) {}
                minimaxDepth = spDepth.getValue();
            }
            return null;
        });

        dialog.showAndWait();
    }
}