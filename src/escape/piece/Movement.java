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
import escape.board.coordinate.Coordinate;
import escape.board.coordinate.Coordinate2D;
import escape.board.coordinate.CoordinateID;

import java.util.*;

import static escape.board.LocationType.BLOCK;
import static escape.piece.PieceAttributeID.DISTANCE;
import static escape.piece.PieceAttributeID.JUMP;
import static escape.piece.PieceAttributeID.UNBLOCK;

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
 * Includes the currently-analyzed node, the open and closed node lists, the game manager, and the piece being moved
 */
@FunctionalInterface
interface Neighbors {
    ArrayList<Movement.Node> getNeighbors(Movement.Node now,
                                          ArrayList<Movement.Node> open, ArrayList<Movement.Node> closed,
                                          BetaGameManager manager, EscapePiece piece);
}

/**
 * An enumeration of the different movement patterns, which take the Piece's attributes into account
 * Each enumeration hold a lambda variable which defines the rules for the given movement type.
 */
public enum Movement implements MovePattern{
    ORTHOGONAL((piece, from, to, manager)->{

        if(manager.getCoordID() == CoordinateID.HEX ||
                getPath(piece, (Coordinate2D)from, (Coordinate2D)to, manager,
                        Movement::addStandardNeighborsToOpenList) == null){
            return false; //false if on hex or no path found
        }
        return true;
    }),
    LINEAR((piece, from, to, manager)->{
        return false;
    }),
    DIAGONAL((piece, from, to, manager)->{
        if(manager.getCoordID() != CoordinateID.SQUARE ||
                getPath(piece, (Coordinate2D)from, (Coordinate2D)to, manager,
                        Movement::addDiagonalNeighborsToOpenList) == null){
            return false; //false if not on square or no path found
        }
        return true;
    }),
    OMNI((piece, from, to, manager)->{
        return getPath(piece, (Coordinate2D)from, (Coordinate2D)to, manager,
                Movement::addStandardNeighborsToOpenList) != null;
    });

    //The variable which hold a validity check function
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
     * @return A list of coordinates that is the shortest path. Null if no path found
     */
    public static ArrayList<Coordinate2D>getPath(EscapePiece piece, Coordinate2D start, Coordinate2D end,
                                                 BetaGameManager manager, Neighbors neighbors){
        ArrayList<Node> open = new ArrayList<>();
        ArrayList<Node> closed = new ArrayList<>();
        ArrayList<Node> path = new ArrayList<>();
        ArrayList<Coordinate2D> coordinatePath = new ArrayList<>();
        int xstart = start.getX();
        int ystart = start.getY();
        int xend = end.getX();
        int yend = end.getY();
        Node now = new Node(null, xstart, ystart, 0, 0);
        closed.add(now);
        open = neighbors.getNeighbors(now, open, closed, manager, piece);
        while (now.x != xend || now.y != yend) {
            if (open.isEmpty()) { // Nothing to examine, return null for no path
                return null;
            }
            now = open.get(0); // get first node (lowest cost)
            open.remove(0); // remove it
            closed.add(now); // and add to the closed
            open = neighbors.getNeighbors(now, open, closed, manager, piece);
        }
        path.add(0, now);
        while (now.x != xstart || now.y != ystart) {
            now = now.parent;
            path.add(0, now);
        }

        //If the path is too long, return null
        if(path.size()-1>manager.getIntPieceAttribute(piece.getName(), DISTANCE)){
            return null;
        }

        //Convert from nodes to proper coordinates
        for(Node n : path){
            System.out.println(n.x+","+n.y);
            coordinatePath.add((Coordinate2D)manager.makeCoordinate(n.x, n.y));
        }
        return coordinatePath;
    }

    /**
     * Adds neighbors to the openlist in the ASTAR check. For Otho and omni movement
     * @param now the current node to check
     * @param open the current openlist
     * @param manager the game manager implementation
     * @param piece the piece to evaluate for attributes
     * @return the list of new neighbor nodes
     */
    public static ArrayList<Node> addStandardNeighborsToOpenList(Node now, ArrayList<Node> open, ArrayList<Node> closed,
                                                                 BetaGameManager manager, EscapePiece piece) {
        Node node;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (shouldSkip(piece, manager, x, y)) {
                    continue; // skip if diagonal movement is not allowed
                }
                node = new Node(now, now.x + x, now.y + y, now.g,
                        manager.makeCoordinate(now.x, now.y).distanceTo(manager.makeCoordinate(x, y)));
                if ((x != 0 || y != 0) // not "now" Node
                        && checkBoundaries(now, x, y, manager) // check if board boundaries
                        && isUNBLOCKED(piece, manager, now.x+x, now.y+y) // check if space is BLOCKed or has a piece
                        && canJump(piece, now,now.x+x, now.y+y, manager) // check if jumpable
                        && !findNeighborInList(open, node) && !findNeighborInList(closed, node)) { // if not already done
                    node.g = node.parent.g + 1; // Add 1 to cost
                    node.justJumped = hasPiece(now.x+x, now.y+y, manager);
                    open.add(node);
                }
            }
        }
        Collections.sort(open);
        return open;
    }

    /**
     * Adds neighbors to the openlist in the ASTAR check. For Diagonal movement
     * @param now the current node to check
     * @param open the current openlist
     * @param manager the game manager implementation
     * @param piece the piece to evaluate for attributes
     * @return
     */
    public static ArrayList<Node> addDiagonalNeighborsToOpenList(Node now, ArrayList<Node> open, ArrayList<Node> closed,
                                                                   BetaGameManager manager, EscapePiece piece) {
        Node node;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x != 0 && y != 0) { // Only check neighbors
                    node = new Node(now, now.x + x, now.y + y, now.g,
                            manager.makeCoordinate(now.x, now.y).distanceTo(manager.makeCoordinate(x, y)));
                    if ((x != 0 || y != 0) // not "now" Node (should always be true)
                            && checkBoundaries(now, x, y, manager) // check if board boundaries
                            && isUNBLOCKED(piece, manager, now.x + x, now.y + y) // check if space is BLOCKed or has a piece
                            && canJump(piece, now, now.x + x, now.y + y, manager) // check if jumpable
                            && !findNeighborInList(open, node) && !findNeighborInList(closed, node)) { // if not already done
                        node.g = node.parent.g + 1; // Add 1 to cost
                        node.justJumped = hasPiece(now.x + x, now.y + y, manager);
                        open.add(node);
                    }
                }
            }
        }
        Collections.sort(open);
        return open;
    }

    /**
     * Checks if the algorithm should consider boundaries when evaluating neighbors
     * @param now the current node
     * @param x the x coordinate of the node to check
     * @param y hte y coordinate of the node to check
     * @param manager the game manager
     * @return a boolean if the check is within bounds
     */
    public static boolean checkBoundaries(Node now, int x, int y, BetaGameManager manager){
        if(manager.getBoard() instanceof BoundedBoard) {
            BoundedBoard board = (BoundedBoard)manager.getBoard();
            return (now.x + x >= 1 && now.x + x < board.getXMax())
                    && (now.y + y >= 1 && now.y + y < board.getYMax());
        }
        return true;
    }

    /**
     * Checks if the algorithm should skip over the x, y coordinate when checking neighbors
     * @param piece the piece being moved
     * @param manager the game manager
     * @param x the x coordinate to move to
     * @param y the y coordinate to move to
     * @return
     */
    public static boolean shouldSkip(EscapePiece piece, BetaGameManager manager, int x, int y){
        if(!(manager.getBoard() instanceof BoundedBoard)){
            return x == y && x != 0;
        }
        return (manager.getPieceMovePattern(piece.getName()) != OMNI && x != 0 && y != 0);
    }

    /**
     * Returns a boolean indicating if a piece can moVe through a position (has UNBLOCK)
     * @param piece the piece to check
     * @param manager the game manager
     * @param x the coordinate x to check
     * @param y the coordinate y to check
     * @return Boolean as to whether the piece can move onto the location or not
     */
    public static boolean isUNBLOCKED(EscapePiece piece, BetaGameManager manager, int x, int y){
        Coordinate c = manager.makeCoordinate(x, y);

        if(manager.getBoard().getLocationType(c) == BLOCK){
           return (manager.getBoolPieceAttribute(piece.getName(), UNBLOCK));
        }
        return true;
    }


    /**
     * Determines if a piece can jump over the (toX, toY) location
     * @param piece the piece jumping
     * @param now the node to jump from
     * @param toX the x coordinate ot jump to
     * @param toY the y coordinate to jump to
     * @param manager the game manager
     * @return Returns a boolean indicating if the piece can jump to the toX or toY coordinates
     */
    private static boolean canJump(EscapePiece piece, Node now, int toX, int toY, BetaGameManager manager){
        if(now.justJumped){
            return alongLine(now.parent.x, now.parent.y, toX, toY)&&(!hasPiece(toX, toY, manager));
            //System.out.println("made it to jump code over "+ toX + "," + toY);
        }

        if(hasPiece(toX, toY, manager)){
            //System.out.println("piece found at " + toX + "," + toY);
            if(manager.getBoolPieceAttribute(piece.getName(), JUMP)){
                //System.out.println("CAN JUMP " + toX + "," + toY);
                return true;
            }else{
                //System.out.println(piece.getName() + " cannot jump");
                return false;
            }
        }
        return true;
    }

    /**
     * Checks to ensure that the location going to is along a line by checking the changes in x and y
     * @param fromX the x coordinate from
     * @param fromY the y coordinate from
     * @param toX the x coordinate to go to
     * @param toY the y coordinate to go to
     * @return a boolean to determine (fromX, fromY) and (toX, toY) are along a line
     */
    private static boolean alongLine(int fromX, int fromY, int toX, int toY){
        int changeX = Math.abs(fromX - toX);
        int changeY = Math.abs(fromY - toY);
        return (changeX == 0) || (changeY == 0) || (changeX == changeY);
    }

    /**
     * Checks to see if there is a piece at the location
     * @param toX
     * @param toY
     * @param manager
     * @return the boolean if the new location has a piece on it
     */
    private static boolean hasPiece(int toX, int toY, BetaGameManager manager){
        Coordinate c = manager.makeCoordinate(toX, toY);
        return(manager.getPieceAt(c) != null);
    }


    /**
     * Looks in a given list for a node of matching x and y
     * @param array: The node array to check through
     * @param node The node ot search for
     ** @return a boolean as to whether the node was found
     */
    private static boolean findNeighborInList(List<Node> array, Node node) {
        return array.stream().anyMatch((n) -> (n.x == node.x && n.y == node.y));
    }

    //Node class for Path determination
    static class Node implements Comparable {
        Node parent;
        boolean justJumped = false;
        int x, y; // The coordinate x and y values
        double g; // Total cost
        double h; // Heuristic Cost
        Node(Node parent, int xpos, int ypos, double g, double h) {
            this.parent = parent;
            this.x = xpos;
            this.y = ypos;
            this.g = g;
            this.h = h;
        }

        // Compare by combined cost and heuristic value
        @Override
        public int compareTo(Object o) {
            Node that = (Node) o;
            return (int)((this.g + this.h) - (that.g + that.h));
        }
    }

}
