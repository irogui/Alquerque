package control;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AlquerqueControllerUnitTest {
    @Test
    public void testAlquerqueController() {
        AlquerqueController alqCont = Mockito.mock(AlquerqueController.class);
        Mockito.when(alqCont.analyseAndPlay(null)).thenReturn(false);
        Mockito.when(alqCont.analyseAndPlay("test")).thenReturn(false);
        Mockito.when(alqCont.analyseAndPlay("luigi")).thenReturn(false);
        Mockito.when(alqCont.analyseAndPlay("B6-B5")).thenReturn(false);
        Mockito.when(alqCont.analyseAndPlay("A1-A0")).thenReturn(false);
        Mockito.when(alqCont.analyseAndPlay("D4-C3")).thenReturn(true);

        /*Mockito.when(alqCont.hasAnyCapture(null, 1)).thenReturn(false);*/
    }
}
