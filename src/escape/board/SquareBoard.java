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

import java.util.*;

import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;

import static escape.board.LocationType.CLEAR;

/**
 * An example of how a Board might be implemented. This board has
 * square coordinates and finite bounds, represented by xMax and yMax.
 * All methods required by the Board interface have been implemented. Students
 * would naturally add methods based upon their design.
 * @version Apr 2, 2020
 */
public class SquareBoard implements BoardWorker<SquareCoordinate>
{
	public Map<SquareCoordinate, LocationType> squares;
	Map<SquareCoordinate, EscapePiece> pieces;
	
	private final int xMax, yMax;
	public SquareBoard(int xMax, int yMax) throws EscapeException{
		if(xMax < 1 || yMax < 1){
			throw new EscapeException("Cannot create a square board with 0 or negative dimensions");
		}
		this.xMax = xMax;
		this.yMax = yMax;
		pieces = new HashMap<SquareCoordinate, EscapePiece>();
		squares = new HashMap<SquareCoordinate, LocationType>();
	}
	
	/*
	 * @see escape.board.Board#getPieceAt(escape.board.coordinate.Coordinate)
	 */
	@Override
	public EscapePiece getPieceAt(SquareCoordinate coord) throws EscapeException{

		if(coord.getX()>xMax || coord.getY()>yMax || coord.getX()<1 || coord.getY()<1){
			throw new EscapeException("Cannot get piece from outside board range");
		}
		// Map returns piece with key coord, null if key has no mapped value
		return pieces.get(coord);
	}

	/*
	 * @see escape.board.Board#putPieceAt(escape.piece.EscapePiece, escape.board.coordinate.Coordinate)
	 */
	@Override
	public void putPieceAt(EscapePiece p, SquareCoordinate coord) throws EscapeException{
		//Can't put a piece on a BLOCK square or outside of coordinate plane
		if(squares.get(coord)== LocationType.BLOCK ||
				coord.getX()>xMax || coord.getY()>yMax ||
				coord.getX()<1 || coord.getY()<1) {
			throw new EscapeException("Cannot place piece outside of hoard range or on a BLOCK space");

		}else {
			pieces.put(coord, p);
		}

	}

	/**
	 * Sets the LocationType of a given location (default is CLEAR)
	 * @param coord the coordinate to apply the locationType
	 * @param lt the location type to be set to the input coordinate
	 */
	public void setLocationType(SquareCoordinate coord, LocationType lt) throws EscapeException{
		if(coord.getX()>xMax || coord.getY()>yMax || coord.getX()<1 || coord.getY()<1){
			throw new EscapeException("Cannot set coordinate outside board range");
		}
		squares.put(coord, lt);
	}

	/**
	 * Returns the locationType from a given coordinate
	 * @return the LocationType
	 */
	public LocationType getLocationType(SquareCoordinate coord) throws EscapeException{
		if(coord.getX()>xMax || coord.getY()>yMax || coord.getX()<1 || coord.getY()<1){
			throw new EscapeException("Cannot get piece from outside board range");
		}else{
			return squares.containsKey(coord)? squares.get(coord) : CLEAR;
		}
	}

	//Getter for the max x coord of the board
	public int getxMax() {
		return xMax;
	}

	//Getter for the max y coord of the board
	public int getyMax() {
		return yMax;
	}
}
