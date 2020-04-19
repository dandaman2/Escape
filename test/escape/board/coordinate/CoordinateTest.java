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

package escape.board.coordinate;

import static org.junit.jupiter.api.Assertions.*;

import escape.exception.EscapeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Tests for various coordinates
 * @version Mar 28, 2020
 */
class CoordinateTest
{

    //Square Coordinates//////////////////////////////////////////////////////////////////////

    //creates a valid square coordinate, and ensures that the instance variables are set
    @Test
    void makeValidSquareCoordinate1(){
        Coordinate c = SquareCoordinate.makeCoordinate(1, 1);
        assertNotNull(c);
    }

    //creates a valid square coordinate, and ensures that the instance variables are set
    @Test
    void makeValidSquareCoordinateCheckVar1(){
        Coordinate c = SquareCoordinate.makeCoordinate(1, 1);
        assertEquals(1, ((SquareCoordinate)c).getX());
        assertEquals(1, ((SquareCoordinate)c).getY());
    }

    //creates a valid square coordinate, and ensures that the instance variables are set
    @Test
    void makeValidSquareCoordinateCheckNEVars(){
        Coordinate c = SquareCoordinate.makeCoordinate(1, 1);
        assertNotEquals(2, ((SquareCoordinate)c).getX());
        assertNotEquals(3, ((SquareCoordinate)c).getY());
    }

    //creates a valid square coordinate, and ensures that the instance variables are set
    @Test
    void makeValidSquareCoordinate2(){
        Coordinate c = SquareCoordinate.makeCoordinate(3, 2);
        assertNotNull(c);
    }

    //creates a valid square coordinate, and ensures that the instance variables are set
    @Test
    void makeValidSquareCoordinateCheckVars2(){
        Coordinate c = SquareCoordinate.makeCoordinate(3, 2);
        assertEquals(3, ((SquareCoordinate)c).getX());
        assertEquals(2, ((SquareCoordinate)c).getY());
    }

    // Helper methods for passing-in invalid square coordinates
    static Stream<Arguments> invalidSquareCoordinateHelper(){
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(-1, 0),
                Arguments.of(0, -1),
                Arguments.of(-1, -1)
        );
    }

    // making an invalid coordinate throws an exception (0 and negative positions)
    @ParameterizedTest
    @MethodSource("invalidSquareCoordinateHelper")
    void makeInvalidSquareCoordinates(int x, int y){
        assertThrows(EscapeException.class, ()->{
            Coordinate c = SquareCoordinate.makeCoordinate(x, y);
        });
    }

    //ensures that the same object is equal to itself
    @Test
    void sameObjSquareCo(){
        Coordinate c = SquareCoordinate.makeCoordinate(3, 2);
        assertTrue(c.equals(c));
    }

    //ensures that the same coordinate is equal to a coordinate with the same x and y values
    @Test
    void sameCoSquareCo(){
        Coordinate c1 = SquareCoordinate.makeCoordinate(3, 2);
        Coordinate c2 = SquareCoordinate.makeCoordinate(3, 2);
        assertTrue(c1.equals(c2));
    }

    //if the x coordinates differ, the points should not be equal
    @Test
    void diffXSquareCo(){
        Coordinate c1 = SquareCoordinate.makeCoordinate(2, 4);
        Coordinate c2 = SquareCoordinate.makeCoordinate(3, 4);
        assertFalse(c1.equals(c2));
    }

    //if the y coordinates differ, the points should not be equal
    @Test
    void diffYSquareCo(){
        Coordinate c1 = SquareCoordinate.makeCoordinate(2, 4);
        Coordinate c2 = SquareCoordinate.makeCoordinate(2, 3);
        assertFalse(c1.equals(c2));
    }

    //if the x and y coordinates differ, the points should not be equal
    @Test
    void diffXandYSquareCo(){
        Coordinate c1 = SquareCoordinate.makeCoordinate(1, 4);
        Coordinate c2 = SquareCoordinate.makeCoordinate(2, 3);
        assertFalse(c1.equals(c2));
    }

    // Helper methods for passing-in valid square coordinates for distance calculation
    static Stream<Arguments> validDistanceSquareCoordinateHelper(){
        return Stream.of(
                Arguments.of(5, 5, 7, 5),
                Arguments.of(5, 5, 7, 7),
                Arguments.of(5, 5, 5, 7),
                Arguments.of(5, 5, 3, 7),
                Arguments.of(5, 5, 3, 5),
                Arguments.of(5, 5, 3, 3),
                Arguments.of(5, 5, 5, 3),
                Arguments.of(5, 5, 7, 3)
        );
    }

    // making an invalid coordinate throws an exception (0 and negative positions)
    @ParameterizedTest
    @MethodSource("validDistanceSquareCoordinateHelper")
    void orthDiagSquareDistanceTo(int x1, int y1, int x2, int y2){
        Coordinate c1 = SquareCoordinate.makeCoordinate(x1, y1);
        Coordinate c2 = SquareCoordinate.makeCoordinate(x2, y2);
        assertEquals(2, c1.distanceTo(c2));
    }

    // making an invalid coordinate throws an exception (0 and negative positions)
    @ParameterizedTest
    @MethodSource("validDistanceSquareCoordinateHelper")
    void orthDiagSquareDistanceToReverse(int x1, int y1, int x2, int y2){
        Coordinate c1 = SquareCoordinate.makeCoordinate(x2, y2);
        Coordinate c2 = SquareCoordinate.makeCoordinate(x1, y1);
        assertEquals(2, c1.distanceTo(c2));
    }

    //distance to self should be zero
    @Test
    void distanceToSelfSquare(){
        Coordinate c1 = SquareCoordinate.makeCoordinate(1, 1);
        Coordinate c2 = SquareCoordinate.makeCoordinate(1, 1);
        assertEquals(0, c1.distanceTo(c2));
    }

    // Helper methods for passing-in indriect valid square coordinates for distance calculation
    static Stream<Arguments> validIndirectDistanceSquareCoordinateHelper(){
        return Stream.of(
                Arguments.of(6, 3),
                Arguments.of(7, 4),
                Arguments.of(7, 6),
                Arguments.of(6, 7),
                Arguments.of(4, 7),
                Arguments.of(3, 6),
                Arguments.of(3, 4),
                Arguments.of(4, 3)
        );
    }

    // distance to an indriect coordinate should be 2 away instead of 3 if not orthosquare
    @ParameterizedTest
    @MethodSource("validIndirectDistanceSquareCoordinateHelper")
    void nonDirectSquareDistanceTo(int x, int y){
        Coordinate c1 = SquareCoordinate.makeCoordinate(5, 5);
        Coordinate c2 = SquareCoordinate.makeCoordinate(x, y);
        assertEquals(2, c1.distanceTo(c2));
    }

    // distance to an indriect coordinate should be 2 away instead of 3 if not orthosquare
    @ParameterizedTest
    @MethodSource("validIndirectDistanceSquareCoordinateHelper")
    void invalidNonDirectSquareDistanceTo(int x, int y){
        Coordinate c1 = SquareCoordinate.makeCoordinate(5, 5);
        Coordinate c2 = SquareCoordinate.makeCoordinate(x, y);
        assertNotEquals(3, c1.distanceTo(c2));
    }



    //OrthoSquare Coordinates//////////////////////////////////////////////////////////////////////

    //creates a valid orthosquare coordinate, and ensures that the instance variables are set
    @Test
    void makeValidOrthoSquareCoordinate1(){
        Coordinate c = OrthoSquareCoordinate.makeCoordinate(1, 1);
        assertNotNull(c);
    }

    //creates a valid orthosquare coordinate, and ensures that the instance variables are set
    @Test
    void makeValidOrthoSquareCoordinateCheckVar1(){
        Coordinate c = OrthoSquareCoordinate.makeCoordinate(1, 1);
        assertEquals(1, ((OrthoSquareCoordinate)c).getX());
        assertEquals(1, ((OrthoSquareCoordinate)c).getY());
    }

    //creates a valid orthosquare coordinate, and ensures that the instance variables are set
    @Test
    void makeValidOrthoSquareCoordinateCheckNEVars(){
        Coordinate c = OrthoSquareCoordinate.makeCoordinate(1, 1);
        assertNotEquals(2, ((OrthoSquareCoordinate)c).getX());
        assertNotEquals(3, ((OrthoSquareCoordinate)c).getY());
    }

    //creates a valid orthosquare coordinate, and ensures that the instance variables are set
    @Test
    void makeValidOrthoSquareCoordinate2(){
        Coordinate c = OrthoSquareCoordinate.makeCoordinate(3, 2);
        assertNotNull(c);
    }

    //creates a valid orthosquare coordinate, and ensures that the instance variables are set
    @Test
    void makeValidOrthoSquareCoordinateCheckVars2(){
        Coordinate c = OrthoSquareCoordinate.makeCoordinate(3, 2);
        assertEquals(3, ((OrthoSquareCoordinate)c).getX());
        assertEquals(2, ((OrthoSquareCoordinate)c).getY());
    }

    // Helper methods for passing-in invalid orthosquare coordinates
    static Stream<Arguments> invalidOrthoSquareCoordinateHelper(){
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(-1, 0),
                Arguments.of(0, -1),
                Arguments.of(-1, -1)
        );
    }

    // making an invalid coordinate throws an exception (0 and negative positions)
    @ParameterizedTest
    @MethodSource("invalidOrthoSquareCoordinateHelper")
    void makeInvalidOrthoSquareCoordinates(int x, int y){
        assertThrows(EscapeException.class, ()->{
            Coordinate c = OrthoSquareCoordinate.makeCoordinate(x, y);
        });
    }

    //ensures that the same object is equal to itself
    @Test
    void sameObjOrthoSquareCo(){
        Coordinate c = OrthoSquareCoordinate.makeCoordinate(3, 2);
        assertTrue(c.equals(c));
    }

    //ensures that the same coordinate is equal to a coordinate with the same x and y values
    @Test
    void sameCoOrthoSquareCo(){
        Coordinate c1 = OrthoSquareCoordinate.makeCoordinate(3, 2);
        Coordinate c2 = OrthoSquareCoordinate.makeCoordinate(3, 2);
        assertTrue(c1.equals(c2));
    }

    //if the x coordinates differ, the points should not be equal
    @Test
    void diffXOrthoSquareCo(){
        Coordinate c1 = OrthoSquareCoordinate.makeCoordinate(2, 4);
        Coordinate c2 = OrthoSquareCoordinate.makeCoordinate(3, 4);
        assertFalse(c1.equals(c2));
    }

    //if the y coordinates differ, the points should not be equal
    @Test
    void diffYOrthoSquareCo(){
        Coordinate c1 = OrthoSquareCoordinate.makeCoordinate(2, 4);
        Coordinate c2 = OrthoSquareCoordinate.makeCoordinate(2, 3);
        assertFalse(c1.equals(c2));
    }

    //if the x and y coordinates differ, the points should not be equal
    @Test
    void diffXandYOrthoSquareCo(){
        Coordinate c1 = OrthoSquareCoordinate.makeCoordinate(1, 4);
        Coordinate c2 = OrthoSquareCoordinate.makeCoordinate(2, 3);
        assertFalse(c1.equals(c2));
    }

    // Helper methods for passing-in valid square coordinates for distance calculation
    static Stream<Arguments> validDistanceOrthoSquareCoordinateHelper(){
        return Stream.of(
                Arguments.of(5, 5, 7, 5),
                Arguments.of(5, 5, 6, 6),
                Arguments.of(5, 5, 5, 7),
                Arguments.of(5, 5, 4, 6),
                Arguments.of(5, 5, 3, 5),
                Arguments.of(5, 5, 4, 4),
                Arguments.of(5, 5, 5, 3),
                Arguments.of(5, 5, 6, 4)
        );
    }

    // making an invalid coordinate throws an exception (0 and negative positions)
    @ParameterizedTest
    @MethodSource("validDistanceOrthoSquareCoordinateHelper")
    void orthoSquareDistanceTo(int x1, int y1, int x2, int y2){
        Coordinate c1 = OrthoSquareCoordinate.makeCoordinate(x1, y1);
        Coordinate c2 = OrthoSquareCoordinate.makeCoordinate(x2, y2);
        assertEquals(2, c1.distanceTo(c2));
    }

    // making an invalid coordinate throws an exception (0 and negative positions)
    @ParameterizedTest
    @MethodSource("validDistanceOrthoSquareCoordinateHelper")
    void orthoSquareDistanceToReverse(int x1, int y1, int x2, int y2){
        Coordinate c1 = OrthoSquareCoordinate.makeCoordinate(x2, y2);
        Coordinate c2 = OrthoSquareCoordinate.makeCoordinate(x1, y1);
        assertEquals(2, c1.distanceTo(c2));
    }

    //distance to self should be zero
    @Test
    void distanceToSelfOrthoSquare(){
        Coordinate c1 = OrthoSquareCoordinate.makeCoordinate(1, 1);
        Coordinate c2 = OrthoSquareCoordinate.makeCoordinate(1, 1);
        assertEquals(0, c1.distanceTo(c2));
    }

    // Helper methods for passing-in indriect valid orthosquare coordinates for distance calculation
    static Stream<Arguments> validIndirectDistanceOrthoSquareCoordinateHelper(){
        return Stream.of(
                Arguments.of(6, 3),
                Arguments.of(7, 4),
                Arguments.of(7, 6),
                Arguments.of(6, 7),
                Arguments.of(4, 7),
                Arguments.of(3, 6),
                Arguments.of(3, 4),
                Arguments.of(4, 3)
        );
    }

    // distance to an indriect coordinate should be 3 away if taking orthosquare route
    @ParameterizedTest
    @MethodSource("validIndirectDistanceOrthoSquareCoordinateHelper")
    void nonDirectOrthoSquareDistanceTo(int x, int y){
        Coordinate c1 = OrthoSquareCoordinate.makeCoordinate(5, 5);
        Coordinate c2 = OrthoSquareCoordinate.makeCoordinate(x, y);
        assertEquals(3, c1.distanceTo(c2));
    }

    // distance to an indriect coordinate should not be 2 if ortohsquare (should be 3)
    @ParameterizedTest
    @MethodSource("validIndirectDistanceOrthoSquareCoordinateHelper")
    void invalidNonDirectOrthoSquareDistanceTo(int x, int y){
        Coordinate c1 = OrthoSquareCoordinate.makeCoordinate(5, 5);
        Coordinate c2 = OrthoSquareCoordinate.makeCoordinate(x, y);
        assertNotEquals(2, c1.distanceTo(c2));
    }



    //Hex Coordinates//////////////////////////////////////////////////////////////////////

    //creates a valid hex coordinate, and ensures that the instance variables are set
    @Test
    void makeValidHexCoordinate1(){
        Coordinate c = HexCoordinate.makeCoordinate(1, 1);
        assertNotNull(c);
    }

    //creates a valid hex coordinate, and ensures that the instance variables are set
    @Test
    void makeValidHexCoordinateCheckVar1(){
        Coordinate c = HexCoordinate.makeCoordinate(1, 1);
        assertEquals(1, ((HexCoordinate)c).getX());
        assertEquals(1, ((HexCoordinate)c).getY());
    }

    //creates a valid hex coordinate, and ensures that the instance variables are set
    @Test
    void makeValidHexCoordinateCheckNEVars(){
        Coordinate c = HexCoordinate.makeCoordinate(1, 1);
        assertNotEquals(2, ((HexCoordinate)c).getX());
        assertNotEquals(3, ((HexCoordinate)c).getY());
    }

    //creates a valid hex coordinate, and ensures that the instance variables are set
    @Test
    void makeValidHexCoordinate2(){
        Coordinate c = HexCoordinate.makeCoordinate(3, 2);
        assertNotNull(c);
    }

    //creates a valid hex coordinate, and ensures that the instance variables are set
    @Test
    void makeValidHexCoordinateCheckVars2(){
        Coordinate c = HexCoordinate.makeCoordinate(3, 2);
        assertEquals(3, ((HexCoordinate)c).getX());
        assertEquals(2, ((HexCoordinate)c).getY());
    }

    // Helper methods for passing-in 0 and negative hex coordinates
    static Stream<Arguments> validHexCoordinateHelper(){
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(-1, 0),
                Arguments.of(0, -1),
                Arguments.of(-1, -1)
        );
    }

    // Negative and zero positions are valid for hex
    @ParameterizedTest
    @MethodSource("validHexCoordinateHelper")
    void makeValidHexCoordinates(int x, int y){
//        assertDoesNotThrow((()->{
//            Coordinate c = HexCoordinate.makeCoordinate(x, y);
//        }));
        Coordinate c = HexCoordinate.makeCoordinate(x, y);
    }

    //ensures that the same object is equal to itself
    @Test
    void sameObjHexCo(){
        Coordinate c = HexCoordinate.makeCoordinate(3, 2);
        assertTrue(c.equals(c));
    }

    //ensures that the same coordinate is equal to a coordinate with the same x and y values
    @Test
    void sameCoHexCo(){
        Coordinate c1 = HexCoordinate.makeCoordinate(3, 2);
        Coordinate c2 = HexCoordinate.makeCoordinate(3, 2);
        assertTrue(c1.equals(c2));
    }

    //if the x coordinates differ, the points should not be equal
    @Test
    void diffXHexCo(){
        Coordinate c1 = HexCoordinate.makeCoordinate(2, 4);
        Coordinate c2 = HexCoordinate.makeCoordinate(3, 4);
        assertFalse(c1.equals(c2));
    }

    //if the y coordinates differ, the points should not be equal
    @Test
    void diffYHexCo(){
        Coordinate c1 = HexCoordinate.makeCoordinate(2, 4);
        Coordinate c2 = HexCoordinate.makeCoordinate(2, 3);
        assertFalse(c1.equals(c2));
    }

    //if the x and y coordinates differ, the points should not be equal
    @Test
    void diffXandYHexCo(){
        Coordinate c1 = HexCoordinate.makeCoordinate(1, 4);
        Coordinate c2 = HexCoordinate.makeCoordinate(2, 3);
        assertFalse(c1.equals(c2));
    }

    // Helper methods for passing-in valid hex coordinates for distance calculation
    static Stream<Arguments> validDistanceHexCoordinateHelper(){
        return Stream.of(
                Arguments.of(0, 0, 0, 2),
                Arguments.of(0, 0, 1, 1),
                Arguments.of(0, 0, 2, 0),
                Arguments.of(0, 0, 2, -1),
                Arguments.of(0, 0, 2, -2),
                Arguments.of(0, 0, 1, -2),
                Arguments.of(0, 0, 0, -2),
                Arguments.of(0, 0, -1, -1),
                Arguments.of(0, 0, -2, 0),
                Arguments.of(0, 0, -2, 1),
                Arguments.of(0, 0, -2, 2),
                Arguments.of(0, 0, -1, 2)
        );
    }

    // Valid directional locations on a hex board, all 2 space away
    @ParameterizedTest
    @MethodSource("validDistanceHexCoordinateHelper")
    void hexDistanceTo(int x1, int y1, int x2, int y2){
        Coordinate c1 = HexCoordinate.makeCoordinate(x1, y1);
        Coordinate c2 = HexCoordinate.makeCoordinate(x2, y2);
        assertEquals(2, c1.distanceTo(c2));
    }

    // making an invalid coordinate throws an exception (0 and negative positions)
    @ParameterizedTest
    @MethodSource("validDistanceHexCoordinateHelper")
    void hexDistanceToReverse(int x1, int y1, int x2, int y2){
        Coordinate c1 = HexCoordinate.makeCoordinate(x2, y2);
        Coordinate c2 = HexCoordinate.makeCoordinate(x1, y1);
        assertEquals(2, c1.distanceTo(c2));
    }

    //distance to self should be zero
    @Test
    void distanceToSelfHex(){
        Coordinate c1 = HexCoordinate.makeCoordinate(1, 1);
        Coordinate c2 = HexCoordinate.makeCoordinate(1, 1);
        assertEquals(0, c1.distanceTo(c2));
    }
}
