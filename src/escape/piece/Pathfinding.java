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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static escape.board.LocationType.BLOCK;
import static escape.board.LocationType.EXIT;
import static escape.piece.PieceAttributeID.*;
import static escape.piece.PieceAttributeID.JUMP;

//Class for pathfinding implementation from Movement.java
public class Pathfinding {
    /**
     * Determines if there is a direct, non-linear path to the to coordinate
     * @param piece the piece being moved
     * @param from the location to move the piece from
     * @param to the location to moce the piece to
     * @param manager the game manager
     * @return boolean if move is possible for the given piece
     */
    public static boolean isLinearPath(EscapePiece piece, Coordinate from, Coordinate to, BetaGameManager manager){
        Coordinate2D start = (Coordinate2D)from;
        Coordinate2D end = (Coordinate2D)to;
        //If path is not along a single line at every point, then invalid linear move
        if(!alongLine(start.getX(), start.getY(), end.getX(), end.getY())){
            //System.out.println("Not along line");
            return false;
        }

        boolean justJumped = false;
        boolean canFly = manager.hasPieceAttribute(piece.getName(), FLY);
        int xIncr = Integer.compare(end.getX(), start.getX());
        int yIncr = Integer.compare(end.getY(), start.getY());
        int newX = start.getX() + xIncr;
        int newY = start.getY() + yIncr;
        int loops = 0;

        //System.out.println("x incr:" + xIncr + " y incr:" + yIncr);
        //If diagonal line on an ortho board, return false
        if(manager.getCoordID() == CoordinateID.ORTHOSQUARE && xIncr !=0 && yIncr !=0 ){
            return false;
        }
        while(!(newX == end.getX() && newY == end.getY())) {
            //System.out.println("about to check " + newX + ", " + newY);
            if (!canFly) {
                // Check jump
                if (manager.getPieceAt(manager.makeCoordinate(newX, newY)) != null) {
                    if (!justJumped && manager.hasPieceAttribute(piece.getName(), JUMP) && manager.getBoolPieceAttribute(piece.getName(), JUMP)) {
                        justJumped = true;
                    } else {
                        return false;
                    }
                } else {
                    justJumped = false;
                }
                // Check unblock
                if (manager.getBoard().getLocationType(manager.makeCoordinate(newX, newY)) == BLOCK) {
                    if (!(manager.hasPieceAttribute(piece.getName(), UNBLOCK) &&
                            manager.getBoolPieceAttribute(piece.getName(), UNBLOCK))) {
                        return false;
                    }
                }
            }
            newX += xIncr;
            newY += yIncr;
            loops++;
        }
        int dist = canFly?
                manager.getIntPieceAttribute(piece.getName(), FLY) :
                manager.getIntPieceAttribute(piece.getName(), DISTANCE);
        return dist > loops;
    }

    /**
     * A static function which determines if there are any paths to the given location.
     * Based on movement type.
     * @param start the coordinate to move from
     * @param end the coordinate to move to
     * @param manager
     * @return A list of coordinates that is the shortest path. Null if no path found
     */
    public static ArrayList<Coordinate2D> getAStarPath(EscapePiece piece, Coordinate2D start, Coordinate2D end,
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
        open = addStandardNeighborsToOpenList(now, end, open, closed, manager, piece, neighbors);
        while (now.x != xend || now.y != yend) {
            if (open.isEmpty()) { // Nothing to examine, return null for no path
                return null;
            }
            //System.out.println("checking: " + now.x + " " + now.y);
            now = open.get(0); // get first node (lowest cost)
            open.remove(0); // remove it
            closed.add(now); // and add to the closed
            open = addStandardNeighborsToOpenList(now, end, open, closed, manager, piece, neighbors);
        }
        //System.out.println("path found:");
        path.add(0, now);
        while (now.x != xstart || now.y != ystart) {
            now = now.parent;
            path.add(0, now);
        }

        //If the path is too long, return null (fly)
        if(manager.hasPieceAttribute(piece.getName(), FLY) &&
                path.size()-1>manager.getIntPieceAttribute(piece.getName(), FLY)){
            return null;
        }

        //If the path is too long, return null (distance)
        if(manager.hasPieceAttribute(piece.getName(), DISTANCE) &&
                path.size()-1>manager.getIntPieceAttribute(piece.getName(), DISTANCE)){
            return null;
        }

        //Convert from nodes to proper coordinates
        for(Node n : path){
            //System.out.println(n.x+","+n.y);
            coordinatePath.add((Coordinate2D)manager.makeCoordinate(n.x, n.y));
        }
        return coordinatePath;
    }

    /**
     * Adds neighbors to the openlist in the ASTAR check. For Ortho and omni movement
     * @param now the current node to check
     * @param end The ending location for jump checking
     * @param open the current openlist
     * @param manager the game manager implementation
     * @param piece the piece to evaluate for attributes
     * @param neighbors the neighbor-checking boolean function
     * @return the list of new neighbor nodes
     */
    public static ArrayList<Node> addStandardNeighborsToOpenList(Node now, Coordinate2D end, ArrayList<Node> open, ArrayList<Node> closed,
                                                                 BetaGameManager manager, EscapePiece piece, Neighbors neighbors){
        Node node;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (neighbors.isNeighbor(manager, x, y)) { // Checking if neighbor is valid using lambda function
                    node = new Node(now, now.x + x, now.y + y, now.g,
                            manager.makeCoordinate(now.x, now.y).distanceTo(manager.makeCoordinate(x, y)));
                    if ((x != 0 || y != 0) // not "now" Node
                            && checkBoundaries(now, x, y, manager) // check if board boundaries
                            && isUNBLOCKED(piece, manager, now.x + x, now.y + y) // check if space is BLOCKed or has a piece
                            && notExit(now.x+x, now.y+y, end, manager) //cannot move over exit spaces
                            && canJump(piece, now, now.x + x, now.y + y, end, manager) // check if jumpable
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
     * @param y the y coordinate of the node to check
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
     * Checks to ensure that a piece cannot go over an exit location unless it is the goal spot
     * @param x the x to check
     * @param y the y to check
     * @param end the end coordinate goal
     * @param manager the game manager
     * @return false if there is an exit at the location and is not the goal
     */
    public static boolean notExit(int x, int y, Coordinate2D end, BetaGameManager manager){
        Coordinate c = manager.makeCoordinate(x, y);
        if(manager.getBoard().getLocationType(c) == EXIT && !((end.getX() == x) && (end.getY() == y))){
            return false;
        }
        return true;
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

        //Can fly over any blocked location
        if(manager.hasPieceAttribute(piece.getName(), FLY)){
            return true;
        }

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
     * @param end The ending coordinate that can be jumped into if it is an opposing piece
     * @param manager the game manager
     * @return Returns a boolean indicating if the piece can jump to the toX or toY coordinates
     */
    private static boolean canJump(EscapePiece piece, Node now, int toX, int toY, Coordinate2D end, BetaGameManager manager){

        //Can fly over any blocked location
        if(manager.hasPieceAttribute(piece.getName(), FLY)){
            return true;
        }

        if(now.justJumped){
            double change1 = (double)Math.abs(now.parent.y - toY)/(double)Math.abs(now.parent.x - toX);
            double change2 = (double)Math.abs(now.parent.y - now.y)/(double)Math.abs(now.parent.x - now.x);;

            return (change1 == change2) &&
                    (!hasPiece(toX, toY, manager));
        }

        if(hasPiece(toX, toY, manager)&&!((toX == end.getX()) && (toY == end.getY()))){
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
