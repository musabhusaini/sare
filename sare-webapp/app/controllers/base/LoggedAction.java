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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package controllers.base;

import org.apache.commons.lang3.*;

import play.*;
import play.mvc.*;
import play.mvc.Http.Context;

public class LoggedAction extends Action.Simple {

	public static String getLogEntry(Context ctx, String message) {
		if (ctx == null) {
			ctx = Context.current();
			Validate.notNull(ctx);
		}
		
		return String.format("%s - { request-method: %s, request-uri: %s, username: %s, remote-address: %s, session: %s }",
			message, ctx.request().method(), ctx.request().uri(), StringUtils.defaultIfEmpty(SessionedAction.getUsername(ctx), "anonymous"),
			ctx.request().remoteAddress(), StringUtils.defaultIfEmpty(SessionedAction.getSessionKey(ctx), "none"));
	}
	
	public static String getLogEntry(String message) {
		return getLogEntry(null, message);
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