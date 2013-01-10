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

import models.document.OpinionDocumentModel;

import play.libs.Json;
import play.mvc.*;

import controllers.base.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentController;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

@With(SareTransactionalAction.class)
public class DocumentsController extends Application {

	private static PersistentDocument fetchDocument(String collection, String document) {
		PersistentDocument documentObj = fetchResource(document, PersistentDocument.class);
		if (!UuidUtils.normalize(collection).equals(UuidUtils.normalize(documentObj.getStore().getIdentifier()))) {
			throw new IllegalArgumentException();
		}
		return documentObj;
	}
	
	public static Result list(String collection) {
		// make sure we have access to this collection.
		fetchResource(collection, PersistentDocumentStore.class);
		
		return ok(play.libs.Json.toJson(new PersistentDocumentController().getAllUuids(em(), collection)));
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result add(String collection) {
		return update(collection, null);
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result update(String collection, String document) {
		PersistentDocumentStore store = fetchResource(collection, PersistentDocumentStore.class);
		
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
	
	public static Result get(String collection, String document) {
		return ok(createViewModel(fetchDocument(collection, document)).asJson());
	}
	
	public static Result delete(String collection, String document) {
		PersistentDocument documentObj = fetchDocument(collection, document);
		em().remove(documentObj);
		return ok(createViewModel(documentObj).asJson());
	}
}