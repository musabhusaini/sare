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

import java.lang.reflect.Modifier;
import java.util.*;

import org.apache.commons.lang3.Validate;
import org.reflections.Reflections;

import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.ILinguisticProcessor;

/**
 * The factory class for creating {@link ILinguisticProcessor} objects.
 * @author Mus'ab Husaini
 */
public class LinguisticProcessorFactory
	implements IFactory<ILinguisticProcessor, LinguisticProcessorFactoryOptions> {
	
	/**
	 * Gets a list of all supported linguistic processors.
	 * @return an {@link Iterable} of {@link LinguisticProcessorInfo} objects, one for each available linguistic processor.
	 */
	public static Iterable<LinguisticProcessorInfo> getSupportedProcessors() {
		List<LinguisticProcessorInfo> languages = Lists.newArrayList();
		
		Reflections reflections = new Reflections("edu.sabanciuniv.sentilab");
		Set<Class<? extends ILinguisticProcessor>> subTypes = reflections.getSubTypesOf(ILinguisticProcessor.class);
		for (Class<? extends ILinguisticProcessor> c : subTypes) {
			LinguisticProcessorInfo info = c.getAnnotation(LinguisticProcessorInfo.class);
			if (info == null || Modifier.isAbstract(c.getModifiers())) {
				continue;
			}
			
			languages.add(info);
		}
		
		return languages;
	}
	
	@Override
	public ILinguisticProcessor create(LinguisticProcessorFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		try {
			Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
		
		Class<? extends ILinguisticProcessor> processorClass = null;
		Reflections reflections = new Reflections("edu.sabanciuniv.sentilab");
		Set<Class<? extends ILinguisticProcessor>> subTypes = reflections.getSubTypesOf(ILinguisticProcessor.class);
		for (Class<? extends ILinguisticProcessor> c : subTypes) {
			LinguisticProcessorInfo info = c.getAnnotation(LinguisticProcessorInfo.class);
			if (info == null || Modifier.isAbstract(c.getModifiers())) {
				continue;
			}
			
			try {
				c.getConstructor();
			} catch (NoSuchMethodException | SecurityException e) {
				continue;
			}
			
			if (options.getName() == null || (options.isIgnoreNameCase() ?
					info.name().equalsIgnoreCase(options.getName()) : info.name().equals(options.getName()))) {
				processorClass = c;
			}
			
			if (processorClass != null &&
				options.getLanguage() != null && !info.languageCode().equalsIgnoreCase(options.getLanguage())) {
				processorClass = null;
			}
			
			if (processorClass != null && options.isMustTag() && !info.canTag()) {
				processorClass = null;
			}
			
			if (processorClass != null && options.isMustParse() && !info.canParse()) {
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