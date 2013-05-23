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

import java.util.*;

import javax.annotation.Nullable;

import org.codehaus.jackson.JsonNode;

import models.base.ViewModel;
import models.document.PersistentDocumentModel;

import com.google.common.base.*;
import com.google.common.collect.*;

import play.libs.Json;
import play.mvc.*;
import views.html.tags.documentEditor;

import controllers.base.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

@With({ SessionedAction.class, SareTransactionalAction.class })
public class DocumentsController
		extends Application {

	public static <T extends PersistentDocument> T fetchDocument(UUID collection, UUID document, Class<T> clazz) {
		T documentObj = fetchResource(document, clazz);
		if (collection != null && !collection.equals(documentObj.getStore().getIdentifier())) {
			throw new IllegalArgumentException();
		}
		return documentObj;
	}
	
	public static PersistentDocument fetchDocument(UUID collection, UUID document) {
		return fetchDocument(collection, document, PersistentDocument.class);
	}
	
	public static PersistentDocumentModel fetchDocumentViewModel(UUID collection, UUID document) {
		return (PersistentDocumentModel)createViewModel(fetchDocument(collection, document));
	}
	
	public static Iterable<UUID> fetchDocumentIds(UUID store) {
		PersistentDocumentStore storeObj = fetchResource(store, PersistentDocumentStore.class);
		return Iterables.transform(storeObj.getDocumentIds(em()), UuidUtils.uuidBytesToUUIDFunction());
	}
	
	public static Result list(UUID collection) {
		return ok(Json.toJson(
			Lists.newArrayList(Iterables.transform(fetchDocumentIds(collection), UuidUtils.uuidToStringFunction()))));
	}
	
	public static Result get(final UUID collection, String document) {
		JsonNode documentNode = Json.parse(document);
		if (documentNode.isTextual()) {
			document = String.format("[%s]", document);
			documentNode = Json.parse(document);
		}
		
		if (documentNode.isArray()) {
			Iterator<ViewModel> documents = Iterators.transform(documentNode.getElements(),
				new Function<JsonNode, ViewModel>() {
					@Override
					@Nullable
					public ViewModel apply(@Nullable JsonNode input) {
						if (!input.isTextual()) {
							throw new IllegalArgumentException();
						}
						return createViewModel(fetchDocument(collection, UuidUtils.create(input.asText())));
					}
				}
			);
			
			return ok(Json.toJson(Iterators.toArray(Iterators.filter(documents, Predicates.notNull()), ViewModel.class)));
		}
		
		throw new IllegalArgumentException();
	}
	
	public static Result editorView(UUID collection, UUID document) {
		return ok(documentEditor.render((PersistentDocumentModel)createViewModel(fetchDocument(collection, document))));
	}
}