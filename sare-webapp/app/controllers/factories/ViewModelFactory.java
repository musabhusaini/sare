package controllers.factories;

import java.lang.reflect.Constructor;
import java.util.*;

import models.base.*;

import org.apache.commons.lang3.*;
import org.codehaus.jackson.*;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import play.libs.Json;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;

public class ViewModelFactory
	implements IFactory<ViewModel, ViewModelFactoryOptions> {

	private ViewModel createSpecific(Object model) {
		Reflections reflections = new Reflections("models", ClasspathHelper.contextClassLoader());
		Set<Class<? extends ViewModel>> viewModelClasses = reflections.getSubTypesOf(ViewModel.class);
		
		List<Class<?>> modelCandidateClasses = ClassUtils.getAllSuperclasses(model.getClass());
		modelCandidateClasses.add(0, model.getClass());
		
		Class<?> availableModelClass = null;
		Class<? extends ViewModel> availableViewModelClass = null;
		for(final Class<?> modelSuperclass : modelCandidateClasses) {
			availableViewModelClass = Iterables.find(viewModelClasses, new Predicate<Class<? extends ViewModel>>() {
				@Override
				public boolean apply(Class<? extends ViewModel> viewModelClass) {
					return ClassUtils.getShortClassName(viewModelClass).equals(ClassUtils.getShortClassName(modelSuperclass) + "View");
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
	
	private ViewModel createSpecific(JsonNode json) {
		if (!json.has("type")) {
			return null;
		}
		
		String type = json.get("type").asText();
		if (StringUtils.isEmpty(type)) {
			return null;
		}
		
		if (!type.endsWith("View")) {
			type += "View";
		}
		
		final String typeName = type;
		Reflections reflections = new Reflections("models", ClasspathHelper.contextClassLoader());
		Class<? extends ViewModel> availableViewModelClass = Iterables.find(reflections.getSubTypesOf(ViewModel.class),
			new Predicate<Class<? extends ViewModel>>() {
				@Override
				public boolean apply(Class<? extends ViewModel> viewModelClass) {
					return ClassUtils.getShortClassName(viewModelClass).equals(typeName);
				}
			}, null);
		
		if (availableViewModelClass == null) {
			return null;
		}

		return Json.fromJson(json, availableViewModelClass);
	}
	
	@Override
	public ViewModel create(ViewModelFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		if (options.getModel() != null) {
			return this.createSpecific(options.getModel());
		} else if (options.getJson() != null) {
			return this.createSpecific(options.getJson());
		}
		
		return null;
	}
}