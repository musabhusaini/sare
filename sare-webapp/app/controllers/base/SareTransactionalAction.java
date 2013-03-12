/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package controllers.base;

import java.security.AccessControlException;

import javax.persistence.*;

import models.ResourceFetchError;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.core.models.UserInaccessibleModel;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.SareEntityManagerFactory;
import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

import play.*;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.Context;

public class SareTransactionalAction extends Action.Simple {

	private static ThreadLocal<EntityManager> currentEntityManager = new ThreadLocal<>();
	
	public static EntityManager em() {
		EntityManager em = currentEntityManager.get();
		if (em == null) {
			throw new RuntimeException("No EntityManager bound to this thread. " +
				"Try annotating your action method with @With(controllers.base.SareTransactionalAction)");
		}
		return em;
	}
	
	public static <T extends PersistentObject> T fetchResource(Context ctx, String id, Class<T> clazz) {
		Validate.notNull(clazz);
		
		Logger.info(LoggedAction.getLogEntry(ctx,
			String.format("attempting to fetch resource: %s of type: %s", id, clazz.getName())));
		
		T object = null;
		if (UuidUtils.isUuid(id) && em() != null) {
			try {
				byte[] uuid = UuidUtils.toBytes(UuidUtils.create(id));
				object = em().find(clazz, uuid);
				
				if (object != null && (!SessionedAction.isOwnerOf(object) || object instanceof UserInaccessibleModel )) {
					throw new AccessControlException(id);
				}
			} catch (EntityNotFoundException e) {
				object = null;
			}
		}
		
		if (object == null) {
			throw new EntityNotFoundException(id);
		}
		
		Logger.info(LoggedAction.getLogEntry(ctx,
			String.format("found resource: %s of type: %s", id, clazz.getName())));
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
			// create entity manager, add it to args, and begin transaction before the call.
			Logger.info(LoggedAction.getLogEntry(ctx, "creating entity manager"));
			em = SareEntityManagerFactory
				.createEntityManager(Play.application().getWrappedApplication().mode().toString());
			currentEntityManager.set(em);
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