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

package controllers.factories;

import java.lang.reflect.Constructor;
import java.util.*;

import javax.annotation.Nullable;

import models.base.*;

import org.apache.commons.lang3.*;
import org.codehaus.jackson.*;
import org.reflections.Reflections;

import play.Play;
import play.libs.Json;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.core.controllers.factory.Factory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;

public class ViewModelFactory
		implements Factory<ViewModel> {

	public static final String MODEL_SUFFIX = "Model";
	
	private Object model;
	private JsonNode json;
	
	public Object getModel() {
		return this.model;
	}
	
	public ViewModelFactory setModel(Object model) {
		this.model = model;
		return this;
	}
	
	public JsonNode getJson() {
		return this.json;
	}
	
	public ViewModelFactory setJson(JsonNode json) {
		this.json = json;
		return this;
	}

	private ViewModel createSpecific(Object model) {
		Reflections reflections = new Reflections("models", Play.application().getWrappedApplication().classloader());
		Set<Class<? extends ViewModel>> viewModelClasses = reflections.getSubTypesOf(ViewModel.class);
		
		List<Class<?>> modelCandidateClasses = ClassUtils.getAllSuperclasses(model.getClass());
		modelCandidateClasses.add(0, model.getClass());
		
		Class<?> availableModelClass = null;
		Class<? extends ViewModel> availableViewModelClass = null;
		for(final Class<?> modelSuperclass : modelCandidateClasses) {
			availableViewModelClass = Iterables.find(viewModelClasses, new Predicate<Class<? extends ViewModel>>() {
				@Override
				public boolean apply(Class<? extends ViewModel> viewModelClass) {
					return ClassUtils.getShortClassName(viewModelClass).equals(ClassUtils.getShortClassName(modelSuperclass) + "Model");
				}
			}, null);
			
			if (availableViewModelClass != null) {
				availableModelClass = modelSuperclass;
				break;
			}
		}
		
		if (availableModelClass == null) {
			return null;
		}
		
		try {
			Constructor<? extends ViewModel> constructor = availableViewModelClass.getConstructor(availableModelClass);
			return constructor.newInstance(model);
		} catch (Throwable e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private ViewModel createSpecific(JsonNode json) {
		if (!json.has("type")) {
			return null;
		}
		
		String type = json.get("type").asText();
		if (StringUtils.isEmpty(type)) {
			return null;
		}
		
		if (type.endsWith(MODEL_SUFFIX)) {
			StringUtils.stripEnd(type, MODEL_SUFFIX);
		}
		
		final String typeName = type;
		Reflections reflections = new Reflections("edu.sabanciuniv.sentilab.sare.models");
		Class<? extends PersistentObject> model = Iterables.find(reflections.getSubTypesOf(PersistentObject.class),
			new Predicate<Class<? extends PersistentObject>>() {
				@Override
				public boolean apply(@Nullable Class<? extends PersistentObject> input) {
					return typeName.equals(ClassUtils.getShortClassName(input));
				}
			}, null);
				
		Class<? extends ViewModel> availableViewModelClass = null;
		while (model != null) {
			final String modelName = ClassUtils.getShortClassName(model);
			reflections = new Reflections("models", Play.application().classloader());
			availableViewModelClass = Iterables.find(reflections.getSubTypesOf(ViewModel.class),
				new Predicate<Class<? extends ViewModel>>() {
					@Override
					public boolean apply(Class<? extends ViewModel> viewModelClass) {
						return ClassUtils.getShortClassName(viewModelClass).equals(modelName + MODEL_SUFFIX);
					}
				}, null);
			
			if (availableViewModelClass != null) {
				break;
			}
			
			model = (Class<? extends PersistentObject>) model.getSuperclass();
			if (!PersistentObject.class.isAssignableFrom(model)) {
				break;
			}
		}
		
		if (availableViewModelClass == null) {
			return null;
		}
		
		return Json.fromJson(json, availableViewModelClass);
	}
	
	@Override
	public ViewModel create()
			throws IllegalFactoryOptionsException {
		
		if (this.getModel() != null) {
			return this.createSpecific(this.getModel());
		} else if (this.getJson() != null) {
			return this.createSpecific(this.getJson());
		}
		
		return null;
	}
}