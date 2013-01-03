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

package controllers;

import static controllers.base.SareTransactionalAction.*;
import static controllers.base.SessionedAction.*;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.codehaus.jackson.JsonNode;

import com.google.common.collect.Iterables;

import models.documentStore.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import controllers.base.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.*;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

@With(SareTransactionalAction.class)
public class CollectionsController extends Application {

	public static Result list() {
		return ok(play.libs.Json.toJson(new PersistentDocumentStoreController().getAllUuids(em(), getUsername())));
	}
	
	public static Result create() {
		return update(null);
	}
	
	public static Result update(String collection) {
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
				OpinionCorpusFactoryOptionsModel viewModel = play.libs.Json.fromJson(json,
					OpinionCorpusFactoryOptionsModel.class);
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
			.setExistingId(collection)
			.setEm(em());
		
		OpinionCorpusFactory corpusFactory = new OpinionCorpusFactory();
		options.setOwnerId(SessionedAction.getUsername(ctx()));
		OpinionCorpus corpus = corpusFactory.create(options);
		if (em().contains(corpus)) {
			em().merge(corpus);
		} else {
			em().persist(corpus);
		}
		
		return created(createViewModel(corpus).asJson());
	}
	
	public static Result get(String collection) {
		PersistentDocumentStore store = fetchResource(collection, PersistentDocumentStore.class);
		return ok(createViewModel(store).asJson());
	}
	
	public static Result delete(String collection) {
		PersistentDocumentStore collectionObj = fetchResource(collection, PersistentDocumentStore.class);
		em().remove(collectionObj);
		
		return ok(createViewModelQuietly(collectionObj).asJson());
	}
}