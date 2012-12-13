package controllers.base;

import java.security.AccessControlException;

import javax.persistence.*;

import models.ResourceFetchError;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject;

import play.*;
import play.api.libs.MimeTypes;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.Context;

import views.html.*;

@With(LoggedAction.class)
public class Application extends Controller {

	protected static EntityManager em() {
		return (EntityManager)ctx().args.get("em");
	}
	
	protected static <T extends PersistentObject> T fetchResource(String id, Class<T> clazz) {
		Validate.notNull(clazz);
		
		T object = null;
		if (UniquelyIdentifiableObject.isUuid(id) && em() != null) {
			try {
				object = em().find(clazz, UniquelyIdentifiableObject.createUuid(id));
				
				if (!SecuredAction.isOwnerOf(object)) {
					throw new AccessControlException(id);
				}
			} catch (EntityNotFoundException e) {
				object = null;
			}
		}
		
		return object;
	}
	
	public static Status notFoundEntity(String id, Throwable e) {
		Logger.warn(LoggedAction.getLogEntry(Context.current(), "non-existent resource"), e);
		return notFound(Json.toJson(ResourceFetchError.nonExistentResourceError(id)));
	}
	
	public static Status notFoundEntity(String id) {
		return notFoundEntity(id, null);
	}
	
	public static Status forbiddenEntity(String id, Throwable e) {
		Logger.warn(LoggedAction.getLogEntry(Context.current(), "forbidden resource"), e);
		return forbidden(Json.toJson(ResourceFetchError.forbiddenResourceError(id)));
	}
	
	public static Status forbiddenEntity(String id) {
		return forbiddenEntity(id, null);
	}
	
	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public static Result login() {
		return TODO;
	}

	public static Result authenticate() {
		return TODO;
	}

	public static Result logout() {
		return TODO;
	}
}