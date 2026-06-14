package control;

import boardifier.model.Model;
import boardifier.model.action.ActionList;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static org.mockito.Mockito.*;

class AlquerqueChainActionUnitTest {

    @Test
    void disableAndEnableCapturesTest() {
        Model model = mock(Model.class);
        ActionList actions = mock(ActionList.class);
        Runnable onDone = mock(Runnable.class);

        when(actions.getActions()).thenReturn(List.of());
        when(actions.mustDoEndOfTurn()).thenReturn(false);

        AlquerqueChainAction chainAction = new AlquerqueChainAction(model, null, actions, onDone);

        chainAction.run();

        InOrder inOrder = inOrder(model, onDone);
        inOrder.verify(model).setCaptureEvents(false);
        inOrder.verify(onDone).run();
        inOrder.verify(model).setCaptureEvents(true);
    }

    @Test
    void notRunCallbackWhenOnDoneIsNullTest() {
        Model model = mock(Model.class);
        ActionList actions = mock(ActionList.class);

        when(actions.getActions()).thenReturn(List.of());
        when(actions.mustDoEndOfTurn()).thenReturn(false);

        AlquerqueChainAction chainAction = new AlquerqueChainAction(model, null, actions, null);

        chainAction.run();

        verify(model).setCaptureEvents(false);
        verify(model).setCaptureEvents(true);
    }

    @Test
    void notEndTurnWhenActionsNotEndTurnTest() {
        Model model = mock(Model.class);
        ActionList actions = mock(ActionList.class);
        Runnable onDone = mock(Runnable.class);

        when(actions.getActions()).thenReturn(List.of());
        when(actions.mustDoEndOfTurn()).thenReturn(false);

        AlquerqueChainAction chainAction = new AlquerqueChainAction(model, null, actions, onDone);

        chainAction.run();

        verify(actions).mustDoEndOfTurn();
    }

    @Test
    void notEndTurnWhenModelIsEndStageTest() {
        Model model = mock(Model.class);
        ActionList actions = mock(ActionList.class);

        when(actions.getActions()).thenReturn(List.of());
        when(actions.mustDoEndOfTurn()).thenReturn(true);
        when(model.isEndStage()).thenReturn(true);

        AlquerqueChainAction chainAction = new AlquerqueChainAction(model, null, actions, null);

        chainAction.run();

        verify(model).isEndStage();
        verify(model, never()).isEndGame();
    }

    @Test
    void notEndTurnWhenModelIsEndGameTest() {
        Model model = mock(Model.class);
        ActionList actions = mock(ActionList.class);

        when(actions.getActions()).thenReturn(List.of());
        when(actions.mustDoEndOfTurn()).thenReturn(true);
        when(model.isEndStage()).thenReturn(false);
        when(model.isEndGame()).thenReturn(true);

        AlquerqueChainAction chainAction = new AlquerqueChainAction(model, null, actions, null);

        chainAction.run();

        verify(model).isEndStage();
        verify(model).isEndGame();
    }
}