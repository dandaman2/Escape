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
package escape.piece;

import escape.BetaGameManager;
import escape.board.BoundedBoard;
import escape.board.OrthoSquareBoard;
import escape.board.coordinate.Coordinate;
import escape.board.coordinate.Coordinate2D;
import escape.board.coordinate.CoordinateID;

import java.util.*;

import static escape.board.LocationType.BLOCK;
import static escape.board.LocationType.EXIT;
import static escape.piece.Pathfinding.*;
import static escape.piece.PieceAttributeID.*;

/**
 * A functional interface to define a lambda which check the validity of a given move by a given piece on a given board
 * from Coordinate from to Coordinate to.
 */
@FunctionalInterface
interface MovePattern {
    boolean isValid(EscapePiece piece, Coordinate from, Coordinate to, BetaGameManager manager);
}

/**
 * A functional interface to define a lambda which gets the appropriate neighbors in the AStar algorithm.
 * This checks to make sure that a given change in coordinates is valid for the given movement type, and on the
 * manager's board.
 */
@FunctionalInterface
interface Neighbors {
    boolean isNeighbor(BetaGameManager manager, int xChange, int yChange);
}

/**
 * An enumeration of the different movement patterns, which take the Piece's attributes into account
 * Each enumeration hold a lambda variable which defines the rules for the given movement type.
 */
public enum Movement implements MovePattern{
    ORTHOGONAL((piece, from, to, manager)->{
        if(manager.getCoordID() == CoordinateID.HEX ||
                getAStarPath(piece, (Coordinate2D)from, (Coordinate2D)to, manager,
                        Movement::checkOrthoNeighbors) == null){
            return false; //false if on hex or no path found
        }
        return true;
    }),

    LINEAR((piece, from, to, manager)->{
       return isLinearPath(piece, from, to, manager);
    }),

    DIAGONAL((piece, from, to, manager)->{
        if(manager.getCoordID() != CoordinateID.SQUARE ||
                getAStarPath(piece, (Coordinate2D)from, (Coordinate2D)to, manager,
                        Movement::checkDiagNeighbors) == null){
            return false; //false if not on square or no path found
        }
        return true;
    }),

    OMNI((piece, from, to, manager)->{
        return getAStarPath(piece, (Coordinate2D)from, (Coordinate2D)to, manager,
                Movement::checkOmniNeighbors) != null;
    });

    //The variable which holds a validity check function
    private MovePattern movement;

    /**
     * Constructor for a  type of movement. This determines how the piece can move around the board
     * @param pattern
     */
    Movement(MovePattern pattern){
        movement = pattern;
    }

    /**
     * Function which determines if a given move is valid. Returns the definition lambda assigned to the Movement
     * @param piece the piece to move/check
     * @param from the coordinate to move from
     * @param to the coordinate to move to
     * @return Boolean if the move is valid.
     * @param manager the game manager
     */
    @Override
    public boolean isValid(EscapePiece piece, Coordinate from, Coordinate to, BetaGameManager manager) {
        return movement.isValid(piece, from, to, manager);
    }

    /**
     * Returns true if the change in x or y for the manager's board is valid.
     * For orthogonal movement checking
     * @param manager the game manager
     * @param xChange the x change in value
     * @param yChange the y change in value
     * @return a boolean which determines if the x and y changes are valid neighbors, assuming a (0,0)->(xChange, yChange) start
     */
    public static boolean checkOrthoNeighbors(BetaGameManager manager, int xChange, int yChange){
        if(!(manager.getBoard() instanceof BoundedBoard)){
            return xChange == yChange && xChange != 0;
        }
        return !(xChange != 0 && yChange != 0);
    }

    /**
     * Returns true if the change in x or y for the manager's board is valid.
     * For diagonal movement checking
     * @param manager the game manager
     * @param xChange the x change in value
     * @param yChange the y change in value
     * @return a boolean which determines if the x and y changes are valid neighbors, assuming a (0,0)->(xChange, yChange) start
     */
    public static boolean checkDiagNeighbors(BetaGameManager manager, int xChange, int yChange){
        return xChange != 0 && yChange != 0;
    }

    /**
     * Returns true if the change in x or y for the manager's board is valid.
     * For omni movement checking
     * @param manager the game manager
     * @param xChange the x change in value
     * @param yChange the y change in value
     * @return a boolean which determines if the x and y changes are valid neighbors, assuming a (0,0)->(xChange, yChange) start
     */
    public static boolean checkOmniNeighbors(BetaGameManager manager, int xChange, int yChange){
        if((manager.getBoard() instanceof OrthoSquareBoard)){
            return  checkOrthoNeighbors(manager, xChange, yChange);
        }
        if(!(manager.getBoard() instanceof BoundedBoard)){ // For hex boards
            return !((xChange == yChange) && xChange !=0);
        }
        return true;
    }
}
