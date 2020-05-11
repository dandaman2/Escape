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
import escape.piece.PieceName;
import escape.piece.Player;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class RuleTests {

    //Test to ensure that sures are being initialized properly from sa config file
    @Test
    void readingRulesTest() throws Exception{
        EscapeGameBuilder egb
                = new EscapeGameBuilder(new File("config/full_games/StartingGame.xml"));
        EscapeGameManager emg = egb.makeGameManager();
        System.out.println(egb.getGameInitializer());
    }


}

