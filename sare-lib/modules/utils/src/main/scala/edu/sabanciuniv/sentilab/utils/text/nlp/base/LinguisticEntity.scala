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
 * Abstract class for any linguistic entity of a text.
 * @author Mus'ab Husaini
 */
abstract class LinguisticEntity(processor: LinguisticProcessorLike)
	extends LinguisticObject(processor) with Comparable[LinguisticEntity] {
	
	/**
	 * Gets the text value of this entity.
	 * @return The text value of this entity.
	 */
	def getText: String
	
	override def toString = getText
	
	/**
	 * Gets a string representation of this entity, possibly an information rich version.
	 * @param enhanced {@code true} if an NLP-enhanced version is needed; {@code false} otherwise.
	 * @return the {@link String} representation of this entity.
	 */
	def toString(enhanced: Boolean): String = toString
	
	override def equals(other: Any) = other match {
	  	case ling: LinguisticEntity => compareTo(ling) == 0
	  	case _ => super.equals(other)
	}
	
	override def hashCode = toString.hashCode
	
	override def compareTo(other: LinguisticEntity) = toString.compareTo(other.toString)
}