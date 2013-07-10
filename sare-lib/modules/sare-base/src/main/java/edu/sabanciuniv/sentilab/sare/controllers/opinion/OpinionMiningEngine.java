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

package edu.sabanciuniv.sentilab.sare.controllers.opinion;

import java.lang.annotation.*;

import org.apache.commons.lang3.*;
import org.reflections.Reflections;

import edu.sabanciuniv.sentilab.sare.controllers.base.ControllerBase;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionMinedCorpusLike;

/**
 * The base class for all opinion mining engines.
 * @author Mus'ab Husaini
 */
public abstract class OpinionMiningEngine
		extends ControllerBase {
	
	/**
	 * The annotation for marking opinion mining engines. 
	 * @author Mus'ab Husaini
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface Of {
		public String name() default "";
		public String code();
	}
	
	/**
	 * Gets the engine code of the provided class.
	 * @param classOfEngine the {@link Class} of the engine.
	 * @return the engine code.
	 */
	public static String getCode(Class<? extends OpinionMiningEngine> classOfEngine) {
		OpinionMiningEngine.Of annotation = classOfEngine.getAnnotation(OpinionMiningEngine.Of.class);
		if (annotation == null) {
			return null;
		}
		
		return annotation.code();
	}
	
	/**
	 * Creates an engine with the specified code which is either a subtype of the provided class or the class itself.
	 * @param code the engine code.
	 * @param classOfBaseEngine the base class of the engine.
	 * @return the desired engine of type {@code T} or {@code null} if none matched.
	 */
	public static <T extends OpinionMiningEngine> T create(String code, Class<T> classOfBaseEngine) {
		Class<? extends T> engine = null;
		
		if (ObjectUtils.equals(code, getCode(classOfBaseEngine))) {
			engine = classOfBaseEngine;
		}
		
		Reflections reflections = new Reflections("edu.sabanciuniv.sentilab.sare.controllers.opinion");
		for (Class<? extends T> classOfEngine : reflections.getSubTypesOf(classOfBaseEngine)) {
			if (StringUtils.equals(code, getCode(classOfEngine))) {
				engine = classOfEngine;
				break;
			}
		}
		
		if (engine != null) {
			try {
				return engine.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
			}
		}
		
		return null;
	}
	
	/**
	 * Creates an engine with the specified code.
	 * @param code the engine code.
	 * @return the desired {@link OpinionMiningEngine} or {@code null} if none matched.
	 */
	public static OpinionMiningEngine create(String code) {
		return create(code, OpinionMiningEngine.class);
	}
	
	protected DocumentCorpus testCorpus;
	
	/**
	 * Gets the code of this class.
	 * @return the code.
	 */
	public String getCode() {
		return getCode(this.getClass());
	}
	
	/**
	 * Gets the corpus under test.
	 * @return the {@link DocumentCorpus} under test.
	 */
	public DocumentCorpus getTestCorpus() {
		return this.testCorpus;
	}
	
	/**
	 * Sets the corpus under test.
	 * @param testCorpus the {@link DocumentCorpus} under test.
	 * @return the {@code this} object.
	 */
	public OpinionMiningEngine setTestCorpus(DocumentCorpus testCorpus) {
		this.testCorpus = testCorpus;
		return this;
	}
	
	/**
	 * Gets the corpus that does or will contain mined results.
	 * @return the {@link IOpinionMinedCorpus} object. If called before {@code mine}, this will not contain any actual results.
	 */
	public abstract OpinionMinedCorpusLike getTargetMinedCorpus();
	
	/**
	 * Applies the opinion mining engine to the provided test corpus.
	 * @return the {@link IOpinionMinedCorpus} object containing mined results.
	 */
	public abstract OpinionMinedCorpusLike mine();
}