package models;

import play.db.ebean.Model;

public class ResourceFetchError extends Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5210039004842627482L;
	
	public String id;
	public String message;
	
	public static ResourceFetchError nonExistentResourceError(String id) {
		ResourceFetchError error = new ResourceFetchError();
		error.id = id;
		error.message = "non-existent resource";
		return error;
	}
	
	public static ResourceFetchError forbiddenResourceError(String id) {
		ResourceFetchError error = new ResourceFetchError();
		error.id = id;
		error.message = "forbidden resource";
		return error;
	}
}
