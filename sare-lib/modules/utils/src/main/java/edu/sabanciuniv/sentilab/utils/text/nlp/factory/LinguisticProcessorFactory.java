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

package edu.sabanciuniv.sentilab.utils.text.nlp.factory;

import java.lang.reflect.Modifier;
import java.util.*;

import org.reflections.Reflections;

import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.core.controllers.factory.Factory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticProcessorLike;

/**
 * The factory class for creating {@link LinguisticProcessorLike} objects.
 * @author Mus'ab Husaini
 */
public class LinguisticProcessorFactory
		implements Factory<LinguisticProcessorLike> {
	
	/**
	 * Gets a list of all supported linguistic processors.
	 * @return an {@link Iterable} of {@link LinguisticProcessorInfo} objects, one for each available linguistic processor.
	 */
	public static Iterable<LinguisticProcessorInfo> getSupportedProcessors() {
		List<LinguisticProcessorInfo> languages = Lists.newArrayList();
		
		Reflections reflections = new Reflections("edu.sabanciuniv.sentilab");
		Set<Class<? extends LinguisticProcessorLike>> subTypes = reflections.getSubTypesOf(LinguisticProcessorLike.class);
		for (Class<? extends LinguisticProcessorLike> c : subTypes) {
			LinguisticProcessorInfo info = c.getAnnotation(LinguisticProcessorInfo.class);
			if (info == null || Modifier.isAbstract(c.getModifiers())) {
				continue;
			}
			
			languages.add(info);
		}
		
		return languages;
	}

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
	public LinguisticProcessorFactory setName(String name) {
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
	public LinguisticProcessorFactory setIgnoreNameCase(boolean ignoreNameCase) {
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
	public LinguisticProcessorFactory setLanguage(String language) {
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
	public LinguisticProcessorFactory setMustTag(boolean mustTag) {
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
	public LinguisticProcessorFactory setMustParse(boolean mustParse) {
		this.mustParse = mustParse;
		return this;
	}
	
	@Override
	public LinguisticProcessorLike create()
			throws IllegalFactoryOptionsException {
		
		Class<? extends LinguisticProcessorLike> processorClass = null;
		Reflections reflections = new Reflections("edu.sabanciuniv.sentilab");
		Set<Class<? extends LinguisticProcessorLike>> subTypes = reflections.getSubTypesOf(LinguisticProcessorLike.class);
		for (Class<? extends LinguisticProcessorLike> c : subTypes) {
			LinguisticProcessorInfo info = c.getAnnotation(LinguisticProcessorInfo.class);
			if (info == null || Modifier.isAbstract(c.getModifiers())) {
				continue;
			}
			
			try {
				c.getConstructor();
			} catch (NoSuchMethodException | SecurityException e) {
				continue;
			}
			
			if (this.getName() == null || (this.isIgnoreNameCase() ?
					info.name().equalsIgnoreCase(this.getName()) : info.name().equals(this.getName()))) {
				processorClass = c;
			}
			
			if (processorClass != null &&
				this.getLanguage() != null && !info.languageCode().equalsIgnoreCase(this.getLanguage())) {
				processorClass = null;
			}
			
			if (processorClass != null && this.isMustTag() && !info.canTag()) {
				processorClass = null;
			}
			
			if (processorClass != null && this.isMustParse() && !info.canParse()) {
				processorClass = null;
			}
			
			if (processorClass != null) {
				break;
			}
		}
		
		try {
			return processorClass.newInstance();
		} catch (Exception e) {
			// Fall back to the default behavior.
		}
		
		return null;
	}
}