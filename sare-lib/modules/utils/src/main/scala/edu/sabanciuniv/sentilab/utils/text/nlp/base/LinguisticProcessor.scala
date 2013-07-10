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

import java.lang.annotation.Annotation

import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo

private object DefaultLinguisticProcessorInfo
	extends LinguisticProcessorInfo {
	
	override def annotationType = classOf[LinguisticProcessorInfo]
	override def name = ""
	override def languageCode = ""
	override def languageName = ""
	override def canTag = true
	override def canParse = false
}

/**
 * The base implementation of {@link LinguisticProcessorLike}.
 * @author Mus'ab Husaini
 */
abstract class LinguisticProcessor
	extends LinguisticProcessorLike {

	private val infoAnnotation = getClass.getAnnotation(classOf[LinguisticProcessorInfo]) match {
	  	case null => DefaultLinguisticProcessorInfo
	  	case info => info
	}
	
	/**
	 * Gets the name of this processor.
	 * @return the name.
	 */
	def getName = infoAnnotation.name
	
	/**
	 * Gets the code of the language that this processor can process.
	 * @return the language code.
	 */
	def getLanguageCode = infoAnnotation.languageCode
	
	/**
	 * Gets the name of the language that this processor can process. 
	 * @return the language name.
	 */
	def getLanguageDisplayName = infoAnnotation.languageName
	
	/**
	 * Gets a flag indicating whether this processor can provide POS tags or not.
	 * @return the flag.
	 */
	def canTag = infoAnnotation.canTag
	
	/**
	 * Gets a flag indicating whether this processor can parse text for dependencies or not.
	 * @return the flag.
	 */
	def canParse = infoAnnotation.canParse
}