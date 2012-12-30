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

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;

/**
 * The generic base class for documents.
 * @author Mus'ab Husaini
 *
 * @param <T> a circular reference to this type of document; must be the same as this class.
 */
public abstract class GenericDocument<T extends GenericDocument<T>>
	extends TokenizedDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2122612051400547475L;

	@SuppressWarnings("unchecked")
	@Override
	public GenericDocumentStore<T> getStore() {
		if (this.store == null) {
			return null;
		}
		
		try {
			return (GenericDocumentStore<T>)this.store;
		} catch(ClassCastException e) {
			return null;
		}
	}
	
	/**
	 * Sets the document store that this document is stored under.
	 * @param stores the {@link GenericDocumentStore} to store this document under.
	 * @return the {@code this} object.
	 */
	@SuppressWarnings("unchecked")
	public GenericDocument<T> setStore(GenericDocumentStore<T> store) {
		GenericDocumentStore<T> prevStore = this.getStore();
		
		this.store = store;
		
		if (prevStore != null) {
			prevStore.removeDocument((T)this);
		}
		
		if (store != null) {
			store.addDocument((T)this);
			this.addReference(store);
		}
		
		return this;
	}
}