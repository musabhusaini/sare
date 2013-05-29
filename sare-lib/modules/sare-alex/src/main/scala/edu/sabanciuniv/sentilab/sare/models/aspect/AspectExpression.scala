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

package edu.sabanciuniv.sentilab.sare.models.aspect

import javax.persistence._

import edu.sabanciuniv.sentilab.sare.models.base.document._

/**
 * A class that represents an aspect expression.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("aspect-expression")
class AspectExpression
	extends LexiconDocument {

	/**
	 * Gets the aspect this expression belongs to.
	 * @return the {@link AspectLexicon} object this expression belongs to; {@code null} if none.
	 */
	def getAspect = getStore match {
	  	case store: AspectLexicon => store
	  	case _ => null
	}
}