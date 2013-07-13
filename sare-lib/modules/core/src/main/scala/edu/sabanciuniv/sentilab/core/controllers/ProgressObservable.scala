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
 * Primitive (Java-compatible) version.
 * @author Mus'ab Husaini
 */
trait ProgressObservablePrimitive {
	
	/**
	 * Adds a progress observer for this object.
	 * @param observer the {@link ProgressObserver} to add as a listener.
	 * @return the {@code this} object.
	 */
	def addProgessObserver(observer: ProgressObserver): ProgressObservablePrimitive
	
	/**
	 * Removes a progress observer from this object.
	 * @param observer the {@link ProgressObserver} to remove.
	 * @return {@code true} if the observer was removed; {@code false} otherwise.
	 */
	def removeProgressObserver(observer: ProgressObserver): Boolean
	
	/**
	 * Notifies progress to all listeners.
	 * @param progress the current progress.
	 * @param message the message to send with the progress.
	 */
	def notifyProgress(progress: Double, message: String): Unit
}

/**
 * A class that implements this interface allows for observing the progress of the last invoked action.
 * @author Mus'ab Husaini
 */
trait ProgressObservable extends ProgressObservablePrimitive {
	
	protected var primitiveObservers: Set[ProgressObserver] = Set()
	protected var observers: Set[(Double, String) => Unit] = Set()
	
	/**
	 * Adds a progress observer for this object.
	 * @param observer the method to add as an observer.
	 * @return the {@code this} object.
	 */
	def addProgressObserver(observer: (Double, String) => Unit) = { observers += observer;  observer }
	
	/**
	 * Removes a progress observer from this object.
	 * @param observer the method to remove.
	 * @return {@code true} if the observer was removed; {@code false} otherwise.
	 */
	def removeProgressObserver(observer: (Double, String) => Unit) = {
		val oldObservers = observers
		observers -= observer
		oldObservers.size == observers.size + 1	  
	}
	
	/**
	 * Adds a progress observer for this object.
	 * @param observer the {@link ProgressObserver} to add as a listener.
	 * @return the {@code this} object.
	 */
	override def addProgessObserver(observer: ProgressObserver) = {
		primitiveObservers += observer
	  	this
	}
	
	/**
	 * Removes a progress observer from this object.
	 * @param observer the {@link ProgressObserver} to remove.
	 * @return {@code true} if the observer was removed; {@code false} otherwise.
	 */
	override def removeProgressObserver(observer: ProgressObserver) = {
		val oldObservers = primitiveObservers
		primitiveObservers -= observer
		oldObservers.size == primitiveObservers.size + 1
	}
	
	/**
	 * Notifies progress to all listeners.
	 * @param progress the current progress.
	 * @param message the message to send with the progress.
	 */
	override def notifyProgress(progress: Double, message: String) = {
		primitiveObservers foreach { _.observe(progress, message) }
		observers foreach { _(progress, message) }
	}
}