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

package edu.sabanciuniv.sentilab.sare.models.aspect.extraction

import scala.collection.JavaConversions._
import org.apache.commons.lang3.Validate._

import edu.sabanciuniv.sentilab.sare.models.base.document._
import edu.sabanciuniv.sentilab.sare.models.base.documentStore._
import edu.sabanciuniv.sentilab.core.models.UserInaccessibleModel
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectExpression
import edu.sabanciuniv.sentilab.utils.CannedMessages

/**
 * @author Mus'ab Husaini
 */
class AspectExprExtrDocument(
		baseDocument: FullTextDocument = null,
		store: AspectExprExtrDocumentStore = null
	)
	extends ShadowFullTextDocument(baseDocument)
	with UserInaccessibleModel {
	
	setStore(store)
	
	private def initialize = {
		(Option(getBaseDocument), Option(getStore)) match {
		  	case (Some(base: FullTextDocument), Some(store: AspectExprExtrDocumentStore)) => {
				base.getParsedContent.getSentences filter { _.getTokens.size < 50 } foreach { sentence =>
			  	  	sentence.getTokens filter { token =>
			  	  	  	!getExtractorStore.stopWords.exists { _ equalsIgnoreCase token.getWord } &&
			  	  	  	token.getWord.length > 2 &&
			  	  	  	token.getPosTag.isNoun &&
			  	  	  	token.getWord.matches("[a-zA-Z\\'\\-]+")
			  	  	} foreach { token =>
			  	  	  	token.setIsLemmatized(true)
			  	  	  	val exp = store.getCandidateExpressions find {
			  	  	  		_ equals token
			  	  	  	} getOrElse new ContextualizedAspectExpression(token, getExtractorStore)
			  	  	  	exp addSentence sentence
			  	  	}
			  	}
		  	}
		  	case _ => ()
		}
		this
	}
	
	override def setStore(store: PersistentDocumentStore) = if (store != getStore) {
		super.setStore(store)
		initialize
	} else this
	
	override def setBaseDocument(base: PersistentDocument) = if (base != getBaseDocument) {
		super.setBaseDocument(base)
		initialize
	} else this

	def getExtractorStore = getStore match {
	  	case store: AspectExprExtrDocumentStore => store
	  	case _ => null
	}
	
	override def getAccessible = getFullTextDocument
}