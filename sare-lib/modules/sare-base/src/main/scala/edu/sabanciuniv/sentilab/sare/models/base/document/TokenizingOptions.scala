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

import scala.collection.JavaConversions._

import edu.sabanciuniv.sentilab.core.models.ModelLike
import edu.sabanciuniv.sentilab.utils.text.nlp.base.PosTag

/**
 * An instance of this class represents tokenizing options for a {@link FullTextDocument}.
 * @author Mus'ab Husaini
 */
class TokenizingOptions
	extends ModelLike {
	
  
	private var tags: java.util.List[String] = List()
	private var lemmatized: Boolean = _
	
	/**
	 * Gets the POS tags that will be captured.
	 * @return the {@link List} of {@link String} objects representing the POS tag pattern that will be captured.
	 */
	def getTags: java.util.List[String] = { tags = Option(tags) getOrElse List(); tags }
	
	/**
	 * Sets the POS tags that need to be captured.
	 * @param tags an {@link Iterable} of {@link String} objects representing the POS tag patterns to be captured.
	 * @return the {@code this} object.
	 */
	def setTags(tags: java.lang.Iterable[String]): TokenizingOptions = {
		this.tags = Option(tags) map { _.toList } getOrElse null
		this
	}
	
	/**
	 * Sets the POS tags that need to be captured.
	 * @param tags a delimited string of tags.
	 * @return the {@code this} object.
	 */
	def setTags(tags: String): TokenizingOptions = setTags(PosTag.splitTagsString(tags))
	
	/**
	 * Gets a flag indicating whether tokens will be lemmatized or not.
	 * @return a {@link Boolean} flag.
	 */
	def isLemmatized = lemmatized
	
	/**
	 * Sets a flag indicating whether tokens should be lemmatized or not.
	 * @param isLemmatized the {@link Boolean} flag to be set.
	 * @return the {@code this} object.
	 */
	def setLemmatized(lemmatized: Boolean) = { this.lemmatized = lemmatized; this }
	
	override def clone = new TokenizingOptions()
		.setTags(getTags)
		.setLemmatized(isLemmatized)

	override def equals(obj: Any) = obj match {
	  	case other: TokenizingOptions => {
	  		isLemmatized == other.isLemmatized &&
	  		getTags.size == other.getTags.size &&
	  		(getTags forall { other.getTags contains _ })
	  	}
	  	case _ => super.equals(obj)
	}
}