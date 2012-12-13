package models.base;

import org.codehaus.jackson.JsonNode;

import play.db.ebean.Model;
import play.libs.Json;

public class ViewModel extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4408751749925885183L;

	public String type;
	
	public JsonNode asJson() {
		return Json.toJson(this);
	}
}
