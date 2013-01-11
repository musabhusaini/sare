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

package controllers.modules;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import play.mvc.Result;
import models.base.ViewModel;
import models.documentStore.PersistentDocumentStoreModel;
import views.html.tags.*;
import controllers.modules.base.Module;

@Module.Requires(types={ PersistentDocumentStoreModel.class })
public class SetCoverModule extends Module {

	@Override
	public String getDisplayName() {
		return "Reduce by set cover";
	}

	@Override
	public String getRoute(Iterable<ViewModel> viewModels) {
		PersistentDocumentStoreModel viewModel = (PersistentDocumentStoreModel)Iterables.find(viewModels,
			Predicates.instanceOf(PersistentDocumentStoreModel.class), null);
		
		if (viewModel == null) {
			throw new IllegalArgumentException();
		}
		
		return controllers.modules.routes.SetCoverModule.module(viewModel.id, false).url();
	}
	
	public static Result module(String collection, boolean partial) {
		return ok(setcover.render());
	}
}