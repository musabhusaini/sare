package models;

import controllers.modules.base.Module;
import models.base.ViewModel;

public class ModuleView extends ViewModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1061007176883583040L;

	public String name;
	public String route;
	public double relevancyScore;
	
	public ModuleView(Module module) {
		if (module != null) {
			this.name = module.getDisplayName();
		}
	}
	
	public ModuleView() {
		this(null);
	}
}
