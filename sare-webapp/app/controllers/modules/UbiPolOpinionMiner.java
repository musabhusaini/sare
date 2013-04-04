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

package controllers.modules;

import static controllers.base.SareTransactionalAction.*;
import static models.base.ViewModel.*;

import java.util.UUID;

import com.google.common.collect.Lists;

import models.base.ViewModel;
import models.documentStore.*;

import play.mvc.Result;
import views.html.tags.*;

import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

@Module.Requireses({
	@Module.Requires({DocumentCorpusModel.class, AspectLexiconModel.class}),
	@Module.Requires({DocumentCorpusModel.class}),
	@Module.Requires({AspectLexiconModel.class}),
	@Module.Requires
})
public class UbiPolOpinionMiner
		extends Module {
	@Override
	public UUID getId() {
		return UuidUtils.create("b628d137c5fd4a70a3632f6be13f9cb0");
	}

	@Override
	public String getDisplayName() {
		return "Run Opinion Miner (UbiPol)";
	}

	@Override
	public String getRoute() {
		DocumentCorpusModel corpusVM = this.findViewModel(DocumentCorpusModel.class, new DocumentCorpusModel());
		AspectLexiconModel lexiconVM = this.findViewModel(AspectLexiconModel.class, new AspectLexiconModel());
		
		return controllers.modules.routes.UbiPolOpinionMiner.modulePage(corpusVM.getIdentifier(), lexiconVM.getIdentifier(), false).url();
	}
	
	public static Result modulePage(UUID corpus, UUID lexicon, boolean partial) {
		DocumentCorpusModel corpusVM = corpus != null ?
			(DocumentCorpusModel)createViewModel(fetchResource(corpus, DocumentCorpus.class)) : null;
		AspectLexiconModel lexiconVM = lexicon != null ?
			(AspectLexiconModel)createViewModel(fetchResource(lexicon, AspectLexicon.class)): null;
		
		return moduleRender(new UbiPolOpinionMiner().setViewModels(Lists.<ViewModel>newArrayList(corpusVM, lexiconVM)),
			ubiPolOpinionMiner.render(corpusVM, lexiconVM), partial);
	}
}