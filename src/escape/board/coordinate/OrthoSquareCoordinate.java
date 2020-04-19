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
//Dan Duff (dfduff)
package escape.board.coordinate;

import escape.exception.EscapeException;

import java.util.Objects;

public class OrthoSquareCoordinate implements Coordinate{

    private final int x;
    private final int y;

    private OrthoSquareCoordinate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates and returns a coordinate with the given location
     * @param x the x value of the coordinate
     * @param y the y value of the coordinate
     * @return The OrthoSquareCoordinate object
     * @throws EscapeException
     */
    public static OrthoSquareCoordinate makeCoordinate(int x, int y) throws EscapeException
    {
        //orthosquare coordinates should be 1 or greater
        if(x<1 || y<1)
            throw new EscapeException("OrthoSquare Coordinates are not valid");

        return new OrthoSquareCoordinate(x, y);
    }

    /*
     * @see escape.board.coordinate.Coordinate#distanceTo(escape.board.coordinate.Coordinate)
     */
    /**
     * Returns the integer distance between the give and input coordinate
     * @param c the other coordinate
     * @return the integer distance between the two coordinates
     */
    @Override
    public int distanceTo(Coordinate c){
        int diffX = Math.abs(this.getX()-((OrthoSquareCoordinate)c).getX());
        int diffY = Math.abs(this.getY()-((OrthoSquareCoordinate)c).getY());
        return diffX + diffY;
    }

    /**
     * @return the x
     */
    public int getX()
    {
        return x;
    }

    /**
     * @return the y
     */
    public int getY()
    {
        return y;
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OrthoSquareCoordinate)) {
            return false;
        }
        OrthoSquareCoordinate other = (OrthoSquareCoordinate) obj;
        return x == other.x && y == other.y;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
}
