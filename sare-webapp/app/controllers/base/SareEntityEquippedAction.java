package controllers.base;

import java.security.AccessControlException;

import javax.persistence.*;

import models.ResourceFetchError;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.SareEntityManagerFactory;
import edu.sabanciuniv.sentilab.sare.models.base.*;

import play.*;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.Context;

public class SareEntityEquippedAction extends Action.Simple {

	public static EntityManager em(Context ctx) {
		if (ctx == null) {
			ctx = Context.current();
			Validate.notNull(ctx);
		}
		
		return (EntityManager)ctx.args.get("em");
	}
	
	public static EntityManager em() {
		return em(null);
	}
	
	public static <T extends PersistentObject> T fetchResource(Context ctx, String id, Class<T> clazz) {
		Validate.notNull(clazz);
		
		T object = null;
		if (UniquelyIdentifiableObject.isUuid(id) && em(ctx) != null) {
			try {
				byte[] uuid = UniquelyIdentifiableObject.getUuidBytes(UniquelyIdentifiableObject.createUuid(id));
				object = em(ctx).find(clazz, uuid);
				
				if (!SessionedAction.isOwnerOf(object)) {
					throw new AccessControlException(id);
				}
			} catch (EntityNotFoundException e) {
				// reformat it to add the id as the message.
				throw new EntityNotFoundException(id);
			}
		}
		
		return object;
	}
	
	public static <T extends PersistentObject> T fetchResource(String id, Class<T> clazz) {
		return fetchResource(null, id, clazz);
	}
	
	public static <T extends PersistentObject> T fetchResourceQuietly(Context ctx, String id, Class<T> clazz) {
		try {
			return fetchResource(ctx, id, clazz);
		} catch (Throwable e) {
			return null;
		}
	}
	
	public static <T extends PersistentObject> T fetchResourceQuietly(String id, Class<T> clazz) {
		return fetchResourceQuietly(null, id, clazz);
	}
	
	public static Status notFoundEntity(Context ctx, String id, Throwable e) {
		Logger.warn(LoggedAction.getLogEntry(ctx, "non-existent resource"), e);
		return notFound(Json.toJson(ResourceFetchError.nonExistentResourceError(id)));
	}

	public static Status notFoundEntity(Context ctx, String id) {
		return notFoundEntity(ctx, id, null);
	}

	public static Status notFoundEntity(String id, Throwable e) {
		return notFoundEntity(null, id, e);
	}
	
	public static Status notFoundEntity(String id) {
		return notFoundEntity(id, null);
	}
	
	public static Status forbiddenEntity(Context ctx, String id, Throwable e) {
		Logger.warn(LoggedAction.getLogEntry(ctx, "forbidden resource"), e);
		return forbidden(Json.toJson(ResourceFetchError.forbiddenResourceError(id)));
	}
	
	public static Status forbiddenEntity(Context ctx, String id) {
		return forbiddenEntity(ctx, id, null);
	}
	
	public static Status forbiddenEntity(String id, Throwable e) {
		return forbiddenEntity(null, id, e);
	}
	
	public static Status forbiddenEntity(String id) {
		return forbiddenEntity(id, null);
	}
	
	@Override
	public Result call(Context ctx) throws Throwable {
		Result result = null;
		EntityManager em = null;
		
		try {
			Logger.info(LoggedAction.getLogEntry(ctx, "creating entity manager"));
			
			// create entity manager, add it to args, and begin transaction before the call.
			em = SareEntityManagerFactory.createEntityManager();
			ctx.args.put("em", em);
			em.getTransaction().begin();

			// call the actual action.
			result = delegate.call(ctx);
			
			// commit active transaction after the call.
			if (em.isOpen() && em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
		} catch (Throwable e) {
			// rollback on error.
			if (em.isOpen() && em.getTransaction().isActive()) {
				Logger.info(LoggedAction.getLogEntry(ctx, "rolling back transaction"));
				em.getTransaction().rollback();
			}
			
			// rethrow.
			throw e;
		} finally {
			// close entity manager.
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
		
		return result;
	}	
}