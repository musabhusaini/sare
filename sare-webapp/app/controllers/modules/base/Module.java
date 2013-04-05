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

package controllers.modules.base;

import java.lang.annotation.*;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Predicates;
import com.google.common.collect.*;

import play.api.templates.Html;
import play.mvc.Result;
import views.html.moduleView;

import controllers.base.Application;

import models.base.*;
import models.web.ModuleModel;

public abstract class Module
	extends Application {
	
	/**
	 * Annotates the types of ViewModel objects a module requires to function.
	 * One object of each type needs to be present in order for a requirement to be satisfied.
	 * @author Mus'ab Husaini
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface Requires {
		public Class<? extends ViewModel>[] value() default {};
	}
	
	/**
	 * Annotates a collection of {@link Requires} annotations.
	 * Any one of these requirements needs to be satisfied in order for the entire requirement to be satisfied.
	 * The first requirement to be satisfied is taken and the rest are ignored.
	 * @author Mus'ab Husaini
	 */
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
	
	public boolean validateViewModels(Iterable<ViewModel> viewModels) {
		return true;
	}
	
	public boolean validateViewModels() {
		return this.validateViewModels(this.viewModels);
	}
	
	public Module setViewModels(Iterable<ViewModel> viewModels) {
		viewModels = ObjectUtils.defaultIfNull(viewModels, Lists.<ViewModel>newArrayList());
		
		if (!validateViewModels(viewModels)) {
			throw new IllegalArgumentException("Supplied view models not valid for the current context.");
		}
		
		this.viewModels = Iterables.filter(viewModels, Predicates.notNull());
		return this;
	}
	
	public <T extends ViewModel> T findViewModel(Class<T> viewModelClass, Iterable<ViewModel> exclude, T defaultViewModel) {
		exclude = ObjectUtils.defaultIfNull(exclude, Lists.<ViewModel>newArrayList());
		return Iterables.getFirst(Iterables.filter(Iterables.filter(
			this.viewModels, Predicates.not(Predicates.in(Lists.newArrayList(exclude)))), viewModelClass),
			defaultViewModel);
	}
	
	public <T extends ViewModel> T findViewModel(Class<T> viewModelClass, T defaultViewModel) {
		return this.findViewModel(viewModelClass, null, defaultViewModel);
	}
	
	public <T extends ViewModel> T findViewModel(Class<T> viewModelClass, Iterable<ViewModel> exclude) {
		return this.findViewModel(viewModelClass, exclude, null);
	}
	
	public <T extends ViewModel> T findViewModel(Class<T> viewModelClass) {
		return this.findViewModel(viewModelClass, (T)null);
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
	
	public Iterable<Module> getSubModules() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Module && this.getId() != null) {
			return ObjectUtils.equals(this.getId(), ((Module)obj).getId());
		}
		
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		if (this.getId() != null) {
			return this.getId().hashCode();
		}
		
		return super.hashCode();
	}

	@Override
	public String toString() {
		if (this.getDisplayName() != null) {
			return this.getDisplayName();
		}
		
		return super.toString();
	}
}