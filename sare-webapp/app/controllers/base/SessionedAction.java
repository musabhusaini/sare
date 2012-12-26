package controllers.base;

import models.WebSession;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.utils.UuidUtils;
import play.Logger;
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
		
		String sessionId = StringUtils.defaultString(
			StringUtils.defaultString(ctx.request().getHeader(SARE_SESSION_HEADER),
			ctx.session().get(SESSION_ID_KEY)));
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
		
		// get the session, or create one if it doesn't exist.
		if (!StringUtils.isEmpty(getSessionKey(ctx))) {
			session = getWebSession(ctx);
			
			if (session != null) {
				Logger.info(LoggedAction.getLogEntry(ctx, "session found"));
				session.touch().update();
			} else {
				Logger.info(LoggedAction.getLogEntry(ctx, "session expired"));
				ctx.session().remove(SESSION_ID_KEY);
				ctx.request().setUsername(null);
			}
		}
		
		if (session == null) {
			Logger.info(LoggedAction.getLogEntry(ctx, "starting new session"));
			session = new WebSession();
			ctx.session().put(SESSION_ID_KEY, UuidUtils.normalize(session.id));
			session.ownerId = getUsername(ctx);
			session.remoteAddress = ctx.request().remoteAddress();
			session.save();
		}
		
		return delegate.call(ctx);
	}
}