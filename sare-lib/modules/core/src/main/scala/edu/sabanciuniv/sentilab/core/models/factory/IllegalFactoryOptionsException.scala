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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.core.models.factory;

/**
 * An exception that is thrown when the {@link IFactoryOptions} object provided to a {@code create} method contains options that are not correct or sufficient to create the desired object.
 * @author Mus'ab Husaini
 */
class IllegalFactoryOptionsException(s: String, cause: Throwable)
	extends IllegalArgumentException(s, cause) {

	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class.
	 */
	def this() = this(null, null)
	
	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class with the specified message.
	 * @param s the detailed message.
	 */
	def this(s: String) = this(s, null)
	
	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class with the specified cause.
	 * @param cause the {@link Throwable} indicating the cause of this exception.
	 */
	def this(cause: Throwable) = this(null, cause)
}