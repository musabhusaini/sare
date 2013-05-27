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

import java.util.Map

import edu.sabanciuniv.sentilab.core.controllers.ControllerLike

/**
 * A class that implements this interface will be able to provide NLP capabilities.
 * @author Mus'ab Husaini
 */
trait LinguisticProcessorLike
	extends ControllerLike {
	
	/**
	 * Gets the basic POS tags of this language.
	 * @return a {@link Map} where the keys are internal POS tag names and the values are their respective pluralized display names.
	 */
	def getBasicPosTags: Map[String, String]
	
	/**
	 * Decomposes a given text using NLP to its sentences and tokens.
	 * @param text the text to decompose.
	 * @return a {@link LinguisticText} object containing the decomposed text.
	 */
	def decompose(text: String): LinguisticText
	
	/**
	 * Tags a given text with POS tags.
	 * @param text the text to tag.
	 * @return a {@link LinguisticText} object containing the tagged text.
	 */
	def tag(text: String): LinguisticText
	
	/**
	 * Parses a given text for linguistic dependencies.
	 * @param text the text to parse.
	 * @return a {@link LinguisticText} object containing the parsed text.
	 */
	def parse(text: String): LinguisticText
}