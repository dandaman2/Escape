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
import escape.EscapeGameManager;
import escape.board.BoardWorker;
import escape.board.coordinate.Coordinate;
import escape.board.coordinate.Coordinate2D;
import escape.board.coordinate.CoordinateID;

import java.util.*;

/**
 * A functional interface to define a lambda which check the validity of a given move on a given board from
 * Coordinate from to Coordinate to.
 */
@FunctionalInterface
interface MovePattern {
    boolean isValid(EscapePiece piece, Coordinate from, Coordinate to, BetaGameManager manager);
}

/**
 * An enumeration of the different movement patterns, which take the Piece's attributes into account
 * Each enumeration hold a lambda variable which defines the rules for the given movement type.
 */
public enum Movements implements MovePattern{
    ORTHAGONAL((piece, from, to, manager)->{
//        if(manager.getCoordID() == CoordinateID.HEX){
//            return false; //not valid on hex boards
//        }

        //return validHorizMove(piece, (Coordinate2D)from, (Coordinate2D)to, manager);
    return false;
    }),
    LINEAR((piece, board, from, to)->{
        return false;
    }),
    DIAGONAL((piece, board, from, to)->{
        return false;
    }),
    OMNI((piece, board, from, to)->{
        return false;
    });

    //The variable which hold a validity check function
    private MovePattern movement;

    /**
     * Constructor for a  type of movement. This determines how the piece can move around the board
     * @param pattern
     */
    Movements(MovePattern pattern){
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
     * Returns a boolean as to whether a valid horizontal move can
     * be made between the two input coordinates
     * @param piece the piece to be checked or moved
     * @param from the first input coordinate to check
     * @param to the second input coordinate to check
     * @param manager the game state
     * @return True if a valid move can be made, False if the coordinates are not on the same horizontal plane,
     * or there are other pieces in the way.
     */
    public static boolean validHorizMove(EscapePiece piece, Coordinate2D from, Coordinate2D to, BetaGameManager manager){
        int start, end;
        if(from.getY() < to.getY()){
            start = from.getY() + 1;
            end = to.getY();
        }else{
            start = to.getY() + 1;
            end = from.getY();
        }
        for(int i = start; i<end; i++){
            if(manager.getPieceAt(manager.makeCoordinate(from.getX(), i)) != null){
                return false;
            }
        }
        return true;
    }

    /**
     * A static function which determines if there are any paths to the given location.
     * Based on movement type.
     * @param start the coordinate to move from
     * @param end the coordinate to move to
     * @param manager
     * @return A list of coordinates that is the shortest path
     */
    public static ArrayList<Coordinate2D>getPath(Coordinate2D start, Coordinate2D end, BetaGameManager manager){
      return null;
    }

    // Class to represent a node in the graph
    static class Node implements Comparator<Node> {
        public Coordinate2D node;
        public int cost;

        public Node(){}

        public Node(Coordinate2D node, int cost) {
            this.node = node;
            this.cost = cost;
        }

        @Override
        public int compare(Node node1, Node node2) {
            if (node1.cost < node2.cost)
                return -1;
            if (node1.cost > node2.cost)
                return 1;
            return 0;
        }
    }

}
