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
import eu.ubipol.opinionmining.database_engine.DatabaseAdapter;
import eu.ubipol.opinionmining.nlp_engine.Utils.WordType;
import eu.ubipol.opinionmining.stem_engine.Stemmer;

@SuppressWarnings("rawtypes")
public class Token implements Comparable {
	private static List<String> nonPriorTypes = new ArrayList<String>(
			Arrays.asList(new String[] { "csubj", "csubjpass", "dobj",
					"npadvmod", "nsubj", "rel", "xcomp" }));
	private static List<String> priorTypes = new ArrayList<String>(
			Arrays.asList(new String[] { "acomp", "advmod", "amod", "appos",
					"neg", "nsubjpass", "partmod", "rcmod" }));
	private Token parentToken;
	private List<Token> childTokens;
	private List<Token> modifiedByList;
	private String originalText;
	private List<String> stemmedTexts;
	private WordType wordType;
	private Map<AspectLexicon, Double> scoreList;
	private Map<AspectLexicon, Integer> weightList;
	private AspectLexicon aspect;
	private double score;
	private double pureScore;
	private int weight;
	private String parentEdgeType;
	private int tokenIndex;
	private int beginPosition;
	private int endPosition;
	private boolean invert;
	private int sentenceBeginPosition;

	protected Token(String original, String lemma, String typeString,
			Token parentToken, String parentEdge, int index, int beginPosition,
			int endPosition, DatabaseAdapter adp, int sentenceBeginPosition) {
		modifiedByList = new ArrayList<Token>();
		childTokens = new ArrayList<Token>();
		scoreList = new HashMap<AspectLexicon, Double>();
		weightList = new HashMap<AspectLexicon, Integer>();
		this.parentToken = parentToken;
		originalText = original.trim();
		parentEdgeType = parentEdge;
		stemmedTexts = Stemmer.GetStems(lemma.toLowerCase(), "h,p,s");
		stemmedTexts.add(0, lemma.toLowerCase());
		tokenIndex = index;
		this.beginPosition = beginPosition;
		this.endPosition = endPosition;
		this.sentenceBeginPosition = sentenceBeginPosition;
		invert = false;

		if (typeString.length() < 2)
			wordType = WordType.OTHER;
		else {
			String typeDef = typeString.substring(0, 2);
			if (typeDef.equals("JJ"))
				wordType = WordType.ADJECTIVE;
			else if (typeDef.equals("RB"))
				wordType = WordType.ADVERB;
			else if (typeDef.equals("VB"))
				wordType = WordType.VERB;
			else if (typeDef.equals("NN"))
				wordType = WordType.NOUN;
			else
				wordType = WordType.OTHER;
		}

		score = new Double(-2);
		for (int i = 0; i < stemmedTexts.size() && score == -2; i++)
			score = OpinionWords.getWordScore(stemmedTexts.get(i), wordType);

		if (score == -2) {
			weight = 1;
			score = 0;
		} else
			weight = 1;
		pureScore = score;

		aspect = null;
		for (int i = 0; i < stemmedTexts.size() && aspect == null; i++)
			aspect = adp.getAspect(stemmedTexts.get(i));
	}

	protected void addChildToken(Token child) {
		childTokens.add(child);
	}

	private double getProcessedScore(double score) {
		return (invert ? score * -1 : score);
	}

	private double getProcessedScore() {
		return (invert ? score * -1 : score);
	}

	private void transferScoreToParent() {
		if (parentEdgeType != null) {
			switch (parentEdgeType) {
			case "acomp":
				// She looks very beautiful acomp(looks, beautiful)
				if (isAKeyword())
					parentToken.addAspectScore(getProcessedScore(), weight,
							aspect);
				else {
					parentToken.addModifier(this);
					parentToken.updateScore(getProcessedScore(), weight);
				}
				break;
			case "advmod":
				// very good advmod(good, very)
				if (isAKeyword())
					parentToken.addAspectScore(getProcessedScore(), weight,
							aspect);
				else {
					parentToken.addModifier(this);
					parentToken.updateScore(getProcessedScore());
				}
				break;
			case "amod":
				// Sam eats red meat amod(meat, red)
				if (isAKeyword())
					parentToken.addAspectScore(getProcessedScore(), weight,
							aspect);
				else {
					parentToken.addModifier(this);
					parentToken.updateScore(getProcessedScore(), weight);
				}
				break;
			case "appos":
				// Bill (John's cousin) appos(Bill, cousin)
				if (isAKeyword())
					parentToken.addAspectScore(getProcessedScore(), weight,
							aspect);
				else {
					parentToken.addModifier(this);
					parentToken.updateScore(getProcessedScore(), weight);
				}
				break;
			// case "aux":
			// He should leave aux(leave, should)
			// if (IsAKeyword())
			// parentToken.AddAspectScore(GetProcessedScore(), weight, aspect);
			// else {
			// parentToken.UpdateScore(GetProcessedScore(), weight);
			// }
			// break;
			case "csubj":
				// What she said is true csubj(true, said)
				addModifier(parentToken);
				updateScore(parentToken.getProcessedScore(),
						parentToken.getWeight());
				if (isAKeyword())
					addAspectScore(getProcessedScore(), getWeight(),
							getAspect());
				break;
			case "csubjpass":
				// That she lied was suspected by everyone csubjpass(suspected,
				// lied)
				addModifier(parentToken);
				updateScore(parentToken.getProcessedScore(),
						parentToken.getWeight());
				if (isAKeyword())
					addAspectScore(getProcessedScore(), getWeight(),
							getAspect());
				break;
			case "dobj":
				// They win the lottery dobj(win, lottery)
				addModifier(parentToken);
				updateScore(parentToken.getProcessedScore(),
						parentToken.getWeight());
				if (isAKeyword())
					addAspectScore(getProcessedScore(), getWeight(),
							getAspect());
				break;
			case "neg":
				// parent �n weight ini negatifle�tir
				parentToken.addModifier(this);
				parentToken.negate();
				break;
			case "npadvmod":
				// The silence is itself significant npadvmod(significant,
				// itself)
				addModifier(parentToken);
				updateScore(parentToken.getProcessedScore(),
						parentToken.getWeight());
				if (isAKeyword())
					addAspectScore(getProcessedScore(), getWeight(),
							getAspect());
				break;
			case "nsubj":
				// The baby is cute nsubj(cute, baby)
				addModifier(parentToken);
				updateScore(parentToken.getProcessedScore(),
						parentToken.getWeight());
				if (isAKeyword())
					addAspectScore(getProcessedScore(), getWeight(),
							getAspect());
				break;
			case "nsubjpass":
				// Dole was defeated by Clinton nsubjpass(defeated, Doll)
				if (isAKeyword())
					parentToken.addAspectScore(getProcessedScore(), weight,
							aspect);
				else {
					parentToken.addModifier(this);
					parentToken.updateScore(getProcessedScore(), weight);
				}
				break;
			case "partmod":
				// Bill tried to shoot demonstrating his incompetence
				// partmod(shoot, demonstrating)
				if (isAKeyword())
					parentToken.addAspectScore(getProcessedScore(), weight,
							aspect);
				else {
					parentToken.addModifier(this);
					parentToken.updateScore(getProcessedScore(), weight);
				}
				break;
			case "rcmod":
				// I saw the man you love rcmod(man, love)
				if (isAKeyword())
					parentToken.addAspectScore(getProcessedScore(), weight,
							aspect);
				else {
					parentToken.addModifier(this);
					parentToken.updateScore(getProcessedScore(), weight);
				}
				break;
			case "rel":
				// I saw the man whose wife you love rel(love, wife)
				addModifier(parentToken);
				updateScore(parentToken.getProcessedScore(),
						parentToken.getWeight());
				if (isAKeyword())
					addAspectScore(getProcessedScore(), getWeight(),
							getAspect());
				break;
			case "xcomp":
				// He says that you like to swim xcomp(like, swim)
				addModifier(parentToken);
				updateScore(parentToken.getProcessedScore(),
						parentToken.getWeight());
				if (isAKeyword())
					addAspectScore(getProcessedScore(), getWeight(),
							getAspect());
				break;
			default:
				if (isAKeyword())
					parentToken.addAspectScore(getProcessedScore(score),
							weight, aspect);
				break;
			}
			if (wordType == WordType.ADJECTIVE || wordType == WordType.ADVERB
					|| (wordType == WordType.NOUN && pureScore != 0)
					|| (wordType == WordType.VERB && pureScore != 0)) { // add
																		// overall
																		// score
				parentToken.addAspectScore(pureScore, 1, null);
			}
			for (Entry<AspectLexicon, Double> e : getScoreMap().entrySet())
				parentToken.addAspectScore(getProcessedScore(e.getValue()),
						weightList.get(e.getKey()), e.getKey());
		} else {
			if (wordType == WordType.ADJECTIVE || wordType == WordType.ADVERB
					|| (wordType == WordType.NOUN && pureScore != 0)
					|| (wordType == WordType.VERB && pureScore != 0)) { // add
																		// overall
																		// score
				addAspectScore(pureScore, 1, null);
			}
			if (isAKeyword())
				addAspectScore(getProcessedScore(), getWeight(), getAspect());
		}
	}

	private void addModifier(Token modifier) {
		modifiedByList.add(modifier);
	}

	protected List<Token> getModifierList() {
		return modifiedByList;
	}

	protected void updateScore(double score, int weight) {
		if (this.score == 0 && this.weight == 1) {
			this.score = score;
			this.weight = weight + 1;
		} else if (score == 0 && weight == 1) {
			// do nothing
		} else {
			this.score = ((this.score * this.weight) + (score * weight))
					/ (this.weight + weight);
			this.weight += weight;
		}
	}

	protected void updateScore(double score) {
		if (this.score < 0)
			this.score = (Math.abs(this.score) + (1 - Math.abs(this.score))
					/ (1 / score))
					* -1;
		else if (this.score > 0) {
			this.score = Math.abs(this.score) + (1 - Math.abs(this.score))
					/ (1 / score);
		} else
			this.score = score;
		weight++;
	}

	protected void addAspectScore(double score, int weight, AspectLexicon aspect) {
		if (!scoreList.containsKey(aspect)) {
			scoreList.put(aspect, score);
			weightList.put(aspect, weight);
		} else {
			scoreList.put(aspect,
					(scoreList.get(aspect) * weightList.get(aspect) + score
							* weight)
							/ (weightList.get(aspect) + weight));
			weightList.put(aspect, weightList.get(aspect) + weight);
		}
	}

	protected void negate() {
		invert = !invert;
	}

	protected int getIndex() {
		return tokenIndex;
	}

	public String getOriginal() {
		return originalText;
	}

	protected List<Token> getChildrenList() {
		return childTokens;
	}

	protected Map<AspectLexicon, Double> getScoreMap() {
		if (invert) {
			Map<AspectLexicon, Double> tempScoreMap = new HashMap<AspectLexicon, Double>();
			for (Entry<AspectLexicon, Double> e : scoreList.entrySet()) {
				tempScoreMap.put(e.getKey(), e.getValue() * -1);
			}
			return tempScoreMap;
		} else
			return scoreList;
	}

	protected Map<AspectLexicon, Integer> getWeightMap() {
		return weightList;
	}

	@SuppressWarnings("unchecked")
	protected void transferScores() {
		Collections.sort(childTokens);
		if (childTokens.size() != 0) {
			for (Token t : childTokens)
				t.transferScores();
		}
		transferScoreToParent();
	}

	public boolean isAKeyword() {
		return (aspect != null);
	}

	public AspectLexicon getAspect() {
		return aspect;
	}

	public Double getScore() {
		return score;
	}

	public Double getPureScore() {
		return pureScore;
	}

	protected int getWeight() {
		return weight;
	}

	protected String getParentEdgeType() {
		return parentEdgeType;
	}

	public int getBeginPosition() {
		return beginPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public int getRelativeBeginPosition() {
		return beginPosition - sentenceBeginPosition;
	}

	public int getRelativeEndPosition() {
		return endPosition - sentenceBeginPosition;
	}

	@Override
	public int compareTo(Object o) {
		Token t = (Token) o;
		if (priorTypes.contains(t.getParentEdgeType())
				&& priorTypes.contains(this.getParentEdgeType()))
			return 0;
		else if (nonPriorTypes.contains(t.getParentEdgeType())
				&& nonPriorTypes.contains(this.getParentEdgeType()))
			return 0;
		else if (!priorTypes.contains(t.getParentEdgeType())
				&& !priorTypes.contains(this.getParentEdgeType())
				&& !nonPriorTypes.contains(t.getParentEdgeType())
				&& !nonPriorTypes.contains(this.getParentEdgeType()))
			return 0;
		else if (priorTypes.contains(t.getParentEdgeType()))
			return 1;
		else if (priorTypes.contains(this.getParentEdgeType()))
			return -1;
		else if (nonPriorTypes.contains(t.getParentEdgeType()))
			return 1;
		else if (nonPriorTypes.contains(this.getParentEdgeType()))
			return -1;
		return 0;
	}
}