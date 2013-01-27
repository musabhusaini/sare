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

import static models.base.ViewModel.*;
import static controllers.base.SessionedAction.*;
import static controllers.base.SareTransactionalAction.*;

import java.util.*;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.codehaus.jackson.JsonNode;

import com.google.common.base.Function;
import com.google.common.collect.*;

import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.tags.*;
import models.document.OpinionDocumentModel;
import models.documentStore.*;
import controllers.DocumentsController;
import controllers.base.*;
import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.FullTextDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

@With(SareTransactionalAction.class)
@Module.Requires
public class CorpusModule extends Module {

	@Override
	public String getDisplayName() {
		return "Corpus selection";
	}

	@Override
	public String getRoute() {
		return controllers.modules.routes.CorpusModule.modulePage(false).url();
	}
		
	public static List<PersistentDocumentStoreModel> getCorpora() {
		PersistentDocumentStoreController docStoreController = new PersistentDocumentStoreController();
		return Lists.transform(docStoreController.getAllUuids(em(), getUsername(), DocumentCorpus.class),
			new Function<String, PersistentDocumentStoreModel>() {
				@Override
				@Nullable
				public PersistentDocumentStoreModel apply(@Nullable String input) {
					return (PersistentDocumentStoreModel)createViewModel(fetchResource(input, PersistentDocumentStore.class));
				}
			});
	}
		
	public static Result modulePage(boolean partial) {
		return moduleRender(storeList.render(getCorpora(), true, "Corpus"), partial);
	}
		
	public static Result create() {
		return update(null);
	}
	
	public static Result update(String corpus) {
		OpinionCorpusFactoryOptions options = null;
		
		MultipartFormData formData = request().body().asMultipartFormData();
		if (formData != null) {
			// if we have a multi-part form with a file.
			if (formData.getFiles() != null) {
				// get either the file named "corpus" or the first one.
				FilePart filePart = ObjectUtils.defaultIfNull(formData.getFile("corpus"),
					Iterables.getFirst(formData.getFiles(), null));
				if (filePart != null) {
					options = new OpinionCorpusFactoryOptions()
						.setFile(filePart.getFile())
						.setFormat(FilenameUtils.getExtension(filePart.getFilename()));
				}
			}
		} else {
			// otherwise try as a json body.
			JsonNode json = request().body().asJson();
			if (json != null) {
				OpinionCorpusFactoryOptionsModel viewModel = Json.fromJson(json, OpinionCorpusFactoryOptionsModel.class);
				if (viewModel != null) {
					options = viewModel.toFactoryOptions();
				} else {
					throw new IllegalArgumentException();
				}
			} else {
				// if not json, then treat the whole thing as a file.
				options = new OpinionCorpusFactoryOptions()
					.setFile(request().body().asRaw().asFile())
					.setFormat(request().getHeader(CONTENT_TYPE));
			}
		}
		
		if (options == null) {
			throw new IllegalArgumentException();
		}
		
		options
			.setExistingId(corpus)
			.setEm(em());
		
		OpinionCorpusFactory corpusFactory = new OpinionCorpusFactory();
		options.setOwnerId(SessionedAction.getUsername(ctx()));
		OpinionCorpus corpusObj = corpusFactory.create(options);
		if (!em().contains(corpusObj)) {
			em().persist(corpusObj);
			return created(createViewModel(corpusObj).asJson());
		}
		
		for (PersistentObject obj : corpusObj.getDocuments()) {
			if (em().contains(obj)) {
				em().merge(obj);
			} else {
				em().persist(obj);
			}
		}
		em().merge(corpusObj);
		return ok(createViewModel(corpusObj).asJson());
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result addDocument(String corpus) {
		return updateDocument(corpus, null);
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result updateDocument(String corpus, String document) {
		DocumentCorpus store = fetchResource(corpus, DocumentCorpus.class);
		
		if (store instanceof OpinionCorpus) {
			OpinionDocumentModel viewModel = Json.fromJson(request().body().asJson(), OpinionDocumentModel.class);
			OpinionDocumentFactoryOptions options = (OpinionDocumentFactoryOptions)new OpinionDocumentFactoryOptions()
				.setContent(viewModel.content)
				.setPolarity(viewModel.polarity)
				.setCorpus((OpinionCorpus)store)
				.setEm(em())
				.setExistingId(document);
			
			OpinionDocument documentObj = new OpinionDocumentFactory().create(options);
			if (em().contains(documentObj)) {
				em().merge(documentObj);
			} else {
				em().persist(documentObj);
			}
			
			return created(createViewModel(documentObj).asJson());
		}
		
		return badRequest();
	}
	
	public static Result deleteDocument(String corpus, String document) {
		FullTextDocument documentObj = DocumentsController.fetchDocument(corpus, document, FullTextDocument.class);
		em().remove(documentObj);
		return ok(createViewModel(documentObj).asJson());
	}
}