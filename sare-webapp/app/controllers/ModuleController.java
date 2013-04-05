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

package controllers;

import static controllers.base.SareTransactionalAction.*;
import static models.base.ViewModel.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import models.base.*;
import models.web.ModuleModel;

import org.apache.commons.lang3.*;
import org.codehaus.jackson.JsonNode;
import org.reflections.Reflections;

import com.google.common.base.*;
import com.google.common.collect.*;

import play.Play;
import play.libs.Json;
import play.mvc.*;
import views.html.moduleView;
import controllers.base.*;
import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

public class ModuleController extends Application {

	private static List<ModuleModel> getNextModules(String input) {
		// get all the supplied view models.
		List<ViewModel> suppliedViewModels = Lists.newArrayList();
		JsonNode inputJson = Json.parse(input);
		
		// convert json nodes to view models.
		if (inputJson != null && inputJson.isArray()) {
			suppliedViewModels = Lists.newArrayList(Iterators.transform(inputJson.getElements(),
				new Function<JsonNode, ViewModel>() {
					@Override
					@Nullable
					public ViewModel apply(@Nullable JsonNode input) {
						if (!input.isTextual()) {
							return null;
						}
						return createViewModelQuietly(fetchResource(UuidUtils.create(input.asText()), PersistentObject.class), null);
							
					}
				}));
		} else if (inputJson != null && inputJson.isObject()) {
			suppliedViewModels.add(createViewModelQuietly(inputJson, null));
		}
		
		suppliedViewModels = Lists.newArrayList(Iterables.filter(suppliedViewModels, Predicates.notNull()));
		
		// get all the modules that can use these inputs.
		Map<Module, Double> nullModulesMap = Maps.newHashMap();
		Map<Module, Double> modulesMap = Maps.newHashMap();
		Reflections reflections = new Reflections("controllers.modules", Play.application().classloader());
		for (Class<? extends Module> moduleClass : reflections.getSubTypesOf(Module.class)) {
			// we're not interested in abstract classes.
			if (Modifier.isAbstract(moduleClass.getModifiers())) {
				continue;
			}
			
			// get the Module.Requires/Requireses annotation for each module class.
			// the requirements within each Module.Require are ANDed.
			// the requirements across multiple Module.Require annotations are ORed.
			List<Module.Requires> requireds = Lists.newArrayList();
			if (moduleClass.isAnnotationPresent(Module.Requires.class)) {
				requireds.add(moduleClass.getAnnotation(Module.Requires.class));
			}
			if (moduleClass.isAnnotationPresent(Module.Requireses.class)) {
				Collections.addAll(requireds, moduleClass.getAnnotation(Module.Requireses.class).value());
			}
			
			if (requireds.size() == 0) {
				requireds.add(null);
			}
			
			for (Module.Requires required : requireds) {
				final Set<Class<? extends ViewModel>> requiredViewModelClasses = Sets.newHashSet();
				if (required != null) {
					Collections.addAll(requiredViewModelClasses, required.value());
				}
				
				// get all the supplied view modules that are relevant to this module.
				List<ViewModel> usefulViewModels = Lists.newArrayList(Iterables.filter(suppliedViewModels,
					new Predicate<ViewModel>() {
						@Override
						public boolean apply(@Nullable ViewModel input) {
							// if this class is required, then return true.
							if (requiredViewModelClasses.contains(input.getClass())) {
								return true;
							}
							
							// if any of its super classes are required, that also works.
							for (Class<?> superClass : ClassUtils.getAllSuperclasses(input.getClass())) {
								if (requiredViewModelClasses.contains(superClass)) {
									return true;
								}
							}
							
							return false;
						}
					}));
				
				// if all the requirements were satisfied.
				if (usefulViewModels.size() >= requiredViewModelClasses.size()) {
					// try to create an instance of the module.
					Module module = null;
					try {
						module = moduleClass.newInstance();
						module.setViewModels(usefulViewModels);
					} catch (InstantiationException | IllegalAccessException e) {
						module = null;
					} finally {
						if (module == null) {
							// in this case, there is a problem with the module, so just ignore.
							continue;
						}
					}
					
					// let's not divide by zero!
					double relevancyScore = suppliedViewModels.size() != 0 ?
						usefulViewModels.size() / (double)suppliedViewModels.size() : 1.0;
					
					// keep null modules separate.
					Map<Module, Double> targetModulesMap = null;
					if (requiredViewModelClasses.size() > 0) {
						// if a module of this type does not exist, add it.
						if (Maps.filterKeys(modulesMap, Predicates.instanceOf(moduleClass)).size() == 0) {
							targetModulesMap = modulesMap;
						}
					} else {
						targetModulesMap = nullModulesMap;
					}
					if (targetModulesMap != null) {
						targetModulesMap.put(module, relevancyScore);
					}
				}
			}
		}
		
		// use null modules only if there are no regular ones.
		if (modulesMap.size() == 0) {
			modulesMap = nullModulesMap;
		}
		
		// convert to view models.
		Set<ModuleModel> moduleViewModels = Sets.newHashSet(
			Iterables.transform(modulesMap.entrySet(), new Function<Entry<Module, Double>, ModuleModel>() {
				@Override
				@Nullable
				public ModuleModel apply(@Nullable Entry<Module, Double> input) {
					return new ModuleModel(input.getKey())
						.setRelevancyScore(input.getValue());
				}
			})
		);
		
		// order first by relevance and then by name.
		return Ordering.from(new Comparator<ModuleModel>() {
			@Override
			public int compare(ModuleModel o1, ModuleModel o2) {
				int relDiff = (int)Math.round((o2.relevancyScore - o1.relevancyScore) * 1000);
				if (relDiff == 0) {
					return o1.name.compareTo(o2.name);
				}
				
				return relDiff;
			}
		}).sortedCopy(moduleViewModels);
	}
	
	@With(SareTransactionalAction.class)
	public static Result options(String input) {
		return ok(play.libs.Json.toJson(getNextModules(input)));
	}
	
	public static Result landingPage() {
		return ok(moduleView.render(null, null, null, null));
	}
}