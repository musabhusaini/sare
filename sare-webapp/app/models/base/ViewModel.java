package models.base;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.util.ClassUtils;

import play.db.ebean.Model;
import play.libs.Json;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ViewModel extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4408751749925885183L;

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
