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

import escape.util.PieceTypeInitializer;

import java.util.ArrayList;

/**
 * A class which holds all of the movement patterns and attributes for a piece.
 * Also contains methods for retrieving the data
 */
public class PieceData {
    private Movements movePattern;
    private ArrayList<PieceTypeInitializer.PieceAttribute> attributes;

    public PieceData(Movements movement, ArrayList<PieceTypeInitializer.PieceAttribute> attributes){
        this.movePattern = movement;
        this.attributes = attributes;
    }

    //Getter for the movement pattern
    public Movements getMovePattern() {
        return movePattern;
    }

    //Getter for the list of attributes
    public ArrayList<PieceTypeInitializer.PieceAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Returns True or False depening if the piece has the given attribute
     * @param id the attribute to search for
     * @return a boolean if the piece has the attribute
     */
    public boolean hasAttribute(PieceAttributeID id){
        for(PieceTypeInitializer.PieceAttribute attr : attributes){
            if(attr.getId() == id){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the boolean value of the attribute (returns false if not set)
     * @param  id attribute to search for
     * @return The boolean value of the attribute. Returns false by default
     */
    public boolean getBoolAttrValue(PieceAttributeID id){
        for(PieceTypeInitializer.PieceAttribute attr : attributes){
            if(attr.getId() == id){
               return attr.isBooleanValue();
            }
        }
        return false;
    }

    /**
     * Returns the int value of the attribute (returns false if not set)
     * @param  id attribute to search for
     * @return The int value of the attribute. Returns 0 by default
     */
    public int getIntAttrValue(PieceAttributeID id){
        for(PieceTypeInitializer.PieceAttribute attr : attributes){
            if(attr.getId() == id){
                return attr.getIntValue();
            }
        }
        return 0;
    }
}
