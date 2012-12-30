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

package edu.sabanciuniv.sentilab.sare.models.base.document;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.core.models.IModel;

/**
 * An instance of this class represents tokenizing options for a {@link TokenizedDocument}.
 * @author Mus'ab Husaini
 */
public class TokenizingOptions
	implements IModel {

	/**
	 * An {@code enum} of various tag-capture options.
	 * @author Mus'ab Husaini
	 */
	public enum TagCaptureOptions {
		/**
		 * Option to ignore case of tags.
		 */
		IGNORE_CASE,
		/**
		 * Option to match the start of tags.
		 */
		STARTS_WITH,
		/**
		 * Option to match the end of tags.
		 */
		ENDS_WITH,
		/**
		 * Option to use the provided pattern strings as regular expression patterns.
		 */
		PATTERN
	}
	
	private List<Pattern> tags;
	private boolean isLemmatized;
	
	/**
	 * Gets the POS tag patterns that will be captured.
	 * @return the {@link List} of {@link Pattern} objects representing the POS tag pattern that will be captured.
	 */
	public List<Pattern> getTags() {
		if (this.tags == null) {
			this.tags = Lists.newArrayList();
		}
		
		return this.tags;
	}
	
	/**
	 * Sets the POS tag pattern that need to be captured.
	 * @param tags an {@link Iterable} of {@link Pattern} objects representing the POS tag patterns to be captured.
	 * @return the {@code this} object.
	 */
	public TokenizingOptions setTags(Iterable<Pattern> tags) {
		this.tags = tags == null ? null : Lists.newArrayList(tags);
		return this;
	}
	
	/**
	 * Sets the POS tags that need to be captured.
	 * @param options an {@link EnumSet} of {@link TagCaptureOptions} to use when building paterns.
	 * @param tags an {@link Iterable} or variable arguments of {@link String} objects representing the POS tags to be captured.
	 * @return the {@code this} object.
	 * @throws IllegalArgumentException if an unacceptable combination of options is provided.
	 */
	public TokenizingOptions setTags(final EnumSet<TagCaptureOptions> options, Iterable<String> tags) {
		if (tags == null) {
			return this.setTags(null);
		}
		
		return this.setTags(Iterables.transform(tags, new Function<String, Pattern>() {
			@Override
			public Pattern apply(String input) {
				if (options == null || !options.contains(TagCaptureOptions.PATTERN)) {
					input = Pattern.quote(input);
				}
				
				if (options != null) {
					if (!options.contains(TagCaptureOptions.PATTERN)) {
						if (options.contains(TagCaptureOptions.STARTS_WITH)) {
							input = "^" + input;
						}
						
						if (options.contains(TagCaptureOptions.ENDS_WITH)) {
							input = input + "$";
						}
					} else if (options.contains(TagCaptureOptions.STARTS_WITH) || options.contains(TagCaptureOptions.ENDS_WITH)) {
						throw new IllegalArgumentException("Cannot combine the PATTERN option with the STARTS_WITH or ENDS_WITH options");
					}
					
					if (options.contains(TagCaptureOptions.IGNORE_CASE)) {
						input = "(?i)" + input;
					}
				}
				
				return Pattern.compile(input);
			}
		}));
	}
	
	/**
	 * Sets the POS tags that need to be captured.
	 * @param options an {@link EnumSet} of {@link TagCaptureOptions} to use when building paterns.
	 * @param tags an {@link Array} or variable arguments of {@link String} objects representing the POS tags to be captured.
	 * @return the {@code this} object.
	 */
	public TokenizingOptions setTags(final EnumSet<TagCaptureOptions> options, String...tags) {
		return this.setTags(options, Arrays.asList(tags));
	}
	
	/**
	 * Gets a flag indicating whether tokens will be lemmatized or not.
	 * @return a {@link Boolean} flag.
	 */
	public boolean isLemmatized() {
		return this.isLemmatized;
	}
	
	/**
	 * Sets a flag indicating whether tokens should be lemmatized or not.
	 * @param isLemmatized the {@link Boolean} flag to be set.
	 * @return the {@code this} object.
	 */
	public TokenizingOptions setLemmatized(boolean isLemmatized) {
		this.isLemmatized = isLemmatized;
		return this;
	}
	
	@Override
	public TokenizingOptions clone() {
		return new TokenizingOptions()
			.setTags(this.getTags())
			.setLemmatized(this.isLemmatized());
	}
}