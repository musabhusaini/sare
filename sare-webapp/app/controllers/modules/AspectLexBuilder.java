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

import static controllers.base.SareTransactionalAction.*;
import static controllers.base.SessionedAction.*;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.*;
import com.google.common.collect.*;

import play.mvc.Result;
import play.mvc.With;
import views.html.tags.*;
import models.documentStore.*;
import controllers.base.SareTransactionalAction;
import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;

@With(SareTransactionalAction.class)
@Module.Requireses({
	@Module.Requires,
	@Module.Requires(DocumentCorpusModel.class),
	@Module.Requires({DocumentCorpusModel.class, AspectLexiconModel.class}),
	@Module.Requires(AspectLexiconModel.class)
})
public class AspectLexBuilder extends Module {

	public static List<PersistentDocumentStoreModel> getLexica() {
		PersistentDocumentStoreController docStoreController = new PersistentDocumentStoreController();
		return Lists.transform(docStoreController.getAllUuids(em(), getUsername(), AspectLexicon.class),
			new Function<String, PersistentDocumentStoreModel>() {
				@Override
				@Nullable
				public PersistentDocumentStoreModel apply(@Nullable String input) {
					return (PersistentDocumentStoreModel)createViewModel(fetchResource(input, PersistentDocumentStore.class));
				}
			});
	}
	
	@Override
	public String getDisplayName() {
		return "Build aspect lexicon";
	}

	@Override
	public String getRoute() {
		DocumentCorpusModel corpus = (DocumentCorpusModel)Iterables.find(this.viewModels, Predicates.instanceOf(DocumentCorpusModel.class), null);
		AspectLexiconModel lexicon = (AspectLexiconModel)Iterables.find(this.viewModels, Predicates.instanceOf(AspectLexiconModel.class), null);
		return controllers.modules.routes.AspectLexBuilder.modulePage(
			corpus != null ? corpus.id : null,
			lexicon != null ? lexicon.id : null,
			false).url();
	}
	
	public static Result modulePage(String corpus, String lexicon, boolean partial) {
		DocumentCorpusModel corpusObj = StringUtils.isNotEmpty(corpus) ?
			(DocumentCorpusModel)createViewModel(fetchResource(corpus, DocumentCorpus.class)) : null;
		AspectLexiconModel lexiconObj = StringUtils.isNotEmpty(lexicon) ?
			(AspectLexiconModel)createViewModel(fetchResource(lexicon, AspectLexicon.class)) : null;
		return moduleRender(aspectLexBuilder.render(corpusObj, lexiconObj, true), partial);
	}
	
	public static Result create(String corpus) {
		return TODO;
	}
}