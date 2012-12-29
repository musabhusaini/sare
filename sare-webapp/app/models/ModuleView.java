package models;

import controllers.modules.base.Module;
import models.base.ViewModel;

public class ModuleView extends ViewModel {

	public String name;
	public String route;
	public double relevancyScore;
	
	public ModuleView(Module module) {
		super(module);
		
		if (module != null) {
			this.name = module.getDisplayName();
		}
	}
	
	public ModuleView() {
		this(null);
	}
	
	public String getName() {
		return name;
	}

	public ModuleView setName(String name) {
		this.name = name;
		return this;
	}

	public String getRoute() {
		return route;
	}

	public ModuleView setRoute(String route) {
		this.route = route;
		return this;
	}

	public double getRelevancyScore() {
		return relevancyScore;
	}

	public ModuleView setRelevancyScore(double relevancyScore) {
		this.relevancyScore = relevancyScore;
		return this;
	}
}
