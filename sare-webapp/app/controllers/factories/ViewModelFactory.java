package controllers.factories;

import java.lang.reflect.Constructor;
import java.util.*;

import models.base.*;

import org.apache.commons.lang3.*;
import org.reflections.Reflections;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;

public class ViewModelFactory
	implements IFactory<ViewModel, ViewModelFactoryOptions> {

	@Override
	public ViewModel create(ViewModelFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		Object model = options.getModel();
		Reflections reflections = new Reflections("models");
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
}