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
import java.util.UUID;

import com.google.common.collect.Lists;

import play.api.templates.Html;
import play.mvc.Result;
import views.html.moduleView;

import controllers.base.Application;

import models.ModuleModel;
import models.base.*;

public abstract class Module
	extends Application {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface Requires {
		public Class<? extends ViewModel>[] value() default {};
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface Requireses {
		Requires[] value();
	}
	
	protected static Result moduleRender(Module module, Html partialView, boolean partial) {
		if (!partial) {
			return ok(moduleView.render(new ModuleModel(module), partialView, null, null));
		} else {
			return ok(partialView);
		}
	}
	
	public Module() {
		this.setViewModels(null);
	}
	
	protected Iterable<ViewModel> viewModels;
	public Module setViewModels(Iterable<ViewModel> viewModels) {
		if (viewModels == null) {
			viewModels = Lists.newArrayList();
		}
		this.viewModels = viewModels;
		return this;
	}
	
	public abstract UUID getId();
	
	public abstract String getDisplayName();
	
	public abstract String getRoute();
	
	public boolean canPartiallyRender() {
		return true;
	}
	
	public boolean allowSelfOutput() {
		return false;
	}
}