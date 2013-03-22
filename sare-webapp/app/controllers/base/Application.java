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

import org.apache.commons.io.FilenameUtils;

import actors.*;
import models.web.WebSession;
import play.*;
import play.db.ebean.Transactional;
import play.mvc.*;

import views.html.*;

@Transactional
@With({ SessionedAction.class, ErrorHandledAction.class, LoggedAction.class })
public class Application extends Controller {
	
	public static String minifyInProd(String file) {
		if (Play.isProd()) {
			String path = FilenameUtils.getFullPath(file);
			String name = FilenameUtils.getBaseName(file);
			String ext = FilenameUtils.getExtension(file);
			
			if (!name.toLowerCase().endsWith(".min")) {
				return String.format("%s%s.min.%s", path, name, ext);
			}
		}
		
		return file;
	}
	
	public static Result homePage() {
		return ok(home.render());
	}

	public static Result keepAlive() {
		Logger.info(LoggedAction.getLogEntry("keeping session alive"));
		return ok();
	}
	
	public static Result login() {
		return TODO;
	}

	public static Result logout() {
		WebSession session = SessionedAction.getWebSession();
		if (session == null) {
			return badRequest();
		}
		
		SessionCleaner.clean(session);
		return ok();
	}
	
	public static Result loginPage() {
		return TODO;
	}

	public static Result logoutPage() {
		logout();
		return redirect(controllers.base.routes.Application.homePage());
	}
	
	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(Routes.javascriptRouter("jsRoutes",
			controllers.routes.javascript.Assets.at(),
			controllers.base.routes.javascript.Application.homePage(),
			controllers.base.routes.javascript.Application.keepAlive(),
			controllers.base.routes.javascript.Application.login(),
			controllers.base.routes.javascript.Application.logout(),
			controllers.routes.javascript.CollectionsController.supportedLanguages(),
			controllers.routes.javascript.CollectionsController.list(),
			controllers.routes.javascript.CollectionsController.get(),
			controllers.routes.javascript.CollectionsController.delete(),
			controllers.routes.javascript.CollectionsController.detailsForm(),
			controllers.routes.javascript.DocumentsController.list(),
			controllers.routes.javascript.DocumentsController.get(),
			controllers.routes.javascript.ModuleController.options(),
			controllers.routes.javascript.ModuleController.landingPage(),
			controllers.modules.routes.javascript.CorpusModule.create(),
			controllers.modules.routes.javascript.CorpusModule.update(),
			controllers.modules.routes.javascript.CorpusModule.addDocument(),
			controllers.modules.routes.javascript.CorpusModule.updateDocument(),
			controllers.modules.routes.javascript.CorpusModule.deleteDocument(),
			controllers.modules.routes.javascript.CorpusModule.twitterGrabberView(),
			controllers.modules.routes.javascript.AspectLexBuilder.create(),
			controllers.modules.routes.javascript.AspectLexBuilder.update(),
			controllers.modules.routes.javascript.AspectLexBuilder.getDocument(),
			controllers.modules.routes.javascript.AspectLexBuilder.seeDocument(),
			controllers.modules.routes.javascript.AspectLexBuilder.getAspects(),
			controllers.modules.routes.javascript.AspectLexBuilder.getAspect(),
			controllers.modules.routes.javascript.AspectLexBuilder.addAspect(),
			controllers.modules.routes.javascript.AspectLexBuilder.updateAspect(),
			controllers.modules.routes.javascript.AspectLexBuilder.deleteAspect(),
			controllers.modules.routes.javascript.AspectLexBuilder.getExpressions(),
			controllers.modules.routes.javascript.AspectLexBuilder.getExpression(),
			controllers.modules.routes.javascript.AspectLexBuilder.addExpression(),
			controllers.modules.routes.javascript.AspectLexBuilder.updateExpression(),
			controllers.modules.routes.javascript.AspectLexBuilder.deleteExpression(),
			controllers.modules.routes.javascript.AspectLexBuilder.lexiconView(),
			controllers.modules.routes.javascript.AspectLexBuilder.documentsView(),
			controllers.modules.routes.javascript.SetCoverBuilder.editorView(),
			controllers.modules.routes.javascript.SetCoverBuilder.create(),
			controllers.modules.routes.javascript.SetCoverBuilder.update(),
			controllers.modules.routes.javascript.SetCoverBuilder.redeem()
		));
	}
}