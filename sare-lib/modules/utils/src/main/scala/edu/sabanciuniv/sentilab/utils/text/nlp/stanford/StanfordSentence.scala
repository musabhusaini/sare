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

package edu.sabanciuniv.sentilab.utils.text.nlp.stanford

import scala.collection.JavaConversions._

import org.apache.commons.lang3.Validate._

import edu.sabanciuniv.sentilab.utils.CannedMessages
import edu.sabanciuniv.sentilab.utils.text.nlp.base._
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.trees.TypedDependency
import edu.stanford.nlp.trees.semgraph.SemanticGraph
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation
import edu.stanford.nlp.util.CoreMap

class StanfordSentence(processor: LinguisticProcessorLike, private val sentence: CoreMap)
	extends LinguisticSentence(processor) {

	notNull(sentence, CannedMessages.NULL_ARGUMENT, "sentence")
	
	override def getTokens = StanfordHelper.getTokens(sentence) map {
		new StanfordToken(this.processor, _).asInstanceOf[LinguisticToken]
	}
	
	override def getDependencies = StanfordHelper.getDependencies(sentence) map {
	  	new StanfordDependency(this.processor, _)
	}
	
	override def getText = StanfordHelper.getText(sentence)
}