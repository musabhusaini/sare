package models.base;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.*;
import org.springframework.util.ClassUtils;

import play.libs.Json;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ViewModel {

	public String type;
	
	public ViewModel(Object obj) {
		if (obj != null) {
			this.type = ClassUtils.getShortName(obj.getClass());
		}
	}
	
	public ViewModel() {
		this(null);
	}
	
	public JsonNode asJson() {
		return Json.toJson(this);
	}
}