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
import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordSentence
	extends LinguisticSentence {

	private CoreMap sentence;
	
	public StanfordSentence(ILinguisticProcessor processor, CoreMap sentence) {
		super(processor);
		
		this.sentence = Validate.notNull(sentence, CannedMessages.NULL_ARGUMENT, "sentence");
	}
	
	@Override
	public Iterable<LinguisticToken> getTokens() {
		return Iterables.transform(this.sentence.get(TokensAnnotation.class), new Function<CoreLabel, LinguisticToken>() {
			@Override
			public LinguisticToken apply(CoreLabel input) {
				return new StanfordToken(processor, input);
			}
		});
	}

	@Override
	public Iterable<LinguisticDependency> getDependencies() {
		SemanticGraph graph = this.sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		if (graph == null) {
			return Lists.newArrayList();
		}
		
		return Iterables.transform(graph.typedDependencies(), new Function<TypedDependency, LinguisticDependency>() {
			@Override
			public LinguisticDependency apply(TypedDependency input) {
				return new StanfordDependency(processor, input);
			}
		});
	}

	@Override
	public String getText() {
		return this.sentence.get(TextAnnotation.class);
	}
}