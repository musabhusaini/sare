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

package controllers.modules.base;

import java.lang.annotation.*;

import play.api.templates.Html;
import play.mvc.Result;
import views.html.moduleView;

import controllers.base.Application;

import models.base.*;

public abstract class Module extends Application {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface Requires {
		public Class<? extends ViewModel>[] types() default {};
	}
	
	protected static Result module(Html partialView, boolean partial) {
		if (!partial) {
			return ok(moduleView.render(partialView, null, null));
		} else {
			return ok(partialView);
		}
	}
	
	public abstract String getDisplayName();
	
	public abstract String getRoute(Iterable<ViewModel> viewModels);
}