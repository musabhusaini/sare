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

import org.apache.commons.lang3.Validate._

import edu.sabanciuniv.sentilab.core.models.ModelLike

/**
 * The base class for all objects produced by an {@link LinguisticProcessorLike}.
 * @author Mus'ab Husaini
 */
abstract class LinguisticObject(val processor: LinguisticProcessorLike)
	extends ModelLike {
	
	notNull(processor, "The argument 'processor' must not be null")
}