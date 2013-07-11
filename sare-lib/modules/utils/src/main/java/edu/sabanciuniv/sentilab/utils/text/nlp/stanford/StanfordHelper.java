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

import java.util.List;

import com.google.common.collect.Lists;

import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * A helper class to get around some annoying Scala problem with Stanford's code.
 * @author Mus'ab Husaini
 */
public final class StanfordHelper {

	private StanfordHelper() {
	}
	
	public static List<CoreLabel> getTokens(CoreMap sentence) {
		return sentence.get(TokensAnnotation.class);
	}
	
	public static List<CoreMap> getSentences(Annotation document) {
		return document.get(SentencesAnnotation.class);
	}
	
	public static String getText(CoreMap sentence) {
		return sentence.get(TextAnnotation.class);
	}
	
	public static List<TypedDependency> getDependencies(CoreMap sentence) {
		return Lists.newArrayList(sentence.get(CollapsedCCProcessedDependenciesAnnotation.class).typedDependencies());
	}
}