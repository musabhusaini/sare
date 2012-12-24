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
	
	@Override
	public Result call(Context ctx) throws Throwable {
		// get the session, or create one if it doesn't exist.
		if (!StringUtils.isEmpty(getSessionKey(ctx))) {
			WebSession session = WebSession.find.byId(getUuidBytes(getSessionKey(ctx)));
			
			if (session != null) {
				Logger.info(LoggedAction.getLogEntry(ctx, "session found"));
				session.touch().update();
			} else {
				Logger.error(LoggedAction.getLogEntry(ctx, "session not found"));
				return badRequest("session not found");
			}
		} else {
			Logger.info(LoggedAction.getLogEntry(ctx, "starting new session"));
			
			WebSession session = new WebSession();
			ctx.session().put(SESSION_ID_KEY, normalizeUuidString(createUuid(session.id)));
			session.ownerId = getUsername(ctx);
			session.remoteAddress = ctx.request().remoteAddress();
			session.save();
		}
		
		return delegate.call(ctx);
	}
}