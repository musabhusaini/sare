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

package edu.sabanciuniv.sentilab.utils.extensions;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A class with some static extensions for {@link Iterable} types.
 * @author Mus'ab Husaini
 */
public abstract class IterablesExtensions {
	
	private IterablesExtensions() {
		// prevent instantiation.
	}
	
	/**
	 * Calculates the sum of a given iterable of {@link Number} type items.
	 * @param iterable the {@link Iterable} of {@link Number} type items whose sum is desired.
	 * @return the {@link Double} value with the sum.
	 */
	public static <I extends Number> double sum(Iterable<I> iterable) {
		Validate.notNull(iterable, CannedMessages.NULL_ARGUMENT, "iterable");
		
		double s = 0;
		for (I item : iterable) {
			s += item.doubleValue();
		}
		
		return s;
	}
}