package controllers.base;

import static edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject.*;

import models.WebSession;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.sare.models.base.*;
import play.Logger;
import play.mvc.*;
import play.mvc.Http.Context;

public class SessionedAction extends Action.Simple {
	
	private  static final String SESSION_ID_KEY = "session_id";
	
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
		
		String sessionId = StringUtils.defaultString(ctx.session().get(SESSION_ID_KEY));
		return isUuid(sessionId) ? sessionId : null;
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
		
		return !normalizeUuidString(session.id).equals(session.ownerId);
	}
	
	public static boolean isAuthenticated(Context ctx) {
		if (ctx == null) {
			ctx = Context.current();
			Validate.notNull(ctx);
		}
		
		WebSession session = new WebSession();
		session.id = getUuidBytes(getSessionKey(ctx));
		session.ownerId = getUsername(ctx);
		return isAuthenticated(session);
	}
	
	public static boolean isAuthenticated() {
		return isAuthenticated((Context)null);
	}
	
	@Override
	public Result call(Context ctx) throws Throwable {
		WebSession session = null;
		
		// get the session, or create one if it doesn't exist.
		if (!StringUtils.isEmpty(getSessionKey(ctx))) {
			session = WebSession.find.byId(getUuidBytes(getSessionKey(ctx)));
			
			if (session != null) {
				Logger.info(LoggedAction.getLogEntry(ctx, "session found"));
				session.touch().update();
			} else {
				ctx.session().remove(SESSION_ID_KEY);
				ctx.request().setUsername(null);
			}
		}
		
		if (session == null) {
			Logger.info(LoggedAction.getLogEntry(ctx, "starting new session"));
			
			session = new WebSession();
			ctx.session().put(SESSION_ID_KEY, normalizeUuidString(session.id));
			session.ownerId = getUsername(ctx);
			session.remoteAddress = ctx.request().remoteAddress();
			session.save();
		}
		
		return delegate.call(ctx);
	}
}