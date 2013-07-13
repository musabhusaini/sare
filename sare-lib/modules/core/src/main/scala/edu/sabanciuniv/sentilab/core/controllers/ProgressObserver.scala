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

package edu.sabanciuniv.sentilab.core.controllers

/**
 * A class that implements this interface can observe progress from {@link ProgressObservable} objects.
 * @author Mus'ab Husaini
 */
trait ProgressObserver {
	
	/**
	 * Observes progress from the event host.
	 * @param progress the fractional progress; with {@code 0.0} indicating no progress and {@code 1.0} indicating completion. 
	 * @param message any message sent from the host.
	 */
	def observe(progress: Double, message: String): Unit
}