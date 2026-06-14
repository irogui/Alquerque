package control;

import boardifier.model.Model;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.AlquerqueView;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlquerqueControllerActionUnitTest {

    private Model model;
    private AlquerqueView view;
    private AlquerqueController controller;

    private MenuItem menuIntro;
    private MenuItem menuSettings;
    private MenuItem menuStart;
    private MenuItem menuQuit;

    @BeforeEach
    void setUp() {
        model = mock(Model.class);
        view = mock(AlquerqueView.class);
        controller = mock(AlquerqueController.class);

        menuIntro = new MenuItem();
        menuSettings = new MenuItem();
        menuStart = new MenuItem();
        menuQuit = new MenuItem();

        when(view.getMenuIntro()).thenReturn(menuIntro);
        when(view.getMenuSettings()).thenReturn(menuSettings);
        when(view.getMenuStart()).thenReturn(menuStart);
        when(view.getMenuQuit()).thenReturn(menuQuit);
    }

    @Test
    void attachMenuHandlersTest() {
        new AlquerqueControllerAction(model, view, controller);

        assertNotNull(menuIntro.getOnAction());
        assertNotNull(menuSettings.getOnAction());
        assertNotNull(menuStart.getOnAction());
        assertNotNull(menuQuit.getOnAction());
    }

    @Test
    void stopGameAndResetViewTest() {
        new AlquerqueControllerAction(model, view, controller);

        menuIntro.fire();

        verify(controller).stopGame();
        verify(view).resetView();
    }

    @Test
    void startHumanVsComputerGameTest() throws Exception {
        when(model.getPlayers()).thenReturn(new ArrayList<>());

        new AlquerqueControllerAction(model, view, controller);

        menuStart.fire();

        verify(model).getPlayers();
        verify(model).addHumanPlayer("WHITE");
        verify(model).addComputerPlayer("BLACK (IA)");
        verify(controller).startGame();
    }

    @Test
    void doNothingWhenNoCapture() {
        when(model.isCaptureActionEvent()).thenReturn(false);

        AlquerqueControllerAction controllerAction = new AlquerqueControllerAction(model, view, controller);

        controllerAction.handle(mock(ActionEvent.class));

        verify(model).isCaptureActionEvent();
        verifyNoMoreInteractions(model);
    }
}