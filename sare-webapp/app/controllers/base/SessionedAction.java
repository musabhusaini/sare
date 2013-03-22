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

import javax.persistence.OptimisticLockException;

import models.web.WebSession;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.utils.UuidUtils;
import play.Logger;
import play.libs.*;
import play.mvc.*;
import play.mvc.Http.Context;

public class SessionedAction extends Action.Simple {
	
	public static final String SESSION_ID_KEY = "session_id";
	public static final String SARE_SESSION_HEADER = "x-sare-session";
	
	public static boolean isOwnerOf(Context ctx, PersistentObject object) {
		if (ctx == null) {
			ctx = Context.current();
			Validate.notNull(ctx);
		}
		
		return StringUtils.defaultString(object.getOwnerId()).equals(getUsername(ctx));
	}
	
	public static boolean isOwnerOf(PersistentObject object) {
		return isOwnerOf(null, object);
	}
	
	public static String getSessionKey(Context ctx) {
		if (ctx == null) {
			ctx = Context.current();
			Validate.notNull(ctx);
		}
		
		// get the session id and decrypt it.
		String sessionId = StringUtils.defaultString(
			StringUtils.defaultString(ctx.request().getHeader(SARE_SESSION_HEADER), ctx.session().get(SESSION_ID_KEY)));
		if (StringUtils.isNotEmpty(sessionId)) {
			try {
				sessionId = Crypto.decryptAES(sessionId);
			} catch (Throwable e) {
				sessionId = null;
			}
		}
		
		return UuidUtils.isUuid(sessionId) ? sessionId : null;
	}
	
	public static String getSessionKey() {
		return getSessionKey(null);
	}
	
	public static String getUsername(Context ctx) {
		if (ctx == null) {
			ctx = Context.current();
			Validate.notNull(ctx);
		}
		
		return StringUtils.defaultIfEmpty(ctx.request().username(), getSessionKey(ctx));
	}
	
	public static String getUsername() {
		return getUsername(null);
	}
	
	public static boolean isAuthenticated(WebSession session) {
		if (session == null) {
			return false;
		}
		
		return !UuidUtils.normalize(session.id).equals(session.ownerId);
	}
	
	public static boolean isAuthenticated(Context ctx) {
		if (ctx == null) {
			ctx = Context.current();
			Validate.notNull(ctx);
		}
		
		WebSession session = new WebSession();
		session.id = UuidUtils.toBytes(getSessionKey(ctx));
		session.ownerId = getUsername(ctx);
		return isAuthenticated(session);
	}
	
	public static boolean isAuthenticated() {
		return isAuthenticated((Context)null);
	}
	
	public static WebSession getWebSession(Context ctx) {
		if (ctx == null) {
			ctx = Context.current();
		}
		
		return WebSession.find.byId(UuidUtils.toBytes(getSessionKey(ctx)));
	}
	
	public static WebSession getWebSession() {
		return getWebSession(null);
	}
	
	@Override
	public Result call(Context ctx) throws Throwable {
		WebSession session = null;
		
		// get the session.
		if (!StringUtils.isEmpty(getSessionKey(ctx))) {
			session = getWebSession(ctx);
			
			if (session != null) {
				Logger.info(LoggedAction.getLogEntry(ctx, "session found"));
				
				// if we hit the optimistic concurrency checking problem, then we don't need to update again.
				try {
					session.touch().update();
				} catch (OptimisticLockException e) {
					session.refresh();
				}
			} else {
				Logger.info(LoggedAction.getLogEntry(ctx, "session expired"));
				ctx.session().remove(SESSION_ID_KEY);
				ctx.request().setUsername(null);
			}
		}
		
		// create one if it doesn't exist.
		if (session == null) {
			Logger.info(LoggedAction.getLogEntry(ctx, "starting new session"));
			session = new WebSession();
			
			String sessionId = Crypto.encryptAES(UuidUtils.normalize(session.id));
			ctx.session().put(SESSION_ID_KEY, sessionId);
			session.ownerId = getUsername(ctx);
			session.remoteAddress = ctx.request().remoteAddress();
			session.save();
		}
		
		return delegate.call(ctx);
	}
}