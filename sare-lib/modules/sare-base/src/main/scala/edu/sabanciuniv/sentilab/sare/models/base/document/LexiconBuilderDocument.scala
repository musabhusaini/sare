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

import javax.persistence._
import edu.sabanciuniv.sentilab.core.models.UserInaccessibleModel
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.LexiconBuilderDocumentStore
import scala.beans.BeanProperty

/**
 * A document that shadows a full text document for a lexicon builder.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("lex-builder-doc")
class LexiconBuilderDocument(baseDocument: FullTextDocument)
	extends ShadowFullTextDocument(baseDocument)
	with UserInaccessibleModel with WeightedDocument {

	weight = Option(baseDocument) match {
	  	case Some(base: WeightedDocument) => base.getWeight
	  	case Some(base) => base.weight
	  	case _ => null
	}
	
	@Transient
	@BeanProperty
	var rank: java.lang.Long = _
	
	/**
	 * Creates an instance of {@link LexiconBuilderDocument}.
	 */
	def this() = this(null)
	
	/**
	 * Gets the lexicon builder this document belongs to.
	 * @return the {@link LexiconBuilderDocumentStore} this document belongs to.
	 */
	def getLexiconBuilder = getStore match {
	  	case store: LexiconBuilderDocumentStore => store
	  	case _ => null
	}
	
	def getWeight = weight
	
	/**
	 * Gets a flag indicating whether this document has been "seen" or not.
	 * @return {@code true} if the document has been seen, {@code false} otherwise.
	 */
	def isSeen = flag
	
	/**
	 * Sets a flag indicating whether this document was "seen" or not.
	 * @param isSeen {@code true} if the document was seen, {@code false} otherwise.
	 * @return the {@code this} object.
	 */
	def setSeen(isSeen: Boolean) = {
		flag = isSeen
		this
	}

	def getAccessible = getFullTextDocument
}