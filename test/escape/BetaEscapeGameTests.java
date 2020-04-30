/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2020 Gary F. Pollice
 *******************************************************************************/

package escape;

import static escape.piece.PieceAttributeID.*;
import static escape.piece.PieceName.*;
import static escape.piece.Player.PLAYER1;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.stream.Stream;

import escape.board.HexBoard;
import escape.board.LocationType;
import escape.board.OrthoSquareBoard;
import escape.board.SquareBoard;
import escape.board.coordinate.CoordinateID;
import escape.board.coordinate.HexCoordinate;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Description
 * @version Apr 24, 2020
 */
class BetaEscapeGameTests
{
    
    /**
     * Example of how the game manager tests will be structured.
     * @throws Exception
     */
    @Test
    void notNullBuilderTest() throws Exception {
        EscapeGameBuilder egb 
            = new EscapeGameBuilder(new File("config/game/SampleEscapeGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        System.out.println(egb.getGameInitializer());
        // Exercise the game now: make moves, check the board, etc.
    }

    //Helper function that holds a stream fo links to different game files (of different board types)
    static Stream<Arguments> differentGameTypesHelper(){
        return Stream.of(
                Arguments.of("config/game/SquareGame.xml", SquareCoordinate.class, SquareBoard.class),
                Arguments.of("config/game/OrthoSquareGame.xml", OrthoSquareCoordinate.class, OrthoSquareBoard.class),
                Arguments.of("config/game/HexGame.xml", HexCoordinate.class, HexBoard.class)
        );
    }

    //Test to ensure that a manager is created for each type
    @ParameterizedTest
    @MethodSource("differentGameTypesHelper")
    void createManagerObjectTest(String filepath) throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File(filepath));
        EscapeGameManager emg = egb.makeGameManager();
        assertNotNull(emg);
    }

    //Test top ensure the correct coordinate type is being produced based on the CoordinateID of the game
    @ParameterizedTest
    @MethodSource("differentGameTypesHelper")
    void createProperCoordinate(String filepath, Class coordInstance) throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File(filepath));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(coordInstance.isInstance(emg.makeCoordinate(1, 1)));
    }

    //Test top ensure the correct board type is being produced based on the CoordinateID of the game
    @ParameterizedTest
    @MethodSource("differentGameTypesHelper")
    void createProperBoard(String filepath, Class coordInstance, Class boardInstance) throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File(filepath));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(boardInstance.isInstance(((BetaGameManager)emg).getBoard()));
    }

    //Test top ensure the correct board dimensions are being set
    @Test
    void createProperBoardDimensions() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertEquals(((SquareBoard)((BetaGameManager)emg).getBoard()).getxMax(), 18);
        assertEquals(((SquareBoard)((BetaGameManager)emg).getBoard()).getyMax(), 19);
        assertEquals(((BetaGameManager)emg).getCoordID(), CoordinateID.SQUARE);
    }

    //The board dimensions should change if the input file changes
    @Test
    void changeProperBoardDimensions() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();

        EscapeGameBuilder egb2 = new EscapeGameBuilder(new File("config/game/OrthoSquareGame.xml"));
        emg = egb2.makeGameManager();

        assertEquals(((OrthoSquareBoard)((BetaGameManager)emg).getBoard()).getxMax(), 20);
        assertEquals(((OrthoSquareBoard)((BetaGameManager)emg).getBoard()).getyMax(), 25);
        assertEquals(((BetaGameManager)emg).getCoordID(), CoordinateID.ORTHOSQUARE);
    }

    //Test to ensure that the locations from the input file are being assigned correctly
    @Test
    void correctLocationTypes() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();

        assertEquals(LocationType.CLEAR,
                ((BetaGameManager)emg).getBoard().getLocationType(emg.makeCoordinate(3, 4)));
        assertEquals(LocationType.BLOCK,
                ((BetaGameManager)emg).getBoard().getLocationType(emg.makeCoordinate(5, 6)));
        assertEquals(LocationType.CLEAR,
                ((BetaGameManager)emg).getBoard().getLocationType(emg.makeCoordinate(8, 8)));
    }

    //Test to ensure that the locations are CLEAR by default
    @Test
    void defaultCLEARLocationTypes() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/edgeCaseGames/BareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();

        assertEquals(LocationType.CLEAR,
                ((BetaGameManager)emg).getBoard().getLocationType(emg.makeCoordinate(3, 4)));
        assertEquals(LocationType.CLEAR,
                ((BetaGameManager)emg).getBoard().getLocationType(emg.makeCoordinate(5, 6)));
        assertEquals(LocationType.CLEAR,
                ((BetaGameManager)emg).getBoard().getLocationType(emg.makeCoordinate(8, 8)));
    }

    //Test to ensure that the locations read-in from outside the board bounds should error
    @Test
    void locationOutsideBoardCoordinates() throws Exception{
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/edgeCaseGames/LocationOutsideBoard.xml"));
            EscapeGameManager emg = egb.makeGameManager();
        });
    }

    //Test to ensure that the manager can properly get a placed piece
    @ParameterizedTest
    @MethodSource("differentGameTypesHelper")
    void setGetManagerPiecesTest(String filepath) throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File(filepath));
        EscapeGameManager emg = egb.makeGameManager();
        ((BetaGameManager)emg).getBoard().putPieceAt(EscapePiece.makePiece(PLAYER1, HORSE), emg.makeCoordinate(3, 4));
        EscapePiece piece = ((BetaGameManager) emg).getPieceAt(emg.makeCoordinate(3, 4));
        assertEquals(HORSE, piece.getName());
        assertEquals(PLAYER1, piece.getPlayer());
    }

    //Test to make sure read-in pieces are getting placed
    @Test
    void correctPiecePlacement() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        EscapePiece piece = ((BetaGameManager) emg).getPieceAt(emg.makeCoordinate(11, 11));
        assertEquals(HORSE, piece.getName());
        assertEquals(PLAYER1, piece.getPlayer());
    }


    //Test to make sure that integer piece attributes are being stored correctly
    @Test
    void getIntAttributes() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertEquals(10, ((BetaGameManager)emg).getIntPieceAttribute(HORSE, DISTANCE));
        assertEquals(5, ((BetaGameManager)emg).getIntPieceAttribute(FROG, DISTANCE));
        assertEquals(0, ((BetaGameManager)emg).getIntPieceAttribute(FROG, FLY));
    }

    //Test to make sure that integer piece attributes are being stored correctly
    @Test
    void getBoolAttributes() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(((BetaGameManager)emg).getBoolPieceAttribute(HORSE, UNBLOCK));
        assertFalse(((BetaGameManager)emg).getBoolPieceAttribute(FROG, UNBLOCK));
    }



    
}
