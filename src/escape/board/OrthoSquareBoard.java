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

import escape.board.coordinate.Coordinate;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;

import static escape.board.LocationType.CLEAR;

/**
 * Similar implementation for Square board, but with OrthoSquare limitations
 * @version Apr 2, 2020
 */
public class OrthoSquareBoard implements BoardWorker<OrthoSquareCoordinate>, BoundedBoard
{
    Map<OrthoSquareCoordinate, LocationType> squares;
    Map<OrthoSquareCoordinate, EscapePiece> pieces;

    private final int xMax, yMax;
    public OrthoSquareBoard(int xMax, int yMax) throws EscapeException{
        if(xMax < 1 || yMax < 1){
            throw new EscapeException("Cannot create an orthosquare board with 0 or negative dimensions");
        }
        this.xMax = xMax;
        this.yMax = yMax;
        pieces = new HashMap<OrthoSquareCoordinate, EscapePiece>();
        squares = new HashMap<OrthoSquareCoordinate, LocationType>();
    }

    /*
     * @see escape.board.Board#getPieceAt(escape.board.coordinate.Coordinate)
     */
    @Override
    public EscapePiece getPieceAt(OrthoSquareCoordinate coord) throws EscapeException{

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
    public void putPieceAt(EscapePiece p, OrthoSquareCoordinate coord) throws EscapeException{
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
    public void setLocationType(OrthoSquareCoordinate coord, LocationType lt) throws EscapeException{
        if(coord.getX()>xMax || coord.getY()>yMax || coord.getX()<1 || coord.getY()<1){
            throw new EscapeException("Cannot set coordinate outside board range");
        }
        squares.put(coord, lt);
    }

    /**
     * Returns the locationType from a given coordinate
     * @return the LocationType
     */
    public LocationType getLocationType(OrthoSquareCoordinate coord) throws EscapeException{
        if(coord.getX()>xMax || coord.getY()>yMax || coord.getX()<1 || coord.getY()<1){
            throw new EscapeException("Cannot get piece from outside board range");
        }else{
           return squares.containsKey(coord)? squares.get(coord) : CLEAR;
        }
    }

    /**
     * Removes a piece from the board and returns it
     * @param c the coordinate to remove the piece from
     * @return The piece that was removed, or null if no piece was at the given coordinate
     */
    @Override
    public EscapePiece removePieceAt(OrthoSquareCoordinate c) {
        return pieces.remove(c);
    }

    //Getter for the max x coord of the board
    public int getXMax() {
        return xMax;
    }

    //Getter for the max y coord of the board
    public int getYMax() {
        return yMax;
    }
}
