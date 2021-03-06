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

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;

/**
 * The base class for all classes that describe POS tags. 
 * @author Mus'ab Husaini
 */
public abstract class PosTag extends LinguisticObject {

	public static final String NOUN = "noun";
	public static final String ADJECTIVE = "adjective";
	public static final String ADVERB = "adverb";
	public static final String VERB = "verb";
	
	/**
	 * Splits a tags string into its constituents.
	 * @param tagsString the {@code string} containing tags separating by an accepted separator.
	 * @return an {@link Iterable} of tags.
	 */
	public static Iterable<String> splitTagsString(String tagsString) {
		return Splitter.on(Pattern.compile("\\||,|;|\\s+")).split(StringUtils.defaultIfEmpty(tagsString, ""));
	}
	
	protected String tag;
	
	/**
	 * Creates an instance of the {@link PosTag} class.
	 * @param processor the {@link LinguisticProcessorLike} that was used to produce this data.
	 * @param tag the tag that was generated by the processor.
	 */
	protected PosTag(LinguisticProcessorLike processor, String tag) {
		super(processor);
		this.tag = tag;
	}

	/**
	 * Attempt to get a simplified version of this POS tag. If not, returns the original tag.
	 * @return a simplified version of the POS tag.
	 */
	public abstract String getSimpleTag();
	
	/**
	 * Gets the original tag that was produced by the processor.
	 * @return the original tag.
	 */
	public String getTag() {
		return this.tag;
	}
		
	/**
	 * Checks if this POS tag is of the given type or not.
	 * @param tagsString the tag name or a comma/pipe/semicolon-separated list of tags.
	 * @return {@code true} if this POS tag is of the given type; {@code false} otherwise.
	 */
	public boolean is(String tagsString) {
		return this.is(splitTagsString(tagsString));
	}
	
	/**
	 * Checks if this POS tag belongs to one of the given type or not.
	 * @param tags the {@link Iterable} of tag strings.
	 * @return {@code true} if this POS tag is one of the given types; {@code false} otherwise.
	 */
	public boolean is(Iterable<String> tags) {
		if (tags == null) {
			return true;
		}
		
		for (String tag : tags) {
			if (StringUtils.equalsIgnoreCase(tag, this.getSimpleTag()) || StringUtils.equalsIgnoreCase(tag, this.getTag())) {
				return true;
			}
		}
		return false;		
	}
	
	/**
	 * Convenience method to check if this POS tag is a noun POS or not.
	 * @return a {@code boolean} flag indicating whether this POS tag is a noun or not.
	 */
	public boolean isNoun() {
		return this.is(NOUN);
	}
	
	/**
	 * Convenience method to check if this POS tag is an adjective POS or not.
	 * @return a {@code boolean} flag indicating whether this POS tag is an adjective or not.
	 */
	public boolean isAdjective() {
		return this.is(ADJECTIVE);
	}
	
	/**
	 * Convenience method to check if this POS tag is an adverb POS or not.
	 * @return a {@code boolean} flag indicating whether this POS tag is an adverb or not.
	 */
	public boolean isAdverb() {
		return this.is(ADVERB);
	}
	
	/**
	 * Convenience method to check if this POS tag is a verb POS or not.
	 * @return a {@code boolean} flag indicating whether this POS tag is a verb or not.
	 */
	public boolean isVerb() {
		return this.is(VERB);
	}
	
	@Override
	public String toString() {
		return this.getSimpleTag();
	}
}