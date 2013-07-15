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

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.beans.BeanProperty

import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.StringUtils._

import edu.sabanciuniv.sentilab.core.models.ModelLike
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectExpression
import edu.sabanciuniv.sentilab.utils.text.nlp.base._

object ContextualizedAspectExpression {
	val polarityLabel = "$polarity$"
	val neutralityLabel = "$neutrality$"
	val sentenceCountLabel = "$sentences$"
	val classLabel = "$class$"
	val contentLabel = "$content$"
}

/**
 * @author Mus'ab Husaini
 */
class ContextualizedAspectExpression(
		val token: LinguisticToken,
		store: AspectExprExtrDocumentStore = null
	)
	extends AspectExpression(Option(token) map { _.getWord } getOrElse null) {
	
	Option(token) foreach { _.setIsLemmatized(true) }
	setStore(store)
  
	var label: Option[Boolean] = None
	var sentences: Seq[LinguisticSentence] = Seq()
	private var context: Map[String, Double] = Map()
	var score: Option[Double] = None
	
	def getExtractorStore = getStore match {
	  	case store: AspectExprExtrDocumentStore => store
	  	case _ => null
	}
	
	def addSentence(sentence: LinguisticSentence) = { sentences = sentences :+ sentence; this }
	
	def removeSentence(sentence: LinguisticSentence) = {
	  	val oldSentences = sentences
	  	sentences = sentences filter { _ equals sentence }
	  	oldSentences.size != sentences.size
	}
	
	private def extractContext = {
		var polarityContext: mutable.Map[String, Double] = mutable.Map()
	  	val validTokens = sentences flatten { _.getTokens } filter {
	  	  !_.equals(token) && !getExtractorStore.stopWords.exists(_ equalsIgnoreCase token.getWord)
	  	}
	  	
	  	val polarityTokens = validTokens filter { token =>
	  	  	token.getPosTag.isAdjective || token.getPosTag.isAdverb || token.getPosTag.isVerb
	  	}
	  	polarityTokens foreach { token =>
	  	  	Option(getExtractorStore.sentimentLexicon.findExpression(token.getLemma, token.getPosTag.getSimpleTag)) foreach { sentExp =>
	  	  		polarityContext += (ContextualizedAspectExpression.neutralityLabel ->
	  	  		    (sentExp.getNeutral + polarityContext.getOrElse(ContextualizedAspectExpression.neutralityLabel, 0.0)))
	  	  		polarityContext += (ContextualizedAspectExpression.polarityLabel ->
	  	  		    (sentExp.getPositive + sentExp.getNegative + 
	  	  		        polarityContext.getOrElse(ContextualizedAspectExpression.polarityLabel, 0.0)))
	  	  	}
	  	}
	  	
	  	polarityContext = polarityContext map { kv => (kv._1 -> (kv._2 / polarityTokens.size)) }
	  	
	  	var tagContext: mutable.Map[String, Double] = mutable.Map()
	  	val tagTokens = validTokens filter { !_.getPosTag.getSimpleTag.matches("^[A-Za-z]+.*$") }
	  	tagTokens foreach { token =>
	  	  	val label = String.format("$%s$", token.getPosTag.getSimpleTag)
	  	  	tagContext += (label -> (1 + tagContext.getOrElse(label, 0.0)))
	  	}
	  	
	  	tagContext = tagContext map { kv =>
	  	  	(kv._1 -> (kv._2 / tagTokens.size))
	  	}
	  	
	  	(polarityContext ++ tagContext).toMap
	}
	
	def resetContext = { context = extractContext; context; }
	
	def getContext = Option(context) getOrElse resetContext
	
	override def equals(obj: Any) = obj match {
	  	case exp: ContextualizedAspectExpression => equalsIgnoreCase(getContent, exp.getContent)
	  	case exp: String => equalsIgnoreCase(getContent, exp)
	  	case token: LinguisticToken => ObjectUtils.equals(this.token, token)
	  	case _ => super.equals(obj)
	}
	
	override def hashCode = Option(getContent) map { _.hashCode } getOrElse super.hashCode
	
	override def toString = Option(getContent) getOrElse super.toString
}