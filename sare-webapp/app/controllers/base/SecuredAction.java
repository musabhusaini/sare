package controllers.base;

import org.apache.commons.lang3.StringUtils;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import play.mvc.*;
import play.mvc.Http.*;

public class SecuredAction extends Security.Authenticator {

	@Override
	public String getUsername(Context ctx) {
		return ctx.session().get("username");
	}
	
	@Override
	public Result onUnauthorized(Context ctx) {
		return redirect(controllers.base.routes.Application.login());
	}
	
	public static boolean isOwnerOf(PersistentObject object) {
		return StringUtils.defaultString(object.getOwnerId()).equals(Context.current().request().username());
	}
}
