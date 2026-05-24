package model;

import boardifier.model.ElementTypes;
import boardifier.model.GameStageModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PawnUnitTest {

    @Test
    void createPawnTest() {
        GameStageModel stageModel = mock(GameStageModel.class);

        /*Creating a white pawn.*/
        Pawn Wpawn = new Pawn(Pawn.PAWN_WHITE, stageModel);

        assertEquals(Pawn.PAWN_WHITE, Wpawn.getColor());

        /*Creating a black pawn.*/
        Pawn Bpawn = new Pawn(Pawn.PAWN_BLACK, stageModel);

        assertEquals(Pawn.PAWN_BLACK, Bpawn.getColor());
    }

    @Test
    void getTypeTest() {
        GameStageModel stageModel = mock(GameStageModel.class);

        Pawn pawn = new Pawn(Pawn.PAWN_WHITE, stageModel);

        assertEquals(ElementTypes.getType("pion"), pawn.getType());
    }
}
