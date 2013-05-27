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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordText extends LinguisticText {
	
	private Annotation document;
	
	public StanfordText(LinguisticProcessorLike processor, Annotation text) {
		super(processor);
		
		this.document = Validate.notNull(text, CannedMessages.NULL_ARGUMENT, "text");
	}

	@Override
	public Iterable<LinguisticSentence> getSentences() {
		return Iterables.transform(document.get(SentencesAnnotation.class), new Function<CoreMap, LinguisticSentence>() {
			@Override
			public LinguisticSentence apply(CoreMap input) {
				return new StanfordSentence(processor(), input);
			}
		});
	}

	@Override
	public Iterable<LinguisticToken> getTokens() {
		return Iterables.transform(document.get(TokensAnnotation.class), new Function<CoreLabel, LinguisticToken>() {
			@Override
			public LinguisticToken apply(CoreLabel input) {
				return new StanfordToken(processor(), input);
			}
		});
	}

	@Override
	public String getText() {
		return this.document.get(TextAnnotation.class);
	}
}