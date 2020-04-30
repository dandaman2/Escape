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
package escape;

import escape.board.BoardWorker;
import escape.board.HexBoard;
import escape.board.OrthoSquareBoard;
import escape.board.SquareBoard;
import escape.board.coordinate.*;
import escape.exception.EscapeException;
import escape.piece.*;
import escape.util.PieceTypeInitializer;

import javax.management.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

public class BetaGameManager<C extends Coordinate> implements EscapeGameManager<C>{

    //The board itself
    private BoardWorker gameBoard;
    //The type of coordinates that the game board uses
    private CoordinateID coordID;

    //The static function which generates coordinates for the given board type.
    private BiFunction<Integer, Integer, C> makeCoordinate = null;


    //A hashmap which holds the movement patterns and attributes for a given piece
    private HashMap<PieceName, PieceData> pieceData = new HashMap<PieceName, PieceData>();

    /**
     * The default constructor for creating the game manager
     */
    public BetaGameManager(BoardWorker board, BiFunction<Integer, Integer, C> makeCoordinate){
        this.gameBoard = board;
        this.makeCoordinate = makeCoordinate;
    }

    /**
     * Adds a moveset to the game based on the PieceName's movement type
     * @param piece the piece to add the moveset to
     * @param pattern the moveset to add
     */
    public void addPieceData(PieceName piece, MovementPatternID pattern, ArrayList<PieceTypeInitializer.PieceAttribute> attributes){
        for(Movements move: Movements.values()){
            if(move.name().equals(pattern.name())){
                pieceData.put(piece, new PieceData(move, attributes));
            }
        }
    }

    /**
     * See EscapeGameManager definition for details
     * @param from starting location
     * @param to ending location
     * @return
     */
    @Override
    public boolean move(C from, C to) {
        //Find the moveset for the piece in the from coordinate
        EscapePiece piece = getPieceAt(from);
        if(piece == null){
            return false; //no piece found at that location
        }

        //Get the piece's moveset
        //this.movesets.get(piece.getName()).isValid();

        return false;
    }

    /**
     * See EscapeGameManager definition for details
     * @param coordinate the location to probe
     * @return
     */
    @Override
    public EscapePiece getPieceAt(C coordinate) {
        return gameBoard.getPieceAt(coordinate);
    }

    /**
     * See EscapeGameManager definition for details
     * @param x the x component of the coordinate to generate
     * @param y the y component of the coordinate to generate
     * @return Coordinate of the Given type
     */
    @Override
    public C makeCoordinate(int x, int y) {
        return makeCoordinate.apply(x, y);
    }

    /**
     * Getter for Coordinate ID
     * @return the coordinate ID of the game's board
     */
    public CoordinateID getCoordID() {
        return coordID;
    }

    /**
     * Setter for CoordID
     * @param coordID
     */
    public void setCoordID(CoordinateID coordID) {
        this.coordID = coordID;
    }

    /**
     * Getter for the game board
     * @return the game board object
     */
    public BoardWorker getBoard() {
        return gameBoard;
    }

    //Getter for a boolean attribute. Returns null if the piece was not defined
    public boolean getBoolPieceAttribute(PieceName name, PieceAttributeID id){
        return pieceData.get(name).getBoolAttrValue(id);
    }

    //Getter for an int attribute. Returns null if the piece was not defined
    public int getIntPieceAttribute(PieceName name, PieceAttributeID id){
        return pieceData.get(name).getIntAttrValue(id);
    }

}
