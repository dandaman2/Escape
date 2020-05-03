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
import escape.board.LocationType;
import escape.board.coordinate.*;
import escape.exception.EscapeException;
import escape.piece.*;
import escape.util.PieceTypeInitializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

/**
 * Implementation of the EscapeGameManager interface
 * @param <C> The Coordinate Type to use
 */
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
     * @param board the game board
     * @param makeCoordinate the static method for creating a coordinate of the proper type
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
        for(Movement move: Movement.values()){
            if(move.name().equals(pattern.name())){
                pieceData.put(piece, new PieceData(move, attributes));
            }
        }
    }

    /**
     * Boolean check as to whether rules ahave been defined for a given piece
     * @param piece the piece to check for
     * @return Boolean as to whether the manager contains data for the given piece
     */
    public boolean hasPieceData(PieceName piece){
        return pieceData.containsKey(piece);
    }

    /**
     * See EscapeGameManager definition for details
     * @param from starting location
     * @param to ending location
     * @return True if the move is valid, false otherwise
     */
    @Override
    public boolean move(C from, C to) {
        //Find the moveset for the piece in the from coordinate
        EscapePiece piece = getPieceAt(from);
        EscapePiece capturedPiece = getPieceAt(to);

        //Return false if no piece on moving location or ending location is a block
        if(piece == null || gameBoard.getLocationType(to) == LocationType.BLOCK){
            return false; //no piece found at that location and cannot move to BLOCK location
        }

        //Return false if the location being moved to has a piece of the same player
        if(capturedPiece != null && capturedPiece.getPlayer() == piece.getPlayer()){
            return false;
        }

        //Get the moveset from the piece data and evaluate
        PieceData p = pieceData.get(piece.getName());
        Movement m = p.getMovePattern();
        if(m.isValid(piece, from, to, this)){

            //Remove enemy piece if captured
            if(capturedPiece != null){
                gameBoard.removePieceAt(to);
            }

            //remove current piece if exit
            if(gameBoard.getLocationType(to) == LocationType.EXIT){
                gameBoard.removePieceAt(from);
            }

            //move piece to location
            gameBoard.putPieceAt(gameBoard.removePieceAt(from), to);
            return true;
        }

        return false;
    }

    /**
     * See EscapeGameManager definition for details
     * @param coordinate the location to probe
     * @return The EscapePiece that was obtained at the location
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

    /**
     * Getter for a boolean attribute. Returns null if the piece was not defined
     * @param name the piece name to search
     * @param id the id to search
     * @throws EscapeException if the piece data is not found in the hashmap
     * @return the boolean value of the attribute
     */
    public boolean getBoolPieceAttribute(PieceName name, PieceAttributeID id) throws EscapeException{
        if(!pieceData.containsKey(name)){
            throw new EscapeException("Could not find the piece data for " +name.name()+"");
        }
        return pieceData.get(name).getBoolAttrValue(id);
    }

    /**
     * * Getter for an int attribute. Returns null if the piece was not defined
     * @param name the piece name to search
     * @param id the id of the attribute
     * @return the avlue of the Int attribute
     * @throws EscapeException if the data is not found in the hashmap
     */
    public int getIntPieceAttribute(PieceName name, PieceAttributeID id) throws EscapeException{
        if(!pieceData.containsKey(name)){
            throw new EscapeException("Could not find the piece data for " +name.name()+"");
        }
        return pieceData.get(name).getIntAttrValue(id);
    }

    /**
     * Returns a boolean as to whether the piece contains the given attribute
     * @param name the piece name to search
     * @param id the id to search
     * @throws EscapeException if the piece data is not found in the hashmap
     * @return a boolean saying whether the piece has the attribute
     */
    public boolean hasPieceAttribute(PieceName name, PieceAttributeID id) throws EscapeException{
        if(!pieceData.containsKey(name)){
            throw new EscapeException("Could not find the piece data for " +name.name()+"");
        }
        return pieceData.get(name).hasAttribute(id);
    }

    /**
     * Returns the movement pattern for the
     * @param name the name of the piece to get the pattern from
     * @throws EscapeException if the piece data is not found in the hashmap
     * @return the Movement enum which holds the validity lambda
     */
    public Movement getPieceMovePattern(PieceName name) throws EscapeException{
        if(!pieceData.containsKey(name)){
            throw new EscapeException("Could not find the piece data for " +name.name()+"");
        }
        return pieceData.get(name).getMovePattern();
    }

}
