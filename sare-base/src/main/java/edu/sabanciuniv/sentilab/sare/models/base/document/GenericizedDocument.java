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

package edu.sabanciuniv.sentilab.sare.models.base.document;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.GenericizedDocumentStore;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A generic wrapper for {@link PersistentDocument} objects. Provides generic accessors.
 * @author Mus'ab Husaini
 * @param <T> the type of document.
 */
public class GenericizedDocument<T extends PersistentDocument>
	implements IDocument {

	private T document;
	
	/**
	 * Creates a new instance of the {@link GenericizedDocument} class.
	 * @param document the underlying {@link PersistentDocument} object.
	 */
	public GenericizedDocument(T document) {
		Validate.notNull(document, CannedMessages.NULL_ARGUMENT, "document");
		
		this.document = document;
	}
	
	/**
	 * Gets the original underlying document.
	 * @return the {@code T} type original document.
	 */
	public T getOriginal() {
		return this.document;
	}
	
	@Override
	public String getContent() {
		return this.document.getContent();
	}

	@Override
	public GenericizedDocumentStore<T> getStore() {
		return new GenericizedDocumentStore<T>(this.document.getStore());
	}
}