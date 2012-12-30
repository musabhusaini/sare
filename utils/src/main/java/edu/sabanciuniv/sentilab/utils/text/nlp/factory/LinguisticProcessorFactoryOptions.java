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

package edu.sabanciuniv.sentilab.utils.text.nlp.factory;

import edu.sabanciuniv.sentilab.core.models.factory.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.ILinguisticProcessor;

/**
 * The options required by the {@link LinguisticProcessorFactory}.
 * @author Mus'ab Husaini
 */
public class LinguisticProcessorFactoryOptions
	implements IFactoryOptions<ILinguisticProcessor> {
	
	private String name;
	private boolean ignoreNameCase;
	private String language;
	private boolean mustTag;
	private boolean mustParse;
	
	/**
	 * Gets the name of the desired linguistic processor.
	 * @return the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the desired linguistic processor.
	 * @param name the name to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Gets a flag indicating whether to ignore case for name.
	 * @return the flag.
	 */
	public boolean isIgnoreNameCase() {
		return this.ignoreNameCase;
	}

	/**
	 * Sets a flag indicating whether to ignore case for name.
	 * @param ignoreNameCase the flag to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setIgnoreNameCase(boolean ignoreNameCase) {
		this.ignoreNameCase = ignoreNameCase;
		return this;
	}

	/**
	 * Gets the language of the desired linguistic processor.
	 * @return the language.
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Sets the language of the desired linguistic processor.
	 * @param language the language to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setLanguage(String language) {
		this.language = language;
		return this;
	}

	/**
	 * Gets a flag indicating whether the desired linguistic processor must be able to tag or not.
	 * @return the flag.
	 */
	public boolean isMustTag() {
		return this.mustTag;
	}

	/**
	 * Sets a flag indicating whether the desired linguistic processor must be able to tag or not.
	 * @param mustTag the flag to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setMustTag(boolean mustTag) {
		this.mustTag = mustTag;
		return this;
	}

	/**
	 * Gets a flag indicating whether the desired linguistic processor must be able to parse or not.
	 * @return the flag.
	 */
	public boolean isMustParse() {
		return this.mustParse;
	}

	/**
	 * Sets a flag indicating whether the desired linguistic processor must be able to parse or not.
	 * @param mustParse the flag to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setMustParse(boolean mustParse) {
		this.mustParse = mustParse;
		return this;
	}
}