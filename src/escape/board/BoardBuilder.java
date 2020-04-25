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

import static escape.board.LocationType.CLEAR;
import java.io.*;
import java.util.function.BiFunction;
import javax.xml.bind.*;

import escape.board.coordinate.*;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;
import escape.util.*;

/**
 * A Builder class for creating Boards. It is only an example and builds
 * just Square boards. If you choose to use this
 * @version Apr 2, 2020
 */
public class BoardBuilder
{
	private BoardInitializer bi;
	/**
	 * The constructor for this takes a file name. It is either an absolute path
	 * or a path relative to the beginning of this project.
	 * @param fileName
	 * @throws Exception 
	 */
	public BoardBuilder(File fileName) throws Exception
	{
		JAXBContext contextObj = JAXBContext.newInstance(BoardInitializer.class);
        Unmarshaller mub = contextObj.createUnmarshaller();
        bi = (BoardInitializer)mub.unmarshal(new FileReader(fileName));
	}

	public BoardBuilder(BoardInitializer bi) throws Exception
	{
		this.bi = bi;
	}

	/**
	 * Method which returns a board object initialized with BoardInitializer
	 * @return the board that has been initialized
	 */
	public Board makeBoard()
	{
		BoardWorker board = null;
		BiFunction<Integer, Integer, Coordinate> makeCoordinate = null;
		switch(bi.getCoordinateId()){
			case SQUARE:
				board = new SquareBoard(bi.getxMax(), bi.getyMax());
				makeCoordinate = (x, y)->SquareCoordinate.makeCoordinate(x, y);
				break;
			case ORTHOSQUARE:
				board = new OrthoSquareBoard(bi.getxMax(), bi.getyMax());
				makeCoordinate = (x, y)->OrthoSquareCoordinate.makeCoordinate(x, y);
				break;
			case HEX:
				board = new HexBoard();
				makeCoordinate = (x, y)->HexCoordinate.makeCoordinate(x, y);
				break;
				default:
					throw new EscapeException("CoordinateID board not found");
		}
		initializeBoard(board, makeCoordinate, bi.getLocationInitializers());
        return board;
	}

	/**
	 * Places the pieces and locations on the board from the gathered file from the BoardIntitializer
	 * @param b the type of board (which has location and coordinate operations)
	 * @param makeCoordinate A lambda BiFunction that holds the coordinate creation method depending on the board type
	 * @param initializers the initializers gathered by the BoardInitializer
	 */
	private void initializeBoard(BoardWorker b, BiFunction makeCoordinate, LocationInitializer... initializers)
	{
		//if not intitializers, return
		if(initializers == null){
			return;
		}
		for (LocationInitializer li : initializers) {
			Coordinate c = (Coordinate)makeCoordinate.apply(li.x, li.y);
			if (li.pieceName != null) {
				b.putPieceAt(new EscapePiece(li.player, li.pieceName), c);
			}
			
			if (li.locationType != null && li.locationType != CLEAR) {
				b.setLocationType(c, li.locationType);
			}
		}
	}
}
