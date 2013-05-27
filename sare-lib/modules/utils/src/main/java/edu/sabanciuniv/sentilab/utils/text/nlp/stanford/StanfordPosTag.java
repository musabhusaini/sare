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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.utils.text.nlp.stanford;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;

public class StanfordPosTag extends PosTag {

	public StanfordPosTag(LinguisticProcessorLike processor, String tag) {
		super(processor, Validate.notNull(tag, CannedMessages.NULL_ARGUMENT, "tag"));
	}

	@Override
	public String getSimpleTag() {
		if (this.tag.startsWith("JJ")) {
			return ADJECTIVE;
		}
		
		if (this.tag.startsWith("NN")) {
			return NOUN;
		}
		
		if (this.tag.startsWith("RB") || this.tag.endsWith("RB")) {
			return ADVERB;
		}
		
		if (this.tag.startsWith("VB")) {
			return VERB;
		}
		
		return this.tag;
	}
}