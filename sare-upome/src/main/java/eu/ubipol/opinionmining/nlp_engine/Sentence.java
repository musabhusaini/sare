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

import java.util.Map;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import eu.ubipol.opinionmining.database_engine.DatabaseAdapter;

public class Sentence {
	private Token sentenceRoot;

	protected Sentence(SemanticGraph dependencies, int indexStart,
			DatabaseAdapter adp, int beginPosition) {
		// System.out.println(dependencies);
		IndexedWord rootWord = dependencies.getFirstRoot();
		sentenceRoot = new Token(rootWord.originalText(), rootWord.lemma(),
				rootWord.tag(), null, null, rootWord.index() + indexStart,
				rootWord.beginPosition(), rootWord.endPosition(), adp,
				beginPosition);
		addChildTokens(sentenceRoot, rootWord, dependencies, indexStart, adp,
				beginPosition);
		sentenceRoot.transferScores();
		if (sentenceRoot.isAKeyword())
			sentenceRoot.addAspectScore(sentenceRoot.getScore(),
					sentenceRoot.getWeight(), sentenceRoot.getAspect());
		indexStart += dependencies.size();
	}

	private void addChildTokens(Token rootToken, IndexedWord currentRoot,
			SemanticGraph dependencies, int indexStart, DatabaseAdapter adp,
			int beginPosition) {
		for (IndexedWord child : dependencies.getChildren(currentRoot)) {
			Token childToken = new Token(child.originalText(), child.lemma(),
					child.tag(), rootToken, dependencies.getEdge(currentRoot,
							child).toString(), child.index() + indexStart,
					child.beginPosition(), child.endPosition(), adp,
					beginPosition);
			rootToken.addChildToken(childToken);
			addChildTokens(childToken, child, dependencies, indexStart, adp,
					beginPosition);
		}
	}

	protected Map<AspectLexicon, Float> getScoreMap() {
		return sentenceRoot.getScoreMap();
	}

	protected Map<AspectLexicon, Integer> getWeightMap() {
		return sentenceRoot.getWeightMap();
	}

	protected Token getRootToken() {
		return sentenceRoot;
	}
}