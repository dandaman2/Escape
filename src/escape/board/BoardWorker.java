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
package escape.board;

import escape.board.coordinate.Coordinate;

/**
 * Collective interface to ensure that all boards have ways to interact with location types
 * and board coordinate features.
 * @param <C> The coordinate system type used
 */
public interface BoardWorker<C extends Coordinate> extends Location<C>, Board<C>{}
