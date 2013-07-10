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

package edu.sabanciuniv.sentilab.utils.text.nlp.base;

/**
 * The base class for all linguistic sentences.
 * @author Mus'ab Husaini
 */
public abstract class LinguisticSentence
	extends LinguisticEntity {
	
	/**
	 * Creates an instance of {@code LinguisticSentence}.
	 * @param processor the {@link LinguisticProcessorLike} that was used to produce this data.
	 */
	protected LinguisticSentence(LinguisticProcessorLike processor) {
		super(processor);
	}
	
	/**
	 * Gets all the tokens in this sentence.
	 * @return the tokens in this sentence.
	 */
	public abstract Iterable<LinguisticToken> getTokens();
	
	/**
	 * Gets all the linguistic dependencies in this sentence.
	 * @return the linguistic dependencies in this sentence.
	 */
	public abstract Iterable<LinguisticDependency> getDependencies();
	
	@Override
	public String toString(boolean enhanced) {
		if (!enhanced) {
			return super.toString(enhanced);
		}
		
		StringBuilder sb = new StringBuilder();
		for (LinguisticToken token : this.getTokens()) {
			sb.append(token.toString(enhanced));
		}
		return sb.toString();
	}
}