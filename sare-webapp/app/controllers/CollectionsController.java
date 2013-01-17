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

import play.mvc.*;

import controllers.base.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

@With(SareTransactionalAction.class)
public class CollectionsController extends Application {

	public static Result list() {
		return ok(play.libs.Json.toJson(new PersistentDocumentStoreController().getAllUuids(em(), getUsername())));
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