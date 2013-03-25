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

import play.Logger;
import play.libs.*;
import play.mvc.*;
import play.mvc.Http.Context;
import views.html.tags.*;
import models.base.ViewModel;
import models.documentStore.*;
import models.web.*;
import controllers.base.*;
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
		return "Optimize Corpus";
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
					DocumentSetCover setcover = fetchResource(input, DocumentSetCover.class);
					PersistentDocumentStoreModel setcoverVM = (PersistentDocumentStoreModel)createViewModel(setcover);
					setcoverVM.populateSize(em(), setcover);
					return setcoverVM;
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
			setCoverVM.populateSize(em(), setCoverObj);
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
		corpusVM.populateSize(em(), corpusObj);
		return moduleRender(new SetCoverBuilder().setViewModels(Lists.<ViewModel>newArrayList(corpusVM, setCoverVM)),
			setCoverBuilder.render(corpusVM, setCoverVM, corpusObj.getLinguisticProcessor().getBasicPosTags()), partial);
	}
	
	public static Result editorView(String setcover) {
		DocumentSetCover setCoverObj = fetchResource(setcover, DocumentSetCover.class);
		DocumentSetCoverModel setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
		setCoverVM.populateSize(em(), setCoverObj);
		if (new DocumentSetCoverController().getSize(em(), setCoverObj) > 0) {
			setCoverVM.coverageMatrix = new SetCoverController().calculateCoverageMatrix(setCoverObj);
		}
		
		Map<String, String> posTags = null;
		if (setCoverObj.getBaseCorpus() != null) {
			posTags = setCoverObj.getBaseCorpus().getLinguisticProcessor().getBasicPosTags();
		}
		
		return ok(setCoverEditor.render(setCoverVM, posTags));
	}
	
	public static Result create(String corpus) {
		return update(corpus, null);
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result update(final String corpus, final String setcover) {
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
				.setTitle("Optimized " + corpusObj.getTitle())
				.setOwnerId(getUsername());
		}
		
		DocumentSetCoverModel setCoverVM = null;
		if (jsonBody == null) {
			setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
			setCoverVM.populateSize(em(), setCoverObj);
			return ok(setCoverVM.asJson());
		}
		
		setCoverVM = Json.fromJson(jsonBody, DocumentSetCoverModel.class);
		final SetCoverFactoryOptions factoryOptions = (SetCoverFactoryOptions)setCoverVM.toFactoryOptions()
			.setOwnerId(getUsername());
		
		final SetCoverController controller = new SetCoverController();
		
		ProgressObserverToken oldPoToken = ProgressObserverToken.find.byId(setCoverObj.getId());
		if (oldPoToken != null) {
			oldPoToken.delete();
		}
		final ProgressObserverToken poToken = new ProgressObserverToken()
			.setId(setCoverObj.getId())
			.setSession(getWebSession());

		// create the response view model from now.
		setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
		setCoverVM.populateSize(em(), setCoverObj);
		
		// if it's a simple change, no need to do anything complicated.
		if (ObjectUtils.equals(factoryOptions.getTokenizingOptions(), ObjectUtils.defaultIfNull(setCoverObj.getTokenizingOptions(), new TokenizingOptions()))
			// using view model weight coverage as it allows for nulls.
			&& ObjectUtils.equals(setCoverVM.weightCoverage, setCoverObj.getWeightCoverage())) {
			
			// TODO: this should happen through the factory as well, but shortcutting for now.
			setCoverObj.setTitle(factoryOptions.getTitle());
			setCoverObj.setDescription(factoryOptions.getDescription());
			
			if (em().contains(setCoverObj)) {
				em().merge(setCoverObj);
			} else {
				em().persist(setCoverObj);
			}
			
			poToken.progress = 1.0;
			poToken.save();
			
			return ok(setCoverVM.asJson());
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
						updatedToken.setProgress(progress);
						updatedToken.update();
					}
				});
			}
		});
		
		final Context ctx = Context.current();
		
		Akka.future(new Callable<DocumentSetCover>() {
			@Override
			public DocumentSetCover call() throws Exception {
				try {
					return execute(new SareTxRunnable<DocumentSetCover>() {
						@Override
						public DocumentSetCover run(EntityManager em) throws Throwable {
							bindEntityManager(em);
							Context.current.set(ctx);
							
							DocumentSetCover setCoverObj = null;
							String corpusId = corpus;
							if (corpusId == null) {
								setCoverObj = fetchResource(setcover, DocumentSetCover.class);
								corpusId = UuidUtils.normalize(setCoverObj.getBaseCorpus().getId());
							}
							
							factoryOptions
								.setStore(fetchResource(corpusId, DocumentCorpus.class))
								.setEm(em);
							
							setCoverObj = controller.create(factoryOptions);
							for (SetCoverDocument document : setCoverObj.getAllDocuments()) {
								if (em.contains(document)) {
									em.merge(document);
								} else {
									em.persist(document);
								}
							}
							em.merge(setCoverObj);
							
							Ebean.execute(new TxRunnable() {
								@Override
								public void run() {
									ProgressObserverToken updatedToken = ProgressObserverToken.find.byId(poToken.id);
									updatedToken.setProgress(1.0);
									updatedToken.update();
								}
							});
							
							return setCoverObj;
						}
					}, ctx);
				} catch (Throwable e) {
					Ebean.execute(new TxRunnable() {
						@Override
						public void run() {
							ProgressObserverToken updatedToken = ProgressObserverToken.find.byId(poToken.id);
							updatedToken.setProgress(1.0);
							updatedToken.update();
						}
					});
					
					Logger.error(LoggedAction.getLogEntry(ctx, "failed to build set cover"), e);
					throw new IllegalArgumentException(e);
				}
			}
		});
		
		return ok(setCoverVM.asJson());
	}
	
	public static Result redeem(String setcover) {
		DocumentSetCover setCoverObj = fetchResource(setcover, DocumentSetCover.class);
		
		ProgressObserverToken updatedToken = ProgressObserverToken.find.byId(setCoverObj.getId());
		if (updatedToken == null) {
			DocumentSetCoverModel setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
			setCoverVM.populateSize(em(), setCoverObj);
			return ok(setCoverVM.asJson());
		}
		
		if (updatedToken.progress >= 1.0) {
			updatedToken.delete();
			
			DocumentSetCoverModel setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
			setCoverVM.populateSize(em(), setCoverObj);
			if (new DocumentSetCoverController().getSize(em(), setCoverObj) > 0) {
				setCoverVM.coverageMatrix = new SetCoverController().calculateCoverageMatrix(setCoverObj);
			}
			
			return ok(setCoverVM.asJson());
		}
		
		return ok(new ProgressObserverTokenModel(updatedToken).asJson());
	}
}