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

package eu.ubipol.opinionmining.nlp_engine;

import java.util.*;
import java.util.Map.Entry;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;
import eu.ubipol.opinionmining.database_engine.DatabaseAdapter;

public class Paragraph {
	private List<SentenceObject> sentences;

	public Paragraph(String text, DatabaseAdapter adp) {
		Annotation document = new Annotation(text);
		Utils.getNLPEngine().annotate(document);
		List<CoreMap> iSentences = document.get(SentencesAnnotation.class);
		sentences = new ArrayList<SentenceObject>();
		int count = 0;
		SemanticGraph dependencies;
		for (CoreMap c : iSentences) {
			dependencies = c.get(BasicDependenciesAnnotation.class);
			if (dependencies.getRoots().size() > 0) {
				sentences.add(new SentenceObject(dependencies, count, adp, c
						.get(CharacterOffsetBeginAnnotation.class), c
						.get(CharacterOffsetEndAnnotation.class), c
						.get(TextAnnotation.class)));
				count += dependencies.size();
			}
		}
	}

	public Map<AspectLexicon, Float> getScoreMap() {
		Map<AspectLexicon, Float> tempScoreMap = new HashMap<AspectLexicon, Float>();
		Map<AspectLexicon, Integer> tempWeightMap = new HashMap<AspectLexicon, Integer>();

		for (SentenceObject so : sentences) {
			Sentence s = so.getSentence();
			for (Entry<AspectLexicon, Float> e : s.getScoreMap().entrySet()) {
				if (!tempScoreMap.containsKey(e.getKey())) {
					tempScoreMap.put(e.getKey(), new Float(0));
					tempWeightMap.put(e.getKey(), 0);
				}
				tempScoreMap
						.put(e.getKey(),
								(tempScoreMap.get(e.getKey())
										* tempWeightMap.get(e.getKey()) + e
										.getValue()
										* s.getWeightMap().get(e.getKey()))
										/ (s.getWeightMap().get(e.getKey()) + tempWeightMap
												.get(e.getKey())));
				tempWeightMap.put(e.getKey(), tempWeightMap.get(e.getKey())
						+ s.getWeightMap().get(e.getKey()));
			}
		}
		return tempScoreMap;
	}

	public List<SentenceObject> getSentenceMap() {
		return sentences;
	}

	public List<ModifierItem> getModifierList() {
		List<ModifierItem> indexList = new ArrayList<ModifierItem>();
		for (SentenceObject so : sentences) {
			indexList.addAll(so.getModifierList());
		}
		return indexList;
	}
}