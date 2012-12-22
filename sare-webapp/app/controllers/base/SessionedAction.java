package controllers.base;

import java.util.*;

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
		return UniquelyIdentifiableObject.isUuid(sessionId) ? sessionId : null;
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
		// get the session id, create one if it doesn't exist.
		if (StringUtils.isEmpty(getSessionKey(ctx))) {
			ctx.session().put(SESSION_ID_KEY, UUID.randomUUID().toString().replace("-", "").toLowerCase());
			Logger.info(LoggedAction.getLogEntry(ctx, "starting new session"));
		} else {
			Logger.info(LoggedAction.getLogEntry(ctx, "session found"));
		}
		
		// TODO: refresh session state here.
		
		return delegate.call(ctx);
	}
}