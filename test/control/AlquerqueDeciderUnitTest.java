package control;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AlquerqueDeciderUnitTest {
    @Test
    public void testAlquerqueDecider() {
        AlquerqueDecider alqDec = Mockito.mock(AlquerqueDecider.class);
        Mockito.when(alqDec.decide()).thenReturn(null);
        /*Mockito.when(alqDec.buildCapture(null, 0, 0, 1, 1, 1)).thenReturn(null);*/
    }
}
