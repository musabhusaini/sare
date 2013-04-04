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
import org.codehaus.jackson.node.JsonNodeFactory;

import com.avaje.ebean.*;
import com.google.common.base.*;
import com.google.common.collect.*;

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
	@Module.Requires({DocumentCorpusModel.class, DocumentSetCoverModel.class}),
	@Module.Requires({DocumentCorpusModel.class})
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
	public static Result update(final String corpus, String setcover) {
		DocumentCorpus corpusObj = null;
		if (corpus != null) {
			corpusObj = fetchResource(corpus, DocumentCorpus.class);
		}
		
		// make sure we have the right combination.
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
		}
		if (jsonBody == null) {
			jsonBody = JsonNodeFactory.instance.objectNode();
		}
		
		DocumentSetCoverModel setCoverVM = null;
		setCoverVM = Json.fromJson(jsonBody, DocumentSetCoverModel.class);
		final SetCoverFactoryOptions factoryOptions = (SetCoverFactoryOptions)setCoverVM.toFactoryOptions()
			.setOwnerId(getUsername());
		final SetCoverController controller = new SetCoverController();
		
		// set the default title.
		if (setcover == null && StringUtils.isEmpty(factoryOptions.getTitle())) {
			factoryOptions.setTitle("Optimized " + corpusObj.getTitle());
		}
		
		SetCoverFactoryOptions tmpFactoryOptions = (SetCoverFactoryOptions)new SetCoverFactoryOptions()
			.setStore(corpusObj)
			.setTitle(factoryOptions.getTitle())
			.setDescription(factoryOptions.getDescription())
			.setOwnerId(factoryOptions.getOwnerId());
		
		// make basic creation/updation first.
		if (setcover == null) {
			setCoverObj = controller.create(tmpFactoryOptions);
			em().persist(setCoverObj);
			setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
			
			// if this is a simple change, just return from here.
			if (ObjectUtils.equals(ObjectUtils.defaultIfNull(factoryOptions.getTokenizingOptions(), new TokenizingOptions()), new TokenizingOptions())
				&& factoryOptions.getWeightCoverage() == SetCoverFactoryOptions.DEFAULT_WEIGHT_COVERAGE) {
				return created(setCoverVM.asJson());
			}
			setcover = UuidUtils.normalize(setCoverObj.getId());
		} else if (!StringUtils.equals(StringUtils.defaultString(tmpFactoryOptions.getTitle(), setCoverObj.getTitle()), setCoverObj.getTitle())
			|| !StringUtils.equals(StringUtils.defaultString(tmpFactoryOptions.getDescription(), setCoverObj.getDescription()), setCoverObj.getDescription())) {
			
			tmpFactoryOptions
				.setEm(em())
				.setExistingId(setcover);
			setCoverObj = controller.create(tmpFactoryOptions);
			em().merge(setCoverObj);
			setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
			setCoverVM.populateSize(em(), setCoverObj);
			
			// if this is a simple change, just return from here.
			if (ObjectUtils.equals(ObjectUtils.defaultIfNull(factoryOptions.getTokenizingOptions(), new TokenizingOptions()),
					ObjectUtils.defaultIfNull(setCoverObj.getTokenizingOptions(), new TokenizingOptions()))
				&& ObjectUtils.equals(factoryOptions.getWeightCoverage(),
					ObjectUtils.defaultIfNull(setCoverObj.getWeightCoverage(), SetCoverFactoryOptions.DEFAULT_WEIGHT_COVERAGE))) {
				return ok(setCoverVM.asJson());
			}
		}
		
		// get rid of any old progress observer tokens and create a new one.
		ProgressObserverToken oldPoToken = ProgressObserverToken.find.byId(setCoverObj.getId());
		if (oldPoToken != null) {
			oldPoToken.delete();
		}
		final ProgressObserverToken poToken = new ProgressObserverToken()
			.setId(setCoverObj.getId())
			.setSession(getWebSession());

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
		final String setCoverId = setcover;
		
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
								setCoverObj = fetchResource(setCoverId, DocumentSetCover.class);
								corpusId = UuidUtils.normalize(setCoverObj.getBaseCorpus().getId());
							}
							
							factoryOptions
								.setStore(fetchResource(corpusId, DocumentCorpus.class))
								.setExistingId(setCoverId)
								.setEm(em);
							
							List<SetCoverDocument> oldDocuments = Lists.newArrayList(setCoverObj.getAllDocuments());
							setCoverObj = controller.create(factoryOptions);
							
							em.flush();
							em.merge(setCoverObj);
							em.getTransaction().commit();
							em.clear();
							
							em.getTransaction().begin();
							for (SetCoverDocument oldDocument : oldDocuments) {
								if (Iterables.find(setCoverObj.getAllDocuments(), Predicates.equalTo(oldDocument), null) == null) {
									SetCoverDocument tmpDocument = em.find(SetCoverDocument.class, oldDocument.getId());
									em.remove(tmpDocument);
								}
							}
							
							return setCoverObj;
						}
					}, ctx);
				} catch (Throwable e) {
					Logger.error(LoggedAction.getLogEntry(ctx, "failed to build set cover"), e);
					throw new IllegalArgumentException(e);
				} finally {
					Ebean.execute(new TxRunnable() {
						@Override
						public void run() {
							ProgressObserverToken updatedToken = ProgressObserverToken.find.byId(poToken.id);
							updatedToken.setProgress(1.1);
							updatedToken.update();
						}
					});
				}
			}
		});

		return ok(setCoverVM.asJson());
	}
	
	public static Result redeem(String setcover) {
		DocumentSetCover setCoverObj = fetchResource(setcover, DocumentSetCover.class);
		
		ProgressObserverToken updatedToken = ProgressObserverToken.find.byId(setCoverObj.getId());
		if (updatedToken == null || updatedToken.getProgress() >= 1.1) {
			if (updatedToken != null) {
				updatedToken.delete();
			}
			
			DocumentSetCoverModel setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
			setCoverVM.populateSize(em(), setCoverObj);
			if (setCoverVM.size > 0) {
				setCoverVM.coverageMatrix = new SetCoverController().calculateCoverageMatrix(setCoverObj);
			}
			
			return ok(setCoverVM.asJson());
		}
		
		return ok(new ProgressObserverTokenModel(updatedToken).asJson());
	}
	
	public static Result getSetCover(String setcover, boolean includeMatrix) {
		DocumentSetCover setCoverObj = fetchResource(setcover, DocumentSetCover.class);
		DocumentSetCoverModel setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
		setCoverVM.populateSize(em(), setCoverObj);
		if (includeMatrix && setCoverVM.size > 0) {
			setCoverVM.coverageMatrix = new SetCoverController().calculateCoverageMatrix(setCoverObj);
		}
		
		return ok(setCoverVM.asJson());
	}
}