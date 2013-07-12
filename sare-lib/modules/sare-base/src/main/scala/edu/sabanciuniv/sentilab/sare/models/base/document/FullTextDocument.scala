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

import java.util._

import javax.persistence._

import scala.collection.JavaConversions._

import org.apache.commons.lang3.Validate._

import edu.sabanciuniv.sentilab.utils.CannedMessages
import edu.sabanciuniv.sentilab.utils.extensions.MapsExtensions._
import edu.sabanciuniv.sentilab.utils.extensions.IterablesExtensions._
import edu.sabanciuniv.sentilab.utils.text.nlp.base._
import edu.sabanciuniv.sentilab.utils.text.nlp.factory._

/**
 * The base class for full text documents.
 * @author Mus'ab Husaini
 */
@Entity
abstract class FullTextDocument
	extends PersistentDocument {

	/**
	 * This is the original content that was used for tokenization.
	 */
	@Transient
	private var tokenizedContent: String = _
	
	@Transient
	private var parsedContent: LinguisticText = _
	
	@Transient
	private var tokenWeightMap: Map[LinguisticToken, java.lang.Double] = _

	private def setTokenWeightMap(map: Map[LinguisticToken, java.lang.Double]) = { tokenWeightMap = map; this }
	
	/**
	 * Gets the token weight map for this document.
	 * @param original {@link Boolean} flag indicating whether to get the original version or an unmodifiable version.
	 * @param suppressRetokenization flag indicating whether to suppress retokenization in all cases.
	 * @return the {@link Map} object representing the token weight map.
	 */
	protected def getTokenWeightMap(original: Boolean, suppressRetokenization: Boolean): Map[LinguisticToken, java.lang.Double] = {
		(suppressRetokenization, Option(tokenWeightMap), Option(tokenizedContent)) match {
		  	case (false, None, _) => retokenize
		  	case (false, _, None) => retokenize
		  	case (false, _, Some(tokenizedContent)) if !tokenizedContent.equals(getContent) => retokenize
		  	case _ => ()
		}
		
		original match {
		  	case true => tokenWeightMap
		  	case _ => tokenWeightMap
		}
	}
	
	/**
	 * Gets the token weight map for this document.
	 * @param original {@link Boolean} flag indicating whether to get the original version or an unmodifiable version.
	 * @return the {@link Map} object representing the token weight map.
	 */
	protected def getTokenWeightMap(original: Boolean): Map[LinguisticToken, java.lang.Double] =
		getTokenWeightMap(original, false)

	/**
	 * Gets the weight of a given token.
	 * @param token the {@link LinguisticToken} object whose weight to get.
	 * @return the weight of the token.
	 */
	protected def getTokenWeight(token: LinguisticToken): Double = Option(getTokenWeightMap(true).get(token)) match {
	  	case Some(weight) => weight
	  	case _ => 0.0
	}
	
	/**
	 * Sets the weight of a given token, if it already exists.
	 * @param token the {@link LinguisticToken} object whose weight to set.
	 * @param weight the weight to set.
	 * @return the weight that was set.
	 */
	protected def setTokenWeight(token: LinguisticToken, weight: Double) =
		incrementTokenWeight(token, weight - getTokenWeight(token))
	
	/**
	 * Increments the weight of a given token by a certain value.
	 * @param token the {@link LinguisticToken} object whose weight is to be incremented.
	 * @param by the value to increment by.
	 * @return the final weight of the token.
	 */
	protected def incrementTokenWeight(token: LinguisticToken, by: Double) = {
		increment(getTokenWeightMap(true), token, by, false)
		getTokenWeight(token)
	}
	
	/**
	 * Gets a copy of the tokenizing options.
	 * @return the {@link TokenizingOptions} object representing a copy of the tokenizing options.
	 */
	def getTokenizingOptions = Option(getProperty("tokenizingOptions", classOf[TokenizingOptions])) match {
	  	case Some(options) => options
	  	case _ => new TokenizingOptions
	}

	/**
	 * Sets the tokenizing options.
	 * @param tokenizingOptions the {@link TokenizingOptions} object to set.
	 * @return the {@code this} object.
	 */
	def setTokenizingOptions(tokenizingOptions: TokenizingOptions) = {
		tokenizedContent = null
		parsedContent = null
		setProperty("tokenizingOptions", tokenizingOptions).asInstanceOf[FullTextDocument]
	}
	
	/**
	 * Gets an unmodifiable version of the token weight map.
	 * @return an unmodifiable version of the token weight map.
	 */
	def getTokenWeightMap: Map[LinguisticToken, java.lang.Double] = getTokenWeightMap(false)
	
	/**
	 * Gets the total token weight.
	 * @return the total token weights.
	 */
	def getTotalTokenWeight = sum(this.getTokenWeightMap(true).values)
	
	/**
	 * Recalculates the tokens and their weights for this document. 
	 * @return the {@code this} object.
	 */
	def retokenize = {
		notNull(getContent, CannedMessages.NULL_ARGUMENT, "this.content")
		notNull(getStore, CannedMessages.NULL_ARGUMENT, "this.store")
		
		setTokenWeightMap(new HashMap[LinguisticToken, java.lang.Double])
		
		// get the right NLP based on the language of the corpus.
		val nlp = new LinguisticProcessorFactory()
			.setMustTag(true)
			.setLanguage(this.getStore.getLanguage)
			.create
		
		// create the map from tokens.
		val nlpText = nlp.tag(this.getContent)
		nlpText.getTokens foreach { nlpToken =>
			// only include token if the pos tag is included in tokenizing options (no tags in the options means we include everything).
			if (getTokenizingOptions.getTags == null || getTokenizingOptions.getTags.size == 0 || nlpToken.getPosTag.is(getTokenizingOptions.getTags)) {
				nlpToken.setIsLemmatized(getTokenizingOptions.isLemmatized)
				increment(getTokenWeightMap(true, true), nlpToken)
			}
		}
		
		// make a note of the content that was used. when it changes, we will retokenize.
		tokenizedContent = getContent
		parsedContent = nlpText
		this
	}
	
	/**
	 * Gets the content as parsed by the NLP engine.
	 * @return the output of the NLP engine as a {@link LinguisticText} object.
	 */
	def getParsedContent = Option(Option(parsedContent) getOrElse retokenize) map { _ => parsedContent } getOrElse null
	
	override def toString = getContent
}