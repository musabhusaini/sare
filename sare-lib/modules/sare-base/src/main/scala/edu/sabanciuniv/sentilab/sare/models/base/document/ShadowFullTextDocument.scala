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

package edu.sabanciuniv.sentilab.sare.models.base.document

/**
 * A full text document that does not have its own content, but uses content from another document.
 * @author Mus'ab Husaini
 */
abstract class ShadowFullTextDocument(baseDocument: FullTextDocument)
	extends FullTextDocument {
  
	setBaseDocument(baseDocument)

	/**
	 * Creates an instance of {@link ShadowFullTextDocument}.
	 */
	def this() = this(null)
	
	/**
	 * Gets the underlying full text document.
	 * @return the underlying {@link FullTextDocument}.
	 */
	def getFullTextDocument = getBaseDocument match {
	  	case doc: FullTextDocument => doc
	  	case _ => null
	}
	
	override def getContent = Option(getBaseDocument) map { _.getContent } getOrElse null
	
	override def getTokenizingOptions =
		Option(super.getTokenizingOptions)
			.getOrElse(Option(getFullTextDocument) map { _.getTokenizingOptions } getOrElse null)
}