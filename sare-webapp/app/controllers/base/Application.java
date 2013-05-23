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

import static controllers.base.SessionedAction.*;

import java.io.*;
import java.net.*;
import java.util.UUID;

import org.apache.commons.io.*;
import org.apache.commons.lang3.*;
import org.codehaus.jackson.JsonNode;

import com.avaje.ebean.*;

import edu.sabanciuniv.sentilab.core.controllers.*;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

import actors.*;
import models.web.*;
import play.*;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

@Transactional
@With({ ErrorHandledAction.class, LoggedAction.class, SecuredAction.class })
public class Application extends Controller {
	
	public static String minifyInProd(String file, Boolean neverMinify) {
		if (Play.isProd() && !ObjectUtils.defaultIfNull(neverMinify, false)) {
			String path = FilenameUtils.getFullPath(file);
			String name = FilenameUtils.getBaseName(file);
			String ext = FilenameUtils.getExtension(file);
			
			if (!name.toLowerCase().endsWith(".min")) {
				return String.format("%s%s.min.%s", path, name, ext);
			}
		}
		
		return file;
	}
	
	public static String minifyInProd(String file) {
		return minifyInProd(file, null);
	}
	
	public static ProgressObserverToken createProgressObserverToken(final byte[] id, final Double initialProgress) {
		return Ebean.execute(new TxCallable<ProgressObserverToken>() {
			@Override
			public ProgressObserverToken call() {
				ProgressObserverToken token = new ProgressObserverToken()
					.setSession(getWebSession())
					.setProgress((double)ObjectUtils.defaultIfNull(initialProgress, 0.0));
				
				if (id != null) {
					token.setId(id);
				}
				
				token.save();
				return token;
			}
		});
	}
	
	public static ProgressObserverToken createProgressObserverToken(byte[] id) {
		return createProgressObserverToken(id, null);
	}
	
	public static ProgressObserverToken createProgressObserverToken(Double initialProgress) {
		return createProgressObserverToken(null, initialProgress);
	}
	
	public static ProgressObserverToken createProgressObserverToken() {
		return createProgressObserverToken(null, null);
	}
	
	public static ProgressObserverToken setProgressFinished(final byte[] id) {
		if (id == null) {
			return null;
		}
		
		return Ebean.execute(new TxCallable<ProgressObserverToken>() {
			@Override
			public ProgressObserverToken call() {
				ProgressObserverToken token = ProgressObserverToken.find.byId(id);
				if (token == null) {
					return null;
				}
				
				token.setProgress(1.1);
				token.update();
				return token;
			}
		});
	}
	
	public static boolean finalizeProgress(final byte[] id) {
		if (id == null) {
			return false;
		}
		
		return ObjectUtils.defaultIfNull(Ebean.execute(new TxCallable<Boolean>() {
			@Override
			public Boolean call() {
				ProgressObserverToken token = ProgressObserverToken.find.byId(id);
				if (token == null) {
					return false;
				}
				
				token.delete();
				return true;
			}
		}), false);
	}
	
	public static ProgressObserverToken redeemProgress(final byte[] id) {
		if (id == null) {
			return null;
		}
		
		return Ebean.execute(new TxCallable<ProgressObserverToken>() {
			@Override
			public ProgressObserverToken call() {
				ProgressObserverToken token = ProgressObserverToken.find.byId(id);
				if (token == null) {
					return null;
				} else if (token.getProgress() >= 1.1) {
					token.delete();
					return null;
				}
				
				return token;
			}
		});
	}
	
	public static ProgressObserverToken redeemProgress(UUID id) {
		return redeemProgress(UuidUtils.toBytes(id));
	}
	
	public static ProgressObserver watchProgress(ProgressObservable remoteObject, final String watchedMessage, final byte[] id) {
		if (remoteObject == null || id == null) {
			return null;
		}
		
		ProgressObserver observer = new ProgressObserver() {
			@Override
			public void observe(final double progress, String message) {
				if (StringUtils.isNotEmpty(watchedMessage) && !watchedMessage.equalsIgnoreCase(message)) {
					return;
				}
				
				Ebean.execute(new TxRunnable() {
					@Override
					public void run() {
						ProgressObserverToken token = ProgressObserverToken.find.byId(id);
						if (token == null) {
							return;
						}
						
						token.setProgress(progress);
						token.update();
					}
				});
			}
		};
		
		remoteObject.addProgessObserver(observer);
		return observer;
	}
	
	public static ProgressObserver watchProgress(ProgressObservable remoteObject, final byte[] id) {
		return watchProgress(remoteObject, null, id);
	}
	
	public static Result homePage() {
		return ok(home.render());
	}
	
	public static Result indexPage() {
		return redirect(routes.Application.homePage());
	}
	
	public static Result aboutPage() {
		return TODO;
	}
	
	public static Result contactPage() {
		return TODO;
	}
	
	public static Result privacyPage() {
		return TODO;
	}
	
	@With(SessionedAction.class)
	public static Result keepAlive() {
		Logger.info(LoggedAction.getLogEntry("keeping session alive"));
		return ok();
	}
	
	private static Result postLoginRedirect(String redirectTo) {
		if (redirectTo != null) {
			return redirect(redirectTo);
		}
		return redirect(routes.Application.homePage());
	}

	public static Result login(String redirectTo, boolean isGuest) {
		if (isGuest) {
			createWebSession();
		} else {
			String token = request().body().asFormUrlEncoded().get("token")[0];
			String apiKey = Play.application().configuration().getString("rpx.apiKey");
			String data;
			String response = null;
			try {
				data = String.format("token=%s&apiKey=%s&format=json",
						URLEncoder.encode(token, "UTF-8"), URLEncoder.encode(apiKey, "UTF-8"));
				URL url = new URL("https://rpxnow.com/api/v2/auth_info");
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		        conn.setRequestMethod("POST");
		        conn.setDoOutput(true);
		        conn.connect();
		        OutputStreamWriter osw = new OutputStreamWriter(
		            conn.getOutputStream(), "UTF-8");
		        osw.write(data);
		        osw.close();
		        response = IOUtils.toString(conn.getInputStream());
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
			
			JsonNode profile = Json.parse(response).path("profile");
			String identifier = profile.path("identifier").asText();
			
			WebUser user = WebUser.find.where().like("providerId", identifier).findUnique();
			
			if (user == null) {
				user = new WebUser()
					.setProviderId(identifier)
					.setProfile(Json.stringify(profile));
				user.save();
			}
			
			createWebSession(user);
		}
		
		return postLoginRedirect(redirectTo);
	}
	
	public static Result logout() {
		WebSession session = getWebSession();
		if (session != null) {
			SessionCleaner.clean(session);
			clearSession();
		}
		
		return ok();
	}
	
	public static Content renderLoginPage(String redirectTo) {
		return login.render(redirectTo, false);
	}
	
	public static Result loginPage(String redirectTo, boolean isGuest) {
		if (isGuest) {
			return login(redirectTo, isGuest);
		}
		
		if (isAuthenticated()) {
			return postLoginRedirect(redirectTo);			
		}
		
		return ok(renderLoginPage(redirectTo));
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
			controllers.base.routes.javascript.Application.loginPage(),
			controllers.routes.javascript.CollectionsController.supportedLanguages(),
			controllers.routes.javascript.CollectionsController.list(),
			controllers.routes.javascript.CollectionsController.get(),
			controllers.routes.javascript.CollectionsController.delete(),
			controllers.routes.javascript.CollectionsController.detailsForm(),
			controllers.routes.javascript.DocumentsController.list(),
			controllers.routes.javascript.DocumentsController.get(),
			controllers.routes.javascript.DocumentsController.editorView(),
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
			controllers.modules.routes.javascript.SetCoverBuilder.getSetCover(),
			controllers.modules.routes.javascript.SetCoverBuilder.redeem(),
			controllers.modules.opinionMiners.base.routes.javascript.AspectOpinionMiner.getMined(),
			controllers.modules.opinionMiners.base.routes.javascript.AspectOpinionMiner.mine(),
			controllers.modules.opinionMiners.base.routes.javascript.AspectOpinionMiner.redeem(),
			controllers.modules.opinionMiners.base.routes.javascript.AspectOpinionMiner.editorView(),
			controllers.modules.opinionMiners.base.routes.javascript.AspectOpinionMiner.resultsView()
		));
	}
}