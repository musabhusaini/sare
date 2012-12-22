package models.base;

import edu.sabanciuniv.sentilab.core.models.factory.IFactoryOptions;

public class ViewModelFactoryOptions implements IFactoryOptions<ViewModel> {

	private Object model;
	
	public Object getModel() {
		return this.model;
	}
	
	public ViewModelFactoryOptions setModel(Object model) {
		this.model = model;
		return this;
	}
}
