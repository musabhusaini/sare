package controllers.base;

import org.apache.commons.lang3.*;

import play.*;
import play.mvc.*;
import play.mvc.Http.Context;

public class LoggedAction extends Action.Simple {

	public static String getLogEntry(Context ctx, String message) {
		Validate.notNull(ctx);
		
		return String.format("%s - { request-uri: %s, username: %s, remote-address: %s }",
			message, ctx.request().uri(), StringUtils.defaultIfEmpty(ctx.request().username(), "anonymous"), ctx.request().remoteAddress());
	}
	
	@Override
	public Result call(Context ctx) throws Throwable {
		Logger.info(getLogEntry(ctx, "beginning"));
		
		try {
			Result result = delegate.call(ctx);
			Logger.info(getLogEntry(ctx, "finished"));
			
			return result;
		} catch (Throwable e) {
			Logger.error(getLogEntry(ctx, "error in action"), e);
			throw e;
		}
	}
}