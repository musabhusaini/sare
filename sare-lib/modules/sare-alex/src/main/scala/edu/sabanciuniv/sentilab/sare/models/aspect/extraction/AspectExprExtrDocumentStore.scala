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

import org.apache.commons.lang3.text.StrTokenizer

import edu.sabanciuniv.sentilab.sare.models.base.documentStore._
import edu.sabanciuniv.sentilab.sare.models.base.document._
import edu.sabanciuniv.sentilab.sare.models.opinion.SentimentLexicon
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon
import edu.sabanciuniv.sentilab.core.models.UserInaccessibleModel

object AspectExprExtrDocumentStore {
	private lazy val stopWordsString = "a,able,about,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your"
	lazy val stopWords: Seq[String] = StrTokenizer.getCSVInstance(stopWordsString).getTokenList()
}

/**
 * @author Mus'ab Husaini
 */
class AspectExprExtrDocumentStore(
		corpus: DocumentCorpus = null,
		aspectLexicon: AspectLexicon = null,
		val sentimentLexicon: SentimentLexicon = null,
		val stopWords: Seq[String] = AspectExprExtrDocumentStore.stopWords
	)
	extends CorpusLexiconHybridStore[AspectLexicon](corpus, aspectLexicon, null)
	with UserInaccessibleModel {

	override def setBaseStore(base: PersistentDocumentStore) = {
		super.setBaseStore(base)
		Option(base) match {
		  	case Some(corpus: DocumentCorpus) => corpus.getDocuments map { _ match {
			  	case doc: FullTextDocument => new AspectExprExtrDocument(doc, this)
			  	case _ => ()
			}}
		  	case _ => ()
		}
		this
	}
	
	def autoLabelCandidateExpressions(k: Int) = {
		val sorted = getCandidateExpressions.toSeq sortBy { _.sentences.size }
		sorted foreach { _.label = None }
		sorted take k foreach { _.label = Some(false) }
		sorted takeRight k foreach { _.label = Some(true) }
		splitCandidateExpressions
	}
	
	def splitCandidateExpressions = (
	    getCandidateExpressions filter { _.label isDefined },
	    getCandidateExpressions filter { _.label isEmpty  }
	)
	
	def splitLabeledCandidateExpressions = (
	    splitCandidateExpressions._1 filter { _.label map { label => label } get },
	    splitCandidateExpressions._1 filterNot { _.label map { label => label } get }
	)
	
	def getCandidateExpressions = getDocuments(classOf[ContextualizedAspectExpression])
	
	def getExtractorDocuments = getDocuments(classOf[AspectExprExtrDocument])
	
	def clearReferences = {
		getExtractorDocuments foreach { _.setBaseDocument(null) }
		removeReference(aspectLexicon)
		setBaseStore(null)
		this
	}
	
	override def getAccessible = getCorpus
}