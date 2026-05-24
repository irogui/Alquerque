package model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.awt.*;

/*Make sure to remove the keyword "private" from the variables that have it for the tests to occur.*/
import static model.AlquerqueBoard.ALL_DIRS;
import static model.AlquerqueBoard.ORTHO_DIRS;

public class AlquerqueBoardUnitTest {
    @Test
    public void testAlquerqueBoard() {
        /*Make sure to remove the keyword "private" from the methods that have it for the tests to occur.*/
        AlquerqueBoard alqBoard = Mockito.mock(AlquerqueBoard.class);
        Mockito.when(alqBoard.getDirs(1,1)).thenReturn(ALL_DIRS);
        Mockito.when(alqBoard.getDirs(2,1)).thenReturn(ORTHO_DIRS);

        Mockito.when(alqBoard.getSimpleMoves(1,1)).thenReturn(null);

        Mockito.when(alqBoard.getCaptures(1,1, 0)).thenReturn(null);
    }
}
