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

package escape.movement;

import static escape.piece.PieceAttributeID.*;
import static escape.piece.PieceName.*;
import static escape.piece.Player.PLAYER1;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.stream.Stream;

import escape.BetaGameManager;
import escape.EscapeGameBuilder;
import escape.EscapeGameManager;
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
import escape.piece.Movement;
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
        assertEquals(((SquareBoard)((BetaGameManager)emg).getBoard()).getXMax(), 18);
        assertEquals(((SquareBoard)((BetaGameManager)emg).getBoard()).getYMax(), 19);
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

        assertEquals(((OrthoSquareBoard)((BetaGameManager)emg).getBoard()).getXMax(), 20);
        assertEquals(((OrthoSquareBoard)((BetaGameManager)emg).getBoard()).getYMax(), 25);
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
    void getIntAttributesTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(((BetaGameManager)emg).hasPieceAttribute(HORSE, DISTANCE));
        assertFalse(((BetaGameManager)emg).hasPieceAttribute(FROG, FLY));

        assertEquals(10, ((BetaGameManager)emg).getIntPieceAttribute(HORSE, DISTANCE));
        assertEquals(5, ((BetaGameManager)emg).getIntPieceAttribute(FROG, DISTANCE));
        assertEquals(0, ((BetaGameManager)emg).getIntPieceAttribute(FROG, FLY));
    }

    //Test to make sure that integer piece attributes are being stored correctly
    @Test
    void getBoolAttributesTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(((BetaGameManager)emg).getBoolPieceAttribute(HORSE, UNBLOCK));
        assertFalse(((BetaGameManager)emg).getBoolPieceAttribute(FROG, UNBLOCK));
    }

    //Tests to make sure that an exception is thrown if the piece was not defined
    @Test
    void getUndefinedPieceBool(){
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
            EscapeGameManager emg = egb.makeGameManager();
            ((BetaGameManager)emg).getBoolPieceAttribute(SNAIL, UNBLOCK);
        });
    }

    //Tests to make sure that an exception is thrown if the piece was not defined
    @Test
    void getUndefinedPieceInt(){
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
            EscapeGameManager emg = egb.makeGameManager();
            ((BetaGameManager)emg).getIntPieceAttribute(FOX, FLY);
        });
    }

    //Tests to make sure that an exception is thrown if the piece was not defined
    @Test
    void getUndefinedPieceMovement(){
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
            EscapeGameManager emg = egb.makeGameManager();
            ((BetaGameManager)emg).getPieceMovePattern(FOX);
        });
    }

    //Test to make sure that a piece with the unblock attribute should be able to move through blocked locations
    @Test
    void unblockedPieceTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(Movement.isUNBLOCKED(EscapePiece.makePiece(PLAYER1, FROG), (BetaGameManager)emg, 5, 6));

    }

    //Test to make sure that a piece with the unblock attribute should be able to move through blocked locations
    @Test
    void unblockedUNBLOCKPieceTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/SquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(Movement.isUNBLOCKED(EscapePiece.makePiece(PLAYER1, HORSE), (BetaGameManager)emg, 5, 6));
    }


    //ORTHOGONAL//////////////////////////////////////////////////////

    //Tests for determining if A star pathfinding is working for orthogonal movement
    @Test
    void firstOrthoMoveASTARTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/ThreeBlockSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(2, 3), emg.makeCoordinate(4,2)));
    }

    //Tests for determining if A star pathfinding is working for orthogonal movement
    @Test
    void secondOrthoMoveASTARTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/ThreeBlockSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(2, 2), emg.makeCoordinate(4,2)));
    }

    //Tests to check whether jumping is valid for
    @Test
    void checkOrthoJumpGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOrthoJumpTest.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(2, 3), emg.makeCoordinate(4,3)));
    }

    //Tests to check whether jumping is valid for ortho
    @Test
    void checkOrthoJumpBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOrthoJumpTest.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(2, 2), emg.makeCoordinate(4,2)));
    }

    //Tests to check whether jumping is valid for ortho
    @Test
    void checkOrthoSideJumpBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOrthoJumpTest.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(2, 3), emg.makeCoordinate(3,4)));
    }

    //Test to make sure cannot jump over 2 pieces
    @Test
    void checkOrthoDoubleJump()throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOrthoJumpTest.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(14, 14), emg.makeCoordinate(14,11)));
    }
    //Test to make sure cannot jump over 2 pieces
    @Test
    void checkOrthoSingleJump()throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOrthoJumpTest.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(14, 13), emg.makeCoordinate(14,11)));
    }

    //DIAGONAL/////////////////////////////////////////////////

    //Tests to check whether diagonal movement is valid
    @Test
    void checkDiagMoveGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardDiagTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(5, 3), emg.makeCoordinate(6,6)));
    }

    //Tests to check invalid diagonal moves
    @Test
    void checkDiagMoveBadTooFar() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardDiagTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(7, 3), emg.makeCoordinate(6,6)));
    }
    //Tests to check invalid diagonal moves
    @Test
    void checkDiagMoveBadNextTo() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardDiagTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(7, 3), emg.makeCoordinate(7,4)));
    }

    //Tests to check whether blocking works for diagonal
    @Test
    void checkDiagBlockBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardDiagTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(2, 2), emg.makeCoordinate(4,4)));
    }

    //Tests to check whether blocking works for diagonal
    @Test
    void checkDiagBlockGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardDiagTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(2, 3), emg.makeCoordinate(4,5)));
    }

    //Test to ensure jumping works properly for diagonal
    @Test
    void validDiagJump()throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardDiagTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(3, 8), emg.makeCoordinate(1,6)));
    }

    //Test to ensure jumping works properly for diagonal
    @Test
    void invalidDiagJump()throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardDiagTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(4, 8), emg.makeCoordinate(2,6)));
    }


    //OMNI////////////////////////////////////////////////////////////

    //Tests to check whether omni movement is valid
    @Test
    void checkOmniMoveGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOmniTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(2, 2), emg.makeCoordinate(5,6)));
    }

    //Tests to check whether omni movement is valid
    @Test
    void checkOmniMoveBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOmniTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(2, 3), emg.makeCoordinate(5,7)));
    }

    //Tests to check whether omni movement is valid with blocking
    @Test
    void checkOmniBlockBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOmniTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(8, 2), emg.makeCoordinate(6,1)));
    }

    //Tests to check whether omni movement is valid with blocking
    @Test
    void checkOmniBlockGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOmniTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(8, 3), emg.makeCoordinate(6,2)));
    }

    //Tests to check whether omni movement is valid with jumping
    @Test
    void checkOmniJumpBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOmniTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(6, 7), emg.makeCoordinate(8,5)));
    }

    //Tests to check whether omni movement is valid with jumping
    @Test
    void checkOmniJumpGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardOmniTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(8, 2), emg.makeCoordinate(8,4)));
    }

    //LINEAR//////////////////////////////////////////////////////////
    //Checking valid horizontal movement
    @Test
    void linearHorizonalGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(5, 2), emg.makeCoordinate(5,7)));
    }

    //Checking invalid horizontal movement
    @Test
    void linearHorizonalBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(5, 2), emg.makeCoordinate(5,9)));
    }

    //Checking valid vertical movement
    @Test
    void linearVerticalGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(5, 2), emg.makeCoordinate(11,2)));
    }

    //Checking invalid vertical movement
    @Test
    void linearVerticalBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(5, 2), emg.makeCoordinate(12,2)));
    }

    //Checking valid diagonal movement
    @Test
    void linearDiagGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(5, 2), emg.makeCoordinate(1,6)));
    }

    //Checking invalid diagonal movement
    @Test
    void linearDiagBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(5, 2), emg.makeCoordinate(12,9)));
    }

    //Checking invalid linear movement
    @Test
    void linearChangeDirection() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(5, 2), emg.makeCoordinate(4,4)));
    }

    //Checking blocking for linear movement
    @Test
    void linearBlockingBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(5, 2), emg.makeCoordinate(2,2)));
    }

    //Checking blocking for linear movement
    @Test
    void linearBlockingGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(3, 1), emg.makeCoordinate(3,4)));
    }

    //Checking jumping for linear movement
    @Test
    void linearJumpBad() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(1, 1), emg.makeCoordinate(6,1)));
    }

    //Checking jumping for linear movement
    @Test
    void linearJumpGood() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/SquareBoardLinearTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(8, 8), emg.makeCoordinate(12,12)));
    }


    //FLY TESTS/////////////////////////////////////////////////////////////////
    //Test to make sure flying works under normal circumstances
    @Test
    void goodFly() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/FlyTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(5, 5), emg.makeCoordinate(1,1)));
    }

    //Test to make sure flying works over blocked locations
    @Test
    void goodBlockedFly() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/FlyTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(5, 5), emg.makeCoordinate(5,1)));
    }

    //Test to make sure flying works over multiple pieces
    @Test
    void goodJumpFly() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/FlyTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(5, 6), emg.makeCoordinate(8,7)));
    }

    //Test to make sure flying works over multiple pieces
    @Test
    void diagonalFly() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/FlyTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(emg.move(emg.makeCoordinate(3, 6), emg.makeCoordinate(6,3)));
    }

    //Test to make sure flying works over multiple pieces
    @Test
    void invalidDiagonalFly() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/game/astarGameTests/FlyTests.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertFalse(emg.move(emg.makeCoordinate(3, 6), emg.makeCoordinate(6,4)));
    }

}
