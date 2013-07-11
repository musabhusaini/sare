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

import scala.collection.JavaConversions._

/**
 * The base class for a linguistic text.
 * @author Mus'ab Husaini
 */
abstract class LinguisticText(processor: LinguisticProcessorLike)
	extends LinguisticEntity(processor) {
	
	/**
	 * Gets all the sentences in this text.
	 * @return the sentences in this text.
	 */
	def getSentences: java.lang.Iterable[LinguisticSentence]
	
	/**
	 * Gets all the tokens in this text.
	 * @return the tokens in this text.
	 */
	def getTokens: java.lang.Iterable[LinguisticToken]
	
	override def toString(enhanced: Boolean) = enhanced match {
	  	case false => super.toString(enhanced)
	  	case _ => {
	  		val sb = new StringBuilder
	  		getSentences foreach { sentence =>
	  		  	sb.append(sentence.toString(enhanced))
	  		}
	  		sb.toString
	  	}
	}
}