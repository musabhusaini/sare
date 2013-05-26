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

package edu.sabanciuniv.sentilab.sare.models.base.document;

import java.util.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.core.models.CoreModel;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.PosTag;

/**
 * An instance of this class represents tokenizing options for a {@link FullTextDocument}.
 * @author Mus'ab Husaini
 */
public class TokenizingOptions
	implements CoreModel {
	
	private List<String> tags;
	private boolean isLemmatized;
	
	/**
	 * Gets the POS tags that will be captured.
	 * @return the {@link List} of {@link String} objects representing the POS tag pattern that will be captured.
	 */
	public List<String> getTags() {
		if (this.tags == null) {
			this.tags = Lists.newArrayList();
		}
		
		return this.tags;
	}
	
	/**
	 * Sets the POS tags that need to be captured.
	 * @param tags an {@link Iterable} of {@link String} objects representing the POS tag patterns to be captured.
	 * @return the {@code this} object.
	 */
	public TokenizingOptions setTags(Iterable<String> tags) {
		this.tags = tags == null ? null : Lists.newArrayList(tags);
		return this;
	}
	
	/**
	 * Sets the POS tags that need to be captured.
	 * @param tags a delimited string of tags.
	 * @return the {@code this} object.
	 */
	public TokenizingOptions setTags(String tags) {
		return this.setTags(PosTag.splitTagsString(tags));
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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TokenizingOptions)) {
			return super.equals(obj);
		}
		
		TokenizingOptions otherObj = (TokenizingOptions)obj;
		if (this.isLemmatized() != otherObj.isLemmatized()) {
			return false;
		}
		
		if (this.getTags().size() != otherObj.getTags().size()) {
			return false;
		}
		
		for (String tag : this.getTags()) {
			if (!otherObj.getTags().contains(tag)) {
				return false;
			}
		}
		
		return true;
	}
}