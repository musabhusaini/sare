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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import org.apache.commons.lang3.Validate;

import com.google.common.base.*;
import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A generic wrapped for {@link PersistentDocumentStore} objects. Provides generic accessors.
 * @author Mus'ab Husaini
 * @param <T> the type of {@link PersistentDocument} objects that the store must take.
 */
public class GenericizedDocumentStore<T extends PersistentDocument>
	implements IDocumentStore {

	/**
	 * Creates an instance of the {@link GenericizedDocumentStore} class.
	 * @param store the {@link PersistentDocumentStore} to wrap this generic around.
	 * @param clazz the type of the store.
	 * @return the newly-created {@link GenericizedDocumentStore} object.
	 */
	public static <D extends PersistentDocument> GenericizedDocumentStore<D> create(PersistentDocumentStore store, Class<D> clazz) {
		Validate.notNull(clazz, CannedMessages.NULL_ARGUMENT, "clazz");
		return new GenericizedDocumentStore<D>(store);
	}
	
	private PersistentDocumentStore store;
	
	/**
	 * Creates a new instance f the {@link GenericizedDocumentStore} class.
	 * @param store the {@link PersistentDocumentStore} to wrap.
	 */
	public GenericizedDocumentStore(PersistentDocumentStore store) {
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		this.store = store;
	}
	
	/**
	 * Gets the original underlying store.
	 * @return the underlying {@link PersistentDocument} object.
	 */
	public PersistentDocumentStore getOriginal() {
		return this.store;
	}
	
	@Override
	public String getTitle() {
		return this.store.getTitle();
	}

	@Override
	public String getLanguage() {
		return this.store.getLanguage();
	}

	@Override
	public String getDescription() {
		return this.store.getDescription();
	}

	@Override
	public Iterable<T> getDocuments()  {
		return Iterables.filter(Iterables.transform(this.store.getDocuments(), new Function<PersistentDocument, T>() {
			@SuppressWarnings("unchecked")
			public T apply(PersistentDocument document) {
				T typedDocument = null;
				try {
					typedDocument = (T)document;
				} catch(ClassCastException e) {
					typedDocument = null;
				}
				
				return typedDocument;
			}
		}), Predicates.notNull());
	}

	/**
	 * Sets the documents in this store.
	 * @param documents the {@link Iterable} of documents to set.
	 * @return the {@code this} object.
	 */
	public GenericizedDocumentStore<T> setDocuments(Iterable<T> documents) {
		this.store.setDocuments(documents);
		return this;
	}
	
	/**
	 * Gets a boolean flag indicating whether the provided document is in this store or not.
	 * @param document the {@code T} object to look for.
	 * @return {@code true} if the document is contained in this store, {@code false} otherwise.
	 */
	public boolean hasDocument(T document) {
		return this.store.hasDocument(document);
	}
	
	/**
	 * Adds a document to this store.
	 * @param document the {@code T} type document to add.
	 * @return the {@code this} object.
	 */
	public GenericizedDocumentStore<T> addDocument(T document) {
		this.store.addDocument(document);
		return this;
	}
	
	/**
	 * Removes a document from this store.
	 * @param document the {@code T} type document to remove.
	 * @return {@code true} if an element was removed as a result of this call. 
	 */
	public boolean removeDocument(T document) {
		return this.store.removeDocument(document);
	}
}