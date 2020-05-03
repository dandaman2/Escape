package escape;

import escape.exception.EscapeException;
import escape.piece.PieceName;
import escape.piece.Player;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class BroadGameTests {
    //Test to make sure orthogonal movement only works on bounded boards (square, ortho)
    @Test
    void orthoOnOrthoBoard() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Ortho/GoodOrthoBoundedBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(6, 6), emg.makeCoordinate(8,5)));
    }

    //Test to make sure orthogonal movement only works on bounded boards (square, ortho)
    @Test
    void orthoOnSquareBoard() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Square/GoodSquareBoundedBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(6, 6), emg.makeCoordinate(4,6)));
    }

    //Test to make sure orthogonal movement only works on bounded boards (square, ortho)
    @Test
    void orthoOnHexBoard() throws Exception{
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/BroadTests/Hex/OrthoMovementOnHexBoard.xml"));
            EscapeGameManager emg = egb.makeGameManager();
        });
    }

    //Test to make sure diagonal only works on square boards
    @Test
    void diagOnOrthoBoard() throws Exception{
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/BroadTests/Ortho/DiagMovementOrthoBoard.xml"));
            EscapeGameManager emg = egb.makeGameManager();
        });
    }

    //Test to make sure orthogonal movement only works on bounded boards (square, ortho)
    @Test
    void squareOnSquareBoard() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Square/GoodSquareBoundedBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(5, 6), emg.makeCoordinate(5,7)));
    }

    //Test to make sure orthogonal movement only works on bounded boards (square, ortho)
    @Test
    void diagOnHexBoard() throws Exception{
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/BroadTests/Hex/DiagMovementOnHexBoard.xml"));
            EscapeGameManager emg = egb.makeGameManager();
        });
    }

    //Test to ensure that omni movements on an orthogonal board act as if orthogonal
    @Test
    void omniOnOrthoBoard() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Ortho/GoodOrthoBoundedBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(11, 11), emg.makeCoordinate(14,14)));
    }

    //Test to make sure that omni movements are valid on a square board
    @Test
    void omniOnSquareBoard() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Square/GoodSquareBoundedBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(11, 11), emg.makeCoordinate(11,15)));
    }

    //Test to make sure that omni movements are valid on a hex board
    @Test
    void omniOnHexBoardValid1() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Hex/GoodMovementOnHexBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(0, 0), emg.makeCoordinate(2,1)));
    }

    //Test to make sure that omni movements are valid on a hex board
    @Test
    void omniOnHexBoardValid2() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Hex/GoodMovementOnHexBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(0, 0), emg.makeCoordinate(-3,0)));
    }

    //Test to make sure that invalid omni movements are invalid on a hex board
    @Test
    void omniOnHexBoardInvalid() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Hex/GoodMovementOnHexBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(-1, 2), emg.makeCoordinate(0,-2)));
    }


    //Test to make sure that linear movements are valid on an orthosquare board
    @Test
    void linearOnOrthoBoard() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Ortho/GoodOrthoBoundedBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(1, 1), emg.makeCoordinate(2,2)));
    }

    //Test to make sure that linear movements are valid on a square board
    @Test
    void linearOnSquareBoard() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Square/GoodSquareBoundedBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(12, 12), emg.makeCoordinate(9,9)));
    }

    //Test to make sure that linear movements are valid on a hex board
    @Test
    void linearDiagOnHexBoard() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Hex/GoodMovementOnHexBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(-1, -1), emg.makeCoordinate(2,-1)));
    }

    //Test to make sure that linear movements are valid on a hex board
    @Test
    void linearVerticalOnHexBoard() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Hex/GoodMovementOnHexBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(-1, -1), emg.makeCoordinate(-1,1)));
    }

    //Test to make sure that linear movements are valid on a hex board
    @Test
    void linearOnHexBoardInvalid() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/BroadTests/Hex/GoodMovementOnHexBoard.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(-1, -1), emg.makeCoordinate(1,-2)));
    }

    //ERROR TESTING//////////////////////////////////////////////
    //Test to esnure that you cannot move onto BLOCK spots
    @Test
    void moveOnBlockTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/edgeCaseGames/BadMovements.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(3, 1), emg.makeCoordinate(3,4)));
    }

    //Test to ensure you can only move onto a piece if it is of the opposite team
    @Test
    void moveOnSamePlayerPieceTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/edgeCaseGames/BadMovements.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(3, 1), emg.makeCoordinate(5,1)));
    }

    //Test to ensure you can only move onto a piece if it is of the opposite team
    @Test
    void moveOnDifferentPlayerPieceTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/edgeCaseGames/BadMovements.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(3, 1), emg.makeCoordinate(1,1)));
    }

    //Test to ensure you cant move a piece to its current location
    @Test
    void moveSameLocationTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/edgeCaseGames/BadMovements.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(3, 1), emg.makeCoordinate(3,1)));
    }

    //FLY and DISTANCE CHECKING////////////////////////////////////////////////

    //An error should be thrown if Fly or Distance are not defined
    @Test
    void noDistOrFlyDefinedTest() throws Exception{
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/edgeCaseGames/NoFlyOrDistanceDefined.xml"));
            EscapeGameManager emg = egb.makeGameManager();
        });
    }

    //An error should be thrown if both Fly and Distance are not defined
    @Test
    void bothDistAndFlyDefinedTest() throws Exception{
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/edgeCaseGames/BothFlyAndDistanceDefined.xml"));
            EscapeGameManager emg = egb.makeGameManager();
        });
    }

    //Physical Movement Tests/////////////////////////////////////////////////

    //Test to ensure that pieces are physically moving when moved
    @Test
    void moveToLocationTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/OrthoSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();

        assertEquals(emg.getPieceAt(emg.makeCoordinate(12, 12)).getName(), PieceName.FROG);
        assertEquals(emg.getPieceAt(emg.makeCoordinate(12, 11)), null);

        emg.move(emg.makeCoordinate(12, 12), emg.makeCoordinate(12,11));

        assertEquals(emg.getPieceAt(emg.makeCoordinate(12, 12)), null);
        assertEquals(emg.getPieceAt(emg.makeCoordinate(12, 11)).getName(), PieceName.FROG);
    }

    //Test to ensure that a piece is removed when reaching a valid EXIT location
    @Test
    void moveToExitTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/HexGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();

        assertEquals(emg.getPieceAt(emg.makeCoordinate(12, 12)).getName(), PieceName.FROG);

        emg.move(emg.makeCoordinate(12, 12), emg.makeCoordinate(12,17));

        assertEquals(emg.getPieceAt(emg.makeCoordinate(12, 12)), null);
        assertEquals(emg.getPieceAt(emg.makeCoordinate(12, 17)), null);

    }

    //Test to ensure that pieces are physically moving when moved
    @Test
    void captureEnemyPieceTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/HexGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();

        assertEquals(emg.getPieceAt(emg.makeCoordinate(12, 12)).getPlayer(), Player.PLAYER1);
        assertEquals(emg.getPieceAt(emg.makeCoordinate(15, 12)).getPlayer(), Player.PLAYER2);

        emg.move(emg.makeCoordinate(12, 12), emg.makeCoordinate(15,12));

        assertEquals(emg.getPieceAt(emg.makeCoordinate(12, 12)), null);
        assertEquals(emg.getPieceAt(emg.makeCoordinate(15, 12)).getPlayer(), Player.PLAYER1);
    }

    //An error should be thrown if a piece that has no rules is placed
    @Test
    void noRulesDefinedTest() throws Exception{
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/edgeCaseGames/NoPieceRuleDefined.xml"));
            EscapeGameManager emg = egb.makeGameManager();
        });
    }


}
