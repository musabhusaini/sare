/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.core.controllers.factory;

import edu.sabanciuniv.sentilab.core.controllers.IController;
import edu.sabanciuniv.sentilab.core.models.factory.*;

/**
 * A class that extends this interface provides a method to create objects of a certain type.
 * @author Mus'ab Husaini
 *
 * @param <T> the type of objects created by this implementation.
 * @param <O> the type of options accepted by this factory; must implement {@link IFactoryOptions}.
 */
public interface IFactory<T, O extends IFactoryOptions<T>>
	extends IController {

	/**
	 * Creates an object of type {@code T}.
	 * @param options the options to use for creating the desired object.
	 * @return the created object.
	 * @throws IllegalFactoryOptionsException when the options are not sufficient to create the object.
	 */
	public T create(O options) throws IllegalFactoryOptionsException;
}