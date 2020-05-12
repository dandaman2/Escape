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
import escape.rule.RuleID;
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

    //A hashmap which holds the game rules that have been read in
    private HashMap<RuleID, Integer> gameRules = new HashMap<RuleID, Integer>();

    //The collection of observers for the game manager
    private ArrayList<GameObserver> observers = new ArrayList<>();

    //The current player's turn.
    private Player currentPlayer = Player.PLAYER1;

    //The message to play once the game has ended. Indicates game-over when set
    String endMessage = null;

    //Hashmap which stores player score
    private HashMap<Player, Integer> scores = new HashMap<Player, Integer>();

    //The Current number of turns. If -1, then there is no turn limit
    private int turnsTillEnd = -1;


    /**
     * The default constructor for creating the game manager
     * @param board the game board
     * @param makeCoordinate the static method for creating a coordinate of the proper type
     */
    public BetaGameManager(BoardWorker board, BiFunction<Integer, Integer, C> makeCoordinate){
        this.gameBoard = board;
        this.makeCoordinate = makeCoordinate;
        scores.put(Player.PLAYER1, 0);
        scores.put(Player.PLAYER2, 0);
    }

    /**
     * Switches the current player id to the next player
     */
    private void nextPlayer(){
        this.currentPlayer = (this.currentPlayer == Player.PLAYER1)? Player.PLAYER2 : Player.PLAYER1;
    }

    /**
     * Switches the current player and goes to the next turn
     */
    private void nextAction(){
        //Send of the turn
        if(currentPlayer == Player.PLAYER2) {
            if (this.turnsTillEnd > 0) {
                this.turnsTillEnd--;
            }
            //check the winner
            if(this.turnsTillEnd == 0 ||
                    (hasRule(RuleID.SCORE) &&
                            (getPlayerScore(Player.PLAYER1) >= getRuleValue(RuleID.SCORE))||
                            (getPlayerScore(Player.PLAYER2) >= getRuleValue(RuleID.SCORE)))){

                //Compare to see higher score
                int compare = Integer.compare(getPlayerScore(Player.PLAYER1), getPlayerScore(Player.PLAYER2));

                //If both players make it to/over score limit, then tie
                if(hasRule(RuleID.SCORE)&&
                        (getPlayerScore(Player.PLAYER1) >= getRuleValue(RuleID.SCORE))&&
                        (getPlayerScore(Player.PLAYER2) >= getRuleValue(RuleID.SCORE))){
                    compare = 0;
                }

                //Select correct outcome based on score at end
                switch (compare){
                    case -1:
                        notifyAll("PLAYER2 wins");
                        endMessage = "Game is over and PLAYER2 has won";
                        break;
                    case 0:
                        notifyAll("The game has ended in a Tie!");
                        endMessage = "The game is over and ended in a tie!";
                        break;
                    case 1:
                        notifyAll("PLAYER1 wins");
                        endMessage = "Game is over and PLAYER1 has won";
                        break;
                    default:
                        //Should never reach here, but just in-case
                        notifyAll("The game has ended in a Tie!");
                        endMessage = "The game is over and ended in a tie!";
                        break;
                }
            }
        }
        nextPlayer();
    }

    /**
     * Adds to the given player's score
     * @param player the player to add the score to
     * @param value the amount to add to the score
     */
    private void addToScore(Player player, int value){
        scores.put(player, scores.get(player)+value);
    }

    /**
     * Gets the score for the given player
     * @param player the player to get the score for
     * @return the in value of the player's score
     */
    public int getPlayerScore(Player player){
        return scores.get(player);
    }

    /**
     * Adds a moveset to the game based on the PieceName's movement type
     * @param piece the piece to add the moveset to
     * @param pattern the moveset to add
     */
    public void addPieceData(PieceName piece, MovementPatternID pattern, ArrayList<PieceTypeInitializer.PieceAttribute> attributes){
        //Set the value if not already defined
        boolean hasValue = false;
        for(PieceTypeInitializer.PieceAttribute attr : attributes){
            if(attr.getId() == PieceAttributeID.VALUE){
                hasValue = true;
            }
        }
        if(!hasValue){
            PieceTypeInitializer.PieceAttribute value = new PieceTypeInitializer.PieceAttribute();
            value.setId(PieceAttributeID.VALUE);
            value.setIntValue(1);
            attributes.add(value);
        }

        //find proper movement pattern and add to hashmap
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
     * Adds a rule to the gameRules hashmap
     * @param id the id of the rule to add
     * @param value teh value of the rule to add. If no value associated, then input 0
     */
    public void addRule(RuleID id, int value){
        if(id == RuleID.TURN_LIMIT){
            turnsTillEnd = value;
        }
        gameRules.put(id, value);
    }

    /**
     * Returns a boolean whether the gameRules hashmap contains the specified rule
     * @param id the id to check for in the hashmap
     * @return a boolean whether the hashmap contains the key or not
     */
    public boolean hasRule(RuleID id){ return gameRules.containsKey(id); }

    /**
     * Returns the value of the given game rule
     * @param id the rule to search for
     * @return the value of the rule specified
     */
    public int getRuleValue(RuleID id){ return gameRules.get(id); }

    /**
     * See EscapeGameManager definition for details
     * @param from starting location
     * @param to ending location
     * @return True if the move is valid, false otherwise
     */
    @Override
    public boolean move(C from, C to) {
        try {

            //Return false if game is over
            if(endMessage != null){
                return falseAndNotify(endMessage);
            }

            //Find the moveset for the piece in the from coordinate
            EscapePiece piece = getPieceAt(from);
            EscapePiece capturedPiece = getPieceAt(to);

            //Return false if no piece on moving location or ending location is a block
            if (piece == null) {
                //no piece found at that location and cannot move to BLOCK location
                return falseAndNotify("No piece chosen to move (no piece at starting location)");
            }

            //Return false if the piece being moved is not owned by the moving player
            if(piece.getPlayer() != currentPlayer){
                return falseAndNotify("The piece being moved does not belong to " + currentPlayer.name());
            }

            //Return false if attempting to move onto a BLOCK location
            if(gameBoard.getLocationType(to) == LocationType.BLOCK){
                return falseAndNotify("Cannot move to a BLOCK location");
            }

            //Return false if the location being moved to has a piece of the same player
            if (capturedPiece != null && capturedPiece.getPlayer() == piece.getPlayer()) {
                return falseAndNotify("Cannot capture a piece belonging to the moving player");
            }

            //return false if the attempting to move onto a piece with no capturing rules
            if(capturedPiece != null && (!hasRule(RuleID.REMOVE) && !hasRule(RuleID.POINT_CONFLICT))){
                return falseAndNotify("Cannot capture a piece with no capturing rules defined (REMOVE, POINT_CONFLICT)");
            }

            //Get the moveset from the piece data and evaluate
            PieceData p = pieceData.get(piece.getName());
            Movement m = p.getMovePattern();
            //System.out.println("starting move check");
            if (m.isValid(piece, from, to, this)) {

                //Enemy capture logic
                if (capturedPiece != null) {
                    if(hasRule(RuleID.POINT_CONFLICT)){ //For POINTS_CONFLICT Rule
                        int movingPieceValue = piece.getValue();
                        int enemyPieceValue = capturedPiece.getValue();
                        int compare = Integer.compare(movingPieceValue, enemyPieceValue);
                        switch (compare){
                            case -1: //movingPiece value is lower
                                gameBoard.removePieceAt(from);
                                capturedPiece.setValue(enemyPieceValue-movingPieceValue);
                                break;
                            case 0: // both are the same value
                                gameBoard.removePieceAt(from);
                                gameBoard.removePieceAt(to);
                                break;
                            case 1: //enemy piece value is lower
                                gameBoard.putPieceAt(gameBoard.removePieceAt(from), to);
                                piece.setValue(movingPieceValue-enemyPieceValue);
                                break;
                        }

                    } else{ // For REMOVE Rule
                        gameBoard.putPieceAt(gameBoard.removePieceAt(from), to);
                    }

                }
                //remove current piece if exit
                else if (gameBoard.getLocationType(to) == LocationType.EXIT) {
                    addToScore(currentPlayer, piece.getValue());
                    gameBoard.removePieceAt(from);
                }else{
                    //move piece to location
                    gameBoard.putPieceAt(gameBoard.removePieceAt(from), to);
                }

               // Go to next turn/player
                nextAction();
                return true;
            }
            return falseAndNotify("No path in-range or within movement type");

        }catch(EscapeException exception){
            notifyAll(exception.getMessage(), exception);
            return false;
        }
    }

    /**
     * Helper method that returns false, and notifies all observers of the cause
     * @param message the message ot send to the observers
     * @return false
     */
    public boolean falseAndNotify(String message){
        notifyAll(message);
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

    /**
     * Adds an observer to the observer list
     * @param observer the observer to add
     * @return the observer that was added
     */
    @Override
    public GameObserver addObserver(GameObserver observer) {
        observers.add(observer);
        return observer;
    }

    /**
     * Removes an observer from the observer list
     * @param observer the observer that was removed
     * @return the removed observer
     */
    @Override
    public GameObserver removeObserver(GameObserver observer){
        observers.remove(observer);
        return observer;
    }

    /**
     * Notifies all observers of a given message
     * @param message the message to send
     * @param cause the Exception to send
     */
    private void notifyAll(String message, Throwable cause){
        for(GameObserver observer : observers){
            observer.notify(message, cause);
        }
    }

    /**
     * Notifies all observers of a given message
     * @param message the message to send
     */
    private void notifyAll(String message){
        for(GameObserver observer : observers){
            observer.notify(message);
        }
    }
}
