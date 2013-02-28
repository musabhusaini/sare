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

package edu.sabanciuniv.sentilab.utils.text.nlp.stanford;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;
import edu.stanford.nlp.ling.CoreLabel;

public class StanfordToken
	extends LinguisticToken {

	private CoreLabel token;
	
	public StanfordToken(ILinguisticProcessor processor, CoreLabel token) {
		super(processor);
		
		this.token = Validate.notNull(token, CannedMessages.NULL_ARGUMENT, "token");
	}
	
	@Override
	public String getLemma() {
		return this.token.lemma(); 
	}

	@Override
	public StanfordPosTag getPosTag() {
		return new StanfordPosTag(this.processor, this.token.tag());
	}

	@Override
	public String getText() {
		return this.token.originalText();
	}
	
	@Override
	public String getTrailingSeparator() {
		return this.token.after();
	}
	
	@Override
	public String toString(boolean enhanced) {
		return super.toString(enhanced) + (enhanced ? token.after() : "");
	}
}