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
import models.documentStore.*;
import views.html.tags.*;
import controllers.modules.base.Module;

@Module.Requires({DocumentCorpusModel.class})
public class SetCoverModule extends Module {

	@Override
	public String getDisplayName() {
		return "Reduce";
	}

	@Override
	public String getRoute() {
		DocumentCorpusModel viewModel = (DocumentCorpusModel)Iterables.find(this.viewModels,
			Predicates.instanceOf(DocumentCorpusModel.class), null);
		
		if (viewModel == null) {
			throw new IllegalArgumentException();
		}
		
		return controllers.modules.routes.SetCoverModule.modulePage(viewModel.id, false).url();
	}
	
	public static Result modulePage(String corpus, boolean partial) {
		return moduleRender(setcover.render(), partial);
	}
}