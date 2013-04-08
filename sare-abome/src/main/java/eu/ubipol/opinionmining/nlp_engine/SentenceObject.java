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

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import eu.ubipol.opinionmining.database_engine.DatabaseAdapter;

public class SentenceObject {
	private Sentence sentence;
	private int beginPosition;
	private int endPosition;
	private String text;

	public SentenceObject(SemanticGraph dependencies, int indexStart,
			DatabaseAdapter adp, int beginPosition, int endPosition, String text) {
		sentence = new Sentence(dependencies, indexStart, adp, beginPosition);
		this.beginPosition = beginPosition;
		this.endPosition = endPosition;
		this.text = text;
	}

	public Sentence getSentence() {
		return sentence;
	}

	public int getBeginPosition() {
		return beginPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public Map<AspectLexicon, Float> getScoreMap() {
		return sentence.getScoreMap();
	}

	public String getText() {
		return text;
	}

	private void gddChildren(List<Token> returnList, Token currentRoot) {
		for (Token t : currentRoot.getChildrenList()) {
			returnList.add(t);
			gddChildren(returnList, t);
		}
	}

	public List<Token> getTokenList() {
		List<Token> wholeToken = new ArrayList<Token>();
		wholeToken.add(sentence.getRootToken());
		gddChildren(wholeToken, sentence.getRootToken());
		return wholeToken;
	}

	public List<ModifierItem> getModifierList() {
		List<ModifierItem> modifierList = new ArrayList<ModifierItem>();
		for (Token t : getTokenList()) {
			if (t.getModifierList().size() > 0) {
				for (Token mt : t.getModifierList()) {
					modifierList.add(new ModifierItem(t, mt));
				}
			} else if (t.isAKeyword()) {
				modifierList.add(new ModifierItem(t));
			}
		}
		return modifierList;
	}
}