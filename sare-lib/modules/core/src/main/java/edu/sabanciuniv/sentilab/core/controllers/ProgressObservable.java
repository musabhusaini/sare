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

package edu.sabanciuniv.sentilab.core.controllers;

/**
 * A class that implements this interface allows for observing the progress of the last invoked action.
 * @author Mus'ab Husaini
 */
public interface ProgressObservable {
	
	/**
	 * Adds a progress observer for this object.
	 * @param observer the {@link ProgressObserver} to add as a listener.
	 * @return the {@code this} object.
	 */
	public ProgressObservable addProgessObserver(ProgressObserver observer);
	
	/**
	 * Removes a progress observer from this object.
	 * @param observer the {@link ProgressObserver} to remove.
	 * @return {@code true} if the observer was removed; {@code false} otherwise.
	 */
	public boolean removeProgressObserver(ProgressObserver observer);
}