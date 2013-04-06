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

package controllers.modules.opinionMiners.base;

import static controllers.base.SareTransactionalAction.*;
import static models.base.ViewModel.*;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicates;
import com.google.common.collect.*;

import models.base.ViewModel;
import models.documentStore.*;

import play.mvc.*;
import views.html.tags.*;

import controllers.base.SareTransactionalAction;
import controllers.modules.*;
import controllers.modules.base.*;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

@With(SareTransactionalAction.class)
@Module.Requireses({
	@Module.Requires({DocumentCorpusModel.class, AspectLexiconModel.class}),
	@Module.Requires({DocumentCorpusModel.class}),
	@Module.Requires({AspectLexiconModel.class}),
	@Module.Requires
})
public class AspectBasedOpinionMiner
		extends OpinionMiner {
	
	@Override
	public UUID getId() {
		return UuidUtils.create("b628d137c5fd4a70a3632f6be13f9cb0");
	}

	@Override
	public String getDisplayName() {
		return "Run Aspect-Based Opinion Miner";
	}
	
	@Override
	public boolean validateViewModels(Iterable<ViewModel> viewModels) {
		if (viewModels == null) {
			viewModels = Lists.newArrayList();
		}
		
		return !(Iterables.find(viewModels, Predicates.instanceOf(AspectLexiconModel.class), null) == null
				&& CorpusModule.getCorpora().size() == 0)
			&& !(Iterables.find(viewModels, Predicates.instanceOf(DocumentCorpusModel.class), null) == null
				&& AspectLexBuilder.getLexica().size() == 0);
	}

	@Override
	public String getRoute() {
		DocumentCorpusModel corpusVM = this.findViewModel(DocumentCorpusModel.class, new DocumentCorpusModel());
		AspectLexiconModel lexiconVM = this.findViewModel(AspectLexiconModel.class, new AspectLexiconModel());
		
		return controllers.modules.opinionMiners.base.routes.AspectBasedOpinionMiner.modulePage(
			corpusVM.getIdentifier(), lexiconVM.getIdentifier(), this.getCode(), false).url();
	}
	
	public static Result modulePage(UUID corpus, UUID lexicon, String engine, boolean partial) {
		if (StringUtils.isEmpty(engine)) {
			throw new IllegalArgumentException("must provide an engine to work with.");
		}
		
		DocumentCorpus corpusObj = corpus != null ? fetchResource(corpus, DocumentCorpus.class) : null;
		DocumentCorpusModel corpusVM = corpusObj != null ? (DocumentCorpusModel)createViewModel(corpusObj) : null;
		if (corpusVM != null) {
			corpusVM.populateSize(em(), corpusObj);
		}
		
		AspectLexicon lexiconObj = lexicon != null ? fetchResource(lexicon, AspectLexicon.class) : null;
		AspectLexiconModel lexiconVM = lexiconObj != null ? (AspectLexiconModel)createViewModel(lexiconObj): null;
		
		return moduleRender(new AspectBasedOpinionMiner().setViewModels(Lists.<ViewModel>newArrayList(corpusVM, lexiconVM)),
			aspectBasedOpinionMiner.render(corpusVM, lexiconVM, engine), partial);
	}
}