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

package edu.sabanciuniv.sentilab.core.models.factory;

/**
 * An exception that is thrown when the {@link IFactoryOptions} object provided to a {@code create} method contains options that are not correct or sufficient to create the desired object.
 * @author Mus'ab Husaini
 *
 */
public class IllegalFactoryOptionsException
	extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8224860508230579554L;

	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class.
	 */
	public IllegalFactoryOptionsException() {
		super();
	}
	
	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class with the specified message.
	 * @param s the detailed message.
	 */
	public IllegalFactoryOptionsException(String s) {
		super(s);
	}
	
	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class with the specified cause.
	 * @param cause the {@link Throwable} indicating the cause of this exception.
	 */
	public IllegalFactoryOptionsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class with the specified cause and message.
	 * @param s the detailed message.
	 * @param cause the {@link Throwable} indicating the cause of this exception.
	 */
	public IllegalFactoryOptionsException(String s, Throwable cause) {
		super(s, cause);
	}
}