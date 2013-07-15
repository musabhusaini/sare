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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore

import scala.collection.JavaConversions._

import com.google.common.base.Function

import edu.sabanciuniv.sentilab.sare.models.base.document._

/**
 * A hybrid store combining a {@link DocumentCorpus} and a {@link Lexicon} type stores.
 * @author Mus'ab Husaini
 */
abstract class CorpusLexiconHybridStore[L <: Lexicon](corpus: DocumentCorpus, lexicon: L, shadowize: PersistentDocument => ShadowFullTextDocument)
	extends HybridDocumentStore(Seq(corpus.asInstanceOf[PersistentDocumentStore], lexicon.asInstanceOf[PersistentDocumentStore])) {
	
	setBaseStore(corpus)
	
	/**
	 * Creates a new instance of {@link CorpusLexiconHybridStore}.
	 */
	def this() = this(null, null.asInstanceOf[L], null)
	
	override def setBaseStore(base: PersistentDocumentStore) = {
		super.setBaseStore(base)
		base match {
		  	case corpus: DocumentCorpus if corpus != null => Option(shadowize) map { shadowize =>
		  		setDocuments(asJavaIterable(corpus.getDocuments map { shadowize(_) } filter { _ != null }))
			}
		  	case _ => setDocuments(null)
		}
		this
	}
	
	/**
	 * Gets the corpus this builder is based on.
	 * @return the {@link DocumentCorpus} object this builder is based on.
	 */
	def getCorpus = getBaseStore match {
	  	case store: DocumentCorpus => store
	  	case _ => null
	}
	
	/**
	 * Gets the lexicon being created by this builder.
	 * @return the {@link Lexicon} object this builder is creating.
	 */
	def getLexicon = getBaseStores(classOf[Lexicon]).head

	override def getTitle = (Option(super.getTitle), Option(getBaseStore)) match {
	  	case (Some(title), _) => title
	  	case (_, Some(base)) => base.getTitle
	  	case _ => null
	}

	override def getLanguage = (Option(super.getLanguage), Option(getBaseStore)) match {
	  	case (Some(language), _) => language
	  	case (_, Some(base)) => base.getLanguage
	  	case _ => null
	}

	override def getDescription = (Option(super.getDescription), Option(getBaseStore)) match {
	  	case (Some(description), _) => description
	  	case (_, Some(base)) => base.getDescription
	  	case _ => null
	}

	override def getOwnerId = (Option(super.getOwnerId), Option(getBaseStore)) match {
	  	case (Some(ownerId), _) => ownerId
	  	case (_, Some(base)) => base.getOwnerId
	  	case _ => null
	}
}