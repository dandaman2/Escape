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
//Dan Duff (dfduff)
package escape.board;

import escape.board.coordinate.Coordinate;
import escape.board.coordinate.HexCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;

import java.util.HashMap;
import java.util.Map;

import static escape.board.LocationType.CLEAR;

public class HexBoard implements BoardWorker<HexCoordinate>{
    Map<HexCoordinate, LocationType> hexes;
    Map<HexCoordinate, EscapePiece> pieces;

    public HexBoard(){
        pieces = new HashMap<HexCoordinate, EscapePiece>();
        hexes = new HashMap<HexCoordinate, LocationType>();
    }

    /*
     * @see escape.board.Board#getPieceAt(escape.board.coordinate.Coordinate)
     */
    @Override
    public EscapePiece getPieceAt(HexCoordinate coord){
        // Map returns piece with key coord, null if key has no mapped value
        return pieces.get(coord);
    }

    /*
     * @see escape.board.Board#putPieceAt(escape.piece.EscapePiece, escape.board.coordinate.Coordinate)
     */
    @Override
    public void putPieceAt(EscapePiece p, HexCoordinate coord) throws EscapeException{
        //Can't put a piece on a BLOCK hex
        if(hexes.get(coord)== LocationType.BLOCK){
            throw new EscapeException("Cannot place piece on a BLOCK space");
        }else {
            pieces.put(coord, p);
        }

    }

    /**
     * Sets the LocationType of a given location (default is CLEAR)
     * @param coord the coordinate to apply the locationType
     * @param lt the location type to be set to the input coordinate
     */
    public void setLocationType(HexCoordinate coord, LocationType lt){
        hexes.put(coord, lt);
    }

    /**
     * Returns the locationType from a given coordinate
     * @return the LocationType
     */
    public LocationType getLocationType(HexCoordinate coord){
        return hexes.containsKey(coord)? hexes.get(coord) : CLEAR;
    }

    /**
     * Removes a piece from the board and returns it
     * @param c the coordinate to remove the piece from
     * @return The piece that was removed, or null if no piece was at the given coordinate
     */
    @Override
    public EscapePiece removePieceAt(HexCoordinate c) {
        return pieces.remove(c);
    }
}
