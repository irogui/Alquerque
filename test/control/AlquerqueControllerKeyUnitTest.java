package control;

import boardifier.control.Controller;
import boardifier.model.Model;
import boardifier.view.View;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AlquerqueControllerKeyUnitTest {

    @Test
    void doNothingWhenModelNotCaptureKeyTest() {
        Model model = mock(Model.class);
        View view = mock(View.class);
        Controller controller = mock(Controller.class);
        KeyEvent event = mock(KeyEvent.class);

        when(model.isCaptureKeyEvent()).thenReturn(false);

        AlquerqueControllerKey controllerKey = new AlquerqueControllerKey(model, view, controller);

        controllerKey.handle(event);

        verify(model).isCaptureKeyEvent();
    }

    @Test
    void readKeyCodeWhenModelCapturesKeyTest() {
        Model model = mock(Model.class);
        View view = mock(View.class);
        Controller controller = mock(Controller.class);
        KeyEvent event = mock(KeyEvent.class);

        when(model.isCaptureKeyEvent()).thenReturn(true);
        when(event.getCode()).thenReturn(KeyCode.A);

        AlquerqueControllerKey controllerKey =
                new AlquerqueControllerKey(model, view, controller);

        controllerKey.handle(event);

        verify(model).isCaptureKeyEvent();
        verify(event).getCode();
    }
}