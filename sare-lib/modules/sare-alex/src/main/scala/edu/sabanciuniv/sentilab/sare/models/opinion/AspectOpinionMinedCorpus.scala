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

package edu.sabanciuniv.sentilab.sare.models.opinion

import javax.persistence._

import edu.sabanciuniv.sentilab.core.models._
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon
import edu.sabanciuniv.sentilab.sare.models.base.document._
import edu.sabanciuniv.sentilab.sare.models.base.documentStore._

/**
 * A {@link DocumentCorpus} that has been mined for aspect ratings.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("alex-opinion-mined-corpus")
class AspectOpinionMinedCorpus(corpus: DocumentCorpus, lexicon: AspectLexicon)
	extends CorpusLexiconHybridStore[AspectLexicon](corpus, lexicon, doc => doc match {
	  	case doc: FullTextDocument => new AspectOpinionMinedDocument(doc)
	  	case _ => null
	})
	with UserInaccessibleModel with OpinionMinedCorpusLike {

	/**
	 * Creates an instance of {@link AspectOpinionMinedCorpus}.
	 */
	def this() = this(null, null)
	
	override def getEngineCode = getProperty("engine", classOf[String])
	
	/**
	 * Sets the engine code of the opinion mining engine used.
	 * @param engine the engine code to set.
	 * @return the {@code this} object.
	 */
	def setEngineCode(engine: String) = {
		setProperty("engine", engine)
		this
	}
	
	override def getAccessible = getCorpus
}