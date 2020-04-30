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
import escape.board.coordinate.Coordinate;
import escape.board.coordinate.HexCoordinate;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;
import escape.util.EscapeGameInitializer;
import escape.util.LocationInitializer;
import escape.util.PieceTypeInitializer;

import static escape.board.coordinate.CoordinateID.*;

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
        EscapeGameManager manager;
        switch(gameInitializer.getCoordinateType()){
            case SQUARE:
                manager = new BetaGameManager<SquareCoordinate>(
                        new SquareBoard(gameInitializer.getxMax(), gameInitializer.getyMax()),
                        (x, y)->SquareCoordinate.makeCoordinate(x, y));
                break;
            case ORTHOSQUARE:
                manager = new BetaGameManager<OrthoSquareCoordinate>(
                        new OrthoSquareBoard(gameInitializer.getxMax(), gameInitializer.getyMax()),
                        (x, y)->OrthoSquareCoordinate.makeCoordinate(x, y));
                break;
            case HEX:
                manager = new BetaGameManager<HexCoordinate>(
                        new HexBoard(),
                        (x, y)->HexCoordinate.makeCoordinate(x, y));
                break;
            default:
                throw new EscapeException("Coordinate type not recognized");
        }
        ((BetaGameManager)manager).setCoordID(gameInitializer.getCoordinateType());


        //iterate through locations and add to board, if any
        if(gameInitializer.getLocationInitializers() != null){
            for(LocationInitializer location : gameInitializer.getLocationInitializers()){
                Coordinate c = manager.makeCoordinate(location.x, location.y);
                if(location.locationType != null){
                    LocationType l = location.locationType;
                    ((BetaGameManager)manager).getBoard().setLocationType(c, l);
                }
                if(location.pieceName != null){
                    ((BetaGameManager)manager).getBoard().putPieceAt(
                            EscapePiece.makePiece(location.player, location.pieceName), c);
                }
            }
        }

        //iterate through pieces, adding movement patterns and attributes
        if(gameInitializer.getPieceTypes() != null){
            for(PieceTypeInitializer piece : gameInitializer.getPieceTypes()){
                ((BetaGameManager) manager).addPieceData(
                        piece.getPieceName(),
                        piece.getMovementPattern(),
                        new ArrayList<>(Arrays.asList(piece.getAttributes())));
            }
        }

        //Return new manager object
        return manager;
    }

    //getter for initializer printing
    public EscapeGameInitializer getGameInitializer() {
        return gameInitializer;
    }
}
