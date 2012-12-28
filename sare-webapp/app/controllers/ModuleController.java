package controllers;

import java.util.*;

import javax.annotation.Nullable;

import models.ModuleView;
import models.base.*;

import org.codehaus.jackson.JsonNode;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import com.google.common.base.*;
import com.google.common.collect.*;

import play.mvc.*;
import play.mvc.BodyParser.Json;
import controllers.base.*;
import controllers.modules.base.Module;

public class ModuleController extends Application {

	@BodyParser.Of(Json.class)
	public static Result options() {
		// get all the supplied view models.
		List<ViewModel> suppliedViewModels = Lists.newArrayList();
		JsonNode body = request().body().asJson();
		
		// convert json nodes to view models.
		if (body != null && body.isArray()) {
			suppliedViewModels = Lists.newArrayList(Iterators.transform(body.getElements(),
				new Function<JsonNode, ViewModel>() {
					@Override
					@Nullable
					public ViewModel apply(@Nullable JsonNode input) {
						return createViewModel(input);
					}
				}));
		} else if (body != null && body.isObject()) {
			suppliedViewModels.add(createViewModelQuietly(body));
		}
		
		// get all the modules that can use these inputs.
		Set<ModuleView> modules = Sets.newHashSet();
		Reflections reflections = new Reflections("controllers.modules", ClasspathHelper.contextClassLoader().toString());
		for (Class<? extends Module> moduleClass : reflections.getSubTypesOf(Module.class)) {
			// get the Module.Requires annotation for each module class.
			Module.Requires reqAnnotation = null;
			if (moduleClass.isAnnotationPresent(Module.Requires.class)) {
				reqAnnotation = moduleClass.getAnnotation(Module.Requires.class);
			}
			
			final Set<Class<? extends ViewModel>> requiredViewModelClasses = Sets.newHashSet();
			if (reqAnnotation != null) {
				Collections.addAll(requiredViewModelClasses, reqAnnotation.types());
			} // no annotation means no requirement
			
			// get all the supplied view modules that are relevant to this module.
			List<ViewModel> usefulViewModels = Lists.newArrayList(Iterables.filter(suppliedViewModels,
				new Predicate<ViewModel>() {
					@Override
					public boolean apply(@Nullable ViewModel input) {
						return requiredViewModelClasses.contains(input.getClass());
					}
				}));
			
			// if all the requirements were satisfied.
			if (usefulViewModels.size() >= requiredViewModelClasses.size()) {
				// try to create an instance of the module.
				Module module = null;
				try {
					module = moduleClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					module = null;
				} finally {
					if (module == null) {
						// in this case, there is a problem with the module, so just ignore.
						continue;
					}
				}
				
				// set the module view model properties and add.
				ModuleView moduleViewModel = new ModuleView(module);
				moduleViewModel.route = module.getRoute(usefulViewModels);
				// let's not divide by zero!
				moduleViewModel.relevancyScore = suppliedViewModels.size() != 0 ?
					usefulViewModels.size() / (double)suppliedViewModels.size() : 1.0;
				
				modules.add(moduleViewModel);
			}
		}
		
		return ok(play.libs.Json.toJson(modules));
	}
}