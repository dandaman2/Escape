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

import escape.GameObserver;

//Test implementation of the GameObserver
public class TestGameObserver implements GameObserver {
    public boolean gotError = false;

    @Override
    public void notify(String message) {
        gotError = true;
        System.out.println(message);
    }

    @Override
    public void notify(String message, Throwable cause) {
        gotError = true;
        System.out.println("Caught from: " + cause.getClass());
        //System.out.println("Attached Message: " + cause.getMessage());
        System.out.println(message);
    }
}
