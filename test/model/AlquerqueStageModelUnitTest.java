package model;

import boardifier.model.Model;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlquerqueStageModelUnitTest {

    @Test
    void allGettersTest() {
        Model model = mock(Model.class);

        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        assertNull(stage.getBoard());
        assertNull(stage.getWhitePawns());
        assertNull(stage.getBlackPawns());
        assertNull(stage.getPlayerName());

        assertEquals(12, stage.getWhitePawnsLeft());
        assertEquals(12, stage.getBlackPawnsLeft());
        assertEquals(1, stage.getCount());
    }

    @Test
    void allSettersTest() {
        /*Setting up the board.*/
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        AlquerqueBoard board = mock(AlquerqueBoard.class);

        stage.setBoard(board);

        assertSame(board, stage.getBoard());

        /*Setting up some of the white pawns.*/
        Pawn Wpawn1 = mock(Pawn.class);
        Pawn Wpawn2 = mock(Pawn.class);
        Pawn[] Wpawns = { Wpawn1, Wpawn2};

        stage.setWhitePawns(Wpawns);

        assertSame(Wpawns, stage.getWhitePawns());

        /*Setting up some of the black pawns.*/
        Pawn Bpawn1 = mock(Pawn.class);
        Pawn Bpawn2 = mock(Pawn.class);
        Pawn[] Bpawns = {Bpawn1, Bpawn2};

        stage.setBlackPawns(Bpawns);

        assertSame(Bpawns, stage.getBlackPawns());

        /*Setting up the player's name.*/
        TextElement textElement = mock(TextElement.class);

        stage.setPlayerName(textElement);

        assertSame(textElement, stage.getPlayerName());
    }

    @Test
    void countTest() {
        /*Testing count incrementation.*/
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        assertEquals(1, stage.getCount());

        stage.incrementCount();

        assertEquals(2, stage.getCount());

        stage.incrementCount();

        assertEquals(3, stage.getCount());
    }

    @Test
    void getDefaultElementFactoryTest() {
        Model model = mock(Model.class);
        AlquerqueStageModel stage = new AlquerqueStageModel("stage", model);

        StageElementsFactory factory = stage.getDefaultElementFactory();
        
        assertInstanceOf(AlquerqueStageFactory.class, factory);
    }
}
