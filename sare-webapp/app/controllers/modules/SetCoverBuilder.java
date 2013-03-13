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
import static controllers.base.SessionedAction.*;
import static models.base.ViewModel.*;

import java.util.*;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import play.mvc.Result;
import play.mvc.With;
import views.html.tags.*;
import models.base.ViewModel;
import models.documentStore.*;
import controllers.base.SareTransactionalAction;
import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.DocumentSetCoverController;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.sare.models.setcover.DocumentSetCover;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

@With(SareTransactionalAction.class)
@Module.Requireses({
	@Module.Requires({DocumentCorpusModel.class}),
	@Module.Requires({DocumentCorpusModel.class, DocumentSetCoverModel.class}),
})
public class SetCoverBuilder extends Module {

	@Override
	public UUID getId() {
		return UuidUtils.create("71d5ff12bb0247f5ace1bc1c0568e926");
	}
	
	@Override
	public String getDisplayName() {
		return "Reduce Corpus";
	}

	@Override
	public String getRoute() {
		DocumentCorpusModel corpusVM = this.findViewModel(DocumentCorpusModel.class, new DocumentCorpusModel());
		DocumentSetCoverModel setcoverVM = this.findViewModel(DocumentSetCoverModel.class, new DocumentSetCoverModel());
		
		return controllers.modules.routes.SetCoverBuilder.modulePage(corpusVM.id, setcoverVM.id, false).url();
	}
	
	public static List<PersistentDocumentStoreModel> getSetCovers(DocumentCorpusModel corpus) {
		if (corpus == null) {
			return Lists.newArrayList();
		}
		
		DocumentCorpus corpusObj = fetchResource(corpus.id, DocumentCorpus.class);
		return Lists.transform(new DocumentSetCoverController().getAllSetCovers(em(), getUsername(), corpusObj),
			new Function<String, PersistentDocumentStoreModel>() {
				@Override
				@Nullable
				public PersistentDocumentStoreModel apply(@Nullable String input) {
					return (PersistentDocumentStoreModel)createViewModel(fetchResource(input, DocumentSetCover.class));
				}
			});
	}
	
	public static Result modulePage(String corpus, String setcover, boolean partial) {
		DocumentCorpus corpusObj = fetchResource(corpus, DocumentCorpus.class);
		DocumentCorpusModel corpusVM = (DocumentCorpusModel)createViewModel(corpusObj);
		DocumentSetCoverModel setcoverVM = setcover != null ?
			(DocumentSetCoverModel)createViewModel(fetchResource(setcover, DocumentSetCover.class)) : null;
		
		if (setcoverVM == null && new DocumentSetCoverController().getAllSetCovers(em(), getUsername(), corpusObj).size() == 0) {
			em().persist(new DocumentSetCover(corpusObj).setOwnerId(getUsername()));
		}
		
		return moduleRender(new SetCoverBuilder().setViewModels(Lists.<ViewModel>newArrayList(corpusVM, setcoverVM)),
			setCoverBuilder.render(corpusVM, setcoverVM), partial);
	}
}