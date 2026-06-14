package control;

import boardifier.model.Model;
import boardifier.model.TextElement;
import boardifier.model.Player;
import model.AlquerqueStageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.scene.control.*;

import javafx.scene.layout.Pane;

import view.AlquerqueView;

import static org.mockito.Mockito.*;

class AlquerqueControllerUnitTest {

    private Model model;
    private AlquerqueView view;
    private AlquerqueStageModel stage;
    private TextElement playerName;
    private AlquerqueController controller;

    @BeforeEach
    void setUp() {
        model = mock(Model.class);
        view = mock(AlquerqueView.class);
        stage = mock(AlquerqueStageModel.class);
        playerName = mock(TextElement.class);

        Pane rootPane = mock(Pane.class);
        when(view.getRootPane()).thenReturn(rootPane);

        MenuItem menuIntro = mock(MenuItem.class);
        when(view.getMenuIntro()).thenReturn(menuIntro);

        controller = new AlquerqueController(model, view, 0, 0);

        when(model.getGameStage()).thenReturn(stage);
        when(stage.getPlayerName()).thenReturn(playerName);
        when(stage.getMovesSinceLastCapture()).thenReturn(0);
    }

    @Test
    void endOfTurnNextBlackTest() {
        Player blackPlayer = mock(Player.class);
        when(blackPlayer.getName()).thenReturn("Black");
        when(blackPlayer.getType()).thenReturn(Player.HUMAN);

        when(model.getCurrentPlayer()).thenReturn(blackPlayer);
        when(model.getIdPlayer()).thenReturn(1);
        when(stage.getMovesSinceLastCapture()).thenReturn(0);

        controller.endOfTurn();

        verify(model).setNextPlayer();
        verify(playerName).setText("Black");
        verify(stage, never()).incrementCount();
    }

    @Test
    void endOfTurnNextWhiteTest() {
        Player whitePlayer = mock(Player.class);
        when(whitePlayer.getName()).thenReturn("White");
        when(whitePlayer.getType()).thenReturn(Player.HUMAN);

        when(model.getCurrentPlayer()).thenReturn(whitePlayer);
        when(model.getIdPlayer()).thenReturn(0);
        when(stage.getMovesSinceLastCapture()).thenReturn(0);

        controller.endOfTurn();

        verify(model).setNextPlayer();
        verify(playerName).setText("White");
        verify(stage).incrementCount();
    }
}
