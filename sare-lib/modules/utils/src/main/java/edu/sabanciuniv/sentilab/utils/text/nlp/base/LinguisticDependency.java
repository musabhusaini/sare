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

package edu.sabanciuniv.sentilab.utils.text.nlp.base;

/**
 * The base class for objects that 
 * @author Mus'ab Husaini
 */
public abstract class LinguisticDependency
	extends LinguisticObject {

	/**
	 * Creates an instance of the {@code LinguisticDependency} object.
	 * @param processor the {@link ILinguisticProcessor} that was used to produce this dependency.
	 */
	protected LinguisticDependency(ILinguisticProcessor processor) {
		super(processor);
	}
	
	/**
	 * Gets the relation of this dependency.
	 * @return the relation.
	 */
	public abstract String getRelation();
	
	/**
	 * Gets the governor token of this dependency.
	 * @return the governor.
	 */
	public abstract LinguisticToken getGovernor();
	
	/**
	 * Gets the dependent token of this dependency.
	 * @return the dependent.
	 */
	public abstract LinguisticToken getDependent();
}