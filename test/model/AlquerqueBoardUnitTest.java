package model;

import boardifier.model.GameStageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.Point;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlquerqueBoardUnitTest {

    private GameStageModel stageModel;
    private AlquerqueBoard board;

    @BeforeEach
    public void setUp() {
        stageModel = Mockito.mock(GameStageModel.class);
        board = new AlquerqueBoard(0, 0, stageModel);
    }

    @Test
    public void createFiveByFiveTest() {
        assertEquals("alquerqueboard", board.getName());
        assertEquals(5, board.getNbRows());
        assertEquals(5, board.getNbCols());
    }

    @Test
    public void getSimpleMovesTest() {
        /*Case if the selected pawn is in the top left corner.*/
        List<Point> moves1 = board.getSimpleMoves(0, 0);

        assertEquals(3, moves1.size());
        assertTrue(moves1.contains(new Point(1, 0)));
        assertTrue(moves1.contains(new Point(0, 1)));
        assertTrue(moves1.contains(new Point(1, 1)));

        /*Case if the selected pawn is at the center.*/
        List<Point> moves2 = board.getSimpleMoves(2, 2);

        assertEquals(8, moves2.size());

        assertTrue(moves2.contains(new Point(2, 1)));
        assertTrue(moves2.contains(new Point(2, 3)));
        assertTrue(moves2.contains(new Point(3, 2)));
        assertTrue(moves2.contains(new Point(1, 2)));
        assertTrue(moves2.contains(new Point(3, 1)));
        assertTrue(moves2.contains(new Point(1, 1)));
        assertTrue(moves2.contains(new Point(3, 3)));
        assertTrue(moves2.contains(new Point(1, 3)));

        /*Case if the selected pawn is in a space with no diagonal moves.*/
        List<Point> moves3 = board.getSimpleMoves(1, 2);

        assertEquals(4, moves3.size());

        assertTrue(moves3.contains(new Point(2, 0)));
        assertTrue(moves3.contains(new Point(2, 2)));
        assertTrue(moves3.contains(new Point(3, 1)));
        assertTrue(moves3.contains(new Point(1, 1)));

        /*Case if the selected pawn got another pawn blocking its path.*/
        Pawn pawn = new Pawn(Pawn.PAWN_WHITE, stageModel);
        board.addElement(pawn, 1, 2);

        List<Point> moves4 = board.getSimpleMoves(2, 2);

        assertFalse(moves4.contains(new Point(2, 1)));
        assertEquals(7, moves4.size());
    }

    @Test
    public void getCapturesEnemyTest() {
        /*Case if the selected pawn got an opponent that meets the requirements of a capture.*/
        Pawn opponent1 = new Pawn(Pawn.PAWN_BLACK, stageModel);
        board.addElement(opponent1, 1, 1);

        List<Point> captures1 = board.getCaptures(0, 0, Pawn.PAWN_WHITE);

        assertEquals(1, captures1.size());
        assertTrue(captures1.contains(new Point(2, 2)));

        /*Case if the selected pawn got an opponent that meets the requirements of a capture but have another pawn blocking the landing space.*/
        Pawn opponent2 = new Pawn(Pawn.PAWN_BLACK, stageModel);
        Pawn blockingPawn = new Pawn(Pawn.PAWN_WHITE, stageModel);

        board.addElement(opponent2, 1, 1);
        board.addElement(blockingPawn, 2, 2);

        List<Point> captures2 = board.getCaptures(0, 0, Pawn.PAWN_WHITE);

        assertTrue(captures2.isEmpty());
    }

    @Test
    public void getCapturesAllyTest() {
        /*Case if the selected pawn got an ally that meets the requirements of a capture.*/
        Pawn friend = new Pawn(Pawn.PAWN_WHITE, stageModel);
        board.addElement(friend, 1, 1);

        List<Point> captures2 = board.getCaptures(0, 0, Pawn.PAWN_WHITE);

        assertTrue(captures2.isEmpty());
    }

    @Test
    public void setValidCellsTest() {
        /*Case if the selected pawn can move.*/
        board.setValidCells(0, 0, Pawn.PAWN_WHITE, false);

        assertTrue(board.canReachCell(0, 1));
        assertTrue(board.canReachCell(1, 0));
        assertTrue(board.canReachCell(1, 1));

        assertFalse(board.canReachCell(0, 0));
        assertFalse(board.canReachCell(0, 2));

        /*Case if the selected pawn can capture.*/
        Pawn opponent = new Pawn(Pawn.PAWN_BLACK, stageModel);
        board.addElement(opponent, 1, 1);

        board.setValidCells(0, 0, Pawn.PAWN_WHITE, true);

        assertTrue(board.canReachCell(2, 2));

        assertFalse(board.canReachCell(0, 1));
        assertFalse(board.canReachCell(1, 0));
        assertFalse(board.canReachCell(1, 1));
    }
}
