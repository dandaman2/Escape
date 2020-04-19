/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Copyright ©2016-2020 Gary F. Pollice
 *******************************************************************************/
package escape.board;

import static escape.board.LocationType.BLOCK;
import static escape.board.LocationType.CLEAR;
import static escape.board.LocationType.EXIT;
import static escape.piece.PieceName.HORSE;
import static escape.piece.Player.PLAYER1;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.stream.Stream;

import escape.board.coordinate.HexCoordinate;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The test file for testing generic board building
 * @version Apr 2, 2020
 */
class BoardTest
{
	
	@Test
	void buildBoard1() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/BoardConfig1.xml"));
		assertNotNull(bb.makeBoard());
		// Now I will do some tests on this board and its contents.
	}


	//////Square Boards//////////////////////////////////////////////////////
	@Test
	void buildSquareBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoards/SquareBoardConfig1.xml"));
		assertNotNull(bb.makeBoard());
	}

	//checks to make sure a board is created properly
	@Test
	void buildSquareBoard() throws Exception{
		SquareBoard sb = new SquareBoard(5, 5);
		assertNotNull(sb);
	}

	static Stream<Arguments> invalidSquareDimensionsHelper(){
		return Stream.of(
				Arguments.of(0, 0),
				Arguments.of(-1, 0),
				Arguments.of(0, -1),
				Arguments.of(-1, -1),
				Arguments.of(-5, -3)
		);
	}

	//Cannot create square board with invalid inputs
	@ParameterizedTest
	@MethodSource("invalidSquareDimensionsHelper")
	void buildInvalidSquareBoard(int x, int y) throws Exception{
		assertThrows(EscapeException.class, ()->{
			SquareBoard sb = new SquareBoard(x, y);
		});

	}


	//Check that locationtypes can be added and retrieved
	@ParameterizedTest
	@EnumSource(LocationType.class)
	void getLocationSquareBoard(LocationType t){
		SquareBoard sb = new SquareBoard(8, 8);
		sb.setLocationType(SquareCoordinate.makeCoordinate(4, 4), t);
		assertEquals(sb.getLocationType(SquareCoordinate.makeCoordinate(4, 4)), t);
	}

	//the default location is clear
	@Test
	void defaultCLEARSquareBoard(){
		SquareBoard sb = new SquareBoard(8, 8);
		assertEquals(sb.getLocationType(SquareCoordinate.makeCoordinate(1, 1)), CLEAR);
	}


	static Stream<Arguments> invalidSquareCoordsHelper(){
		return Stream.of(
				Arguments.of(0, 0),
				Arguments.of(-1, 0),
				Arguments.of(0, -1),
				Arguments.of(-1, -1),
				Arguments.of(8, 9),
				Arguments.of(9, 8)
		);
	}

	//Cannot set a locationType of an invalid coordinate
	@ParameterizedTest
	@MethodSource("invalidSquareCoordsHelper")
	void setInvalidSquareLocationType(int x, int y){
		assertThrows(EscapeException.class, ()->{
			SquareBoard sb = new SquareBoard(8, 8);
			sb.setLocationType(SquareCoordinate.makeCoordinate(x, y), BLOCK);
		});
	}

	//Placing and getting a piece at a location
	@Test
	void validPlaceSquarePieceTest(){
		SquareBoard sb = new SquareBoard(8, 8);
		sb.putPieceAt(new EscapePiece(PLAYER1, HORSE), SquareCoordinate.makeCoordinate(4,4 ));
		EscapePiece p = sb.getPieceAt(SquareCoordinate.makeCoordinate(4, 4));
		assertTrue(p.getName() == HORSE && p.getPlayer() == PLAYER1);
	}

	//Cannot get a piece that was not placed (should be null)
	@Test
	void getNullPieceSquareTest(){
		SquareBoard sb = new SquareBoard(8, 8);
		assertNull(sb.getPieceAt(SquareCoordinate.makeCoordinate(4, 4)));
	}

	//Cannot get or put piece outside of the board
	@ParameterizedTest
	@MethodSource("invalidSquareCoordsHelper")
	void putOutsidePieceSquareTest(int x, int y){
		assertThrows(EscapeException.class, ()->{
			SquareBoard sb = new SquareBoard(8, 8);
			sb.putPieceAt(new EscapePiece(PLAYER1, HORSE), SquareCoordinate.makeCoordinate(x, y));
		});
	}

	//Cannot get or put piece outside of the board
	@ParameterizedTest
	@MethodSource("invalidSquareCoordsHelper")
	void getOutsidePieceSquareTest(int x, int y){
		assertThrows(EscapeException.class, ()->{
			SquareBoard sb = new SquareBoard(8, 8);
			sb.getPieceAt(SquareCoordinate.makeCoordinate(x,y ));
		});
	}

	//Cannot place a piece that is at a BLOCK location
	@Test
	void placePieceAtBLOCKSquareTest(){
		assertThrows(EscapeException.class, ()->{
			SquareBoard sb = new SquareBoard(8, 8);
			sb.setLocationType(SquareCoordinate.makeCoordinate(4, 4), BLOCK);
			sb.putPieceAt(new EscapePiece(PLAYER1, HORSE), SquareCoordinate.makeCoordinate(4, 4
			));
		});
	}


	//////OrthoSquare Boards//////////////////////////////////////////////////////
	//Creation of orthosquare board
	@Test
	void buildOrthoSquareBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoards/OrthoSquareBoardConfig1.xml"));
		assertNotNull(bb.makeBoard());
	}

	//checks to make sure a board is created properly
	@Test
	void buildOrthoSquareBoard() throws Exception{
		OrthoSquareBoard osb = new OrthoSquareBoard(5, 5);
		assertNotNull(osb);
	}

	static Stream<Arguments> invalidOrthoSquareDimensionsHelper(){
		return Stream.of(
				Arguments.of(0, 0),
				Arguments.of(-1, 0),
				Arguments.of(0, -1),
				Arguments.of(-1, -1),
				Arguments.of(-5, -3)
		);
	}

	//Cannot create an orthosquare board with invalid inputs
	@ParameterizedTest
	@MethodSource("invalidOrthoSquareDimensionsHelper")
	void buildInvalidOrthoSquareBoard(int x, int y) throws Exception{
		assertThrows(EscapeException.class, ()->{
			OrthoSquareBoard osb = new OrthoSquareBoard(x, y);
		});

	}


	//Cannot affirm that every location type can be stored and gathered
	@ParameterizedTest
	@EnumSource(LocationType.class)
	void getLocationOrthoSquareBoard(LocationType t){
		OrthoSquareBoard osb = new OrthoSquareBoard(8, 8);
		osb.setLocationType(OrthoSquareCoordinate.makeCoordinate(4, 4), t);
		assertEquals(osb.getLocationType(OrthoSquareCoordinate.makeCoordinate(4, 4)), t);
	}

	//the default location is clear
	@Test
	void defaultCLEAROrthoSquareBoard(){
		OrthoSquareBoard sb = new OrthoSquareBoard(8, 8);
		assertEquals(sb.getLocationType(OrthoSquareCoordinate.makeCoordinate(1, 1)), CLEAR);
	}


	static Stream<Arguments> invalidOrthoSquareCoordsHelper(){
		return Stream.of(
				Arguments.of(0, 0),
				Arguments.of(-1, 0),
				Arguments.of(0, -1),
				Arguments.of(-1, -1),
				Arguments.of(8, 9),
				Arguments.of(9, 8)
		);
	}

	//Cannot set a locationType of an invalid coordinate
	@ParameterizedTest
	@MethodSource("invalidOrthoSquareCoordsHelper")
	void setInvalidOthoSquareLocationType(int x, int y){
		assertThrows(EscapeException.class, ()->{
			OrthoSquareBoard osb = new OrthoSquareBoard(8, 8);
			osb.setLocationType(OrthoSquareCoordinate.makeCoordinate(x, y), BLOCK);
		});
	}

	//Placing and getting a piece at a location
	@Test
	void validPlaceOrthoSquarePieceTest(){
		OrthoSquareBoard osb = new OrthoSquareBoard(8, 8);
		osb.putPieceAt(new EscapePiece(PLAYER1, HORSE), OrthoSquareCoordinate.makeCoordinate(4,4 ));
		EscapePiece p = osb.getPieceAt(OrthoSquareCoordinate.makeCoordinate(4, 4));
		assertTrue(p.getName() == HORSE && p.getPlayer() == PLAYER1);
	}

	//Cannot get a piece that was not placed (should be null)
	@Test
	void getNullPieceOrthoSquareTest(){
		OrthoSquareBoard osb = new OrthoSquareBoard(8, 8);
		assertNull(osb.getPieceAt(OrthoSquareCoordinate.makeCoordinate(2, 2)));
	}

	//Cannot put piece outside of the board
	@ParameterizedTest
	@MethodSource("invalidOrthoSquareCoordsHelper")
	void putOutsidePieceOrthoSquareTest(int x, int y){
		assertThrows(EscapeException.class, ()->{
			OrthoSquareBoard osb = new OrthoSquareBoard(8, 8);
			osb.putPieceAt(new EscapePiece(PLAYER1, HORSE), OrthoSquareCoordinate.makeCoordinate(x, y));
		});
	}

	//Cannot get piece outside of the board
	@ParameterizedTest
	@MethodSource("invalidOrthoSquareCoordsHelper")
	void getOutsidePieceOrthoSquareTest(int x, int y){
		assertThrows(EscapeException.class, ()->{
			OrthoSquareBoard osb = new OrthoSquareBoard(8, 8);
			osb.getPieceAt(OrthoSquareCoordinate.makeCoordinate(x, y));
		});
	}

	//Cannot place a piece that is at a BLOCK location
	@Test
	void placePieceAtBLOCKOrthoSquareTest(){
		assertThrows(EscapeException.class, ()->{
			OrthoSquareBoard osb = new OrthoSquareBoard(8, 8);
			osb.setLocationType(OrthoSquareCoordinate.makeCoordinate(4, 4), BLOCK);
			osb.putPieceAt(new EscapePiece(PLAYER1, HORSE), OrthoSquareCoordinate.makeCoordinate(4, 4
			));
		});
	}


	//////Hex Boards//////////////////////////////////////////////////////
	//Creation of hex board
	@Test
	void buildHexBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/HexBoards/HexBoardConfig1.xml"));
		assertNotNull(bb.makeBoard());
	}

	//checks to make sure a board is created properly
	@Test
	void buildHexBoard(){
		HexBoard hb = new HexBoard();
		assertNotNull(hb);
	}


	//Test to ensure that a location can be get and set correctly
	@ParameterizedTest
	@EnumSource(LocationType.class)
	void GetSetHexLocationTypes(LocationType type){
		HexBoard hb = new HexBoard();
		hb.setLocationType(HexCoordinate.makeCoordinate(-1, -1), type);
		assertEquals(hb.getLocationType(HexCoordinate.makeCoordinate(-1, -1)), type);
	}

	//the default location is clear
	@Test
	void defaultCLEARHexBoard(){
		HexBoard hb = new HexBoard();
		assertEquals(hb.getLocationType(HexCoordinate.makeCoordinate(1, 1)), CLEAR);
	}

	//Placing and getting a piece at a location
	@Test
	void validPlaceHexPieceTest(){
		HexBoard hb = new HexBoard();
		hb.putPieceAt(new EscapePiece(PLAYER1, HORSE), HexCoordinate.makeCoordinate(4,4));
		EscapePiece p = hb.getPieceAt(HexCoordinate.makeCoordinate(4, 4));
		assertTrue(p.getName() == HORSE && p.getPlayer() == PLAYER1);
	}

	//Cannot get a piece that was not placed (should be null)
	@Test
	void getNullPieceHexTest(){
		HexBoard hb = new HexBoard();
		assertNull(hb.getPieceAt(HexCoordinate.makeCoordinate(2, 2)));
	}


	//Cannot place a piece that is at a BLOCK location
	@Test
	void placePieceAtBLOCKHexTest(){
		assertThrows(EscapeException.class, ()->{
			HexBoard hb = new HexBoard();
			hb.setLocationType(HexCoordinate.makeCoordinate(4, 4), BLOCK);
			hb.putPieceAt(new EscapePiece(PLAYER1, HORSE), HexCoordinate.makeCoordinate(4, 4
			));
		});
	}

	//BoardBuilder Tests///////////////////////////////////////////////////////////
	//Testing the creation of a Square board
	@Test
	void BBSquareBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoards/SquareBoardConfig1.xml"));
		assertTrue(bb.makeBoard() instanceof SquareBoard);
	}

	//Ensuring that a piece is placed properly on the board
	@Test
	void GetPieceSquareBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoards/SquareBoardConfig1.xml"));
		SquareBoard sb = (SquareBoard)bb.makeBoard();
		assertEquals(sb.getPieceAt(SquareCoordinate.makeCoordinate(2,2)).getName(), HORSE);
	}

	//Ensuring that a location is placed properly on the board
	@Test
	void GetLocationSquareBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoards/SquareBoardConfig1.xml"));
		SquareBoard sb = (SquareBoard)bb.makeBoard();
		assertEquals(sb.getLocationType(SquareCoordinate.makeCoordinate(3,5)), BLOCK);
	}

	//Testing the creation of an OrthoSquare board
	@Test
	void BBOrthoSquareBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoards/OrthoSquareBoardConfig1.xml"));
		assertTrue(bb.makeBoard() instanceof OrthoSquareBoard);
	}

	//Ensuring that a piece is placed properly on the board
	@Test
	void GetPieceOrthoSquareBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoards/OrthoSquareBoardConfig1.xml"));
		OrthoSquareBoard osb = (OrthoSquareBoard)bb.makeBoard();
		assertEquals(osb.getPieceAt(OrthoSquareCoordinate.makeCoordinate(2,2)).getName(), HORSE);
	}

	//Ensuring that a location is placed properly on the board
	@Test
	void GetLocationOrthoSquareBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoards/OrthoSquareBoardConfig1.xml"));
		OrthoSquareBoard osb = (OrthoSquareBoard)bb.makeBoard();
		assertEquals(osb.getLocationType(OrthoSquareCoordinate.makeCoordinate(3,5)), BLOCK);
	}

	//Testing the creation of a hex board
	@Test
	void BBHexBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/HexBoards/HexBoardConfig1.xml"));
		assertTrue(bb.makeBoard() instanceof HexBoard);
	}

	//Ensuring that a piece is placed properly on the board
	@Test
	void GetPieceHexBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/HexBoards/HexBoardConfig1.xml"));
		HexBoard hb = (HexBoard)bb.makeBoard();
		assertEquals(hb.getPieceAt(HexCoordinate.makeCoordinate(0,0)).getName(), HORSE);
	}

	//Ensuring that a location is placed properly on the board
	@Test
	void GetLocationHexBoardFromFile() throws Exception
	{
		BoardBuilder bb = new BoardBuilder(new File("config/board/HexBoards/HexBoardConfig1.xml"));
		HexBoard hb = (HexBoard)bb.makeBoard();
		assertEquals(hb.getLocationType(HexCoordinate.makeCoordinate(3,5)), BLOCK);
	}
}
