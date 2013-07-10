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

import org.apache.commons.lang3.Validate._

import edu.sabanciuniv.sentilab.utils.CannedMessages
import edu.sabanciuniv.sentilab.utils.text.nlp.base._
import edu.stanford.nlp.ling.CoreLabel

class StanfordToken(processor: LinguisticProcessorLike, token: CoreLabel)
	extends LinguisticToken(processor) {

	notNull(token, CannedMessages.NULL_ARGUMENT, "token")
	
	override def getLemma = token.lemma

	override def getPosTag: StanfordPosTag = new StanfordPosTag(processor, token.tag)

	override def getText = token.originalText
	
	override def getTrailingSeparator = token.after
	
	override def toString(enhanced: Boolean) = "%s%s".format(super.toString(enhanced), (if (enhanced) token.after else ""))
}