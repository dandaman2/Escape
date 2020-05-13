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

package escape.game;

import escape.*;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;
import escape.piece.PieceName;
import escape.piece.Player;
import escape.rule.RuleID;
import escape.util.LocationInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static escape.piece.Player.PLAYER1;
import static escape.piece.Player.PLAYER2;
import static org.junit.jupiter.api.Assertions.*;

public class RuleObserverTests {

    //Test to ensure that rules are being initialized properly from a config file
    @Test
    void readingRulesTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingHexGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        //for toString coverage
        egb.getGameInitializer().getRules()[0].toString();
        new LocationInitializer(0, 0, null, null, null);

        boolean found = false;
        for (int i = 0; i < egb.getGameInitializer().getRules().length; i++) {
            if (egb.getGameInitializer().getRules()[i].getId() == RuleID.SCORE) {
                found = true;
            }
        }
        assertTrue(found);
    }

    //Tests to ensure that only specified rules are contained
    @Test
    void containsFakeRulesTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingHexGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        //System.out.println(egb.getGameInitializer());
        boolean found = false;
        for (int i = 0; i < egb.getGameInitializer().getRules().length; i++) {
            if (egb.getGameInitializer().getRules()[i].getId() == RuleID.TURN_LIMIT) {
                found = true;
            }
        }
        assertFalse(found);
    }

    //Tests to ensure that the game contains the correct rules
    @Test
    void hasRuleTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingHexGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertTrue(((BetaGameManager) emg).hasRule(RuleID.SCORE));
        assertFalse(((BetaGameManager) emg).hasRule(RuleID.TURN_LIMIT));
    }

    //Test to ensure that a rule's value is being correctly set
    @Test
    void hasRuleValueTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingHexGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        assertEquals(((BetaGameManager) emg).getRuleValue(RuleID.SCORE), 10);
        assertEquals(((BetaGameManager) emg).getRuleValue(RuleID.REMOVE), 0);
    }

    //Checks to ensure that an observer is not notified when a move is valid
    @Test
    void noErrorObsTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingHexGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);
        assertTrue(emg.move(emg.makeCoordinate(5, -3), emg.makeCoordinate(5, -1)));
        assertFalse(obs.gotError);
    }

    //Tests removing of observers
    @Test
    void removeObsTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingHexGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);
        assertTrue(emg.move(emg.makeCoordinate(5, -3), emg.makeCoordinate(5, -1)));
        emg.removeObserver(obs);

        TestGameObserver obs2 = new TestGameObserver();
        emg.addObserver(obs2);
        assertFalse(emg.move(emg.makeCoordinate(5, -1), emg.makeCoordinate(10, 10)));
        assertTrue(obs2.gotError);
        assertFalse(obs.gotError);

    }

    //Helper method for checking recieved observer error
    static Stream<Arguments> gotErrorHelper() {
        return Stream.of(
                Arguments.of(5, -3, 5, 5),
                Arguments.of(5, -3, 5, -4),
                Arguments.of(0, 0, 1, 1),
                Arguments.of(2, 0, 3, 0)
        );
    }

    //Checks to make sure an error (or bad move) was caught and sent to the observers
    @ParameterizedTest
    @MethodSource("gotErrorHelper")
    void observersCatchErrors(int startx, int starty, int endx, int endy) throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingHexGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);
        assertFalse(emg.move(emg.makeCoordinate(startx, starty), emg.makeCoordinate(endx, endy)));
        assertTrue(obs.gotError);
    }
    //Scoring/////////////////////////////////////////////////////////////////////////

    //Test for player 1 with a higher score
    @Test
    void player1HigherScoreTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        assertEquals(((BetaGameManager) emg).getPlayerScore(PLAYER1), 0);
        assertTrue(emg.move(emg.makeCoordinate(19, 19), emg.makeCoordinate(19, 18)));
        assertEquals(((BetaGameManager) emg).getPlayerScore(PLAYER1), 5);
    }

    //Test for player 2 with a higher score
    @Test
    void player2HigherScoreTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        assertEquals(((BetaGameManager) emg).getPlayerScore(PLAYER2), 0);
        assertTrue(emg.move(emg.makeCoordinate(19, 19), emg.makeCoordinate(19, 18)));
        assertTrue(emg.move(emg.makeCoordinate(18, 18), emg.makeCoordinate(19, 18)));
        assertEquals(((BetaGameManager) emg).getPlayerScore(PLAYER2), 1);
    }

    //TURN LIMIT PLAYER ORDER////////////////////////////////////////////////////////

    //Test to ensure that player 1 does not go twice
    @Test
    void player1TwiceMoveTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);
        assertTrue(emg.move(emg.makeCoordinate(3, 3), emg.makeCoordinate(3, 8)));
        assertFalse(emg.move(emg.makeCoordinate(3, 8), emg.makeCoordinate(3, 3)));
    }

    //Player 2 should not go first
    @Test
    void player2FirstTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);
        assertFalse(emg.move(emg.makeCoordinate(10, 3), emg.makeCoordinate(11, 3)));
        assertTrue(obs.gotError);
    }

    @Test
    void correctTurnOrderTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);
        assertTrue(emg.move(emg.makeCoordinate(3, 3), emg.makeCoordinate(3, 4)));
        assertTrue(emg.move(emg.makeCoordinate(10, 3), emg.makeCoordinate(11, 3)));
        assertTrue(emg.move(emg.makeCoordinate(3, 4), emg.makeCoordinate(3, 5)));
        assertTrue(emg.move(emg.makeCoordinate(11, 3), emg.makeCoordinate(12, 5)));
    }


    //Checks to make sure an error (or bad move) was caught and sent to the observers
    @Test
    void endsAtTurnLimitTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);

        //turn 1
        assertTrue(emg.move(emg.makeCoordinate(3, 3), emg.makeCoordinate(3, 4)));
        assertTrue(emg.move(emg.makeCoordinate(10, 3), emg.makeCoordinate(11, 3)));

        //turn 2
        assertTrue(emg.move(emg.makeCoordinate(3, 4), emg.makeCoordinate(3, 5)));
        assertTrue(emg.move(emg.makeCoordinate(11, 3), emg.makeCoordinate(12, 3)));

        //turn 3
        assertTrue(emg.move(emg.makeCoordinate(3, 5), emg.makeCoordinate(3, 4)));
        assertTrue(emg.move(emg.makeCoordinate(12, 3), emg.makeCoordinate(13, 3)));

        //invalid 4th turn
        assertFalse(emg.move(emg.makeCoordinate(3, 6), emg.makeCoordinate(3, 7)));
    }

    //SCORE REACHED////////////////////////////////////////////////////////////////////////////////////

    //Test to ensure that if player 1 reaches the correct score, player 2 should go before the end of the turn and game
    @Test
    void scoreReachedPlayer1Test() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);

        //turn 1
        assertTrue(emg.move(emg.makeCoordinate(19, 19), emg.makeCoordinate(19, 18)));
        assertTrue(emg.move(emg.makeCoordinate(10, 3), emg.makeCoordinate(13, 3)));

        //turn 2
        assertTrue(emg.move(emg.makeCoordinate(3, 3), emg.makeCoordinate(3, 6))); // Player 1 wins here
        assertTrue(emg.move(emg.makeCoordinate(18, 18), emg.makeCoordinate(17, 16)));

        assertTrue(obs.gotError); //PLAYER 1 WINS

        assertFalse(emg.move(emg.makeCoordinate(1, 1), emg.makeCoordinate(1, 2)));
    }

    //Tests to make sure that player 2 wins if it meets the point value
    @Test
    void scoreReachedPlayer2Test() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);

        //turn 1
        assertTrue(emg.move(emg.makeCoordinate(3, 17), emg.makeCoordinate(2, 19)));
        assertTrue(emg.move(emg.makeCoordinate(1, 19), emg.makeCoordinate(2, 19)));

        //turn 2
        assertTrue(emg.move(emg.makeCoordinate(3, 3), emg.makeCoordinate(3, 6)));
        assertTrue(emg.move(emg.makeCoordinate(1, 18), emg.makeCoordinate(2, 19)));

        assertTrue(obs.gotError); //PLAYER 2 WINS

        assertFalse(emg.move(emg.makeCoordinate(1, 1), emg.makeCoordinate(1, 2)));
    }

    //Checks to make sure that a tying condition is met at the end of the turn limit if scores are identical
    @Test
    void tiesAtTurnLimitTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);

        //turn 1
        assertTrue(emg.move(emg.makeCoordinate(3, 3), emg.makeCoordinate(3, 4)));
        assertTrue(emg.move(emg.makeCoordinate(10, 3), emg.makeCoordinate(11, 3)));

        //turn 2
        assertTrue(emg.move(emg.makeCoordinate(3, 4), emg.makeCoordinate(3, 5)));
        assertTrue(emg.move(emg.makeCoordinate(11, 3), emg.makeCoordinate(12, 3)));

        //turn 3
        assertTrue(emg.move(emg.makeCoordinate(14, 2), emg.makeCoordinate(14, 3)));
        assertTrue(emg.move(emg.makeCoordinate(12, 3), emg.makeCoordinate(13, 3)));

        //invalid 4th turn
        assertFalse(emg.move(emg.makeCoordinate(3, 6), emg.makeCoordinate(3, 7)));
    }

    //Checks to make sure that a tying condition is met if scores are identical at the win condition
    @Test
    void tiesAtScoreLimitTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);

        //turn 1
        assertTrue(emg.move(emg.makeCoordinate(19, 19), emg.makeCoordinate(19, 18)));
        assertTrue(emg.move(emg.makeCoordinate(1, 19), emg.makeCoordinate(2, 19)));

        //turn 2
        assertTrue(emg.move(emg.makeCoordinate(3, 3), emg.makeCoordinate(3, 6)));
        assertTrue(emg.move(emg.makeCoordinate(1, 18), emg.makeCoordinate(2, 19)));
        assertTrue(obs.gotError); // game is over

        assertFalse(emg.move(emg.makeCoordinate(3, 6), emg.makeCoordinate(3, 7))); // game ended in a tie
    }


    //REMOVE RULE////////////////////////////////////////////////////////////////////////////////////
    //Test to ensure that REMOVE simply removes the captured pieces on the board, regardless of value
    @Test
    void player1RemoveTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);


        assertTrue(emg.move(emg.makeCoordinate(19, 19), emg.makeCoordinate(18, 18)));
        assertNull(emg.getPieceAt(emg.makeCoordinate(19, 19)));
        EscapePiece piece  = emg.getPieceAt(emg.makeCoordinate(18, 18));

        assertEquals(piece.getName(), PieceName.HORSE);
        assertEquals(piece.getPlayer(), PLAYER1);
        assertEquals(piece.getValue(), 5);
    }

    //Test to ensure that REMOVE simply removes the captured pieces on the board, regardless of value
    @Test
    void player2RemoveTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingSquareGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);


        assertTrue(emg.move(emg.makeCoordinate(14, 2), emg.makeCoordinate(14, 4)));
        assertTrue(emg.move(emg.makeCoordinate(18, 18), emg.makeCoordinate(19, 19)));

        assertNull(emg.getPieceAt(emg.makeCoordinate(18, 18)));
        EscapePiece piece  = emg.getPieceAt(emg.makeCoordinate(19, 19));

        assertEquals(piece.getName(), PieceName.SNAIL);
        assertEquals(piece.getPlayer(), PLAYER2);
        assertEquals(piece.getValue(), 1);
    }

    //POINTS_CONFLICT//////////////////////////////////////////////////////////////////////////////
    //Test to ensure that POINTS_CONFLICT reduces the higher score and removes the piece with the lower score
    @Test
    void higherToLowerPointConflictTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingOrthoGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);


        assertTrue(emg.move(emg.makeCoordinate(1, 1), emg.makeCoordinate(2, 1)));
        assertNull(emg.getPieceAt(emg.makeCoordinate(1, 1)));

        EscapePiece piece  = emg.getPieceAt(emg.makeCoordinate(2, 1));
        assertEquals(piece.getName(), PieceName.HORSE);
        assertEquals(piece.getPlayer(), PLAYER1);
        assertEquals(piece.getValue(), 4);

        assertTrue(emg.move(emg.makeCoordinate(5, 6), emg.makeCoordinate(3, 6)));
        assertTrue(emg.move(emg.makeCoordinate(2, 1), emg.makeCoordinate(2, 2)));

        assertEquals(((BetaGameManager) emg).getPlayerScore(PLAYER1), 4);
        assertEquals(((BetaGameManager) emg).getPlayerScore(PLAYER2), 5);
    }

    //Test to ensure that POINTS_CONFLICT reduces the higher score and removes the piece with the lower score
    @Test
    void lowerToHigherPointConflictTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingOrthoGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);


        assertTrue(emg.move(emg.makeCoordinate(15, 16), emg.makeCoordinate(17, 16)));
        assertTrue(emg.move(emg.makeCoordinate(2, 1), emg.makeCoordinate(1, 1)));
        assertNull(emg.getPieceAt(emg.makeCoordinate(2, 1)));

        EscapePiece piece  = emg.getPieceAt(emg.makeCoordinate(1, 1));
        assertEquals(piece.getName(), PieceName.HORSE);
        assertEquals(piece.getPlayer(), PLAYER1);
        assertEquals(piece.getValue(), 4);
    }

    //Test to make sure that conflicting pieces with the same value are both removed
    @Test
    void sameValuePointConflictTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingOrthoGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);

        assertTrue(emg.move(emg.makeCoordinate(15, 16), emg.makeCoordinate(12, 16)));
        assertNull(emg.getPieceAt(emg.makeCoordinate(15, 16)));
        assertNull(emg.getPieceAt(emg.makeCoordinate(12, 16)));

    }

    //An error should be thrown if both POINTS_CONFLICT and REMOVE are defined
    @Test
    void noRemoveAndConflictTest() throws Exception {
        assertThrows(EscapeException.class, ()->{
            EscapeGameBuilder egb
                    = new EscapeGameBuilder(new File("config/full_games/InvalidOrthoGame.xml"));
            EscapeGameManager emg = egb.makeGameManager();
            TestGameObserver obs = new TestGameObserver();
        });
    }

    //Pieces should not be captured if REMOVE or POINTS_CONFLICT is not defined
    @Test
    void noRemovalTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/NoCaptureOrthoGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);

        //Fail on capture
        assertFalse(emg.move(emg.makeCoordinate(1, 1), emg.makeCoordinate(2, 1)));
        assertNotNull(emg.getPieceAt(emg.makeCoordinate(2, 1)));
        assertNotNull(emg.getPieceAt(emg.makeCoordinate(1, 1)));

    }

    //If a move fails, the player should still have their turn until a valid move is made
    @Test
    void noTurnChangeOnFailTest() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/NoCaptureOrthoGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        TestGameObserver obs = new TestGameObserver();
        emg.addObserver(obs);

        //Fail on capture
        assertFalse(emg.move(emg.makeCoordinate(1, 1), emg.makeCoordinate(2, 1)));
        assertNotNull(emg.getPieceAt(emg.makeCoordinate(2, 1)));
        assertNotNull(emg.getPieceAt(emg.makeCoordinate(1, 1)));

        //should pass now
        assertTrue(emg.move(emg.makeCoordinate(1, 1), emg.makeCoordinate(1, 2)));

    }


}

