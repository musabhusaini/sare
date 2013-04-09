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

import com.avaje.ebean.annotation.Transactional;
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
		
		return controllers.modules.routes.SetCoverBuilder.modulePage(corpusVM.getIdentifier(), setCoverVM.getIdentifier(), false).url();
	}
	
	public static List<PersistentDocumentStoreModel> getSetCovers(DocumentCorpusModel corpus) {
		if (corpus == null) {
			return Lists.newArrayList();
		}
		
		DocumentCorpus corpusObj = fetchResource(UuidUtils.create(corpus.id), DocumentCorpus.class);
		return Lists.transform(new DocumentSetCoverController().getAllSetCovers(em(), getUsername(), corpusObj),
			new Function<String, PersistentDocumentStoreModel>() {
				@Override
				@Nullable
				public PersistentDocumentStoreModel apply(@Nullable String input) {
					DocumentSetCover setcover = fetchResource(UuidUtils.create(input), DocumentSetCover.class);
					PersistentDocumentStoreModel setcoverVM = (PersistentDocumentStoreModel)createViewModel(setcover);
					setcoverVM.populateSize(em(), setcover);
					return setcoverVM;
				}
			});
	}
	
	public static Result modulePage(UUID corpus, UUID setcover, boolean partial) {
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
	
	public static Result editorView(UUID setcover) {
		DocumentSetCover setCoverObj = fetchResource(setcover, DocumentSetCover.class);
		DocumentSetCoverModel setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
		setCoverVM.populateSize(em(), setCoverObj);
		
		Map<String, String> posTags = null;
		if (setCoverObj.getBaseCorpus() != null) {
			posTags = setCoverObj.getBaseCorpus().getLinguisticProcessor().getBasicPosTags();
		}
		
		return ok(setCoverEditor.render(setCoverVM, posTags));
	}
	
	public static Result create(UUID corpus) {
		return update(corpus, null);
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result update(final UUID corpus, UUID setcover) {
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
		final SetCoverController factory = (SetCoverController)setCoverVM.toFactory()
			.setOwnerId(getUsername());
		
		// set the default title.
		if (setcover == null && StringUtils.isEmpty(factory.getTitle())) {
			factory.setTitle("Optimized " + corpusObj.getTitle());
		}
		
		SetCoverController tmpFactory = (SetCoverController)new SetCoverController()
			.setStore(corpusObj)
			.setTitle(factory.getTitle())
			.setDescription(factory.getDescription())
			.setOwnerId(factory.getOwnerId());
		
		// make basic creation/updation first.
		if (setcover == null) {
			setCoverObj = tmpFactory.create();
			em().persist(setCoverObj);
			setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
			
			// if this is a simple change, just return from here.
			if (ObjectUtils.equals(ObjectUtils.defaultIfNull(factory.getTokenizingOptions(), new TokenizingOptions()), new TokenizingOptions())
				&& factory.getWeightCoverage() == SetCoverController.DEFAULT_WEIGHT_COVERAGE) {
				return created(setCoverVM.asJson());
			}
			setcover = setCoverObj.getIdentifier();
		} else if (!StringUtils.equals(StringUtils.defaultString(tmpFactory.getTitle(), setCoverObj.getTitle()), setCoverObj.getTitle())
			|| !StringUtils.equals(StringUtils.defaultString(tmpFactory.getDescription(), setCoverObj.getDescription()), setCoverObj.getDescription())) {
			
			tmpFactory
				.setEm(em())
				.setExistingId(setcover);
			setCoverObj = tmpFactory.create();
			em().merge(setCoverObj);
			setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
			setCoverVM.populateSize(em(), setCoverObj);
			
			// if this is a simple change, just return from here.
			if (ObjectUtils.equals(ObjectUtils.defaultIfNull(factory.getTokenizingOptions(), new TokenizingOptions()),
					ObjectUtils.defaultIfNull(setCoverObj.getTokenizingOptions(), new TokenizingOptions()))
				&& ObjectUtils.equals(factory.getWeightCoverage(),
					ObjectUtils.defaultIfNull(setCoverObj.getWeightCoverage(), SetCoverController.DEFAULT_WEIGHT_COVERAGE))) {
				return ok(setCoverVM.asJson());
			}
		}
		
		// get rid of any old progress observer tokens and create a new one.
		finalizeProgress(setCoverObj.getId());
		createProgressObserverToken(setCoverObj.getId());
		watchProgress(factory, "create", setCoverObj.getId());
		
		final Context ctx = Context.current();
		final UUID setCoverId = setcover;
		
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
							UUID corpusId = corpus;
							if (corpusId == null) {
								setCoverObj = fetchResource(setCoverId, DocumentSetCover.class);
								corpusId = setCoverObj.getBaseCorpus().getIdentifier();
							}
							
							factory
								.setStore(fetchResource(corpusId, DocumentCorpus.class))
								.setExistingId(setCoverId)
								.setEm(em);
							
							List<SetCoverDocument> oldDocuments = Lists.newArrayList(setCoverObj.getAllDocuments());
							setCoverObj = factory.create();
							
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
					setProgressFinished(UuidUtils.toBytes(setCoverId));
				}
			}
		});

		return ok(setCoverVM.asJson());
	}
	
	@Transactional
	public static Result redeem(UUID setcover) {
		DocumentSetCover setCoverObj = fetchResource(setcover, DocumentSetCover.class);
		
		ProgressObserverToken poToken = redeemProgress(setCoverObj.getId());
		if (poToken == null) {
			DocumentSetCoverModel setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
			setCoverVM.populateSize(em(), setCoverObj);
			if (setCoverVM.size > 0) {
				setCoverVM.coverageMatrix = setCoverObj.calculateCoverageMatrix();
			}
			
			return ok(setCoverVM.asJson());
		}
		
		return ok(new ProgressObserverTokenModel(poToken).asJson());
	}
	
	public static Result getSetCover(UUID setcover, boolean includeMatrix) {
		DocumentSetCover setCoverObj = fetchResource(setcover, DocumentSetCover.class);
		DocumentSetCoverModel setCoverVM = (DocumentSetCoverModel)createViewModel(setCoverObj);
		setCoverVM.populateSize(em(), setCoverObj);
		if (includeMatrix && setCoverVM.size > 0) {
			setCoverVM.coverageMatrix = setCoverObj.calculateCoverageMatrix();
		}
		
		return ok(setCoverVM.asJson());
	}
}