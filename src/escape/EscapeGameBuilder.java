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

package escape;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.bind.*;

import escape.board.HexBoard;
import escape.board.LocationType;
import escape.board.OrthoSquareBoard;
import escape.board.SquareBoard;
import escape.board.coordinate.*;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;
import escape.piece.MovementPatternID;
import escape.util.EscapeGameInitializer;
import escape.util.LocationInitializer;
import escape.util.PieceTypeInitializer;

import static escape.board.coordinate.CoordinateID.*;
import static escape.piece.PieceAttributeID.DISTANCE;
import static escape.piece.PieceAttributeID.FLY;
import static escape.piece.PieceAttributeType.INTEGER;

/**
 * This class is what a client will use to creat an instance of a game, given
 * an Escape game configuration file. The configuration file contains the 
 * information needed to create an instance of the Escape game.
 * @version Apr 22, 2020
 */
public class EscapeGameBuilder
{
    private EscapeGameInitializer gameInitializer;
    
    /**
     * The constructor takes a file that points to the Escape game
     * configuration file. It should get the necessary information 
     * to be ready to create the game manager specified by the configuration
     * file and other configuration files that it links to.
     * @param fileName the file for the Escape game configuration file.
     * @throws Exception 
     */
    public EscapeGameBuilder(File fileName) throws Exception
    {
        JAXBContext contextObj = JAXBContext.newInstance(EscapeGameInitializer.class);
        Unmarshaller mub = contextObj.createUnmarshaller();
        gameInitializer = 
            (EscapeGameInitializer)mub.unmarshal(new FileReader(fileName));
    }
    
    /**
     * Once the builder is constructed, this method creates the
     * EscapeGameManager instance.
     * @return the EscapeGameManager single instance
     */
    public EscapeGameManager makeGameManager()
    {
        BetaGameManager manager;
        switch(gameInitializer.getCoordinateType()){
            case SQUARE:
                manager = new BetaGameManager<SquareCoordinate>(
                        new SquareBoard(gameInitializer.getxMax(), gameInitializer.getyMax()),
                        SquareCoordinate::makeCoordinate);
                break;
            case ORTHOSQUARE:
                manager = new BetaGameManager<OrthoSquareCoordinate>(
                        new OrthoSquareBoard(gameInitializer.getxMax(), gameInitializer.getyMax()),
                        OrthoSquareCoordinate::makeCoordinate);
                break;
            case HEX:
                manager = new BetaGameManager<HexCoordinate>(
                        new HexBoard(),
                        (HexCoordinate::makeCoordinate));
                break;
            default:
                throw new EscapeException("Coordinate type not recognized");
        }

        //Set the Coordinate ID
        manager.setCoordID(gameInitializer.getCoordinateType());

        //Iterate through pieces, adding movement patterns and attributes
        if(gameInitializer.getPieceTypes() != null){
            for(PieceTypeInitializer piece : gameInitializer.getPieceTypes()){
                // Check if read-in piece is valid to create data for
                checkValidPieceRules(piece, manager.getCoordID());
                manager.addPieceData(
                        piece.getPieceName(),
                        piece.getMovementPattern(),
                        new ArrayList<>(Arrays.asList(piece.getAttributes())));
            }
        }

        //Iterate through locations and add to board, if any
        if(gameInitializer.getLocationInitializers() != null){
            for(LocationInitializer location : gameInitializer.getLocationInitializers()){
                Coordinate c = manager.makeCoordinate(location.x, location.y);
                if(location.locationType != null){
                    LocationType l = location.locationType;
                    manager.getBoard().setLocationType(c, l);
                }
                if(location.pieceName != null){
                    if(!manager.hasPieceData(location.pieceName))
                        throw new EscapeException("No rules or attributes were found for piece "+location.pieceName);
                    manager.getBoard().putPieceAt(EscapePiece.makePiece(location.player, location.pieceName), c);
                }
            }
        }

        //print read-in data
        //System.out.println(gameInitializer);

        //Return new manager object
        return manager;
    }

    /**
     * Helper method which throws an error if there are invalid move patterns for the manager's coordID
     * @param piece the piece that is being checked
     * @param boardCoordType the coordinate type that the board uses
     * @throws EscapeException if the read-in info has user errors
     */
    private static void checkValidPieceRules(PieceTypeInitializer piece, CoordinateID boardCoordType){
        MovementPatternID movePattern = piece.getMovementPattern();
        switch (boardCoordType){
            case HEX:
                if(movePattern == MovementPatternID.ORTHOGONAL ||
                        movePattern == MovementPatternID.DIAGONAL)
                    throw new EscapeException("Cannot use diagonal or orthogonal movements on a hex board");
                break;
            case ORTHOSQUARE:
                if(movePattern == MovementPatternID.DIAGONAL)
                    throw new EscapeException("Cannot use diagonal on an orthosquare board");
                break;
            case SQUARE:
                break;
        }
        //Check to ensure that a piece has either a fly or distance attribute set
        boolean hasFly = false, hasDistance = false;
        for(PieceTypeInitializer.PieceAttribute attr : piece.getAttributes()){
            if(attr.getId() == FLY && attr.getAttrType() == INTEGER){
                hasFly = true;
            }
            if(attr.getId() == DISTANCE && attr.getAttrType() == INTEGER){
                hasDistance = true;
            }
        }
        if((hasFly && hasDistance) || (!hasDistance && !hasFly)){
            throw new EscapeException("Distance OR Fly attributes must be set");
        }
    }

    //Getter for initializer printing
    public EscapeGameInitializer getGameInitializer() {
        return gameInitializer;
    }
}
