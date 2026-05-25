package control;

import boardifier.model.Model;
import boardifier.model.TextElement;
import boardifier.view.View;
import model.AlquerqueStageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.mockito.Mockito.*;

class AlquerqueControllerUnitTest {

    private Model model;
    private View view;
    private AlquerqueStageModel stage;
    private TextElement playerName;
    private AlquerqueController controller;

    @BeforeEach
    void setUp() {
        model = mock(Model.class);
        view = mock(View.class);
        stage = mock(AlquerqueStageModel.class);
        playerName = mock(TextElement.class);

        when(model.getGameStage()).thenReturn(stage);
        when(stage.getPlayerName()).thenReturn(playerName);

        controller = new AlquerqueController(model, view, 0, 0, new Scanner(""));
    }

    @Test
    void endOfTurnNextBlackTest() {
        /*Getting player id and name, end its turn, and check if the next player is set.*/
        when(model.getIdPlayer()).thenReturn(1);
        when(model.getCurrentPlayerName()).thenReturn("Black");

        controller.endOfTurn();

        verify(model).setNextPlayer();
        verify(model).getCurrentPlayerName();
        verify(stage, never()).incrementCount();
        verify(playerName).setText("Black");
    }

    @Test
    void endOfTurnNextWhiteTest() {
        when(model.getIdPlayer()).thenReturn(0);
        when(model.getCurrentPlayerName()).thenReturn("White");

        controller.endOfTurn();

        verify(model).setNextPlayer();
        verify(model).getCurrentPlayerName();
        verify(stage).incrementCount();
        verify(playerName).setText("White");
    }
}
