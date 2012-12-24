package controllers.base;

import java.security.AccessControlException;

import javax.persistence.EntityNotFoundException;

import play.mvc.*;
import play.mvc.Http.Context;

public class ErrorHandledAction extends Action.Simple {
	@Override
	public Result call(Context ctx) throws Throwable {
		try {
			return delegate.call(ctx);
		} catch (AccessControlException e) {
			return SareTransactionalAction.forbiddenEntity(ctx, e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			return badRequest();
		} catch (EntityNotFoundException e) {
			return SareTransactionalAction.notFoundEntity(ctx, e.getMessage(), e);
		} catch (Throwable e) {
			return internalServerError();
		}
	}
}