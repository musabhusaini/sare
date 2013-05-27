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

package edu.sabanciuniv.sentilab.utils.text.nlp.base

/**
 * The base class for a linguistic token.
 * @author Mus'ab Husaini
 */
abstract class LinguisticToken(processor: LinguisticProcessorLike)
	extends LinguisticEntity(processor) {
	
	/**
	 * A flag indicating whether this token is lemmatized or not.
	 */
	protected var isLemmatized: Boolean = _
	
	/**
	 * Sets the flag indicating whether this token is lemmatized or not.
	 * @param isLemmatized the flag to set.
	 * @return this object.
	 */
	def setLemmatized(isLemmatized: Boolean) = {
	  	this.isLemmatized = isLemmatized
		this
	}

	/**
	 * Gets the lemma of this token.
	 * @return The lemma.
	 */
	def getLemma: String

	/**
	 * Gets the POS tag of this token.
	 * @return A {@link PosTag} object representing the POS tag.
	 */
	def getPosTag: PosTag
	
	/**
	 * Gets the word that this token represents. Might be lemmatized depending on the value of the {@code isLemmatized} property.
	 * @return the word that this token represents.
	 */
	def getWord = if (isLemmatized) getLemma else getText
	
	/**
	 * Gets the whitespace character that separates this token from the next one.
	 * @return the trailing whitespace character.
	 */
	def getTrailingSeparator = " "
	
	override def toString = "%s/%s".format(getWord, getPosTag)
}