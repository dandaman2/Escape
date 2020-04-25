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
package escape.board.coordinate;

import escape.exception.EscapeException;

import java.util.Objects;

/**
 * This is an example of how a SquareCoordinate might be organized.
 * 
 * @version Mar 27, 2020
 */
public class SquareCoordinate implements Coordinate
{
    private final int x;
    private final int y;
    
    private SquareCoordinate(int x, int y)
    {
    	this.x = x;
    	this.y = y;
    }

	/**
	 * Creates and returns a coordinate with the given location
	 * @param x the x value of the coordinate
	 * @param y the y value of the coordinate
	 * @return The SquareCoordinate object
	 * @throws EscapeException
	 */
    public static SquareCoordinate makeCoordinate(int x, int y) throws EscapeException
    {
    	//square coordinates should be 1 or greater
    	if(x<1 || y<1)
    		throw new EscapeException("Square Coordinates are not valid");

    	return new SquareCoordinate(x, y);
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
	public int distanceTo(Coordinate c) throws EscapeException{
		if(!(c instanceof SquareCoordinate)){
			throw new EscapeException("Can only calculate the distance of two coordinates of the same type");
		}

		int diffX = Math.abs(this.getX()-((SquareCoordinate)c).getX());
		int diffY = Math.abs(this.getY()-((SquareCoordinate)c).getY());
		return Math.max(diffX, diffY);
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
		if (!(obj instanceof SquareCoordinate)) {
			return false;
		}
		SquareCoordinate other = (SquareCoordinate) obj;
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
