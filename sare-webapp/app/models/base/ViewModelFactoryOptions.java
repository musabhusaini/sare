package models.base;

import org.codehaus.jackson.*;

import edu.sabanciuniv.sentilab.core.models.factory.IFactoryOptions;

public class ViewModelFactoryOptions implements IFactoryOptions<ViewModel> {

	private Object model;
	private JsonNode json;
	
	public Object getModel() {
		return this.model;
	}
	
	public ViewModelFactoryOptions setModel(Object model) {
		this.model = model;
		return this;
	}
	
	public JsonNode getJson() {
		return this.json;
	}
	
	public ViewModelFactoryOptions setJson(JsonNode json) {
		this.json = json;
		return this;
	}
}
