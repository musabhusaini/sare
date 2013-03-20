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
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.*;
import org.codehaus.jackson.JsonNode;

import com.avaje.ebean.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import play.db.ebean.Transactional;
import play.libs.*;
import play.mvc.*;
import views.html.tags.*;
import models.ProgressObserverToken;
import models.base.ViewModel;
import models.documentStore.*;
import controllers.base.SareTransactionalAction;
import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.core.controllers.ProgressObserver;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.DocumentSetCoverController;
import edu.sabanciuniv.sentilab.sare.controllers.setcover.SetCoverController;
import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;
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
		DocumentSetCoverModel setCoverVM = this.findViewModel(DocumentSetCoverModel.class, new DocumentSetCoverModel());
		DocumentCorpusModel corpusVM = this.findViewModel(DocumentCorpusModel.class, Lists.<ViewModel>newArrayList(setCoverVM));
		
		if (corpusVM instanceof DocumentSetCoverModel) {
			DocumentSetCoverModel tmpVM = (DocumentSetCoverModel)corpusVM;
			if (tmpVM.baseCorpus != null && setCoverVM.id != null && StringUtils.equals(tmpVM.baseCorpus.id, setCoverVM.id)) {
				corpusVM = new DocumentCorpusModel();
				setCoverVM = tmpVM;
			}
		} else if (corpusVM == null) {
			corpusVM = this.findViewModel(DocumentCorpusModel.class);
			setCoverVM = new DocumentSetCoverModel();
		}
		
		return controllers.modules.routes.SetCoverBuilder.modulePage(corpusVM.id, setCoverVM.id, false).url();
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
		DocumentCorpus corpusObj = null;
		DocumentCorpusModel corpusVM = null;
		if (corpus != null) {
			corpusObj = fetchResource(corpus, DocumentCorpus.class);
		}
		
		DocumentSetCover setCoverObj = null;
		DocumentSetCoverModel setCoverVM = null;
		if (setcover != null) {
			setCoverObj = fetchResource(setcover, DocumentSetCover.class);
			setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
		}
			
		if (corpusObj == null && setCoverObj == null) {
			throw new IllegalArgumentException();
		} else if (corpusObj == null) {
			corpusObj = setCoverObj.getBaseCorpus();
		} else if (setCoverObj != null && ObjectUtils.notEqual(corpusObj, setCoverObj.getBaseCorpus())) {
			throw new IllegalArgumentException();
		}
		if (corpusObj == null) {
			throw new IllegalArgumentException();
		}
		
		if (setCoverObj == null && new DocumentSetCoverController().getAllSetCovers(em(), getUsername(), corpusObj).size() == 0) {
			create(corpus);
		}
		
		corpusVM = (DocumentCorpusModel)createViewModel(corpusObj);
		return moduleRender(new SetCoverBuilder().setViewModels(Lists.<ViewModel>newArrayList(corpusVM, setCoverVM)),
			setCoverBuilder.render(corpusVM, setCoverVM, corpusObj.getLinguisticProcessor().getBasicPosTags()), partial);
	}
	
	public static Result editorView(String setcover) {
		DocumentSetCover setCoverObj = fetchResource(setcover, DocumentSetCover.class);
		Map<String, String> posTags = null;
		if (setCoverObj.getBaseCorpus() != null) {
			posTags = setCoverObj.getBaseCorpus().getLinguisticProcessor().getBasicPosTags();
		}
		
		return ok(setCoverEditor.render((DocumentSetCoverModel)createViewModel(setCoverObj), posTags));
	}
	
	public static Result create(String corpus) {
		return update(corpus, null);
	}
	
	@Transactional
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result update(String corpus, String setcover) {
		DocumentCorpus corpusObj = null;
		if (corpus != null) {
			corpusObj = fetchResource(corpus, DocumentCorpus.class);
		}
		
		DocumentSetCover setCoverObj = null;
		if (setcover != null) {
			setCoverObj = fetchResource(setcover, DocumentSetCover.class);
			if (corpusObj == null) {
				corpusObj = setCoverObj.getBaseCorpus();
			} else if (ObjectUtils.notEqual(corpusObj, setCoverObj.getBaseCorpus())) {
				throw new IllegalArgumentException();
			}
		} else if (corpusObj == null) {
			throw new IllegalArgumentException();
		}
		
		JsonNode jsonBody = request().body().asJson();
		if (jsonBody == null && setcover != null) {
			throw new IllegalArgumentException();
		} else if (setcover == null) {
			setCoverObj = (DocumentSetCover)new DocumentSetCover(corpusObj)
				.setTitle(corpusObj.getTitle() + " untitled reduction")
				.setOwnerId(getUsername());
			em().persist(setCoverObj);
		}
		
		if (jsonBody == null) {
			return ok(createViewModel(setCoverObj).asJson());
		}
		
		DocumentSetCoverModel viewModel = Json.fromJson(jsonBody, DocumentSetCoverModel.class);
		final SetCoverFactoryOptions factoryOptions = (SetCoverFactoryOptions)viewModel.toFactoryOptions()
			.setStore(corpusObj)
			.setOwnerId(getUsername())
			.setEm(em());
		
		final SetCoverController controller = new SetCoverController();
		
		ProgressObserverToken tmpPoToken = ProgressObserverToken.find.byId(setCoverObj.getId());
		if (tmpPoToken != null) {
			tmpPoToken.delete();
		}
		final ProgressObserverToken poToken = new ProgressObserverToken()
			.setId(setCoverObj.getId())
			.setSession(getWebSession());

		// if it's a simple change, no need to do anything complicated.
		if (ObjectUtils.equals(factoryOptions.getTokenizingOptions(), ObjectUtils.defaultIfNull(setCoverObj.getTokenizingOptions(), new TokenizingOptions()))
			// using view model weight coverage as it allows for nulls.
			&& ObjectUtils.equals(viewModel.weightCoverage, setCoverObj.getWeightCoverage())) {
			
			// TODO: this should happen through the factory as well, but shortcutting for now.
			setCoverObj.setTitle(factoryOptions.getTitle());
			setCoverObj.setDescription(factoryOptions.getDescription());
			em().merge(setCoverObj);
			
			poToken.progress = 1.0;
			poToken.save();
			return ok(createViewModel(setCoverObj).asJson());
		}
		
		poToken.save();
		controller.addProgessObserver(new ProgressObserver() {
			@Override
			public void observe(final double progress, String message) {
				if (!"create".equalsIgnoreCase(message)) {
					return;
				}
				
				Ebean.execute(new TxRunnable() {
					@Override
					public void run() {
						ProgressObserverToken updatedToken = ProgressObserverToken.find.byId(poToken.id);
						updatedToken.progress = progress;
						updatedToken.update();
					}
				});
			}
		});
		
		Akka.future(new Callable<DocumentSetCover>() {
			@Override
			public DocumentSetCover call() throws Exception {
				try {
					return execute(new SareTxRunnable<DocumentSetCover>() {
						@Override
						public DocumentSetCover run(EntityManager em) throws Throwable {
							DocumentSetCover setcover = controller.create(factoryOptions);
							em.merge(setcover);
							for (SetCoverDocument document : setcover.getAllDocuments()) {
								if (em.contains(document)) {
									em.merge(document);
								} else {
									em.persist(document);
								}
							}
							
							return setcover;
						}
					});
				} catch (Throwable e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		
		return ok(createViewModel(setCoverObj).asJson());
	}
}