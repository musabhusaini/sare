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

package edu.sabanciuniv.sentilab.utils.text.nlp.base;

/**
 * Abstract class for any linguistic entity of a text.
 * @author Mus'ab Husaini
 */
public abstract class LinguisticEntity
	extends LinguisticObject
	implements Comparable<LinguisticEntity> {
	
	/**
	 * Creates an instance of {@link LinguisticEntity} with the specified text value.
	 * @param processor the {@link ILinguisticProcessor} that was used to produce this data.
	 */
	protected LinguisticEntity(ILinguisticProcessor processor) {
		super(processor);
	}
	
	/**
	 * Gets the text value of this entity.
	 * @return The text value of this entity.
	 */
	public abstract String getText();
	
	@Override
	public String toString() {
		return this.getText();
	}
	
	/**
	 * Gets a string representation of this entity, possibly an information rich version.
	 * @param enhanced {@code true} if an NLP-enhanced version is needed; {@code false} otherwise.
	 * @return the {@link String} representation of this entity.
	 */
	public String toString(boolean enhanced) {
		return this.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof LinguisticEntity) {
			return this.compareTo((LinguisticEntity)other) == 0;
		}
		
		return super.equals(other);
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public int compareTo(LinguisticEntity other) {
		return this.toString().compareTo(other.toString());
	}
}