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

package controllers;

import static models.base.ViewModel.*;
import static controllers.base.SareTransactionalAction.*;
import static controllers.base.SessionedAction.*;

import java.util.*;

import models.documentStore.PersistentDocumentStoreModel;

import org.codehaus.jackson.node.*;

import com.google.common.collect.Lists;

import play.libs.Json;
import play.mvc.*;
import views.html.tags.*;

import controllers.base.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.LinguisticProcessorFactory;

@With(SareTransactionalAction.class)
public class CollectionsController extends Application {

	public static List<LinguisticProcessorInfo> getSupportedLanguages() {
		return Lists.newArrayList(LinguisticProcessorFactory.getSupportedProcessors());
	}

	public static Result supportedLanguages() {
		ArrayNode jsonArray = Json.newObject().arrayNode();
		for (LinguisticProcessorInfo lpi : getSupportedLanguages()) {
			ObjectNode json = Json.newObject();
			json.put("code", lpi.languageCode());
			json.put("name", lpi.languageName());
			jsonArray.add(json);
		}
		
		return ok(jsonArray);
	}

	public static Result list() {
		return ok(play.libs.Json.toJson(new PersistentDocumentStoreController().getAllUuids(em(), getUsername())));
	}
	
	public static Result get(UUID collection) {
		PersistentDocumentStore store = fetchResource(collection, PersistentDocumentStore.class);
		PersistentDocumentStoreModel storeVM = (PersistentDocumentStoreModel)createViewModel(store);
		storeVM.populateSize(em(), store);
		return ok(storeVM.asJson());
	}
	
	public static Result delete(UUID collection) {
		PersistentDocumentStore collectionObj = fetchResource(collection, PersistentDocumentStore.class);
		em().remove(collectionObj);
		
		return ok(createViewModelQuietly(collectionObj).asJson());
	}
	
	public static Result detailsForm(UUID collection) {
		PersistentDocumentStore store = fetchResource(collection, PersistentDocumentStore.class);
		PersistentDocumentStoreModel storeVM = (PersistentDocumentStoreModel)createViewModel(store);
		storeVM.populateSize(em(), store);
		
		return ok(storeDetails.render(storeVM, store instanceof IDerivedStore, store instanceof DocumentCorpus));
	}
}