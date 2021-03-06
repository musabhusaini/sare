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

import models.web.*;
import models.web.WebSession.SessionStatus;

import org.apache.commons.lang3.*;

import actors.SessionCleaner;

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
			ctx = Validate.notNull(Context.current());
		}
		
		return StringUtils.defaultString(object.getOwnerId()).equals(getUsername(ctx));
	}
	
	public static boolean isOwnerOf(PersistentObject object) {
		return isOwnerOf(null, object);
	}
	
	public static String getSessionKey(Context ctx) {
		if (ctx == null) {
			ctx = Validate.notNull(Context.current());
		}
		
		String sessionId = null;
		
		// try to get the session id from the query string.
		String[] param = ctx.request().queryString().get("session");
		if (param != null && param.length > 0) {
			sessionId = param[0];
			WebSession session = getWebSession(sessionId);
			if (session == null || session.getStatus() != SessionStatus.IMMORTALIZED) {
				sessionId = null;
			}
		}
		
		// if not, try to get the session id from headers or cookies.
		if (StringUtils.isEmpty(sessionId)) {
			// decrypt it.
			sessionId = StringUtils.defaultString(
				StringUtils.defaultString(ctx.request().getHeader(SARE_SESSION_HEADER), ctx.session().get(SESSION_ID_KEY)));
			if (StringUtils.isNotEmpty(sessionId)) {
				try {
					sessionId = Crypto.decryptAES(sessionId);
				} catch (Throwable e) {
					sessionId = null;
				}
			}
		}
		
		return UuidUtils.isUuid(sessionId) ? sessionId : null;
	}
	
	public static String getSessionKey() {
		return getSessionKey(null);
	}
	
	public static String getUsername(WebSession session) {
		if (session == null) {
			return null;
		}
		
		WebUser user = session.getOwner();
		return user == null ? UuidUtils.normalize(session.getId()) : user.getProvierId();
	}
	
	public static String getUsername(Context ctx) {
		return getUsername(getWebSession(ctx));
	}
	
	public static String getUsername() {
		return getUsername((Context)null);
	}
	
	public static boolean isAuthenticated(WebSession session) {
		return session == null ? false : session.getOwner() != null;
	}
	
	public static boolean isAuthenticated(Context ctx) {
		if (ctx == null) {
			ctx = Validate.notNull(Context.current());
		}
		
		return isAuthenticated(getWebSession(ctx));
	}
	
	public static boolean isAuthenticated() {
		return isAuthenticated((Context)null);
	}
	
	public static boolean hasWebSession(Context ctx) {
		return getWebSession(ctx) != null;
	}
	
	public static boolean hasWebSession() {
		return hasWebSession(null);
	}
	
	public static WebSession getWebSession(String key) {
		if (!UuidUtils.isUuid(key)) {
			return null;
		}
		
		WebSession session = WebSession.find
			.fetch("owner")
			.where()
				.eq("id", UuidUtils.toBytes(key))
			.findUnique();
		
		return SessionCleaner.isSessionValid(session) ? session : null;
	}
	
	public static WebSession getWebSession(Context ctx) {
		return getWebSession(getSessionKey(ctx));
	}
	
	public static WebSession getWebSession() {
		return getWebSession((Context)null);
	}
	
	public static WebUser getWebUser(Context ctx) {
		WebSession session = getWebSession(ctx);
		return session == null ? null : session.getOwner();
	}
	
	public static WebUser getWebUser() {
		return getWebUser(null);
	}
	
	public static void embedSession(Context ctx, String sessionId) {
		if (ctx == null) {
			ctx = Validate.notNull(Context.current());
		}
		
		sessionId = Crypto.encryptAES(UuidUtils.normalize(sessionId));
		if (!ctx.session().containsKey(SESSION_ID_KEY) || !sessionId.equals(ctx.session().get(SESSION_ID_KEY))) {
			ctx.session().put(SESSION_ID_KEY, sessionId);
		}
	}
	
	public static void embedSession(Context ctx, byte[] sessionId) {
		embedSession(ctx, UuidUtils.normalize(sessionId));
	}
	
	public static WebSession createWebSession(Context ctx, WebUser owner) {
		if (ctx == null) {
			ctx = Validate.notNull(Context.current());
		}
		
		Logger.info(LoggedAction.getLogEntry(ctx,
			String.format("starting a new session%s", owner != null ? " for user: " + owner.getProvierId() : "")));
		WebSession session = new WebSession()
			.setStatus(SessionStatus.ALIVE)
			.setRemoteAddress(ctx.request().remoteAddress())
			.setOwner(owner);
		embedSession(ctx, session.getId());
		
		session.save();
		return session;
	}
	
	public static WebSession createWebSession(WebUser owner) {
		return createWebSession(null, owner);
	}
	
	public static WebSession createWebSession(Context ctx) {
		return createWebSession(ctx, null);
	}
	
	public static WebSession createWebSession() {
		return createWebSession(null, null);
	}
	
	public static void clearSession(Context ctx) {
		if (ctx == null) {
			ctx = Validate.notNull(Context.current());
		}
		
		ctx.session().remove(SESSION_ID_KEY);
	}
	
	public static void clearSession() {
		clearSession(null);
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
		
		if (session == null) {
			return unauthorized(Application.renderLoginPage(ctx.request().uri()));
		}
		
		// embed the session.
		embedSession(ctx, session.getId());
		ctx.request().setUsername(getUsername(ctx));
		
		return delegate.call(ctx);
	}
}